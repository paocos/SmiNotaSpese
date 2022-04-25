package com.paocos.sminotaspese;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.paocos.sminotaspese.data.DataLogDataProvider;
import com.paocos.sminotaspese.manager.ServerSync;
import com.paocos.sminotaspese.model.entities.DataLog;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class LunchActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private boolean isFirstTime = true;
    private Boolean isNetworkConnected;
    private boolean isSede;
    private boolean isSyncing;

    private Button psbOk;

    private Calendar myCalendar;

    private DataLogDataProvider dataLogDataProvider;

    private Date dateLunch;

    private double impoLunch;

    private EditText txtDateLunch;
    private EditText txtImpoLunch;

    private Handler lunchHandler;

    private ProgressBar progressBar_cyclic;

    private ServerSync serverSync;

    private SharedPreferences prefs;

    private SQLiteDatabase sqLiteDatabase;

    private static final int TIMER_LUNCH_INTERVAL = 1000;

    private ToggleButton psbIsSede;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // impostazioni iniziali
        if (isFirstTime) {

            readPrefs();

            initialSettings();

            isFirstTime = false;
        }

        txtDateLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myCalendar == null) {
                    myCalendar = Calendar.getInstance();
                }
                myCalendar.setTime(dateLunch);
                new DatePickerDialog(LunchActivity.this, LunchActivity.this, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        psbOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    moveFromVideoToVars();
                    Intent output = new Intent();
                    output.putExtra("DateLunch", dateLunch);
                    output.putExtra("ImpoLunch", impoLunch);
                    output.putExtra("IsSede", isSede);
                    setResult(Activity.RESULT_OK , output);
                    finish();
                } catch (ParseException e) {
                    Snackbar.make(v, "Inserita data o importo errato", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });


    }
    private void readPrefs() {
        setDefaultPrefs();
        prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
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

    private void initialSettings() {

        dataLogDataProvider = new DataLogDataProvider(getApplicationContext());
        sqLiteDatabase = dataLogDataProvider.getWritableDatabase();

        // assegna parti
        AssignPart();

        // imposta valore iniziale
        setInitialValue();

        // controllo se network attivo
        checkNetworkstatus();

        // leggo dati partenza da db
        readFromDb();

        // visualizzo
        moveFromVarsToVideo();

        progressBar_cyclic.setVisibility(INVISIBLE);
    }

    private void moveFromVarsToVideo() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txtDateLunch.setText(sdf.format(dateLunch));
        DecimalFormat myFormatter = new DecimalFormat("##.##");
        txtImpoLunch.setText(myFormatter.format(impoLunch));
        psbIsSede.setChecked(isSede);
    }

    private void moveFromVideoToVars() throws ParseException {
        try {
            DateFormat format = new SimpleDateFormat("dd/MM/yy");
            dateLunch = format.parse(txtDateLunch.getText().toString());
            impoLunch = Double.parseDouble(txtImpoLunch.getText().toString().replace(',','.'));
            isSede = psbIsSede.isChecked();
        } catch (ParseException e) {
            throw e;
        }
    }

    private void AssignPart() {
        psbOk = (Button) findViewById(R.id.psbOk);
        txtDateLunch = (EditText) findViewById(R.id.txtDateLunch);
        txtImpoLunch = (EditText) findViewById(R.id.txtImpoLunch);
        psbIsSede = (ToggleButton) findViewById(R.id.psbIsSede);
        progressBar_cyclic = (ProgressBar) findViewById(R.id.progressBar_cyclic);
    }

    private void readFromDb() {
        DataLog dataLog = dataLogDataProvider.getByKey(sqLiteDatabase, dateLunch);
        if (dataLog != null) {
            moveFromDbToVars(dataLog);
        }
    }

    private void moveFromDbToVars(DataLog dataLog) {
        isSede = dataLog.isLunch_sede();
        impoLunch = dataLog.getLunch_impo();
    }

    private void checkNetworkstatus() {
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isNetworkConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        txtDateLunch.setText(sdf.format(myCalendar.getTime()));
    }

    private void setInitialValue() {
        dateLunch = new Date();
        isSyncing = false;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, month);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateLunch = myCalendar.getTime();
        if (!dateLunch.equals(new Date()))  {
            // timer aggiornamento video
            lunchHandler = new Handler();
            lunchHandler.removeCallbacks(lunchTimer);
            lunchHandler.postDelayed(lunchTimer, TIMER_LUNCH_INTERVAL);
        }
//        updateLabel();
    }

    private Runnable lunchTimer = new Runnable() {
        public void run() {
            if (!isSyncing) {
                checkNetworkstatus();
                if (isNetworkConnected) {
                    isSyncing = true;
                    psbOk.setEnabled(false);
                    progressBar_cyclic.setVisibility(VISIBLE);
                    // richiesta al server
                    if (serverSync == null) {
                        serverSync = new ServerSync(sqLiteDatabase , dataLogDataProvider);
                    }
                    serverSync.setSharedPreferences(prefs);
                    serverSync.setContext(getApplicationContext());
                    DataLog dataLog = new DataLog();
                    dataLog.setData(dateLunch);
                    serverSync.setDataLog(dataLog);
                    serverSync.syncData(ServerSync.LUNCH);
                    lunchHandler.postDelayed(lunchTimer, TIMER_LUNCH_INTERVAL);
                }
            } else {
                // se server ancora in run reimmetto timer
                if (serverSync.isRunning()) {
                    lunchHandler.postDelayed(lunchTimer, TIMER_LUNCH_INTERVAL);
                } else {
                    // altrimento leggo valori e mi fermo
                    if (serverSync.getDataLog() == null) {
                        impoLunch=0D;
                        isSede = false;
                    } else {
                        impoLunch = serverSync.getDataLog().getLunch_impo();
                        isSede = serverSync.getDataLog().isLunch_sede();
                    }
                    moveFromVarsToVideo();
                    progressBar_cyclic.setVisibility(INVISIBLE);
                    psbOk.setEnabled(true);
                    isSyncing = false;
                }
            }
        }
    };


}
