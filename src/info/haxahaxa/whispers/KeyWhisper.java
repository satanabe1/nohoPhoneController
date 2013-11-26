package info.haxahaxa.whispers;

import info.haxahaxa.Environment;
import info.haxahaxa.JsonGenerator;
import info.haxahaxa.PreferenceKey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

public class KeyWhisper implements IWhisper {
	public static final String tag = Environment.project + "#"
			+ KeyWhisper.class.getSimpleName();

	private IWhisperCore core;
	private TextView infoText;
	private Context context;
	private String volup;
	private String voldown;

	public KeyWhisper(Context context) {
		this.context = context;
		readPreference();
	}

	@Override
	public void setCore(IWhisperCore core) {
		// TODO Auto-generated method stub
		this.core = core;
	}

	public void setInfoText(TextView infoText) {
		this.infoText = infoText;
	}

	public boolean trakKeyEvent(KeyEvent event) {
		String json;
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				// write("vol down");
				json = JsonGenerator.toJson("name", Environment.device, "type",
						volup, "x", 0, "y", 0);
				write(json);
				return true;
			case KeyEvent.KEYCODE_VOLUME_UP:
				json = JsonGenerator.toJson("name", Environment.device, "type",
						voldown, "x", 0, "y", 0);
				write(json);
				return true;
			case KeyEvent.KEYCODE_MENU:
				write("menu");
				return false;
			}
		}
		return false;
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
		volup = preferences.getString(PreferenceKey.volup, "");
		voldown = preferences.getString(PreferenceKey.voldown, "");
	}
}
