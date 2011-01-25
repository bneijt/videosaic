
package nl.bneijt.videosaic;
import java.awt.image.BufferedImage;


class Frame extends BufferedImage {
    
    private long frameNumber;
	private final String meta;

    public Frame(
        int w, int h,
        int imageType,
        long frameNumber, String meta)
    {
        super(w, h, imageType);
        this.frameNumber = frameNumber;
		this.meta = meta;
    }
    public long frameNumber()
    {
        return this.frameNumber;
    }
	public String meta() {
		return this.meta;
	}
}

