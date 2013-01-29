package com.redcricket.flashlife;

/* try looking here
 * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/camera/CameraManager.java
 * and
 * here
 * http://stackoverflow.com/questions/7615446/runtime-error-failed-to-connect-to-camera-service-in-android?rq=1
 */
import java.io.IOException;
import java.util.Collection;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class FlashlightActivity extends Activity implements
		SurfaceHolder.Callback {

	private static String TAG = "flashlight";
	private ToggleButton toggleButton1;
	private boolean hasSurface;
	static Camera cam = null;

	
	@Override
	public void onPause() {
		Log.i(TAG, "[32] onPause()");
		super.onPause();
		if (cam != null) {
			Log.i("flashlight", "[35] onPause() cam not null. Calling stopPreview and release cam. Setting cam to null.");
			Log.i(TAG, "onPause() step 9 (step 7 & 8 are optional)");
			cam.stopPreview();
			cam.setPreviewCallback(null); // this doesn't seem to help
			cam.release();
			cam = null;
		}
	}

	public void turnFlashOn() {
		Log.i(TAG, "[42] turnFlashOn()");
		if (cam == null) {
			Log.i(TAG, "turnFlashOn() step 1");
			cam = Camera.open();
			Log.i(TAG, "[64] turnFlashOn() cam was null. Camera.open() called");
			/* new stuff */
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			Log.i(TAG, "[174] turnFlashOn() calling initCamera(surfaceHolder) hasSurface is " + hasSurface );
			initCamera(surfaceHolder);
			/* end new stuff */
		}
		if (cam != null) {
			Log.i(TAG, "[67] turnFlashOn() cam not null. calling setDesiredCameraParameters(cam).");
			setDesiredCameraParameters(cam);
			Log.i(TAG, "[69] turnFlashOn() startPreview step 6");
			/*
			 * step 6
			 * http://developer.android.com/reference/android/hardware/Camera.html
			 */
			cam.startPreview();
			Log.i(TAG,"[75] turnFlashOn() after startPreview");
			cam.autoFocus(new AutoFocusCallback() {
				public void onAutoFocus(boolean success, Camera camera) {
					if (success) {
						Log.i(TAG,
								"[80] turnFlashOn() onAutoFocus success = true");
					} else {
						Log.i(TAG,
								"[83] turnFlashOn() onAutoFocus success = false");
					}
				}
			});
			//Log.i(TAG,"[69] turnFlashOn() after cam.autoFocus TAKEPIC");
			//cam.takePicture(null, null, null, null);
		}
	}

	/*
	 * http://developer.android.com/reference/android/hardware/Camera.html steps 2 & 3
	 */
	void setDesiredCameraParameters(Camera camera) {
		Log.i(TAG, "setDesiredCameraParameters() step 2");
		Camera.Parameters parameters = camera.getParameters();
		Log.i("flashlight",
				"[77] FlashlightActivity.java setDestiredVcmaeria paramers");
		if (parameters == null) {
			Log.w(TAG,
					"Device error: no camera parameters are available. Proceeding without configuration.");
			return;
		}

		Log.i(TAG, "[84] Initial camera parameters: " + parameters.flatten());

		initializeTorch(parameters);
		Log.i(TAG, "setDesiredCameraParameters() step 3");
		camera.setParameters(parameters);
	}

	private void initializeTorch(Camera.Parameters parameters) {
		Log.i(TAG, "[95] initializeTorch()");
		doSetTorch(parameters);
	}

	private void old_doSetTorch(Camera.Parameters parameters) {
		Log.i(TAG, "[100] dosetTorch()");
		String flashMode;

		flashMode = findSettableValue(parameters.getSupportedFlashModes(),
				Camera.Parameters.FLASH_MODE_TORCH);
		Log.i(TAG, "[104] doSetTorch() flashmode is " + flashMode);
		// ,
		// Camera.Parameters.FLASH_MODE_ON,
		// Camera.Parameters.FOCUS_MODE_INFINITY);
		if (flashMode != null) {
			Log.i(TAG, "[109] doSetTorch() called parameters.setFlashMode( " + flashMode + ")");
			parameters.setFlashMode(flashMode);
		}
	}
	
	private void doSetTorch(Camera.Parameters parameters) {
	    String flashMode;
	      flashMode = findSettableValue(parameters.getSupportedFlashModes(),
	                                    Camera.Parameters.FLASH_MODE_TORCH,
	                                    Camera.Parameters.FLASH_MODE_ON);
	    if (flashMode != null) {
	      parameters.setFlashMode(flashMode);
	    }
	  }


	private static String findSettableValue(Collection<String> supportedValues,
			String... desiredValues) {
		Log.i(TAG, "[117] findSettableValue() Supported values: " + supportedValues);
		String result = null;
		if (supportedValues != null) {
			for (String desiredValue : desiredValues) {
				if (supportedValues.contains(desiredValue)) {
					result = desiredValue;
					Log.i("flashlight", "[123] findSettableValue() result set to : " + desiredValue);
					break;
				}
			}
		}
		Log.i("flashlight", "[128] findSettableValue() Settable value: " + result);
		return result;
	}

	public void turnFlashOff() {
		Log.i("flashlight", "[133] turnFlashOff()");
		if (cam != null) {
			Log.i("flashlight", "[135] turnFlashOff() cam is not null.");
			Log.i(TAG, "turnFlashOff() step 9 (step 7 & 8 are optional)");
			cam.stopPreview();
			cam.release();
			cam = null;
		}
	}

	public void addListenerOnButton() {
		Log.i("flashlight", "[143] addListenerOnButton()");
		toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
		toggleButton1.setOnCheckedChangeListener(
				new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						Log.i("flashlight", "[148] onCheckedChanged() isChecked = " + isChecked);
						if (isChecked) {
							turnFlashOn();
						} else {
							turnFlashOff();
						}
					}
				});
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "[163] onCreate() hasSurface is " + hasSurface );
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();

		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			Log.i(TAG, "[174] onCreate() calling initCamera(surfaceHolder) hasSurface is " + hasSurface );
			initCamera(surfaceHolder);
		} else {
			Log.i(TAG, "[177] does not have surface, so install callback and wait for surfaceCreated() to init the camera");
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			Log.i(TAG, "onCreate() step 1");
			cam = Camera.open();
			Log.i(TAG, "[202] onCreate(). Camera.open() called");
			addListenerOnButton();
		} else {
			Toast.makeText(getApplicationContext(),
					"This device does not have flash bulb.\nExitting.", 60)
					.show();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "[196] surfaceCreated() hasSurface is " + hasSurface );
		if (holder == null) {
			Log.e(TAG,
					"*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			Log.i("flashlight", "[240] surfaceCreated() set hasSurface to true. Calling initCamera(holder)");
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i("flashlight", "[209] surfaceDestroyed()");
		hasSurface = false;
		if ( cam != null ) {
			cam.stopPreview();
			cam.release();
			cam = null;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i("flashlight", "[216] surfaceChanged(holder,format,width,height)");
	}

	@Override
	protected void onDestroy() {
		Log.i("flashlight", "[220] onDestroy()");
		try {
			Log.i(TAG, "onDestroy() step 9 (step 7 & 8 are optional)");
			cam.stopPreview();
			cam.setPreviewCallback(null);
			try {
				cam.release();
			} catch (Exception e) {
			}
			cam = null;
		} catch (Exception e) {
		}
		super.onDestroy();
	}

	/* new stuff */
	private void initCamera(SurfaceHolder surfaceHolder) {
		Log.i(TAG, "[236] initCamera() trying to call openDriver(holder)");

		try {
			openDriver(surfaceHolder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Opens the camera driver and initializes the hardware parameters.
	 * 
	 * @param holder
	 *            The surface object which the camera will draw preview frames
	 *            into.
	 * @throws IOException
	 *             Indicates the camera driver failed to open.
	 */
	public synchronized void openDriver(SurfaceHolder holder)
			throws IOException {
		Log.i(TAG, "[256] openDriver() top");

		Camera theCamera = cam;
		if (theCamera == null) {
			Log.i(TAG, "[278] openDriver()");
			Log.i(TAG, "openDrive() step 1");
			theCamera = Camera.open();
			Log.i(TAG, "[280] openDriver() Camera.open() called");
			if (theCamera == null) {
				Log.i(TAG, "[263] openDriver()");
				throw new IOException();
			}
			cam = theCamera;
		}
		/*
		 * do this as per
		 * http://developer.android.com/reference/android/hardware/Camera.html
		 * step 5
		 */
		Log.i(TAG, "[281] openDriver() calling setPreviewDisplay(holder)");
		Log.i(TAG, "openDriver() step 5 (step 4 is optional)");
		theCamera.setPreviewDisplay(holder);
	}

	public synchronized boolean isOpen() {
		return cam != null;
	}

	/**
	 * Closes the camera driver if still in use.
	 */
	public synchronized void closeDriver() {
		Log.i(TAG, "[285] closeDriver()");
		if (cam != null) {
			cam.release();
			cam = null;
		}
	}

	/* end new stuff */
}