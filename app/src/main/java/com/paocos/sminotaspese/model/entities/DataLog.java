package com.paocos.sminotaspese.model.entities;

import android.provider.BaseColumns;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by paocos on 10/03/17.
 */

public final class DataLog {

    private Date data;
    private Timestamp time_morn_start;
    private Timestamp time_morn_end;
    private Timestamp time_aft_start;
    private Timestamp time_aft_end;
    private Timestamp time_sync_ts;
    private boolean time_sync;
    private String time_sync_ip;
    private double km_works;
    private double km_pers;
    private long time_work;
    private long time_pers;
    private ArrayList<GpsPoint> gpsPoints;
    private Timestamp gps_sync_ts;
    private boolean gps_sync;
    private String spost_sync_ip;
    private double lunch_impo;
    private boolean lunch_sede;
    private Timestamp lunch_sync_ts;
    private boolean lunch_sync;
    private String lunch_sync_ip;

    public DataLog() {}

    /* Inner class that defines the table contents */
    public static class DataLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "DataLog";
        public static final String COLUMN_NAME_DATA = "data";
        public static final String COLUMN_NAME_TIME_MORN_START = "time_morn_start";
        public static final String COLUMN_NAME_TIME_MORN_END = "time_morn_end";
        public static final String COLUMN_NAME_TIME_AFT_START = "time_aft_start";
        public static final String COLUMN_NAME_TIME_AFT_END = "time_aft_end";
        public static final String COLUMN_NAME_TIME_SYNC_TS = "time_sync_ts";
        public static final String COLUMN_NAME_TIME_SYNC = "time_synched";
        public static final String COLUMN_NAME_TIME_SYNC_IP = "time_synched_ip";
        public static final String COLUMN_NAME_KM_WORK = "km_work";
        public static final String COLUMN_NAME_KM_PERS = "km_pers";
        public static final String COLUMN_NAME_TIME_WORK = "time_work";
        public static final String COLUMN_NAME_TIME_PERS = "time_pers";
        public static final String COLUMN_NAME_GPS_SYNC_TS = "gps_sync_ts";
        public static final String COLUMN_NAME_GPS_SYNC = "gps_synched";
        public static final String COLUMN_NAME_SPOST_SYNC_IP = "gps_synched_ip";
        public static final String COLUMN_NAME_LUNCH_IMPO = "lunch_impo";
        public static final String COLUMN_NAME_LUNCH_SEDE = "lunch_sede";
        public static final String COLUMN_NAME_LUNCH_SYNC_TS = "lunch_sync_ts";
        public static final String COLUMN_NAME_LUNCH_SYNC = "lunch_synched";
        public static final String COLUMN_NAME_LUNCH_SYNC_IP = "lunch_synched_ip";
    }

    /* Inner class that defines the table contents */
    public static class DataLogEntryGpsPoints implements BaseColumns {
        public static final String TABLE_NAME = "DataLogGpsPoints";
        public static final String COLUMN_NAME_DATA = "data";
        public static final String COLUMN_NAME_GPS_POINTS = "gps_points";
        public static final String COLUMN_NAME_GPS_SYNC_TS = "gps_sync_ts";
        public static final String COLUMN_NAME_GPS_SYNC = "gps_synched";
    }



        public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Timestamp getTime_morn_start() {
        return time_morn_start;
    }

    public void setTime_morn_start(Timestamp time_morn_start) {
        this.time_morn_start = time_morn_start;
    }

    public Timestamp getTime_morn_end() {
        return time_morn_end;
    }

    public void setTime_morn_end(Timestamp time_morn_end) {
        this.time_morn_end = time_morn_end;
    }

    public Timestamp getTime_aft_start() {
        return time_aft_start;
    }

    public void setTime_aft_start(Timestamp time_aft_start) {
        this.time_aft_start = time_aft_start;
    }

    public Timestamp getTime_aft_end() {
        return time_aft_end;
    }

    public void setTime_aft_end(Timestamp time_aft_end) {
        this.time_aft_end = time_aft_end;
    }

    public double getKm_works() {
        return km_works;
    }

    public void setKm_works(double km_works) {
        this.km_works = km_works;
    }

    public double getKm_pers() {
        return km_pers;
    }

    public void setKm_pers(double km_pers) {
        this.km_pers = km_pers;
    }

    public ArrayList<GpsPoint> getGpsPoints() {
        return gpsPoints;
    }

    public long getTime_work() {
        return time_work;
    }

    public void setTime_work(long time_work) {
        this.time_work = time_work;
    }

    public long getTime_pers() {
        return time_pers;
    }

    public void setTime_pers(long time_pers) {
        this.time_pers = time_pers;
    }

    public void setGpsPoints(ArrayList<GpsPoint> gpsPoints) {
        this.gpsPoints = gpsPoints;
    }

    public Timestamp getTime_sync_ts() {
        return time_sync_ts;
    }

    public void setTime_sync_ts(Timestamp time_sync_ts) {
        this.time_sync_ts = time_sync_ts;
    }

    public boolean isTime_sync() {
        return time_sync;
    }

    public void setTime_sync(boolean time_sync) {
        this.time_sync = time_sync;
    }

    public Timestamp getGps_sync_ts() {
        return gps_sync_ts;
    }

    public void setGps_sync_ts(Timestamp gps_sync_ts) {
        this.gps_sync_ts = gps_sync_ts;
    }

    public boolean isGps_sync() {
        return gps_sync;
    }

    public void setGps_sync(boolean gps_sync) {
        this.gps_sync = gps_sync;
    }

    public double getLunch_impo() {
        return lunch_impo;
    }

    public void setLunch_impo(double lunch_impo) {
        this.lunch_impo = lunch_impo;
    }

    public Timestamp getLunch_sync_ts() {
        return lunch_sync_ts;
    }

    public void setLunch_sync_ts(Timestamp lunch_sync_ts) {
        this.lunch_sync_ts = lunch_sync_ts;
    }

    public boolean isLunch_sync() {
        return lunch_sync;
    }

    public void setLunch_sync(boolean lunch_sync) {
        this.lunch_sync = lunch_sync;
    }

    public boolean isLunch_sede() {
        return lunch_sede;
    }

    public void setLunch_sede(boolean lunch_sede) {
        this.lunch_sede = lunch_sede;
    }

    public String getTime_sync_ip() {
        return time_sync_ip;
    }

    public void setTime_sync_ip(String time_sync_ip) {
        this.time_sync_ip = time_sync_ip;
    }

    public String getSpost_sync_ip() {
        return spost_sync_ip;
    }

    public void setSpost_sync_ip(String spost_sync_ip) {
        this.spost_sync_ip = spost_sync_ip;
    }

    public String getLunch_sync_ip() {
        return lunch_sync_ip;
    }

    public void setLunch_sync_ip(String lunch_sync_ip) {
        this.lunch_sync_ip = lunch_sync_ip;
    }
}
