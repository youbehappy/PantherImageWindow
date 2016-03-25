package panther.test;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class ZoomActivity extends Activity implements
		OnClickListener
{

	private Button btn_show;
	private Button btn_hide;
	private static WindowManager windowManager;
	private static WindowManager.LayoutParams params;

	Button imageview;
	float startDistance = 0;
	PointF midPoint;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		btn_show = (Button) findViewById(R.id.btn_show);
		btn_hide = (Button) findViewById(R.id.btn_hide);
		btn_show.setOnClickListener(this);
		btn_hide.setOnClickListener(this);


		imageview = new Button(this);
		imageview.setBackgroundResource(R.drawable.flower);
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btn_show:
			showImageWindow(ZoomActivity.this,imageview);
			break;
		case R.id.btn_hide:
			dimissImageWindow(ZoomActivity.this,imageview);
			break;
		}
	}
	void showImageWindow(Context context, View imageView){
		params = new WindowManager.LayoutParams();
		params.type = WindowManager.LayoutParams.TYPE_APPLICATION;

		params.format = PixelFormat.RGBA_8888;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);



		params.width = 100;
		params.height = 100;


		imageview.setOnTouchListener(new View.OnTouchListener(){
			int lastX, lastY;
			int paramX, paramY;
			boolean isZoom = false;
			float scale = 1;
			int pointerCount = 1;
			boolean isFirstMultiPointer = true;
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						Log.e("scale", "================ACTION_DOWN===================");
						lastX = (int) event.getRawX();
						lastY = (int) event.getRawY();
						paramX = params.x;
						paramY = params.y;
						break;
					case MotionEvent.ACTION_MOVE:
						Log.e("scale", startDistance + "================ACTION_MOVE===================" + pointerCount);
						//check how many pointers right now on the screen
						int count = event.getPointerCount();
						if(count>=2){
							pointerCount = 2;
							Log.e("scale", "=======pointerCount = 2================" + isFirstMultiPointer);
							if(isFirstMultiPointer){
								startDistance = distance(event);
								isFirstMultiPointer = false;
							}else{
//								float endDistance = distance(event);
//								if(endDistance > 10f){
//									scale = endDistance / startDistance;
//									float height = params.height*scale;
//									float width = params.width*scale;
//									Log.e("scale", "=================================ZOOM_EVENT==" + scale);
//									params.height = (int)height;
//									params.width = (int)width;
//
//									windowManager.updateViewLayout(imageview, params);
//								}
							}

						}else{
							pointerCount = 1;
							int dx = (int) event.getRawX() - lastX;
							int dy = (int) event.getRawY() - lastY;
							params.x = paramX + dx;
							params.y = paramY + dy;
							// Update the location of the window
							windowManager.updateViewLayout(imageview, params);
						}

						break;
					case MotionEvent.ACTION_UP:
						Log.e("scale", "================ACTION_UP===================" + startDistance);
						pointerCount = 1;
						isFirstMultiPointer = true;
//						float endDist = distance(event);
//						if(endDist > 10f && endDist!=0 && startDistance!=0){
//							scale = endDist / startDistance;
//							float height = params.height*scale;
//							float width = params.width*scale;
//							Log.e("scale", endDist+"=================================ZOOM_EVENT==" + scale);
//							params.height = (int)height;
//							params.width = (int)width;
//
//							windowManager.updateViewLayout(imageview, params);
//							startDistance = 0;
//						}

						break;
					case MotionEvent.ACTION_POINTER_UP://One pointer leaves the screen, others left.
						Log.e("scale", "================ACTION_POINTER_UP===================" + startDistance);
						pointerCount = 1;
						isFirstMultiPointer = true;
						float endDistance = distance(event);
						if(endDistance > 10f && endDistance!=0 && startDistance!=0){
							scale = endDistance / startDistance;
							float height = params.height*scale;
							float width = params.width*scale;
							Log.e("scale", endDistance + "=================================ZOOM_EVENT==" + scale);
							params.height = (int)height;
							params.width = (int)width;

							windowManager.updateViewLayout(imageview, params);
							startDistance = 0;
						}

						break;
					case MotionEvent.ACTION_POINTER_DOWN://One more pointer touch down given some pointers already being touching down

//						startDistance = distance(event);
						Log.e("scale", "================ACTION_POINTER_DOWN===================");
//						if(startDistance > 10f){
//							midPoint = mid(event);
//							Log.e("scale", "================ACTION_POINTER_DOWN=================ZOOM_EVENT==" + midPoint);
//						}
						break;
				}
				return true;
			}
		});

		windowManager.addView(imageview, params);
		params.height = (int)(params.height*2);
		params.width = (int)(params.width*2);
		windowManager.updateViewLayout(imageview, params);
	}
	void dimissImageWindow(Context context, View imageView){
		windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		windowManager.removeView(imageView);
	}
	/**
	 * Get the distance between two pointers
	 * @param event
	 * @return distance
	 */
	private static float distance(MotionEvent event){
		try{
			float dx = event.getX(1) - event.getX(0);
			float dy = event.getY(1) - event.getY(0);
			return FloatMath.sqrt(dx * dx + dy * dy);
		}catch(java.lang.IllegalArgumentException e){

		}
		return 0;
	}
	/**
	 * Get the distance between pointers and the middle point
	 * @param event
	 * @return
	 */
	private static PointF mid(MotionEvent event){
		float midx = event.getX(1) + event.getX(0);
		float midy = event.getY(1) - event.getY(0);

		return new PointF(midx/2, midy/2);
	}


}