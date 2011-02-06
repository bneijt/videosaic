
package nl.bneijt.videosaic;
import java.awt.Graphics2D;
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
	BufferedImage scale(final int width, final int height)
	{
		return Frame.scale(this, width, height);
	}
    public long frameNumber()
    {
        return this.frameNumber;
    }
	public static BufferedImage scale(BufferedImage img, int width, int height) {
		BufferedImage scaledImage = new BufferedImage(width, height, img.getType());
		Graphics2D graphics2D = scaledImage.createGraphics();
		graphics2D.drawImage(img, 0, 0, width, height, null);
		graphics2D.dispose();
		return scaledImage;
	}
}

