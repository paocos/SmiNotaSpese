package com.paocos.sminotaspese.model.entities.json;

import com.paocos.sminotaspese.model.entities.Setting;

import java.util.ArrayList;

/**
 * Created by paocos on 07/05/17.
 */

public class SetSettingRequest {

    private ArrayList<Setting> settings;

    public ArrayList<Setting> getSettings() {
        return settings;
    }

    public void setSettings(ArrayList<Setting> settings) {
        this.settings = settings;
    }
}
