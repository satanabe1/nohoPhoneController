package info.haxahaxa;

import info.haxahaxa.whispers.ButtonActionWhisper;
import info.haxahaxa.whispers.GestureWhisper;
import info.haxahaxa.whispers.KeyWhisper;
import info.haxahaxa.whispers.SensingWhisper;
import info.haxahaxa.whispers.WhisperConnector;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final String tag = Environment.project + "#"
			+ MainActivity.class.getSimpleName();

	private TextView azimuthText;
	private TextView pitchText;
	private TextView rollText;
	private TextView infoText;
	private TextView gestureInfoText;
	private TextView keyInfoText;
	private Button cavButton;

	private SensingWhisper sensingWhisper;
	private GestureWhisper gestureWhisper;
	private KeyWhisper keyWhisper;
	private ButtonActionWhisper buttonActionWhisper;
	private WhisperConnector connector;

	private SharedPreferences preferences;

	private String serverName;
	private String serverPort;
	private int scale;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (preferences.getAll().isEmpty()) {
			PreferenceManager.setDefaultValues(this, R.xml.pref_general, true);
		}

		setContentView(R.layout.activity_main);
		findViews();

		sensingWhisper = new SensingWhisper(this, azimuthText, rollText,
				pitchText);
		sensingWhisper.setInfoText(infoText);

		gestureWhisper = new GestureWhisper(this);
		gestureWhisper.setInfoText(gestureInfoText);

		keyWhisper = new KeyWhisper(this);
		keyWhisper.setInfoText(keyInfoText);

		buttonActionWhisper = new ButtonActionWhisper(this);
		buttonActionWhisper.setInfoText(keyInfoText);

		cavButton.setOnTouchListener(buttonActionWhisper);
	}

	@Override
	public void onResume() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onResume();

		connect();
	}

	public void connect() {
		readPreference();
		// printPreference();
		try {
			connector = new WhisperConnector(sensingWhisper, gestureWhisper,
					keyWhisper, buttonActionWhisper);
			connector.execute(serverName, serverPort);
		} catch (Exception ex) {
			Log.e(tag, "connect", ex);
		}

		sensingWhisper.resume();
		sensingWhisper.readPreference();

		gestureWhisper.setScale(scale);
		gestureWhisper.readPreference();

		keyWhisper.readPreference();
	}

	@Override
	public void onPause() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		sensingWhisper.pause();
		connector.dispose();

		super.onPause();
	}

	protected void findViews() {
		azimuthText = (TextView) findViewById(R.id.azimuth);
		pitchText = (TextView) findViewById(R.id.pitch);
		rollText = (TextView) findViewById(R.id.roll);
		infoText = (TextView) findViewById(R.id.info);
		gestureInfoText = (TextView) findViewById(R.id.gestureInfo);
		keyInfoText = (TextView) findViewById(R.id.keyinfo);
		cavButton = (Button) findViewById(R.id.cavButton);
	}

	private void readPreference() {
		serverName = preferences.getString(PreferenceKey.servername,
				"localhost");
		serverPort = preferences.getString(PreferenceKey.serverport, "11000");
		String rateStr = preferences.getString(PreferenceKey.scale, "100%");
		scale = Integer.parseInt(rateStr.substring(0, rateStr.length() - 1));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureWhisper.invokeDetector(event)) {
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (keyWhisper.trakKeyEvent(event)) {
			return true;
		}
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@SuppressWarnings("unused")
	private void write(String text) {
		if (infoText != null) {
			infoText.setText(text);
		}
	}
}
