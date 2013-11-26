package info.haxahaxa.whispers;

/**
 * 各種センサーは、このインターフェースを実装したクラスのインスタンスを通してサーバへ通信を送る
 * 
 * @author wtnbsts
 */
public interface IWhisperCore {

	public void write(String text);
}
