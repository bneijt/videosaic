
package nl.bneijt.videosaic;
import java.awt.image.BufferedImage;


class Frame extends BufferedImage {
    
    private long frameNumber;

    public Frame(
        int w, int h,
        int imageType,
        long frameNumber)
    {
        super(w, h, imageType);
        this.frameNumber = frameNumber;
    }
    public long frameNumber()
    {
        return this.frameNumber;
    }
}

