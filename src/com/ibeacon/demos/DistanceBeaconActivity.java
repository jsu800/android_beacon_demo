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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.estimote.examples.demos.R;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

/**
 * Repurpose for Comcast demo week. Visualizes distance from beacon to the device.
 *
 * @author Joseph Su
 */
public class DistanceBeaconActivity extends Activity {

	private static final String TAG = DistanceBeaconActivity.class.getSimpleName();

  	// Y positions are relative to height of bg_distance image.
	private static final double RELATIVE_START_POS = 320.0 / 1110.0;
	private static final double RELATIVE_STOP_POS = 885.0 / 1110.0;

	private static final String ESTIMOTE_BEACON_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
	private static final String ESTIMOTE_IOS_PROXIMITY_UUID = "8492E75F-4FD6-469D-B132-043FE94921D8";

	// this is the beacon used in EC
	private static final String BEACON_PURPLE_MAC = "C4:84:85:8D:17:40";
	private static final int BEACON_PURPLE_MAJOR = 1104;
	private static final int BEACON_PURPLE_MINOR = 22223;
	
	
	//Let's put dot at the end of the scale when it's further than MAX_DISTANCE_SCALE meters away.
	private static final double MAX_DISTANCE_SCALE = 6.0;
	private static final int TARGET_Y = 490;
	private static final double FUDGE_FACTOR = 15;
	private static int mNumSamplings = 0;
	
	private DecimalFormat df = new DecimalFormat("#.##");
  
	private BeaconManager beaconManager;
	private Beacon finderBeacon = new Beacon(ESTIMOTE_BEACON_PROXIMITY_UUID, 
			"THEATER_SEAT_FINDER_BEACON",
			BEACON_PURPLE_MAC,
			BEACON_PURPLE_MAJOR,
			BEACON_PURPLE_MINOR,
			-78,
			-76
			);
	
	private Region region;	
	private View dotView, targetView;
	private int startY = -1;
	private int segmentLength = -1;
	
	private double mAverageAccuracy = 0;
	private double mTotalAccuracy = 0;
	private static final double SIGNAL_DIFFERENCE_THRESHOLD_PERCENTAGE = 300.0;

  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getActionBar().setDisplayHomeAsUpEnabled(true);
    setContentView(R.layout.distance_view);
    dotView = findViewById(R.id.dot);
    targetView = findViewById(R.id.target);

    
    region = new Region("regionid", finderBeacon.getProximityUUID(), finderBeacon.getMajor(), finderBeacon.getMinor());    
    if (finderBeacon == null) {
      Toast.makeText(this, "Beacon not found in intent extras", Toast.LENGTH_LONG).show();
      finish();
    }

    beaconManager = new BeaconManager(this);
    beaconManager.setRangingListener(new BeaconManager.RangingListener() {
      @Override
      public void onBeaconsDiscovered(Region region, final List<Beacon> rangedBeacons) {

    	  // Note that results are not delivered on UI thread.
    	  runOnUiThread(new Runnable() {
        	
    		  @Override
    		  public void run() {
    			  //Just in case if there are multiple beacons with the same uuid, major, minor.
    			  Beacon foundBeacon = null;
    			  
    			  List<Beacon> estimoteBeacons = filterBeacons(rangedBeacons);
    			  
    			  //System.out.println("# of estimote found: " + estimoteBeacons.size());
    			  
    			  for (Beacon estimoteBeacon : estimoteBeacons) {
    				  if (estimoteBeacon.getMajor() == finderBeacon.getMajor() && estimoteBeacon.getMinor() == finderBeacon.getMinor()) {
	        				foundBeacon = estimoteBeacon;
    				  }
    			  }
    			  
    			  if (foundBeacon != null) {
    				  
    				  if (doneTakingSamplings(foundBeacon) == true)
    					  updateDistanceView();
    			  }
    		  }
        });
      }
    });

    
    final View view = findViewById(R.id.sonar);
    view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

        startY = (int) (RELATIVE_START_POS * view.getMeasuredHeight());
        int stopY = (int) (RELATIVE_STOP_POS * view.getMeasuredHeight());
        segmentLength = stopY - startY;
        
        dotView.setVisibility(View.VISIBLE);
        dotView.setTranslationY(computeDotPosY(finderBeacon));
        
        targetView.setVisibility(View.VISIBLE);
        targetView.setTranslationY(TARGET_Y);
        
      }
    });
  }
  
  private void updateDistanceView(Beacon foundBeacon) {
	  if (segmentLength == -1)
    	return;

	  dotView.animate().translationY(computeDotPosY(foundBeacon)).start();
  }

  private void updateDistanceView() {
	  if (segmentLength == -1)
    	return;

	  dotView.animate().translationY(computeDotPosY()).start();
  }

  
  private boolean doneTakingSamplings(Beacon beacon) {

  	if (mNumSamplings >= 3) {
  		
  		mAverageAccuracy = mTotalAccuracy / mNumSamplings; 
  		
  		System.out.println("average ACCURACY: " + mAverageAccuracy);
  		
  		// reset everything and returning true
  		mNumSamplings = 1;
  		mTotalAccuracy = 0;
  		
  		return true;
  		
  	} else {

  		double tmpAccuracy = computeAccuracy(beacon);
  		
//  		if (mAverageAccuracy > 0) {
//  			
//  			// when mAverageAccuray > 0 there's already a moving average from last assessment
//  	  		// Then for each local accuracy, see if it is way off the chart namely over 200% of the average
//  			
//  			double percentDifference = (Math.abs(tmpAccuracy - mAverageAccuracy) / mAverageAccuracy) * 100;
//  			
//  			if (percentDifference > SIGNAL_DIFFERENCE_THRESHOLD_PERCENTAGE) {
//  				
//  				// don't increment sampling count; we won't be using this signal dataset
//  				System.out.println("% DIFFERENCE: " + percentDifference);
//  				return false;
//  			} 
//  			
//  		}

		mTotalAccuracy += tmpAccuracy;
  	  	mNumSamplings++;
  	  	
  	  	// don't forget to compute moving average each time
  	  	mAverageAccuracy = mTotalAccuracy / mNumSamplings; 
  			
  		
  	  	return false;
  	}
  	
  }

  private double getAverageAccuracy() {
	  return mAverageAccuracy;
  }
  
  private int computeDotPosY() {
	  
	  double accuracy = getAverageAccuracy();
	  double realDistance = accuracy * FUDGE_FACTOR;
	  getActionBar().setSubtitle("Customer is " + df.format(realDistance) + "m away");
    
	  // Let's put dot at the end of the scale when it's further than 6m.
	  double distance = Math.min(realDistance, MAX_DISTANCE_SCALE);	
	  int temp = startY + (int) (segmentLength * (distance / MAX_DISTANCE_SCALE));

	  return temp;
    
  }

  // this is needed for setting up view 
  private int computeDotPosY(Beacon beacon) {
	  
	  double accuracy = computeAccuracy(beacon);
	  double realDistance = accuracy * FUDGE_FACTOR;
	  getActionBar().setSubtitle("Customer is " + df.format(realDistance) + "m away");
    
	  // Let's put dot at the end of the scale when it's further than 6m.
	  double distance = Math.min(realDistance, MAX_DISTANCE_SCALE);	
	  int temp = startY + (int) (segmentLength * (distance / MAX_DISTANCE_SCALE));

	  return temp;
    
  }

  private double computeAccuracy(Beacon beacon) {	  
		return Utils.computeAccuracy(beacon);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onStart() {
    super.onStart();

    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
      @Override
      public void onServiceReady() {
        try {
          beaconManager.startRanging(region);
        } catch (RemoteException e) {
          Toast.makeText(DistanceBeaconActivity.this, "Cannot start ranging, something terrible happened",
              Toast.LENGTH_LONG).show();
          Log.e(TAG, "Cannot start ranging", e); 
        }
      }
    });
  }

  @Override
  protected void onStop() {
	  beaconManager.disconnect();
	
	  super.onStop();
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
  
}
