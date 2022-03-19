package com.paocos.sminotaspese.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.paocos.sminotaspese.model.entities.DataLog;
import com.paocos.sminotaspese.model.entities.DataLog.DataLogEntry;
import com.paocos.sminotaspese.model.entities.GpsPoint;
import com.paocos.sminotaspese.shared.ConstantUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by paocos on 10/03/17.
 */

public class DataLogDataProvider extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    //public static final int DATABASE_VERSION = 1; // iniziale
    //public static final int DATABASE_VERSION = 1; // i punti gps diventano json
    //public static final int DATABASE_VERSION = 2; // campi km_word , pers e lunch_impo diventano double
    //public static final int DATABASE_VERSION = 3; // db non modificato, serve solo per attivare la cancellazione del log
    public static final int DATABASE_VERSION = 4; // modifica datagpspoints per aggiungere riga
    public static final String DATABASE_NAME = "SmiNotaSpese.db";

    public static final int SAVE_ALL = 111;
    public static final int SAVE_TIMES = 1;
    public static final int SAVE_GPS = 10;
    public static final int SAVE_LUNCH = 100;

    private static final String TAG = "SmiNotaSpese";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DataLogEntry.TABLE_NAME + " (" +
                    DataLogEntry.COLUMN_NAME_DATA + " DATE PRIMARY KEY," +
                    DataLogEntry.COLUMN_NAME_TIME_MORN_START + " TIMESTAMP," +
                    DataLogEntry.COLUMN_NAME_TIME_MORN_END + " TIMESTAMP," +
                    DataLogEntry.COLUMN_NAME_TIME_AFT_START + " TIMESTAMP," +
                    DataLogEntry.COLUMN_NAME_TIME_AFT_END + " TIMESTAMP," +
                    DataLogEntry.COLUMN_NAME_TIME_SYNC_TS + " TIMESTAMP," +
                    DataLogEntry.COLUMN_NAME_TIME_SYNC + " BOOLEAN," +
                    DataLogEntry.COLUMN_NAME_TIME_SYNC_IP + " STRING," +
                    DataLogEntry.COLUMN_NAME_KM_WORK + " DOUBLE," +
                    DataLogEntry.COLUMN_NAME_KM_PERS + " DOUBLE," +
                    DataLogEntry.COLUMN_NAME_TIME_WORK + " LONG," +
                    DataLogEntry.COLUMN_NAME_TIME_PERS + " LONG," +
                    DataLogEntry.COLUMN_NAME_GPS_SYNC_TS + " TIMESTAMP," +
                    DataLogEntry.COLUMN_NAME_GPS_SYNC + " BOOLEAN," +
                    DataLogEntry.COLUMN_NAME_SPOST_SYNC_IP + " STRING," +
                    DataLogEntry.COLUMN_NAME_LUNCH_IMPO + " DOUBLE," +
                    DataLogEntry.COLUMN_NAME_LUNCH_SEDE + " BOOLEAN," +
                    DataLogEntry.COLUMN_NAME_LUNCH_SYNC_TS + " TIMESTAMP," +
                    DataLogEntry.COLUMN_NAME_LUNCH_SYNC + " BOOLEAN," +
                    DataLogEntry.COLUMN_NAME_LUNCH_SYNC_IP + " STRING)";

    private static final String SQL_CREATE_ENTRIES_TMP =
            "CREATE TABLE " + DataLogEntry.TABLE_NAME + "New (" +
                    DataLogEntry.COLUMN_NAME_DATA + " DATE PRIMARY KEY," +
                    DataLogEntry.COLUMN_NAME_TIME_MORN_START + " TIMESTAMP," +
                    DataLogEntry.COLUMN_NAME_TIME_MORN_END + " TIMESTAMP," +
                    DataLogEntry.COLUMN_NAME_TIME_AFT_START + " TIMESTAMP," +
                    DataLogEntry.COLUMN_NAME_TIME_AFT_END + " TIMESTAMP," +
                    DataLogEntry.COLUMN_NAME_TIME_SYNC_TS + " TIMESTAMP," +
                    DataLogEntry.COLUMN_NAME_TIME_SYNC + " BOOLEAN," +
                    DataLogEntry.COLUMN_NAME_TIME_SYNC_IP + " STRING," +
                    DataLogEntry.COLUMN_NAME_KM_WORK + " DOUBLE," +
                    DataLogEntry.COLUMN_NAME_KM_PERS + " DOUBLE," +
                    DataLogEntry.COLUMN_NAME_TIME_WORK + " LONG," +
                    DataLogEntry.COLUMN_NAME_TIME_PERS + " LONG," +
                    DataLogEntry.COLUMN_NAME_GPS_SYNC_TS + " TIMESTAMP," +
                    DataLogEntry.COLUMN_NAME_GPS_SYNC + " BOOLEAN," +
                    DataLogEntry.COLUMN_NAME_SPOST_SYNC_IP + " STRING," +
                    DataLogEntry.COLUMN_NAME_LUNCH_IMPO + " DOUBLE," +
                    DataLogEntry.COLUMN_NAME_LUNCH_SEDE + " BOOLEAN," +
                    DataLogEntry.COLUMN_NAME_LUNCH_SYNC_TS + " TIMESTAMP," +
                    DataLogEntry.COLUMN_NAME_LUNCH_SYNC + " BOOLEAN," +
                    DataLogEntry.COLUMN_NAME_LUNCH_SYNC_IP + " STRING)";

    private static final String SQL_CREATE_ENTRIES_GPS_POINTS =
            "CREATE TABLE " + DataLog.DataLogEntryGpsPoints.TABLE_NAME + " (" +
                    DataLog.DataLogEntryGpsPoints.COLUMN_NAME_DATA + " DATE ," +
                    DataLog.DataLogEntryGpsPoints.COLUMN_NAME_GPS_POINTS + " BLOB," +
                    DataLog.DataLogEntryGpsPoints.COLUMN_NAME_GPS_SYNC_TS + " TIMESTAMP," +
                    DataLog.DataLogEntryGpsPoints.COLUMN_NAME_GPS_SYNC + " BOOLEAN)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DataLogEntry.TABLE_NAME;

    public DataLogDataProvider(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.d(TAG , "Creato db con sql : " +  SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES_GPS_POINTS);
        Log.d(TAG , "Creato db con sql : " +  SQL_CREATE_ENTRIES_GPS_POINTS);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        File logfile;

        // versione 1 iniziale
        // versione 1 i punti gps diventano json
        // versione 1 campi km_word , pers e lunch_impo diventano double
        // versione 3 db non modificato, serve solo per attivare la cancellazione del log
        switch (oldVersion) {
            case 1:
                // creo nuova tabella temporaneA
                String sql = SQL_CREATE_ENTRIES_TMP;
                db.execSQL(sql);
                Log.d(TAG , "Creato db con sql : " +  sql);

                // copia dati da vecchia tabella
                sql = "insert into DataLogNew select * from DataLog";
                db.execSQL(sql);
                Log.d(TAG , "Copiati dati con sql : " +  sql);

                // cancello vecchio tabella
                sql = "drop table datalog";
                db.execSQL(sql);
                Log.d(TAG , "Cancellata tabella con sql : " +  sql);

                // creo nuova tabella
                sql = SQL_CREATE_ENTRIES;
                db.execSQL(sql);
                Log.d(TAG , "Creato db con sql : " +  sql);

                // copia dati da tabella temporanea
                sql = "insert into DataLog select * from DataLogNew";
                db.execSQL(sql);
                Log.d(TAG , "Copiati dati con sql : " +  sql);

                // cancello tabella temporanea
                sql = "drop table datalognew";
                db.execSQL(sql);
                Log.d(TAG , "Cancellata tabella con sql : " +  sql);

                // cancello log
                logfile = new File("automonitor.csv");
                logfile.delete();
                Log.d(TAG , "Log cancellato");

            case 2:

                // cancello log
                logfile = new File("automonitor.csv");
                logfile.delete();
                Log.d(TAG , "Log cancellato");

            case 3:

                // cancello log
                logfile = new File("automonitor.csv");
                logfile.delete();
                Log.d(TAG , "Log cancellato");

                // creo nuova tabella con campo aggiuntivo
                sql = "CREATE TABLE DataLogGpsPointsTmp (data DATE , row INTEGER PRIMARY KEY ASC , gps_points BLOB,gps_sync_ts TIMESTAMP,gps_synched BOOLEAN)";
                db.execSQL(sql);
                Log.d(TAG , "Creato nuova tabella temmporanea con sql : " +  sql);

                // copio i dati da tabella originale
                sql = "insert into DataLogGpsPointsTmp (data , gps_points , gps_sync_ts , gps_synched) select data , gps_points , gps_sync_ts , gps_synched from DataLogGpsPoints";
                db.execSQL(sql);
                Log.d(TAG , "Creato nuova tabella temmporanea con sql : " +  sql);

                // cancello tabella originale
                sql = "drop table DataLogGpsPoints";
                db.execSQL(sql);
                Log.d(TAG , "Cancello tabella originale con sql : " +  sql);

                // rinomino tabella con nome originale
                sql = "alter TABLE DataLogGpsPointsTmp RENAME TO DataLogGpsPoints";
                db.execSQL(sql);
                Log.d(TAG , "rinomino tabella con nome originale con sql : " +  sql);

        }

    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public long update(SQLiteDatabase db , DataLog dataLog , int saveWhat) {
        // calcolo dati da salvare
        int temp = saveWhat + 1000;
        String tempA = "" + temp;

        // recordo giorno
        long recsUpdated = 0;
        // campi da aggiornare
        ContentValues cv = new ContentValues();
        cv.put(DataLogEntry.COLUMN_NAME_DATA , ConstantUtil.getSqlFormattedData(dataLog.getData()));
        if (tempA.substring(3 , 4).equals("1")) {
            cv.put(DataLogEntry.COLUMN_NAME_TIME_MORN_START, ConstantUtil.getSqlFormattedTimeStamp(dataLog.getTime_morn_start()));
            cv.put(DataLogEntry.COLUMN_NAME_TIME_MORN_END, ConstantUtil.getSqlFormattedTimeStamp(dataLog.getTime_morn_end()));
            cv.put(DataLogEntry.COLUMN_NAME_TIME_AFT_START, ConstantUtil.getSqlFormattedTimeStamp(dataLog.getTime_aft_start()));
            cv.put(DataLogEntry.COLUMN_NAME_TIME_AFT_END, ConstantUtil.getSqlFormattedTimeStamp(dataLog.getTime_aft_end()));
            cv.put(DataLogEntry.COLUMN_NAME_TIME_SYNC, dataLog.isTime_sync());
            cv.put(DataLogEntry.COLUMN_NAME_TIME_SYNC_TS, ConstantUtil.getSqlFormattedTimeStamp(dataLog.getTime_sync_ts()));
            cv.put(DataLogEntry.COLUMN_NAME_TIME_SYNC_IP, dataLog.getTime_sync_ip());
        }
        if (tempA.substring(2 , 3).equals("1")) {
            cv.put(DataLogEntry.COLUMN_NAME_KM_WORK, dataLog.getKm_works());
            cv.put(DataLogEntry.COLUMN_NAME_KM_PERS, dataLog.getKm_pers());
            cv.put(DataLogEntry.COLUMN_NAME_TIME_WORK, dataLog.getTime_work());
            cv.put(DataLogEntry.COLUMN_NAME_TIME_PERS, dataLog.getTime_pers());
            cv.put(DataLogEntry.COLUMN_NAME_GPS_SYNC, dataLog.isGps_sync());
            cv.put(DataLogEntry.COLUMN_NAME_GPS_SYNC_TS, ConstantUtil.getSqlFormattedTimeStamp(dataLog.getGps_sync_ts()));
            cv.put(DataLogEntry.COLUMN_NAME_SPOST_SYNC_IP, dataLog.getSpost_sync_ip());
        }
        if (tempA.substring(1 , 2).equals("1")) {
            cv.put(DataLogEntry.COLUMN_NAME_LUNCH_IMPO, dataLog.getLunch_impo());
            cv.put(DataLogEntry.COLUMN_NAME_LUNCH_SEDE, dataLog.isLunch_sede());
            cv.put(DataLogEntry.COLUMN_NAME_LUNCH_SYNC_TS, ConstantUtil.getSqlFormattedTimeStamp(dataLog.getLunch_sync_ts()));
            cv.put(DataLogEntry.COLUMN_NAME_LUNCH_SYNC, dataLog.isLunch_sync());
            cv.put(DataLogEntry.COLUMN_NAME_LUNCH_SYNC_IP, dataLog.getLunch_sync_ip());
        }

        // chiave
        String keyNames = DataLogEntry.COLUMN_NAME_DATA + "= ? ";
        String[] keyValues = new String[]{ConstantUtil.getSqlFormattedData(dataLog.getData())};
        recsUpdated = db.update(DataLogEntry.TABLE_NAME, cv, keyNames , keyValues);
        if (recsUpdated == 0) {
            recsUpdated = db.insert(DataLogEntry.TABLE_NAME , null , cv);
        }

        if (tempA.substring(2 , 3).equals("1")) {
            ArrayList<GpsPoint> gpsPoints = dataLog.getGpsPoints();
            ArrayList<GpsPoint> gpsPointsToUpdate = new ArrayList<GpsPoint>();
            int i = -1;
            if (gpsPoints != null) {
                for (GpsPoint gpsPoint : gpsPoints) {
                    if (i>=4000) {
                        recsUpdated = okSaveGpsPoints(db , dataLog , gpsPointsToUpdate);
                        gpsPointsToUpdate = new ArrayList<GpsPoint>();
                        i = -1;
                    }
                    i++;
                    gpsPointsToUpdate.add(gpsPoint);
                }
                if (i > -1) {
                    recsUpdated = okSaveGpsPoints(db , dataLog , gpsPointsToUpdate);
                }
            }

        }

        return recsUpdated;
    }

    private long okSaveGpsPoints(SQLiteDatabase db , DataLog dataLog , ArrayList<GpsPoint> gpsPoints) {
        // record punti gps
        long recsUpdated = 0;
        ContentValues cv = new ContentValues();
        // campi da aggiornare
        cv = new ContentValues();
        cv.put(DataLog.DataLogEntryGpsPoints.COLUMN_NAME_DATA, ConstantUtil.getSqlFormattedData(dataLog.getData()));
        cv.put(DataLog.DataLogEntryGpsPoints.COLUMN_NAME_GPS_POINTS, ConstantUtil.inCode(gpsPoints));
        cv.put(DataLog.DataLogEntryGpsPoints.COLUMN_NAME_GPS_SYNC, dataLog.isGps_sync());
        cv.put(DataLog.DataLogEntryGpsPoints.COLUMN_NAME_GPS_SYNC_TS, ConstantUtil.getSqlFormattedTimeStamp(dataLog.getGps_sync_ts()));
        // chiave
        recsUpdated = db.insert(DataLog.DataLogEntryGpsPoints.TABLE_NAME, null, cv);
        return recsUpdated;
    }

    public DataLog getByKey(SQLiteDatabase db, Date data) {

        ArrayList<DataLog> dataLogs = readBySql(db, getSqlByKey(data));
        if (dataLogs == null || dataLogs.size() == 0) {
            return null;
        } else {
            return dataLogs.get(0);
        }
    }

    public ArrayList<DataLog> getUnSynched(SQLiteDatabase db) {
        ArrayList<DataLog> dataLogs = readBySql(db, getSqlUnSynched());
        return dataLogs;
    }

    public String getSqlUnSynched() {
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append("dl.* , dlg.gps_points , dlg.gps_sync_ts as dlg_gps_sync_ts , dlg.gps_synched as dlg_gps_synched ");
        sql.append("from ");
        sql.append("DataLog dl left join DataLogGpsPoints dlg on ");
        sql.append("dl.\"data\" = dlg.\"data\" ");
        sql.append("where ");
        sql.append("not dl.time_synched ");
        sql.append("or not dl.gps_synched ");
        sql.append("or not dl.lunch_synched ");
        sql.append("or not dlg_gps_synched ");
        sql.append("order by ");
        sql.append("dl.\"data\", ");
        sql.append("dlg_gps_sync_ts");
        return sql.toString();
    }

    public ArrayList<DataLog> readBySql(SQLiteDatabase db , String sql) {
        Cursor cursor = null;
        ArrayList<DataLog> dataLogs = new ArrayList<>();
        Date data_old = null;
        DataLog dataLog = new DataLog();
        cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Date data = ConstantUtil.getDateFromString(cursor.getString(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_DATA)));
                if (data_old == null || !data.equals(data_old)) {
                    dataLog = new DataLog();
                    dataLogs.add(dataLog);
                    dataLog.setData(data);
                    dataLog.setTime_morn_start(ConstantUtil.getTimeStampFromString(cursor.getString(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_TIME_MORN_START))));
                    dataLog.setTime_morn_end(ConstantUtil.getTimeStampFromString(cursor.getString(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_TIME_MORN_END))));
                    dataLog.setTime_aft_start(ConstantUtil.getTimeStampFromString(cursor.getString(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_TIME_AFT_START))));
                    dataLog.setTime_aft_end(ConstantUtil.getTimeStampFromString(cursor.getString(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_TIME_AFT_END))));
                    dataLog.setTime_sync_ts(ConstantUtil.getTimeStampFromString(cursor.getString(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_TIME_SYNC_TS))));
                    dataLog.setTime_sync(cursor.getInt(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_TIME_SYNC)) == 0 ? false : true);
                    dataLog.setTime_sync_ip(cursor.getString(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_TIME_SYNC_IP)));
                    dataLog.setKm_works(cursor.getDouble(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_KM_WORK)));
                    dataLog.setKm_pers(cursor.getDouble(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_KM_PERS)));
                    dataLog.setTime_work(cursor.getLong(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_TIME_WORK)));
                    dataLog.setTime_pers(cursor.getLong(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_TIME_PERS)));
                    dataLog.setGps_sync_ts(ConstantUtil.getTimeStampFromString(cursor.getString(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_GPS_SYNC_TS))));
                    dataLog.setGps_sync(cursor.getInt(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_GPS_SYNC)) == 0 ? false : true);
                    dataLog.setSpost_sync_ip(cursor.getString(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_SPOST_SYNC_IP)));
                    dataLog.setLunch_impo(cursor.getDouble(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_LUNCH_IMPO)));
                    dataLog.setLunch_sede(cursor.getInt(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_LUNCH_SEDE)) == 0 ? false : true);
                    dataLog.setLunch_sync_ts(ConstantUtil.getTimeStampFromString(cursor.getString(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_LUNCH_SYNC_TS))));
                    dataLog.setLunch_sync(cursor.getInt(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_LUNCH_SYNC)) == 0 ? false : true);
                    dataLog.setLunch_sync_ip(cursor.getString(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_LUNCH_SYNC_IP)));
                    data_old = data;
                }
                ArrayList<GpsPoint> gpsPoints = ConstantUtil.deCode(cursor.getString(cursor.getColumnIndexOrThrow(DataLog.DataLogEntryGpsPoints.COLUMN_NAME_GPS_POINTS)));
                if (gpsPoints != null) {
                    if (dataLog.getGpsPoints() == null) {
                        dataLog.setGpsPoints(new ArrayList<GpsPoint>());
                    }
                    dataLog.getGpsPoints().addAll(gpsPoints);
                }
                cursor.moveToNext();
            }
        }
        return dataLogs;
    }

    @NonNull
    private String getSqlByKey(Date data) {
        StringBuilder sql = new StringBuilder();
        sql.append("select");
        sql.append(" dl.* , dlg.gps_points , dlg.gps_sync_ts , dlg.gps_synched ");
        sql.append("from ");
        sql.append("DataLog dl left join DataLogGpsPoints dlg on ");
        sql.append("dl.\"data\" = dlg.\"data\" ");
        sql.append("where ");
        sql.append("dl.\"data\" = '");
        sql.append(ConstantUtil.getSqlFormattedData(data));
        sql.append("' ");
        sql.append("order by ");
        sql.append("dl.\"data\", ");
        sql.append("dlg.gps_sync_ts ");
        return sql.toString();
    }

    public void updateFromServer(SQLiteDatabase db , ArrayList<DataLog> dataLogs) {
        for (DataLog dataLog:dataLogs) {
            deleteKey(db , dataLog);
            dataLog.setGpsPoints(null); // una volta inviati al server i punti gps non servono piu
            setSynched(dataLog);
            update(db, dataLog , SAVE_ALL);
        }
    }

    private void deleteKey(SQLiteDatabase db, DataLog dataLog) {

        int recDeleted = 0;
        String keyNames = null;
        String[] keyValues = null;

        keyNames = DataLogEntry.COLUMN_NAME_DATA + "= ? ";
        keyValues = new String[]{ConstantUtil.getSqlFormattedData(dataLog.getData())};
        recDeleted = db.delete(DataLogEntry.TABLE_NAME, keyNames, keyValues);

        keyNames = DataLog.DataLogEntryGpsPoints.COLUMN_NAME_DATA + "= ? ";
        keyValues = new String[]{ConstantUtil.getSqlFormattedData(dataLog.getData())};
        recDeleted = db.delete(DataLog.DataLogEntryGpsPoints.TABLE_NAME, keyNames, keyValues);

    }

    private void setSynched(DataLog dataLog) {
        dataLog.setTime_sync(true);
        dataLog.setGps_sync(true);
        dataLog.setLunch_sync(true);
    }

    public void clearSynchedRecs(SQLiteDatabase db, Date data) {

        StringBuilder sql = new StringBuilder();
        sql.append("delete ");
        sql.append("from ");
        sql.append("DataLogGpsPoints ");
        sql.append("where ");
        sql.append("gps_synched and ");
        sql.append("\"data\" < '");
        sql.append(ConstantUtil.getSqlFormattedData(data));
        sql.append("' ");

//        Cursor cursor = null;
//        cursor = db.rawQuery(sql.toString(), null);
        db.execSQL(sql.toString());

        sql = new StringBuilder();
        sql.append("delete ");
        sql.append("from ");
        sql.append("DataLog ");
        sql.append("where ");
        sql.append("time_synched and ");
        sql.append("gps_synched and ");
        sql.append("lunch_synched and ");
        sql.append("\"data\" < '");
        sql.append(ConstantUtil.getSqlFormattedData(data));
        sql.append("' ");

//        cursor = null;
//        cursor = db.rawQuery(sql.toString(), null);
        db.execSQL(sql.toString());

    }

    public void vacuumDb(SQLiteDatabase db) {
        try {
            db.execSQL("VACUUM");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
