package com.paocos.sminotaspese.shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paocos.sminotaspese.model.entities.GpsPoint;
import com.paocos.sminotaspese.model.entities.json.JGpsPoints;

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
    private static final String START_OBJ = "{";
    private static final String END_OBJ = "}";
    private static final String START_FLD = "[";
    private static final String END_FLD = "]";

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

    public static String getTimeFormattedFromMillsec(long millisec) {
        return String.format("%02d:%02d:%02d", ((millisec / (1000 * 60 * 60)) % 24) , ((millisec / (1000 * 60)) % 60), ((millisec / 1000) % 60));
    }

    public static  String getSqlFormattedTimeStamp(Timestamp ts) {
        String toReturn=null;
        if (ts != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

    public static JGpsPoints deCode(String jGpsPointsToDecode) {

        if (jGpsPointsToDecode == null) {
            return null;
        }

        JGpsPoints jGpsPoints = new JGpsPoints();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            jGpsPoints = objectMapper.readValue(jGpsPointsToDecode , JGpsPoints.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jGpsPoints;

  }

    public static String inCode(ArrayList<GpsPoint> gpsPoints) {
        String toReturn = null;

        if (gpsPoints != null && gpsPoints.getGpsPoints() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                toReturn = objectMapper.writeValueAsString(jGpsPoints);
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
}
