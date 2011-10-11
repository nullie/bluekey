package org.nullie.bluekey;

import java.util.Set;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class SelectDeviceActivity extends ListActivity {
	protected class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {
		public BluetoothDeviceAdapter(Context context, int textViewResourceId, BluetoothDevice[] devices) {
			super(context, textViewResourceId, devices);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
				convertView = inflater.inflate(android.R.layout.simple_list_item_2, null);
				
				BluetoothDevice device = getItem(position);
				
				final Uri data = Uri.fromParts("bluetooth", device.getAddress(), null);
				
				convertView.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent(SelectDeviceActivity.this, org.nullie.bluekey.BlueKeyActivity.class);
						
						intent.setData(data);
			    		
			    		SelectDeviceActivity.this.startActivity(intent);
					}
				});
			
				TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
				TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);
				
				text1.setText(device.getName());
				text2.setText(device.getAddress());
			}
			
			return convertView;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		
		if(adapter == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			builder.setMessage("Your device doesn't have bluetooth adapter!")
				   .setCancelable(false)
				   .setIcon(android.R.drawable.ic_dialog_alert)
				   .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					   @Override
					   public void onClick(DialogInterface dialog, int id) {
						   System.exit(0);
					   }
				   });
			
			builder.create().show();
			
			finish();
		}
		
		Set<BluetoothDevice> devices = adapter.getBondedDevices(); 
		
		BluetoothDevice[] deviceArray = devices.toArray(new BluetoothDevice[devices.size()]);
		
		setListAdapter(new BluetoothDeviceAdapter(this, android.R.layout.simple_list_item_2, deviceArray));
	}
}
