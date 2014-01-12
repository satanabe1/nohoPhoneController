package info.haxahaxa.whispers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class WhisperCoreThreadImpl implements IWhisperCore {

	private InetAddress iadd;
	private int port;

	public WhisperCoreThreadImpl(InetAddress iadd, int port) {
		this.iadd = iadd;
		this.port = port;
	}

	/**
	 * 
	 */
	@Override
	public void write(String text) {
		SendThread sendThread = new SendThread(iadd, port, text.getBytes());
		sendThread.start();
	}

	/**
	 * 
	 * @author wtnbsts
	 */
	private class SendThread extends Thread {

		private InetAddress iadd;
		private int port;
		private byte[] outdata;

		public SendThread(InetAddress iadd, int port, byte[] outdata) {
			this.iadd = iadd;
			this.port = port;
			this.outdata = outdata;
			setDaemon(true);
		}

		public void run() {
			try {
				DatagramSocket dsock = new DatagramSocket();
				DatagramPacket dpack = new DatagramPacket(outdata,
						outdata.length, iadd, port);
				dsock.send(dpack);
				dsock.close();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
