package nl.bneijt.videosaic;

public class EchoFrameStorage extends SubFrameStorage {
	@Override
	public byte[] bestMatchFor(byte[] query) {
		return query;
	}
}
