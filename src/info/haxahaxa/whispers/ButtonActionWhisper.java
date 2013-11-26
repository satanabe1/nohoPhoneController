package info.haxahaxa.whispers;

import info.haxahaxa.Environment;
import info.haxahaxa.MainActivity;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

public class ButtonActionWhisper implements IWhisper, OnTouchListener {
	public static final String tag = Environment.project + "#"
			+ ButtonActionWhisper.class.getSimpleName();

	private TextView infoText;
	private IWhisperCore core;
	private MainActivity activity;

	public ButtonActionWhisper(MainActivity activity) {
		this.activity = activity;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (v instanceof TextView) {
				TextView textView = (TextView) v;
				activity.connect();
				write("Text:" + textView.getText().toString());
			} else if (v instanceof Button) {
				Button button = (Button) v;
				activity.connect();
				write("Button:" + button.getText().toString());
			}
		}
		return false;
	}

	@Override
	public void setCore(IWhisperCore core) {
		// TODO Auto-generated method stub
		this.core = core;
	}

	public void setInfoText(TextView infoText) {
		this.infoText = infoText;
	}

	private void write(String text) {
		if (infoText != null) {
			infoText.setText(text);
		}
		try {
			if (core != null) {
				core.write(text);
			}
		} catch (Exception ex) {
			Log.e(tag, "write", ex);
		}
	}
}
