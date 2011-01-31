package nl.bneijt.videosaic;

import java.io.File;

/**
 * Represents a location of a frame. Should be a combination of the filename and
 * the frame number.
 * 
 * @author bram
 * 
 */
public class FrameLocation {
	private final String location;
	private final long frameNumber;
	public FrameLocation(String location) {
		if (location.contains(":")) {
			this.frameNumber = Integer.parseInt(location.substring(location
					.lastIndexOf(':') + 1));
			this.location = location.substring(0, location.lastIndexOf(':'));
		} else{
			this.location = location;
			this.frameNumber = 0;
		}
	}

	public FrameLocation(String pathname, long frameNumber) {
		this.location = pathname;
		this.frameNumber = frameNumber;
	}

	@Override
	public String toString() {
		return String.format("%s:%d", location, frameNumber);
	}

	public File getFile() {
		return new File(this.location);
	}

	public long getFrameNumber() {
		return this.frameNumber;
	}

}
