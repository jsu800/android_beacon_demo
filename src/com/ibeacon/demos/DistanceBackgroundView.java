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


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.estimote.examples.demos.R;

public class DistanceBackgroundView extends View {

  private final Drawable drawable;

  public DistanceBackgroundView(Context context, AttributeSet attrs) {
    super(context, attrs);
    drawable = context.getResources().getDrawable(R.drawable.palace_seat_chart_zoom);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    int width = drawable.getIntrinsicWidth() * canvas.getHeight() / drawable.getIntrinsicHeight();
    int deltaX = (width - canvas.getWidth()) / 2;
    drawable.setBounds(-deltaX, 0, width - deltaX, canvas.getHeight());
    drawable.draw(canvas);
  }
}
