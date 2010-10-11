package nl.bneijt.videosaic;

/**
 * Represents a location of a frame. Should be a combination of the filename and the frame number.
 * @author bram
 *
 */
public class FrameLocation {
	private final String location;
	public FrameLocation(String location) {
		this.location = location;
	}
	@Override
	public String toString() {
		return location;

	}
}
