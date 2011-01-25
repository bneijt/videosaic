
package nl.bneijt.videosaic;
import java.awt.image.BufferedImage;

/**
 * BufferedImage with a framenumber added
 * @author A. Bram Neijt <bneijt@gmail.com>
 *
 */
class Frame extends BufferedImage {
    
    private long frameNumber;

    public boolean valid;//Hack to notify the end of the queue

    public Frame(
        int w, int h,
        int imageType,
        long frameNumber)
    {
        super(w, h, imageType);
    	valid = true;
        this.frameNumber = frameNumber;
    }
    public long frameNumber()
    {
        return this.frameNumber;
    }
}

