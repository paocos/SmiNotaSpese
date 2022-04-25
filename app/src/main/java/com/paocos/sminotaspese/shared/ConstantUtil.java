package com.paocos.sminotaspese.shared;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.BatteryManager;
import androidx.core.app.NotificationCompat;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paocos.sminotaspese.model.entities.GpsPoint;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by paocos on 07/04/17.
 */

public class ConstantUtil {

    private static Boolean powerDisconnected;
    private static Boolean powerConnected;
    private static Boolean appRunning;
    private static boolean requestClosing;

    public static Integer CARD_GPS=0;
    public static Integer CARD_TEMP=1;
    public static Integer CARD_PRANZO=2;
    public static Integer CARD_ORARI=3;
    public static Integer CARD_RIEP=4;

    public static int ID_NOTIFY_LOCAL_SAVE = 1;
    public static int ID_NOTIFY_LOCAL_SAVE_LUNCH = 4;
    public static int ID_NOTIFY_READ_RIEP = 3;
    public static int ID_NOTIFY_REMOTE_SAVE = 2;
    public static int ID_NOTIFY_STATUS = 5;

    public static String SHUTDOWN = "com.paocos.sminotaspese.SHUTDOWN";

    public static Date getDateFromString(String data) {
        DateFormat format = null;
        Date dt = null;
        try {
            format = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
            dt = format.parse(data);
        } catch (ParseException e) {
            return null;
        }
        return  dt;
    } // dateFromAs400    }

    public static Timestamp getTimeStampFromString(String timeStamp) {
        if (timeStamp == null) {
            return null;
        }
        DateFormat format = null;
        Timestamp ts = null;
        try {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
            Date dt = format.parse(timeStamp);
            ts = new Timestamp(dt.getTime());
        } catch (ParseException e) {
            return null;
        }
        return  ts;
    }

    public static String getSqlFormattedTime(Time ts) {
        String toReturn=null;
        if (ts != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            toReturn = sdf.format(ts);
        }
        return toReturn;
    }

    public static String getTimeFormattedFromTimeStamp(Timestamp ts) {
        return ts == null ? "" : getTimeFormattedFromMillsec(ts.getTime());
    }

    public static String getTimeFormattedFromMillsec(long millisec) {
        return String.format("%02d:%02d:%02d", ((millisec / (1000 * 60 * 60)) % 24) , ((millisec / (1000 * 60)) % 60), ((millisec / 1000) % 60));
    }

    public static  String getSqlFormattedTimeStamp(Timestamp ts) {
        String toReturn=null;
        if (ts != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" , Locale.getDefault());
            toReturn = sdf.format(ts);
        }
        return toReturn;
    }

    public static  String getSqlFormattedData(Date data) {
        String toReturn=null;
        if (data != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            toReturn = sdf.format(data);
        }
        return toReturn;
    }

    public static double calcVelMedia(double km , long millisec) {
        if (millisec == 0) {
            return 0;
        } else {
            return (km / millisec * 3600000);
        }
    }

    public static ArrayList<GpsPoint> deCode(String gpsPointsToDecode) {

        if (gpsPointsToDecode == null) {
            return null;
        }

        ArrayList<GpsPoint> gpsPoints = new ArrayList<GpsPoint>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            gpsPoints = objectMapper.readValue(gpsPointsToDecode , new TypeReference<ArrayList<GpsPoint>>(){});

        } catch (IOException e) {
            e.printStackTrace();
        }

        return gpsPoints;

  }

    public static String inCode(ArrayList<GpsPoint> gpsPoints) {
        String toReturn = null;

        if (gpsPoints != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                toReturn = objectMapper.writeValueAsString(gpsPoints);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        if (toReturn == null) {
            return null;
        } else { // (toReturn == null) {
            return toReturn.toString();
        } // (toReturn == null) {
    }

    public static Boolean getPowerDisconnected() {
        return powerDisconnected == null ? false : powerDisconnected;
    }

    public static void setPowerDisconnected(Boolean powerDisconnected) {
        ConstantUtil.powerDisconnected = powerDisconnected;
    }

    public static Boolean getPowerConnected() {
        return powerConnected == null ? false : powerConnected;
    }

    public static void setPowerConnected(Boolean powerConnected) {
        ConstantUtil.powerConnected = powerConnected;
    }

    public static boolean isConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

    public static void setPowerStatus(Context context) {
        if (ConstantUtil.isConnected(context)) {
            setPowerConnected(true);
            setPowerDisconnected(false);
        } else {
            setPowerConnected(false);
            setPowerDisconnected(false);
        }
    }

    public static Boolean getAppRunning() {
        return appRunning;
    }

    public static void setAppRunning(Boolean appRunning) {
        ConstantUtil.appRunning = appRunning;
    }


    public static void showToast(Context context , String messaggio) {
        Toast toast = Toast.makeText(context, messaggio , Toast.LENGTH_LONG);
        toast.show();
    }

    public static NotificationManager addNotification(Context context, NotificationManager mNotifyMgr, Object systemService, String projectName, int id_icon, String title, String body, int id_notification) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(id_icon)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH);
        mNotifyMgr = (NotificationManager) systemService;
        // Builds the notification and issues it.
        mNotifyMgr.notify(id_notification, mBuilder.build());
        return mNotifyMgr;
    }

    public static void removeNotification(NotificationManager mNotifyMgr, int id_notification) {
        if (mNotifyMgr != null) {
            mNotifyMgr.cancel(id_notification);
        }
    }

    public static void beep(int idTone) {
        try {
            final ToneGenerator tg = new ToneGenerator(
                    AudioManager.STREAM_NOTIFICATION, 100);
            tg.startTone(idTone);
        } catch (Exception e) {}
    }

    public static boolean getRequestClosing() {
        return requestClosing;
    }

    public static void setRequestClosing(Boolean requestClosing) {
        ConstantUtil.requestClosing = requestClosing;
    }

}
