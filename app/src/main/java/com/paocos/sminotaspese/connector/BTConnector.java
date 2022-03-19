package com.paocos.sminotaspese.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BTConnector implements Runnable {

	private boolean isConnected;
	private boolean dataSended;
	private static final int REQUEST_ENABLE_BT = 1;
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static String address = "98:D3:31:50:0D:D7";
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket mmSocket;
	private BluetoothDevice mmDevice;
	private InputStream inStream;
	private OutputStream outStream;
	private static final String TAG = "BTConnector"; // Debug
	private static final boolean D = true; // Debug

	private Thread t;
	private boolean running = true;

	private String dataRead;

	/**
	 * crea connessione
	 */
	public BTConnector() {
		isConnected = false;
		dataSended = false;
		getConnect();
		t = new Thread(this, "bluetooth");
	}

	public void run() {
		try {
			while (running) {
				if (!isConnected) {
					getConnect();
				}
				Thread.sleep(100);
			}
			closeConnection();
		} catch (InterruptedException e) {

		}
	}

	public void startThread() {
		t.start();
	}

	public void stopThread() {
		running = false;
	}

	/**
	 * connesione a device bluetooth
	 */
	private void getConnect() {

		// if (1 == 2) { // debug
		// Get the BluetoothAdapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {

			mmDevice = mBluetoothAdapter.getRemoteDevice(address);
			Log.e(TAG, mmDevice.getName() + " connected");

			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code
				tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
				mmSocket = tmp;
				// Cancel discovery because it will slow down the connection
				// mBluetoothAdapter.cancelDiscovery();

				try {
					// Connect the device through the socket. This will block
					// until it succeeds or throws an exception
					mmSocket.connect();
					outStream = mmSocket.getOutputStream();
					inStream = mmSocket.getInputStream();
					isConnected = true;
				} catch (IOException connectException) {
					// Unable to connect; close the socket and get out
					try {
						tmp.close();
						mmSocket.close();
					} catch (IOException closeException) {
					}
					return;
				}
			} catch (IOException e) {
			}
			// }
		}
	}

	/**
	 * invia dati a device
	 * 
	 * @param charToSend
	 *            H:get help V:get version X:read sensor 1
	 */
	public void sendData(char charToSend) {
		if (!isConnected) {
			getConnect();
		}
		if (outStream != null && isConnected && (charToSend == 'X')) {
			try {
				// prima di tutto svuoto il buffer
				getData();// per svuotare il buffer
				outStream.write(charToSend);
				dataSended = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * legge dati da sensore, da lanciare prima di getTemperature e getHumidity
	 */
	public void getData() {
		dataRead = "";
		String dataDummy = "";
		if (isConnected && dataSended) {
			byte[] buffer = new byte[256];
			int bytes;
			// handle Connection
			try {
				while (inStream != null && inStream.available() != 0) {
					bytes = inStream.read(buffer);
					dataDummy = new String(buffer, 0, bytes);
					dataRead += dataDummy;
					dataSended = false;
				}
			} catch (IOException e3) {
				Log.e(TAG, "disconnected");
			}
		} else {
			if (!isConnected) {
				getConnect();
			}
		}
	}

	/*
	 * public void getData() { dataRead = null; if (isConnected && dataSended) {
	 * byte[] buffer = new byte[256]; int bytes; // handle Connection try { if
	 * (inStream.available()!=0) { bytes = inStream.read(buffer); dataRead = new
	 * String(buffer, 0, bytes); dataSended = false; } } catch (IOException e3)
	 * { Log.e(TAG, "disconnected"); } } else { if (!isConnected) {
	 * getConnect(); } } }
	 */

	/**
	 * legge dati da sensore, da lanciare prima di getTemperature e getHumidity
	 */
	/*
	 * public void emptyBuffer() { dataRead = null; if (isConnected &&
	 * dataSended) { byte[] buffer = new byte[256]; int bytes; // handle
	 * Connection try { bytes = inStream.read(buffer); dataRead = new
	 * String(buffer, 0, bytes); dataSended = false; } catch (IOException e3) {
	 * Log.e(TAG, "disconnected"); } } else { if (!isConnected) { getConnect();
	 * } } }
	 */

	public int getTemperature() {
		int temperature = 999;
		// Se relativo a sensore #1
		if (dataRead != null && dataRead.trim().length() > 12
				&& dataRead.substring(0, 2).equals("X:")) {
			int posStart = dataRead.indexOf("T1:") + 3;
			int posEnd = dataRead.indexOf(";", posStart - 1);
			temperature = Integer
					.parseInt(dataRead.substring(posStart, posEnd));
		}
		return temperature;
	}
	
	public String toString() {
		return dataRead;
	}

	public int getHumidity() {
		int humidity = 999;
		// Se relativo a sensore #1
		if (dataRead != null && dataRead.trim().length() > 12
				&& dataRead.substring(0, 2).equals("X:")) {
			int posStart = dataRead.indexOf("H1:") + 3;
			int posEnd = dataRead.indexOf(";", posStart - 1);
			humidity = Integer.parseInt(dataRead.substring(posStart, posEnd));
		}
		return humidity;
	}

	public void closeConnection() {
		// try {
//		if (mBluetoothAdapter != null) {
//			mBluetoothAdapter.disable();
//			mBluetoothAdapter = null;
//		}
		if (isConnected) {
			// inStream.close();
			try {inStream.close();} catch (Exception e) {}
			inStream = null;
			// outStream.close();
			try {outStream.close();} catch (Exception e) {}
			outStream = null;
			// mmSocket.close();
			try {mmSocket.close();} catch (Exception e) {}
			mmSocket = null;

			mmDevice = null;
		}
		// } catch (IOException e) {
		// }

	}
}
