package com.paocos.sminotaspese.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.paocos.sminotaspese.shared.ConstantUtil;

public class ShutdownReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        String type = intent.getType();
        if (ConstantUtil.SHUTDOWN.equalsIgnoreCase(action)  && type != null) {
//            if (Intent.ACTION_SEND.equals(action)  && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent);
            }
        }

    }

    private void handleSendText(Intent intent) {
        //ConstantUtil.setPowerDisconnected(true);
        ConstantUtil.setRequestClosing(true);
    }

}
