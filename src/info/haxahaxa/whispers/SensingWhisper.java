package info.haxahaxa.whispers;

import info.haxahaxa.Environment;
import info.haxahaxa.JsonGenerator;
import info.haxahaxa.PreferenceKey;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

/**
 * 傾きを拾って通信する子
 * 
 * @author wtnbsts
 */
public class SensingWhisper implements IWhisper, SensorEventListener {

	private static final String tag = Environment.project + "#"
			+ SensingWhisper.class.getSimpleName();

	protected final static double RAD2DEG = 180 / Math.PI;
	private SensorManager sensorManager;

	private float[] rotationMatrix = new float[9];
	private float[] gravity = new float[3];
	private float[] geomagnetic = new float[3];
	private float[] attitude = new float[3];

	private IWhisperCore core;

	private TextView azimText, rollText, pitchText;
	private TextView infoText;
	private Context context;

	private String hand;
	@SuppressWarnings("unused")
	private String mode;

	private int baseYaw = 0;

	public SensingWhisper(Context context, TextView azim, TextView roll,
			TextView pitch) {
		this.azimText = azim;
		this.rollText = roll;
		this.pitchText = pitch;
		this.context = context;

		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		readPreference();
	}

	@Override
	public void setCore(IWhisperCore core) {
		// TODO Auto-generated method stub
		this.core = core;
	}

	public void pause() {
		sensorManager.unregisterListener(this);
	}

	public void resume() {
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_NORMAL);
		baseYaw = lastYaw;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	private int mask = -2;
	private int yR = 0;
	private int zR = 0;
	private int xR = 0;
	private int lastYaw;

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub

		switch (event.sensor.getType()) {
		case Sensor.TYPE_MAGNETIC_FIELD:
			geomagnetic = event.values.clone();
			break;
		case Sensor.TYPE_ACCELEROMETER:
			gravity = event.values.clone();
			break;
		}

		if (geomagnetic != null && gravity != null) {

			SensorManager.getRotationMatrix(rotationMatrix, null, gravity,
					geomagnetic);

			SensorManager.getOrientation(rotationMatrix, attitude);

			int yaw = (int) (attitude[0] * RAD2DEG);
			if (yaw != 0) {
				lastYaw = yaw;
			}
			yaw -= baseYaw;
			int pitch = (int) (attitude[1] * RAD2DEG);
			int roll = (int) (attitude[2] * RAD2DEG);

			roll &= mask;
			pitch &= mask;
			yaw &= mask;

			if (xR == roll && yR == yaw * -1 && zR == pitch) {
				return;
			}
			xR = roll;
			yR = yaw * -1;
			zR = pitch;

			// yR -= baseYaw;
			// zR = (int) (attitude[1] * RAD2DEG);
			// xR = (int) (attitude[2] * RAD2DEG);
			//
			azimText.setText("azim :" + yR);
			pitchText.setText("pitch:" + zR);
			rollText.setText("roll :" + xR);

			try {
				String json = JsonGenerator.toJson("name", Environment.device,
						"type", hand, "x", xR, "y", yR, "z", zR);
				write(json);
			} catch (Exception ex) {
				Log.e(tag, "Socket out", ex);
			}
		}
	}

	public void setInfoText(TextView view) {
		infoText = view;
	}

	@SuppressLint("DefaultLocale")
	public void readPreference() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		hand = preferences.getString(PreferenceKey.hand, "").toLowerCase();
		if (hand.equals("left")) {
			mode = "turn";
		} else if (hand.equals("right")) {
			mode = "move";
		}
	}

	private void write(String text) {
		if (infoText != null) {
			infoText.setText(tag + ":" + text);
		}
		if (core != null) {
			try {
				core.write(text);
				Log.v(tag, text);
			} catch (Exception ex) {
				Log.e(tag, "write", ex);
			}
		}
	}
}
