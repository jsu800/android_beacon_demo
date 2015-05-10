/*
 * Copyright (c) 2014 Joseph Su
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibeacon.demos;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.webkit.WebView;
import android.widget.Toast;

import com.estimote.examples.demos.R;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;


public class FindMyBeaconActivity extends Activity {

	private static final String ESTIMOTE_BEACON_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
	private static final String ESTIMOTE_IOS_PROXIMITY_UUID = "8492E75F-4FD6-469D-B132-043FE94921D8";
	private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
	
	private static final double TRIGGERING_DISTANCE_THRESHOLD = 0.5;
	
	// Welcome Office
	private static final String BEACON_BLUE_MAC_WELCOME_OFFICE = "E0:22:1B:2E:F6:7E";
	private static final int BEACON_BLUE_MAJOR_WELCOME_OFFICE = 63102;
	private static final int BEACON_BLUE_MINOR_WELCOME_OFFICE = 1111;
	
	// Theater find me beacon
	private static final String BEACON_PURPLE_MAC_FIND_ME = "C4:84:85:8D:17:40";
	private static final int BEACON_PURPLE_MAJOR_FIND_ME = 5952;
	private static final int BEACON_PURPLE_MINOR_FIND_ME = 34189;

	// Parking
	private static final String BEACON_GREEN_MAC_PARKING = "CE:00:9D:99:F8:5E";
	private static final int BEACON_GREEN_MAJOR_PARKING = 63582;
	private static final int BEACON_GREEN_MINOR_PARKING = 40345;
	
	// Poster 
	private static final String BEACON_PURPLE_MAC_POSTER = "E0:78:40:E5:25:64";
	private static final int BEACON_PURPLE_MAJOR_POSTER = 9572;
	private static final int BEACON_PURPLE_MINOR_POSTER = 16613;
	
	// Ticket
	private static final String BEACON_GREEN_MAC_TICKET = "DC:CC:56:3A:57:B3";
	private static final int BEACON_GREEN_MAJOR_TICKET = 22451;
	private static final int BEACON_GREEN_MINOR_TICKET = 22074;
		
	
	private static final int REQUEST_ENABLE_BT = 1234;
	private BeaconManager beaconManager;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_find_beacon);

	    
		// initialize beacon manager
	    beaconManager = new BeaconManager(this);
	    beaconManager.setRangingListener(new BeaconManager.RangingListener() {
	    	
	    	@Override
	    	public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
	    		//Note that results are not delivered on UI thread.
	    		runOnUiThread(new Runnable() {
	          	    			
	    			@Override
	    			public void run() {

	    				Beacon foundBeacon = null;
	    				String redirectURL = null;
	    				double distance = 0;
	    				
	    				// Note that beacons reported here are already sorted by estimated
	    				// distance between device and beacon.
	    				List<Beacon> estimoteBeacons = filterBeacons(beacons);
	    					    				
	    				for (Beacon estimoteBeacon : estimoteBeacons) {

		    				// then find if any is of < TRIGGERING_DISTANCE_THRESHOLD
		    				distance = Utils.computeAccuracy(estimoteBeacon); 

		    				if (distance < TRIGGERING_DISTANCE_THRESHOLD) {
		    					
		    					if (estimoteBeacon.getMajor() == 1101 && estimoteBeacon.getMinor() == 22221) {
		    						
		    						foundBeacon = estimoteBeacon;
		    						redirectURL = "http://medialabsus.com/comcast/labweek/fandango_mg/movie_poster.html";
		    						break;
		    						
		    						
		    					} else if (estimoteBeacon.getMajor() == 1102 && estimoteBeacon.getMinor() == 22222) {

		    						foundBeacon = estimoteBeacon;
		    						redirectURL = "http://medialabsus.com/comcast/labweek/fandango_mg/ticket.html";
		    						break;
		    						
		    					} else if (estimoteBeacon.getMajor() == 1103 && estimoteBeacon.getMinor() == 22223) {
		    					
		    						foundBeacon = estimoteBeacon;
		    						redirectURL = "http://medialabsus.com/comcast/labweek/fandango_mg/redirect.html";
		    						break;
		    						
		    					}
		    					
		    				}		    				
	    					
	      			  	}
	    				
	    				if (foundBeacon != null) {
      				  	    					
    						WebView myWebView = (WebView) findViewById(R.id.webview);
    						setContentView(myWebView);
    						
    						if (myWebView != null && redirectURL != null) {
	    						    							
    							myWebView.getSettings().setLoadWithOverviewMode(true);
    							myWebView.getSettings().setUseWideViewPort(true);
    							myWebView.getSettings().setBuiltInZoomControls(true);
    							myWebView.loadUrl(redirectURL);

    							finish();
    						
    						}
	    				}	    				
	    			}
	    		});
	    	}
	    });		
		
		
		// kick off beacon search
		
		// listen and search for matched beacon
		
		// display distance while searching
		
		// when matched send an in-app notification
		
		
	}

	@Override
	protected void onDestroy() {
		
		System.out.println("onDestroy()");

		beaconManager.disconnect();
		super.onDestroy();
  	}
	@Override
	protected void onStop() {
		
		System.out.println("onStop()");
		try {
			beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
		} catch (RemoteException e) {
		}

		super.onStop();
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
	
	    // Check if device supports Bluetooth Low Energy.
		if (!beaconManager.hasBluetooth()) {
			Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
			return;
		}
	
		// If Bluetooth is not enabled, let user enable it.
	    if (!beaconManager.isBluetoothEnabled()) {
	    	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	    	startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	    } else {
	    	connectToService();
	    }
	  }
	
	private List<Beacon> filterBeacons(List<Beacon> beacons) {
		List<Beacon> filteredBeacons = new ArrayList<Beacon>(beacons.size());
		for (Beacon beacon : beacons) {
			if (beacon.getProximityUUID().equalsIgnoreCase(ESTIMOTE_BEACON_PROXIMITY_UUID)
					|| beacon.getProximityUUID().equalsIgnoreCase(ESTIMOTE_IOS_PROXIMITY_UUID)) {
				filteredBeacons.add(beacon);
			}
	    }
	    return filteredBeacons;
	}
	
	  private void connectToService() {
		    getActionBar().setSubtitle("Scanning...");
		    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
		      @Override
		      public void onServiceReady() {
		        try {
		          beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
		        } catch (RemoteException e) {
		          Toast.makeText(FindMyBeaconActivity.this, "Cannot start ranging, something terrible happened",
		              Toast.LENGTH_LONG).show();
		        }
		      }
		    });
	  }	

}
