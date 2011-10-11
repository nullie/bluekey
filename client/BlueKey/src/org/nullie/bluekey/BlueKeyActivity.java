package org.nullie.bluekey;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class BlueKeyActivity extends Activity {
	protected static final int REQUEST_ENABLE_BT = 0;

	private static final UUID MY_UUID = UUID.fromString("c29f1e50-f115-11e0-be50-0800200c9a66");

	protected BluetoothAdapter mBluetoothAdapter;
	
	// Deliberately made view variable, because when we are
	// ready to send, we really want to send the last command
	protected Command mCommand;

	private OnClickListener mLockListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			sendCommand(Command.LOCK);
		}
	};
	
	private OnClickListener mUnlockListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			sendCommand(Command.UNLOCK);
		}
	};

	public BluetoothDevice mmDevice;

	private BluetoothDevice mDevice;
	
	protected void sendCommand(Command command) {
		mCommand = command;
		
		SenderThread thread = new SenderThread(this, command);
		
		thread.start();
	}
	
	protected class SenderThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final Activity mmActivity;

		public SenderThread(Activity activity, Command command) {
			BluetoothSocket tmp = null;
			mmActivity = activity;
			
			try {
				tmp = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
				error("Couldn't create socket", e);
			}
			
			mmSocket = tmp;
		}
		
		private void error(String error, Exception exception) {
			final String message = String.format("%s: %s", error, exception.getMessage());
			mmActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(mmActivity, message, Toast.LENGTH_SHORT).show();						
				}
			});
						
		}
		
		@Override
		public void run() {
			//mBluetoothAdapter.cancelDiscovery();
			
			try {
				mmSocket.connect();
			} catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
				error("Connect failed", connectException);
				
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) { }

	            return;
			}
			
			OutputStream stream = null;
			
			try {
				stream = mmSocket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				error("Couldn't get output stream", e);
				
				return;
			}
			
			try {
				stream.write(mCommand.code);
				stream.flush();
				stream.close();
			} catch (IOException e) {
				error("Write failed", e);
			}
			
			try {
				mmSocket.close();
			} catch (IOException e) {
				error("Close failed", e);
			}
		}
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		Intent intent = getIntent();
		
		Uri data = intent.getData();
		
		assert(data != null);
		
		assert(data.getScheme() == "bluetooth");
		
		mDevice = mBluetoothAdapter.getRemoteDevice(data.getSchemeSpecificPart());
		
        setContentView(R.layout.main);
        
        Button lock = (Button)findViewById(R.id.lock);
        Button unlock = (Button)findViewById(R.id.unlock);
        
        lock.setOnClickListener(mLockListener);
        unlock.setOnClickListener(mUnlockListener);
    }
}