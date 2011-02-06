package nl.bneijt.videosaic;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Create a scaled grayscale vector as a matcher
 * @author A. Bram Neijt <bneijt@gmail.com>
 *
 */
public class GrayscaleIdentProducer implements IdentProducer {

	@Override
	public List<String> identify(BufferedImage img) {
		//Grayscale
		BufferedImage scaledImg = DiskFrameStorage.scale(img, 32, 24);
		BufferedImageOp op = new ColorConvertOp(
	       ColorSpace.getInstance(ColorSpace.CS_GRAY), null); 
	    BufferedImage gray = op.filter(scaledImg, null);
	    try {
			ImageIO.write(gray, "png", new File("/tmp/gray.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    int width = gray.getWidth();
	    int height = gray.getHeight();
	    //TODO Scale down
		List<String> ident = new ArrayList<String>();
		for(int y = 0; y < height; ++y)
		{
			Raster pixels = gray.getData();
			int lastPixel = pixels.getSample(0, y, 0);
			String ci = String.format("%3d", lastPixel);
			for(int x = 1; x < width; ++x)
			{
				int p = pixels.getSample(x, y, 0);
				if(p > 255)
					p = 255;
				if(p < 0)
					p = 0;
				if(p != lastPixel)
				{
					lastPixel = p;
					ci += String.valueOf(p);
				}
			}
			String lastCi = ident.size() > 0 ? ident.get(ident.size() -1) : "";
			if(! ci.equals(lastCi))
				ident.add(ci);
		}
		return ident;
	}

}
