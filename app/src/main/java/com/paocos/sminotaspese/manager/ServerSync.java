package com.paocos.sminotaspese.manager;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paocos.sminotaspese.data.DataLogDataProvider;
import com.paocos.sminotaspese.model.entities.DataLog;
import com.paocos.sminotaspese.model.entities.Setting;
import com.paocos.sminotaspese.model.entities.json.CardAllSync;
import com.paocos.sminotaspese.model.entities.json.RspBase;
import com.paocos.sminotaspese.model.entities.json.SetDataLogsResponse;
import com.paocos.sminotaspese.model.entities.json.SetDataLogsRequest;
import com.paocos.sminotaspese.model.entities.json.SetSettingRequest;
import com.paocos.sminotaspese.model.entities.json.SetSettingResponse;
import com.paocos.sminotaspese.shared.ConstantUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by paocos on 11/05/17.
 */

public class ServerSync  {


    private SharedPreferences sharedPreferences;

    private boolean running;

    private Context context;

    private CardAllSync cardAllSync;

    private DataLog dataLog;

    private DataLogDataProvider dataLogDataProvider;

    private NotificationManager mNotifyMgr;


    private final String FILESEPARATOR = File.separator;
    private final String PROTOCOL = "http://";
    private static final int IS_GET = 0;
    private static final int IS_POST = 1;

    private HttpURLConnection httpURLConnection;

    private SQLiteDatabase sqLiteDatabase;

    private static final String TAG = "ServerSync";

    private String url;

    public static final String DATALOGS = "DATALOGS";
    public static final String LUNCH = "LUNCH";
    public static final String RIEP = "RIEP";


    public ServerSync(SQLiteDatabase sqLiteDatabase, DataLogDataProvider dataLogDataProvider) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.dataLogDataProvider = dataLogDataProvider;
    }

    public void syncData(String... params) {
        new SyncData().execute(params);
    }

    /**
     * Sincronizza i dati con il server
     * @return
     */
    private class SyncData extends AsyncTask<String, Void , String> {

        @Override
        protected String doInBackground(String... params) {
            try {

                setRunning(true);

                // imposto url
                setUrl();

                // invio i settings al server
                sendSettings();

                if (params[0].trim().equalsIgnoreCase(DATALOGS)) {
                    // invio oggetto da sincronizzare
                    sendObjects();
                } else if (params[0].trim().equalsIgnoreCase(LUNCH)) {
                    // richiesta dati pranzo
                    //setDataLog(null);
                    getLunchData();
                } else if (params[0].trim().equalsIgnoreCase(RIEP)) {
                    // richiesta dati riepilogo
                    setCardAllSync(null);
                    getRiepData();
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "ko";
            } finally {

            }
            return "ok";
        }

        @Override
        protected void onPostExecute(String result) {
            setRunning(false);
        }

    }

    private void getLunchData() throws Exception {
        try {

            // invio settings al server
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(getDataLog());

            String response = sendToServer("getDataLunch" , json);

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            setDataLog(mapper.readValue(response , DataLog.class));

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void getRiepData() throws Exception {
        try {

            String response = sendToServer("getRiep");

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            setCardAllSync(mapper.readValue(response , CardAllSync.class));

            ConstantUtil.removeNotification(mNotifyMgr , ConstantUtil.ID_NOTIFY_READ_RIEP);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void sendObjects() throws Exception {
        try {

            SetDataLogsRequest setDataLogsRequest = new SetDataLogsRequest();

            setDataLogsRequest.setDataLogs(dataLogDataProvider.getUnSynched(sqLiteDatabase));

            // invio settings al server
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(setDataLogsRequest);

            String response = sendToServer("setDataLogs" , json);

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            SetDataLogsResponse setDataLogsResponse = mapper.readValue(response , SetDataLogsResponse.class);
            Log.d(TAG , "Response from setDataLogs: " + ((RspBase) setDataLogsResponse).toString());

            // se ok aggiorno il db
            if (setDataLogsResponse.isStatus()) {
                dataLogDataProvider.updateFromServer(sqLiteDatabase , setDataLogsResponse.getDataLogs());
            }

            ConstantUtil.removeNotification(mNotifyMgr , ConstantUtil.ID_NOTIFY_REMOTE_SAVE);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void setUrl() {
        String serverIP = getSharedPreferences().getString("serverIp", null);
        String serverPath = getSharedPreferences().getString("serverPath", null);
        String serverPort = getSharedPreferences().getString("serverPort", null);
        url = PROTOCOL + serverIP.trim() + ":" + serverPort + FILESEPARATOR + serverPath.trim();
    }

    private void sendSettings() throws Exception {
        try {

            SetSettingRequest setSettingRequest = new SetSettingRequest();
            setSettingRequest.setSettings(new ArrayList<Setting>());

            // carico le prefs in un hashmap
            Map<String,?> keys = getSharedPreferences().getAll();
            for(Map.Entry<String,?> entry : keys.entrySet()){
                Setting setting = new Setting();
                setting.setKey(entry.getKey());
                setting.setValue(entry.getValue().toString());
                setSettingRequest.getSettings().add(setting);
            }

            // invio settings al server
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(setSettingRequest);

            String response = sendToServer("setSettings" , json);

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            SetSettingResponse setSettingResponse = mapper.readValue(response , SetSettingResponse.class);
            Log.d(TAG , "Response from setSettings: " + setSettingResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private String sendToServer(String method , String data) throws Exception {

        String result;

        //Connect
        final String myUrl = url + FILESEPARATOR + method.trim();
        httpURLConnection = (HttpURLConnection) ((new URL (myUrl).openConnection()));
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.connect();

        //Write
        OutputStream os = httpURLConnection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(data);
        writer.close();
        os.close();

        //Read
        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));

        String line = null;
        StringBuilder sb = new StringBuilder();

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();
        httpURLConnection.disconnect();
        result = sb.toString();

        return result;

    }

    private String sendToServer(String method) throws Exception {
        return sendToServer(method , ServerSync.IS_POST);
    }

    private String sendToServer(String method , int type) throws Exception {

        String result;

        //Connect
        final String myUrl = url + FILESEPARATOR + method.trim();
        httpURLConnection = (HttpURLConnection) ((new URL (myUrl).openConnection()));
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        if (type == ServerSync.IS_GET) {
            httpURLConnection.setRequestMethod("GET");
        } else {
            httpURLConnection.setRequestMethod("POST");
        }
        httpURLConnection.connect();

        //Read
        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));

        String line = null;
        StringBuilder sb = new StringBuilder();

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();
        httpURLConnection.disconnect();
        result = sb.toString();

        return result;

    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public SQLiteDatabase getSqLiteDatabase() {
        return sqLiteDatabase;
    }

    public void setSqLiteDatabase(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public DataLogDataProvider getDataLogDataProvider() {
        return dataLogDataProvider;
    }

    public void setDataLogDataProvider(DataLogDataProvider dataLogDataProvider) {
        this.dataLogDataProvider = dataLogDataProvider;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public DataLog getDataLog() {
        return dataLog;
    }

    public void setDataLog(DataLog dataLog) {
        this.dataLog = dataLog;
    }

    public CardAllSync getCardAllSync() {
        return cardAllSync;
    }

    public void setCardAllSync(CardAllSync cardAllSync) {
        this.cardAllSync = cardAllSync;
    }

    public NotificationManager getmNotifyMgr() {
        return mNotifyMgr;
    }

    public void setmNotifyMgr(NotificationManager mNotifyMgr) {
        this.mNotifyMgr = mNotifyMgr;
    }
}
