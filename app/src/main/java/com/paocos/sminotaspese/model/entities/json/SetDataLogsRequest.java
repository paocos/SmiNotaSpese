package com.paocos.sminotaspese.model.entities.json;

import com.paocos.sminotaspese.model.entities.DataLog;
import com.paocos.sminotaspese.model.entities.Setting;

import java.util.ArrayList;

/**
 * Created by paocos on 07/05/17.
 */

public class SetDataLogsRequest {

    private ArrayList<DataLog> dataLogs;

    public ArrayList<DataLog> getDataLogs() {
        return dataLogs;
    }

    public void setDataLogs(ArrayList<DataLog> dataLogs) {
        this.dataLogs = dataLogs;
    }
}
