package info.haxahaxa.whispers;

import info.haxahaxa.Environment;
import info.haxahaxa.JsonGenerator;
import info.haxahaxa.PreferenceKey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.TextView;

public class GestureWhisper implements OnGestureListener, OnDoubleTapListener,
		IWhisper {
	public static final String tag = Environment.project + "#"
			+ GestureWhisper.class.getSimpleName();

	private IWhisperCore core;
	private TextView infoText;
	private Context context;
	private GestureDetector detector;
	private int scale;

	/**
	 * pref_hand
	 */
	private String hand;

	/**
	 * pref_hand
	 */
	private String mode;

	/**
	 * 
	 * pref_doubletap
	 */
	private String doubletap;

	public GestureWhisper(Context context) {
		this.context = context;
		detector = new GestureDetector(context, this);
		readPreference();
	}

	public boolean invokeDetector(MotionEvent event) {
		return detector.onTouchEvent(event);
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public void setInfoText(TextView infoText) {
		this.infoText = infoText;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		int[] xy = scaleTi(e);
		String json = JsonGenerator.toJson("name", Environment.device, "type",
				doubletap, "x", xy[0], "y", xy[1]);
		write(json);
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		int[] xy = scaleTi(e);
		String json = JsonGenerator.toJson("name", Environment.device, "type",
				doubletap, "x", xy[0], "y", xy[1]);
		write(json);
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		int[] xy = scaleTi(e);
		String json = JsonGenerator.toJson("name", Environment.device, "type",
				"Dend", "x", xy[0], "y", xy[1]);
		write(json);
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		int[] xy = scaleTi(e);
		String json = JsonGenerator.toJson("name", Environment.device, "type",
				"Dstart", "x", xy[0], "y", xy[1]);
		write(json);
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		int[] xy = scaleTi(e1);
		int[] xy2 = scaleTi(e2);
		String json = JsonGenerator.toJson("name", Environment.device, "type",
				"onfling", "x", xy[0], "y", xy[1]);
		String json2 = JsonGenerator.toJson("name", Environment.device, "type",
				"Dend", "x", xy2[0], "y", xy2[1]);
		Log.v(tag, json);
		Log.v(tag, json2);
		write(json2);
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onScroll(MotionEvent e, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		// int[] xy1 = scaleTi(e);
		int[] xy2 = scaleTi(e2);

		// String json1 = JsonGenerator.toJson("name", Environment.device,
		// "type",
		// "scrollF", "x", xy1[0], "y", xy1[1]);
		String json2 = JsonGenerator.toJson("name", Environment.device, "type",
				mode, "x", xy2[0], "y", xy2[1]);
		write(json2);
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		int[] xy = scaleTi(e);
		String json = JsonGenerator.toJson("name", Environment.device, "type",
				"Dend", "x", xy[0], "y", xy[1]);
		write(json);
		return true;
	}

	@Override
	public void setCore(IWhisperCore core) {
		// TODO Auto-generated method stub
		this.core = core;
	}

	private void write(String text) {
		if (infoText != null) {
			infoText.setText(text);
		}
		try {
			core.write(text);
		} catch (Exception ex) {
			Log.e(tag, "write", ex);
		}
	}

	@SuppressLint("DefaultLocale")
	public void readPreference() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String rateStr = preferences.getString(PreferenceKey.scale, "100%");
		scale = Integer.parseInt(rateStr.substring(0, rateStr.length() - 1));

		hand = preferences.getString(PreferenceKey.hand, "").toLowerCase();
		if (hand.equals("left")) {
			mode = "turn";
		} else if (hand.equals("right")) {
			mode = "move";
		}
		doubletap = preferences.getString(PreferenceKey.doubletap, "");
	}

	private int[] scaleTi(MotionEvent e) {
		int[] values = new int[2];
		values[0] = (int) (e.getX() * scale / 100);
		values[1] = (int) (e.getY() * scale / 100);
		return values;
	}
}
