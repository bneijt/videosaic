
package nl.bneijt.videosaic;
import nl.bneijt.videosaic.IdentProducer;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

/** Mean of quadrant ident producer
*/
class MeanIdentProducer implements IdentProducer
{
    /** Return string based ident of BufferedImage
        This will return 4 strings based on the intensity
        of their 4 quadrants
    */
    public List<String> identify(BufferedImage i)
    {
    	int[] levels = MeanIdentProducer.quadrantMeans(i);
    	String[] s = {
    				String.valueOf(levels[0]),
    				String.valueOf(levels[1]),
    				String.valueOf(levels[2]),
    				String.valueOf(levels[3]),
    		};
    	//Return the value as a string
        return Arrays.asList(s);
    }
    static public BufferedImage[] quadrants(BufferedImage i)
    {
    	int w = i.getWidth() / 2;
    	int h = i.getHeight() / 2;
    	BufferedImage a = i.getSubimage(0, 0, w, h);
    	BufferedImage b = i.getSubimage(w, 0, w, h);
    	BufferedImage c = i.getSubimage(0, h, w, h);
    	BufferedImage d = i.getSubimage(w, h, w, h);
    	BufferedImage[] r = {a,b,c,d};
    	return r;
    }
    static public int[] quadrantMeans(BufferedImage i) {
        //Split the image into 4 quadrants [a b; c d]
    	assert(i != null);
    	int w = i.getWidth() / 2;
    	int h = i.getHeight() / 2;
    	BufferedImage a = i.getSubimage(0, 0, w, h);
    	BufferedImage b = i.getSubimage(w, 0, w, h);
    	BufferedImage c = i.getSubimage(0, h, w, h);
    	BufferedImage d = i.getSubimage(w, h, w, h);
    	
        //Find the mean intensity of each quadrant
    	int[] levels = {meanIntensity(a), meanIntensity(b), meanIntensity(c), meanIntensity(d)};
    	return levels;
	}
	/**
     * Return the mean intensity of the BufferedImage i using summed RGB value of all pixels
     * @param i
     * @return
     */
    static public int meanIntensity(BufferedImage i)
    {
    	long sum = 0;
    	final int w = i.getWidth();
    	final int h = i.getHeight();
    	for(int x = 0; x < w; ++x)
    		for(int y = 0; y < h; ++y)
    		{
    			int pixel = i.getRGB(x, y);
    			//int alpha = (pixel >> 24) & 0xff;
    		    int red = (pixel >> 16) & 0xff;
    		    int green = (pixel >> 8) & 0xff;
    		    int blue = (pixel) & 0xff;

    			sum += (red + green + blue) / 3;
    		}
    	return (int) (sum / (w * h)); 
    }
}


