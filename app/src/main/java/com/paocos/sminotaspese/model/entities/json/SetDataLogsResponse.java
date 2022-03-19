package com.paocos.sminotaspese.model.entities.json;

import com.paocos.sminotaspese.model.entities.DataLog;

import java.util.ArrayList;

/**
 * Created by paocos on 27/07/17.
 */

public class SetDataLogsResponse extends RspBase {

    private ArrayList<DataLog> dataLogs;

    /**
     * @return the dataLogs
     */
    public ArrayList<DataLog> getDataLogs() {
        return dataLogs;
    }

    /**
     * @param dataLogs the dataLogs to set
     */
    public void setDataLogs(ArrayList<DataLog> dataLogs) {
        this.dataLogs = dataLogs;
    }

}
