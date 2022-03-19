package com.paocos.sminotaspese;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.paocos.sminotaspese.adapter.RVAdapter;
import com.paocos.sminotaspese.connector.GPSConnector;
import com.paocos.sminotaspese.connector.GpsConnectorFrg;
import com.paocos.sminotaspese.data.DataLogDataProvider;
import com.paocos.sminotaspese.manager.ServerSync;
import com.paocos.sminotaspese.model.entities.CardAll;
import com.paocos.sminotaspese.model.entities.CardGps;
import com.paocos.sminotaspese.model.entities.CardOrari;
import com.paocos.sminotaspese.model.entities.CardPranzo;
import com.paocos.sminotaspese.model.entities.CardRiep;
import com.paocos.sminotaspese.model.entities.CardTemp;
import com.paocos.sminotaspese.model.entities.DataLog;
import com.paocos.sminotaspese.model.entities.MyLocation;
import com.paocos.sminotaspese.shared.ConstantUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * adb root shell
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

//    private ArrayList<GpsPoint> gpsPoints;

    private Boolean closePending = false;

    private boolean gpsReading;
    private boolean isFirstTime = true;
    private Boolean isNetworkConnected;
    private boolean toReadRiep=false;
    private boolean isSede;
    private boolean localSaving;
    private boolean localSaving_lunch;
    private boolean prfIsGpsActive;
    private boolean remoteSaving;
    private boolean waitingForRiep;

    private CardAll cardAll;

    private DataLogDataProvider dataLogDataProvider;

    private Date dateLunch;

    private double curr_km_work;
    private double impoLunch;

    private GPSConnector gpsConnector;

    private Intent gpsIntentFrg;

    private Handler gpsHandler;
    private Handler localSaveHandler;
    private Handler remoteSaveHandler;
    private Handler readRiepHandler;
    private Handler videoHandler;

    private int curr_vmax_work;
    private int prfgpsInterval;
    private int prfRemoteSaveMillesc;
    private int LUNCH_REQ_CODE = 1;
    private LinearLayoutManager llm;

    private List<Integer> cards;

    private long curr_time_work;
    private long timeDelta;
    private long timeEnd;
    private long timeStart;

    private MyLocation myLocation;

    private NotificationManager mNotifyMgr;

    private RecyclerView rv;

    private RVAdapter adapter;

    private ServerSync serverSync;

    private SharedPreferences prefs;

    private SQLiteDatabase sqLiteDatabase;

    private static final int RESULT_SETTINGS = 1;
    private static final int TIMER_LOCAL_SAVE_INTERVAL = 60000;
    private static final int TIMER_VIDEO_INTERVAL = 1000;
    private static final int READ_RIEP_INTERVAL = 1000;
    private static final char READ_SENSOR_INT = 'X';
    private static final int REQUEST_ENABLE_BT = 2;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Richiesta chiusura
        Intent intent = getIntent();

        // pulsante per pranzo
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            // This code will start the new activity when the settings button is clicked on the bar at the top.
            Intent intent = new Intent(getApplicationContext(), LunchActivity.class);
            startActivityForResult(intent, LUNCH_REQ_CODE);
//            startActivity(intent);

            }
        });

        // menu preferenze
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // impostazioni iniziali
        if (isFirstTime) {
            // legge preferenze
            readPrefs();

            // impostazioni iniziali
            initialSettings();

            // pulizia da db dei records inviati a server
            clearSynchedRecs();

            // forza lettura riepilogo legge dati riepilogo
//            readRiep();
            setToReadRiep(true);

            // se gps attivo
            if (prfIsGpsActive) {
                startGPS();
            }

            isFirstTime = false;
        }

        ConstantUtil.setPowerStatus(this);
        ConstantUtil.setAppRunning(true);

        // timer aggiornamento video
        videoHandler = new Handler();
        videoHandler.removeCallbacks(videoTimer);
        videoHandler.postDelayed(videoTimer, TIMER_VIDEO_INTERVAL);

        if (prfIsGpsActive) {
            // timer aggiornamento gps
            gpsHandler = new Handler();
            gpsHandler.removeCallbacks(gpsTimer);
            gpsHandler.postDelayed(gpsTimer, prfgpsInterval);
            // timer aggiornamento salvataggio locale
            localSaveHandler = new Handler();
            localSaveHandler.removeCallbacks(localSaveTimer);
            localSaveHandler.postDelayed(localSaveTimer, TIMER_LOCAL_SAVE_INTERVAL);
            // timer aggiornamento salvataggio remoto
            remoteSaveHandler = new Handler();
            remoteSaveHandler.removeCallbacks(remoteSaveTimer);
            remoteSaveHandler.postDelayed(remoteSaveTimer, prfRemoteSaveMillesc);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //readRiep();
        setToReadRiep(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * attiva timer per leggere riepilgoo da server
     */
    private void readRiep() {

        mNotifyMgr = ConstantUtil.addNotification(getBaseContext() , mNotifyMgr , getSystemService(NOTIFICATION_SERVICE) , "SmiPaoCos" , R.drawable.ic_read_riep , "Reading Recap" , "Lettura riepilogo dal server" , ConstantUtil.ID_NOTIFY_READ_RIEP);

        // timer lettura riepilogo da server
        readRiepHandler = new Handler();
        readRiepHandler.removeCallbacks(readRiepTimer);
        readRiepHandler.postDelayed(readRiepTimer, READ_RIEP_INTERVAL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LUNCH_REQ_CODE && resultCode == RESULT_OK && data != null) {
            // local save
            dateLunch = (Date) data.getSerializableExtra("DateLunch");
            impoLunch = data.getDoubleExtra("ImpoLunch" , 0D);
            isSede = data.getBooleanExtra("IsSede", true);
            localSaveLunch();
            // remote save ?
            if (!prfIsGpsActive) {
                remoteSaveTimer.run();
            }
        }
    }

    private void clearSynchedRecs() {
        dataLogDataProvider.clearSynchedRecs(sqLiteDatabase, new Date());
    }

    private void startGPS() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            gpsIntentFrg = new Intent(this, GpsConnectorFrg.class);
        } else {
            gpsConnector = new GPSConnector(this ,  getApplicationContext());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!gpsIntentFrg.isLocationEnabled()) {
                showGPSSetting();
            } else {
                getApplicationContext().startForegroundService(gpsIntentFrg);
            }
        } else {
            if (!gpsConnector.isLocationEnabled()) {
                showGPSSetting();
            } else {
                gpsConnector.start();
            }
        }
    }

    private void showGPSSetting() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS non attivo");

        // Setting Dialog Message
        alertDialog
                .setMessage("GPS non abilitato. Apro il pannello impostazioni ?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Impostazioni",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Annulla",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("gpsActive", false);
                        editor.apply();
                        dialog.cancel();
                        prfIsGpsActive = false;
                        visualizzaDialog("GPS disabilitato", "Ricezione GPS disabilitata");
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    private void visualizzaDialog(String title, String body) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog
                .setMessage(body);
        // On pressing Settings button
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        // Showing Alert Message
        alertDialog.show();
    }

    private void readPrefs() {
        setDefaultPrefs();
        prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        prfIsGpsActive = prefs.getBoolean("gpsActive", false);
        prfgpsInterval = Integer.parseInt(prefs.getString("gpsInterval", "1000"));
        prfRemoteSaveMillesc = Integer.parseInt(prefs.getString("sync_frequency", "15")) * 60 * 1000;
    }

    /**
     * imposta eventuali prefs a null
     */
    private void setDefaultPrefs() {
        prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        // data_sync
        setDftPref("sync_frequency" , "15");
        // general
        setDftPref("serverIp" , "paocos.ddns.net");
        setDftPref("serverPort" , "8080");
        setDftPref("serverPath" , "SmiPaoCos/restservices/paocos");
        // gps
        setDftPref("gpsActive" , false);
        setDftPref("gpsPInc" , "10");
        setDftPref("gpsInterval" , "500");
        setDftPref("gpsPrecision" , "50");
        // lunch
        setDftPref("lunchQuota" , "12");
        // temp
        setDftPref("tempActive" , false);
        setDftPref("tempSoglia" , "10");
    }

    private void setDftPref(String key, int dft) {
        System.out.println("key:" + key);
        System.out.println("dft:" + dft);
        if (prefs.getInt(key, -1) < 0) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(key, dft);
            editor.apply();
        }
    }

    private void setDftPref(String key, String dft) {
        if (prefs.getString(key, null) == null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, dft);
            editor.apply();
        }
    }

    private void setDftPref(String key, boolean dft) {
        if (prefs.getBoolean(key, false) == false) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(key, dft);
            editor.apply();
        }
    }

    private void readFromDb() {
        DataLog dataLog = dataLogDataProvider.getByKey(sqLiteDatabase, new Date());
        if (dataLog != null) {
            dataLog.setGpsPoints(null); // non tengo conto dei punti gps già salvati in partenza app
            moveFromDbToVars(dataLog);
        }
    }

    private void vacuumDb() {
        dataLogDataProvider.vacuumDb(sqLiteDatabase);
    }

    private void moveFromDbToVars(DataLog dataLog) {
        curr_km_work = dataLog.getKm_works() + dataLog.getKm_pers();
        curr_time_work = dataLog.getTime_work() + dataLog.getTime_pers();
    }

    private void initialSettings() {

        dataLogDataProvider = new DataLogDataProvider(getApplicationContext());
        sqLiteDatabase = dataLogDataProvider.getWritableDatabase();

        // assegna parti
        AssignPart();

        // imposta valore iniziale
        setInitialValue();

        // controllo se network attivo
        checkNetworkstatus();

        // compatta il db
        vacuumDb();

        // leggo dati partenza da db
        readFromDb();

//        // tiene acceso lo schermo solo se gps attivo
//        if (prfIsGpsActive) {
//            View myView = findViewById(R.id.content_main);
//            myView.setKeepScreenOn(true);
//        }

    }

    private void checkNetworkstatus() {
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isNetworkConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void setInitialValue() {
        // imposta ordine schede
        cards = new ArrayList<>();
        if (prfIsGpsActive) {
            cards.add(ConstantUtil.CARD_GPS);
        }
        cards.add(ConstantUtil.CARD_PRANZO);
        cards.add(ConstantUtil.CARD_ORARI);
        cards.add(ConstantUtil.CARD_RIEP);

        cardAll = new CardAll();
        cardAll.setCardGps(new CardGps());
        cardAll.setCardTemp(new CardTemp());
        cardAll.setCardOrari(new CardOrari());
        cardAll.setCardPranzo(new CardPranzo());
        cardAll.setCardRiep(new CardRiep());

        adapter = new RVAdapter(cards);
        adapter.setCardAll(cardAll);
        rv.setAdapter(adapter);

        // se gps attivo imposto in registrazione
        if (prfIsGpsActive) {
            timeStart = SystemClock.uptimeMillis();
        }

        // imposto valori iniziali
        curr_km_work=0;
        curr_time_work = 0;
        curr_vmax_work=0;


    }

    private void AssignPart() {
        // recycler
        // poer oternere n puntatore alla recyvler view
        rv = (RecyclerView) findViewById(R.id.rv);
        // Se si è certi che le dimensioni del RecyclerView non cambieranno, è possibile aggiungere la seguente stringa per migliorare le prestazioni:
        rv.setHasFixedSize(true);
        llm = new LinearLayoutManager(this.getApplicationContext());
        rv.setLayoutManager(llm);
        // assegna parti
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            lastActions();
            super.onBackPressed();
        }
    }

    private void lastActions() {
        // rimette flag schermo a posto
        View myView = findViewById(R.id.content_main);
        myView.setKeepScreenOn(false);
        setClosePending(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivityForResult(i, RESULT_SETTINGS);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Runnable localSaveTimer;

    {
        localSaveTimer = new Runnable() {
            public void run() {

                try {

                    if (!gpsReading && !isRemoteSaving() && !isClosePending()) {

                        // verifico stato rete
                        checkNetworkstatus();

                        localSave();

                        if (prfIsGpsActive && !isClosePending()) {
                            localSaveHandler.postDelayed(localSaveTimer, TIMER_LOCAL_SAVE_INTERVAL);
                        }

                    } else {

                        if (prfIsGpsActive && !isClosePending()) {
                            localSaveHandler.postDelayed(localSaveTimer, 1000);
                        }

                    }

                    } catch(Exception e){
                        e.printStackTrace();
                    }
            }
        };
    }


    private Runnable remoteSaveTimer;

    {
        remoteSaveTimer = new Runnable() {
            public void run() {

                try {

                    // entro se attiva la rete, se non sono in lettura del gps e non sto salvando localmente
                    if (isNetworkConnected && !gpsReading && !isLocalSaving() && !isLocalSaving_lunch() && !isClosePending()) {

                        if (serverSync == null) {
                            serverSync = new ServerSync(sqLiteDatabase , dataLogDataProvider);
                            serverSync.setContext(getApplicationContext());
                            serverSync.setmNotifyMgr(mNotifyMgr);
                        } // (serverSync == null) {

                        // se ancora in esecuzione sta finendo la sincronizzazione
                        if (serverSync.isRunning()) {

                            remoteSaveHandler.postDelayed(remoteSaveTimer, 1100);
                        } else { // (serverSync.isRunning()) {

                            // se sono in remote saving, significa che ha finito, il giro precedente
                            if (isRemoteSaving()) {
                                // spengo il remote saving e reimposto il timer per il tempo std
                                //20180214 setRemoteSaving(false);
                                if (prfIsGpsActive && !isClosePending()) {
                                    //readRiepTimer.run();
                                    //readRiep();
                                    //20180214 setToReadRiep(true);
                                    remoteSaveHandler.postDelayed(remoteSaveTimer, TIMER_LOCAL_SAVE_INTERVAL);
                                }

                            } else { // (isRemoteSaving()) {

                                remoteSave();

                                if (prfIsGpsActive && !isClosePending()) {
                                    //readRiep();
                                    //20180214 setToReadRiep(true);
                                    remoteSaveHandler.postDelayed(remoteSaveTimer, prfRemoteSaveMillesc);
                                }
                            } // (isRemoteSaving()) {
                        } // (serverSync.isRunning()) {

                    } else { // (isNetworkConnected && !gpsReading && !isLocalSaving()) {

                        if (prfIsGpsActive && !isClosePending()) {
                            if (isNetworkConnected) {
                                remoteSaveHandler.postDelayed(remoteSaveTimer, 1100);
                            } else {
                                remoteSaveHandler.postDelayed(remoteSaveTimer, TIMER_LOCAL_SAVE_INTERVAL);
                            }
                        }
                    } // (isNetworkConnected && !gpsReading && !isLocalSaving()) {

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
    }

    private void remoteSave() {
        // altrimenti faccio il submit del salvataggio
        setRemoteSaving(true);

        mNotifyMgr = ConstantUtil.addNotification(getBaseContext() , mNotifyMgr , getSystemService(NOTIFICATION_SERVICE) , "SmiPaoCos" , R.drawable.ic_remote_save , "Remote Save" , "Sincronizzazione sul server" , ConstantUtil.ID_NOTIFY_REMOTE_SAVE);

        try {
            // solo se attiva connessione di rete
            if (isNetworkConnected) {
                if (serverSync == null) {
                    serverSync = new ServerSync(sqLiteDatabase, dataLogDataProvider);
                    serverSync.setContext(getApplicationContext());
                    serverSync.setmNotifyMgr(mNotifyMgr);
                }
                serverSync.setSharedPreferences(prefs);
                serverSync.syncData(ServerSync.DATALOGS);

            }
        } catch (Exception e) {}

//20180214         setRemoteSaving(false);

    }

    private void localSave() {

        setLocalSaving(true);

        mNotifyMgr = ConstantUtil.addNotification(getBaseContext() , mNotifyMgr , getSystemService(NOTIFICATION_SERVICE) , "SmiPaoCos" , R.drawable.ic_local_save , "Local Save" , "Salvataggio locale dei dati" , ConstantUtil.ID_NOTIFY_LOCAL_SAVE);

        ConstantUtil.removeNotification(mNotifyMgr , ConstantUtil.ID_NOTIFY_STATUS);
        mNotifyMgr = ConstantUtil.addNotification(getBaseContext(), mNotifyMgr, getSystemService(NOTIFICATION_SERVICE), "SmiPaoCos", R.drawable.ic_save_lunch, "Status", "Percorsi KM " + String.format("%.1f" , curr_km_work) , ConstantUtil.ID_NOTIFY_STATUS);

        DataLog dataLog = new DataLog();
        dataLog.setData(new Date());
        dataLog.setGps_sync(false);
        dataLog.setGps_sync_ts(new Timestamp(System.currentTimeMillis()));
        dataLog.setKm_works((double) curr_km_work);
        dataLog.setKm_pers(0D);
        dataLog.setTime_work(curr_time_work);
        dataLog.setTime_pers(0L);
//        dataLog.setGpsPoints(gpsPoints);
        dataLog.setGpsPoints(null);
        dataLogDataProvider.update(sqLiteDatabase, dataLog , DataLogDataProvider.SAVE_GPS);
//        gpsPoints = new ArrayList<>();

        ConstantUtil.removeNotification(mNotifyMgr , ConstantUtil.ID_NOTIFY_LOCAL_SAVE);

        setLocalSaving(false);

    }

    private void localSaveLunch() {

        setLocalSaving_lunch(true);

        mNotifyMgr = ConstantUtil.addNotification(getBaseContext() , mNotifyMgr , getSystemService(NOTIFICATION_SERVICE) , "SmiPaoCos" , R.drawable.ic_save_lunch , "Local Save" , "Salvataggio locale del pranzo" , ConstantUtil.ID_NOTIFY_LOCAL_SAVE_LUNCH);

        DataLog dataLog = new DataLog();
        dataLog.setData(dateLunch);
        dataLog.setLunch_impo(impoLunch);
        dataLog.setLunch_sede(isSede);
        dataLog.setLunch_sync_ts(new Timestamp(System.currentTimeMillis()));
        dataLog.setLunch_sync(false);
        dataLogDataProvider.update(sqLiteDatabase, dataLog , DataLogDataProvider.SAVE_LUNCH);

        ConstantUtil.removeNotification(mNotifyMgr , ConstantUtil.ID_NOTIFY_LOCAL_SAVE_LUNCH);

        setLocalSaving_lunch(false);

    }

    private Runnable readRiepTimer = new Runnable() {
        public void run() {
            if (isWaitingForRiep()) {
                // se server ancora in run reimmetto timer
                if (serverSync.isRunning()) {
                    readRiepHandler.postDelayed(readRiepTimer, READ_RIEP_INTERVAL);
                } else {
                    // altrimento leggo valori e mi fermo
                    if (serverSync.getCardAllSync() != null) {
                        if (cardAll == null) {
                            cardAll = new CardAll();
                            cardAll.setCardGps(new CardGps());
                            cardAll.setCardTemp(new CardTemp());
                        }
                        cardAll.setCardPranzo(serverSync.getCardAllSync().getCardPranzo());
                        cardAll.setCardOrari(serverSync.getCardAllSync().getCardOrari());
                        cardAll.setCardRiep(serverSync.getCardAllSync().getCardRiep());
                    }
                    setWaitingForRiep(false);
                }
            } else {
                checkNetworkstatus();
                if (isNetworkConnected) {
                    setWaitingForRiep(true);
                    // richiesta al server
                    if (serverSync == null) {
                        serverSync = new ServerSync(sqLiteDatabase , dataLogDataProvider);
                        serverSync.setContext(getApplicationContext());
                        serverSync.setmNotifyMgr(mNotifyMgr);
                    }
                    serverSync.setSharedPreferences(prefs);
                    serverSync.setContext(getApplicationContext());
                    serverSync.syncData(ServerSync.RIEP);
                    readRiepHandler.postDelayed(readRiepTimer, READ_RIEP_INTERVAL);
                }
            }
        }
    };


    private Runnable videoTimer = new Runnable() {
        public void run() {

            boolean riepReaded = false;

            // controllo se finito il remote saving
            if (isRemoteSaving() && !serverSync.isRunning()) {
                setRemoteSaving(false);
                setToReadRiep(true);
            }

            if (!isRemoteSaving() && isToReadRiep()) {
                readRiep();
                setToReadRiep(false);
                riepReaded = true;
            }

            calcFields();
            displayFields();
            checkClosePending();
            adapter.notifyDataSetChanged();

            if (isToRefreshVideo() || riepReaded) {
                videoHandler.postDelayed(videoTimer, TIMER_VIDEO_INTERVAL);
                riepReaded = false;
            } else {
                if (isClosePending()) {
                    closeActions();
                }
            }
        }
    };

    private boolean isToRefreshVideo() {
        boolean toReturn = false;
        if (prfIsGpsActive) toReturn = true;
        if (isWaitingForRiep()) toReturn = true;
        if (isClosePending()) toReturn = false;
        return toReturn;
    }

    private void closeActions() {
        if (gpsConnector != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stopService(gpsIntentFrg);
            } else {
                gpsConnector.stop();
            }
        }
        syncAll();
        ConstantUtil.setAppRunning(false);
        ConstantUtil.removeNotification(mNotifyMgr , ConstantUtil.ID_NOTIFY_STATUS);
        //super.onBackPressed();
        finish();
    }

    private void syncAll() {
        localSave();
        remoteSave();
    }

    private void checkClosePending() {
        /**
         * il processo si chiude alla disconnessione del bluetooth
         */
//        if (prfIsGpsActive && ConstantUtil.getPowerDisconnected()) {
//            if (prfIsGpsActive && ConstantUtil.getPowerConnected() && ConstantUtil.getPowerDisconnected()) {
 //           setClosePending(true);
   //     }
    }

    private void calcFields() {

        timeEnd = SystemClock.uptimeMillis();
        timeDelta = timeEnd - timeStart;
        timeStart = timeEnd;
        curr_time_work += timeDelta;

    }

    private void displayFields() {
        if (myLocation != null) {

            cardAll.getCardGps().setKm(curr_km_work);
            cardAll.getCardGps().setTime(curr_time_work);
            if (myLocation.getAddress() != null) {
                cardAll.getCardGps().setWhereIAm(
                        myLocation.getAddress().getAddressLine(0) != null ? myLocation.getAddress().getAddressLine(0) : "" +
                                myLocation.getAddress().getAddressLine(1) != null ? "-" + myLocation.getAddress().getAddressLine(1) : ""+
                                myLocation.getAddress().getAddressLine(2) != null ? "-" + myLocation.getAddress().getAddressLine(2) : "");
            }

        }

    }

    private Runnable gpsTimer = new Runnable() {
        public void run() {

            if (!isLocalSaving() && !isLocalSaving_lunch() && !isRemoteSaving()) {

                // tiene acceso lo schermo solo se gps attivo
                View myView = findViewById(R.id.content_main);
//                if (ConstantUtil.getPowerConnected() && !myView.getKeepScreenOn()) {
                if (!myView.getKeepScreenOn()) {
                    myView.setKeepScreenOn(true);
                }
                if (ConstantUtil.getPowerDisconnected() && myView.getKeepScreenOn()) {
                    myView.setKeepScreenOn(false);
                }

                if (ConstantUtil.getRequestClosing()) {
                    ConstantUtil.setRequestClosing(false);
                    setClosePending(true);
                }

                setGpsReading(true);

                myLocation = gpsConnector.getMyLocation(isNetworkConnected);

                if (myLocation != null) {

                    if (gpsConnector.getGpsPoint() != null) {
//                        gpsPoints.add(gpsConnector.getGpsPoint());
                        gpsConnector.setGpsPoint(null);
                    }

                    // sommo campi
                    curr_km_work += myLocation.getDistance();
                    if (myLocation.getMySpeed() > curr_vmax_work) {
                        curr_vmax_work = (int) myLocation.getMySpeed();
                    }

                }

                setGpsReading(false);

                if (prfIsGpsActive && !isClosePending()) {
                    gpsHandler.postDelayed(gpsTimer, prfgpsInterval);
                }

            } else {

                if (prfIsGpsActive && !isClosePending()) {
                    gpsHandler.postDelayed(gpsTimer, 1000);
                }

            }
        }
    };

    public Boolean isClosePending() {
        return closePending;
    }

    public void setClosePending(Boolean closePending) {
        this.closePending = closePending;
    }

    public boolean isGpsReading() {
        return gpsReading;
    }

    public void setGpsReading(boolean gpsReading) {
        this.gpsReading = gpsReading;
    }

    public boolean isLocalSaving() {
        return localSaving;
    }

    public void setLocalSaving(boolean localSaving) {
        this.localSaving = localSaving;
    }

    public boolean isRemoteSaving() {
        return remoteSaving;
    }

    public void setRemoteSaving(boolean remoteSaving) {
        this.remoteSaving = remoteSaving;
    }

    public boolean isWaitingForRiep() {
        return waitingForRiep;
    }

    public void setWaitingForRiep(boolean waitingForRiep) {
        this.waitingForRiep = waitingForRiep;
    }

    public boolean isToReadRiep() {
        return toReadRiep;
    }

    public void setToReadRiep(boolean toReadRiep) {
        this.toReadRiep = toReadRiep;
    }

    public boolean isLocalSaving_lunch() {
        return localSaving_lunch;
    }

    public void setLocalSaving_lunch(boolean localSaving_lunch) {
        this.localSaving_lunch = localSaving_lunch;
    }

}

