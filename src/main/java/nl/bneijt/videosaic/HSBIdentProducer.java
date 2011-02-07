package nl.bneijt.videosaic;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class HSBIdentProducer implements IdentProducer {

	@Override
	public Identity identify(BufferedImage img) {
		final int width = 32;
		final int height = 24;
		BufferedImage scaled = Frame.scale(img, width, height);
		
		byte[] ident = new byte[width * height * 3];
		for(int x = 0; x < scaled.getWidth(); ++x)
		{
			for(int y = 0; y < scaled.getHeight(); ++y)
			{
				Color p = new Color(scaled.getRGB(x, y));
				float[] hsb = Color.RGBtoHSB(p.getRed(), p.getGreen(), p.getBlue(), null);
				assert(hsb[0] <= 1.0);
				assert(hsb[1] <= 1.0);
				assert(hsb[2] <= 1.0);
				assert(hsb[0] >= 0);
				assert(hsb[1] >= 0);
				assert(hsb[2] >= 0);
				ident[x * 3 + y * (width * 3) + 0] = (new Float(hsb[0] * 255.0)).byteValue();
				ident[x * 3 + y * (width * 3) + 1] = (new Float(hsb[1] * 255.0)).byteValue();
				ident[x * 3 + y * (width * 3) + 2] = (new Float(hsb[2] * 255.0)).byteValue();
			}
		}
		return new Identity(ident);
	}

}
