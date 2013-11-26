package info.haxahaxa.whispers;

import info.haxahaxa.Environment;

import java.net.InetAddress;
import java.net.Socket;

import android.os.AsyncTask;
import android.util.Log;

public class WhisperConnector extends AsyncTask<String, IWhisper, String> {

	private static final String tag = Environment.project + "#"
			+ WhisperConnector.class.getSimpleName();
	private Socket sock;
	// private OutputStream out;
	private IWhisper[] whispers;

	public WhisperConnector(IWhisper... whispers) {
		this.whispers = whispers;
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		try {
			// sock = new Socket();
			// sock.connect(new InetSocketAddress(params[0], Integer
			// .parseInt(params[1])));

			// out = new DataOutputStream(sock.getOutputStream());
			IWhisperCore whisperCore = new WhisperCoreThreadImpl(
					InetAddress.getByName(params[0]),
					Integer.parseInt(params[1]));
			for (IWhisper whisper : whispers) {
				whisper.setCore(whisperCore);
			}
			return "connect[" + params[0] + ":" + params[1] + "]";
		} catch (Exception ex) {
			Log.e(tag, "doInBackground", ex);
			return ex.getMessage();
		}
	}

	public void dispose() {

		try {
			for (IWhisper whisper : whispers) {
				whisper.setCore(null);
			}
		} catch (Exception ex) {
			Log.e(tag, "pause", ex);
		}

		if (sock != null) {
			try {
				sock.close();
				sock = null;
			} catch (Exception ex) {
				Log.e(tag, "pause", ex);
			}
		}
	}
}
