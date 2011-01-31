package nl.bneijt.videosaic;

import java.awt.image.BufferedImage;

public class MeanColorIdentProducer implements IdentProducer {
	@Override
    public String identify(BufferedImage i){
    	
        //Split the image into 4 quadrants [a b; c d]
    	assert(i != null);
    	long sum[] = {0,0,0};
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

    			sum[0] += red;
    			sum[1] += green;
    			sum[2] += blue;
    		}
    	sum[0] = sum[0] / (w*h);
    	sum[1] = sum[1] / (w*h);
    	sum[2] = sum[2] / (w*h);
    	return String.format("%d.%d.%d", sum);
	}


}
