package info.haxahaxa.whispers;

public interface IWhisper {

	/**
	 * ここにセットされた core を使ってサーバへ通信する
	 * 
	 * @param core
	 */
	public void setCore(IWhisperCore core);
}
