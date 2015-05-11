/*
 * MIT License
 * Copyright (c) 2015 Joseph Su (http://www.github.com/jsu800)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and 
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of 
 * the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO 
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package com.ibeacon.demos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.estimote.examples.demos.R;

/**
 * Repurpose for Comcast Demo
 *
 * @author Joseph Su
 */
public class AllDemosActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //setContentView(R.layout.all_demos);
    setContentView(R.layout.movie_goer_ibeacon_demo);
    
    
    findViewById(R.id.distance_demo_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        
    	  // COMCAST DEMO
    	  Intent intent = new Intent(AllDemosActivity.this, DistanceBeaconActivity.class);
    	  
    	  startActivity(intent);
      }
    });

    findViewById(R.id.find_specific_beacon_button).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(AllDemosActivity.this, FindMyBeaconActivity.class);
          startActivity(intent);
        }
      });
    
  }
}
