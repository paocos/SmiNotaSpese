package com.paocos.sminotaspese.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.paocos.sminotaspese.model.entities.DataLog;
import com.paocos.sminotaspese.model.entities.DataLog.DataLogEntry;
import com.paocos.sminotaspese.model.entities.GpsPoint;
import com.paocos.sminotaspese.shared.ConstantUtil;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by paocos on 10/03/17.
 */

public class DataLogDataProvider extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    //public static final int DATABASE_VERSION = 1; // iniziale
    public static final int DATABASE_VERSION = 1; // i punti gps diventano json
    public static final String DATABASE_NAME = "SmiNotaSpese.db";

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
                    DataLogEntry.COLUMN_NAME_KM_WORK + " INTEGER," +
                    DataLogEntry.COLUMN_NAME_KM_PERS + " INTEGER," +
                    DataLogEntry.COLUMN_NAME_TIME_WORK + " LONG," +
                    DataLogEntry.COLUMN_NAME_TIME_PERS + " LONG," +
                    DataLogEntry.COLUMN_NAME_GPS_SYNC_TS + " TIMESTAMP," +
                    DataLogEntry.COLUMN_NAME_GPS_SYNC + " BOOLEAN," +
                    DataLogEntry.COLUMN_NAME_LUNCH_IMPO + " LONG," +
                    DataLogEntry.COLUMN_NAME_LUNCH_SEDE + " BOOLEAN," +
                    DataLogEntry.COLUMN_NAME_LUNCH_SYNC_TS + " TIMESTAMP," +
                    DataLogEntry.COLUMN_NAME_LUNCH_SYNC + " BOOLEAN)";


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
        // versione 1 iniziale
        // versione 2 i punti gps diventano json
        switch (oldVersion) {
            case 1:
                String sql = "delete from DataLogGpsPoints";
                db.execSQL(sql);
        }

    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public long update(SQLiteDatabase db , DataLog dataLog) {
        // recordo giorno
        long recsUpdated = 0;
        // campi da aggiornare
        ContentValues cv = new ContentValues();
        cv.put(DataLogEntry.COLUMN_NAME_DATA , ConstantUtil.getSqlFormattedData(dataLog.getData()));
        cv.put(DataLogEntry.COLUMN_NAME_TIME_SYNC , dataLog.isTime_sync());
        cv.put(DataLogEntry.COLUMN_NAME_TIME_SYNC_TS , ConstantUtil.getSqlFormattedTimeStamp(dataLog.getTime_sync_ts()));
        cv.put(DataLogEntry.COLUMN_NAME_TIME_MORN_START , ConstantUtil.getSqlFormattedTimeStamp(dataLog.getTime_morn_start()));
        cv.put(DataLogEntry.COLUMN_NAME_TIME_MORN_END , ConstantUtil.getSqlFormattedTimeStamp(dataLog.getTime_morn_end()));
        cv.put(DataLogEntry.COLUMN_NAME_TIME_AFT_START , ConstantUtil.getSqlFormattedTimeStamp(dataLog.getTime_aft_start()));
        cv.put(DataLogEntry.COLUMN_NAME_TIME_AFT_END , ConstantUtil.getSqlFormattedTimeStamp(dataLog.getTime_aft_end()));
        cv.put(DataLogEntry.COLUMN_NAME_KM_WORK , dataLog.getKm_works());
        cv.put(DataLogEntry.COLUMN_NAME_KM_PERS , dataLog.getKm_pers());
        cv.put(DataLogEntry.COLUMN_NAME_TIME_WORK , dataLog.getTime_work());
        cv.put(DataLogEntry.COLUMN_NAME_TIME_PERS , dataLog.getTime_pers());
        cv.put(DataLogEntry.COLUMN_NAME_GPS_SYNC , dataLog.isGps_sync());
        cv.put(DataLogEntry.COLUMN_NAME_GPS_SYNC_TS , ConstantUtil.getSqlFormattedTimeStamp(dataLog.getGps_sync_ts()));
        cv.put(DataLogEntry.COLUMN_NAME_LUNCH_IMPO ,dataLog.getLunch_impo());
        cv.put(DataLogEntry.COLUMN_NAME_LUNCH_SEDE ,dataLog.isLunch_sede());
        cv.put(DataLogEntry.COLUMN_NAME_LUNCH_SYNC_TS, ConstantUtil.getSqlFormattedTimeStamp(dataLog.getLunch_sync_ts()));
        cv.put(DataLogEntry.COLUMN_NAME_LUNCH_SYNC, dataLog.isLunch_sync());

        // chiave
        String keyNames = DataLogEntry.COLUMN_NAME_DATA + "= ? ";
        String[] keyValues = new String[]{ConstantUtil.getSqlFormattedData(dataLog.getData())};
        recsUpdated = db.update(DataLogEntry.TABLE_NAME, cv, keyNames , keyValues);
        if (recsUpdated == 0) {
            recsUpdated = db.insert(DataLogEntry.TABLE_NAME , null , cv);
        }
        // record punti gps
        recsUpdated = 0;
        // campi da aggiornare
        cv = new ContentValues();
        cv.put(DataLog.DataLogEntryGpsPoints.COLUMN_NAME_DATA , ConstantUtil.getSqlFormattedData(dataLog.getData()));
        cv.put(DataLog.DataLogEntryGpsPoints.COLUMN_NAME_GPS_POINTS , ConstantUtil.inCode(new JGpsPoints(dataLog.getGpsPoints())));
        cv.put(DataLog.DataLogEntryGpsPoints.COLUMN_NAME_GPS_SYNC , dataLog.isGps_sync());
        cv.put(DataLog.DataLogEntryGpsPoints.COLUMN_NAME_GPS_SYNC_TS , ConstantUtil.getSqlFormattedTimeStamp(dataLog.getGps_sync_ts()));
        // chiave
        keyNames = DataLog.DataLogEntryGpsPoints.COLUMN_NAME_DATA + "= ? ";
        keyValues = new String[]{ConstantUtil.getSqlFormattedData(dataLog.getData())};
        recsUpdated = db.insert(DataLog.DataLogEntryGpsPoints.TABLE_NAME , null , cv);
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
        sql.append("dl.* , dlg.gps_points , dlg.gps_sync_ts , dlg.gps_synched ");
        sql.append("from ");
        sql.append("DataLog dl left join DataLogGpsPoints dlg on ");
        sql.append("dl.\"data\" = dlg.\"data\" ");
        sql.append("where ");
        sql.append("not dl.time_synched ");
        sql.append("or not dl.gps_synched ");
        sql.append("or not dlg.gps_synched ");
        sql.append("order by ");
        sql.append("dl.\"data\", ");
        sql.append("dlg.gps_sync_ts");
        return sql.toString();
    }

    public ArrayList<DataLog> readBySql(SQLiteDatabase db , String sql) {
        Cursor cursor = null;
        ArrayList<DataLog> dataLogs = new ArrayList<>();
        Date data_old = null;
        DataLog dataLog = new DataLog();
        cursor = db.rawQuery(sql, null);
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
                dataLog.setKm_works(cursor.getInt(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_KM_WORK)));
                dataLog.setKm_pers(cursor.getInt(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_KM_PERS)));
                dataLog.setTime_work(cursor.getLong(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_TIME_WORK)));
                dataLog.setTime_pers(cursor.getLong(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_TIME_PERS)));
                dataLog.setGps_sync_ts(ConstantUtil.getTimeStampFromString(cursor.getString(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_GPS_SYNC_TS))));
                dataLog.setGps_sync(cursor.getInt(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_GPS_SYNC)) == 0 ? false : true);
                dataLog.setLunch_impo(cursor.getInt(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_LUNCH_IMPO)));
                dataLog.setLunch_sede(cursor.getInt(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_LUNCH_SEDE)) == 0 ? false : true);
                dataLog.setLunch_sync_ts(ConstantUtil.getTimeStampFromString(cursor.getString(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_LUNCH_SYNC_TS))));
                dataLog.setLunch_sync(cursor.getInt(cursor.getColumnIndexOrThrow(DataLogEntry.COLUMN_NAME_LUNCH_SYNC)) == 0 ? false : true);
                data_old = data;
            }
            JGpsPoints oGpsPoints = ConstantUtil.deCode(cursor.getString(cursor.getColumnIndexOrThrow(DataLog.DataLogEntryGpsPoints.COLUMN_NAME_GPS_POINTS)));
            ArrayList<GpsPoint> gpsPoints = null;
            if (oGpsPoints != null) {
                gpsPoints = oGpsPoints.getGpsPoints();
            }
            if (gpsPoints != null) {
                if (dataLog.getGpsPoints() == null) {
                    dataLog.setGpsPoints(new ArrayList<GpsPoint>());
                }
                dataLog.getGpsPoints().addAll(gpsPoints);
            }
            cursor.moveToNext();
        }
        return dataLogs;
    }

    @NonNull
    private String getSqlByKey(Date data) {
        StringBuilder sql = new StringBuilder();
        sql.append("select");
        sql.append(" * ");
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
            setSynched(dataLog);
            update(db, dataLog);
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
        dataLog.setLunch_sync(true);
        dataLog.setLunch_sync(true);
    }

}
