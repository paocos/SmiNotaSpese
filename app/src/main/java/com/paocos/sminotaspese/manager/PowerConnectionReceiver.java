package com.paocos.sminotaspese.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.paocos.sminotaspese.MainActivity;
import com.paocos.sminotaspese.shared.ConstantUtil;

public class PowerConnectionReceiver extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		// leggo preferenze , l'applicazione deve partire solo gps attivo
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean prfIsGpsActive = prefs.getBoolean("gpsActive", false);

		if (prfIsGpsActive) {
			if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
				// il processo parte da macrodroid per connessione bluetooth.
				// se tensione attaccata lascio lo schermo acceso
//				Intent aServiceIntent = new Intent(context, MainActivity.class);
//				aServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				if (ConstantUtil.getAppRunning() == null || !ConstantUtil.getAppRunning()) {
//					context.startActivity(aServiceIntent);
//				}
				ConstantUtil.setPowerConnected(true);
				ConstantUtil.setPowerDisconnected(false);
			} else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
				ConstantUtil.setPowerDisconnected(true);
			}
		}
	}
}
