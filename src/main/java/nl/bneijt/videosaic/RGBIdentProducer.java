package nl.bneijt.videosaic;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class RGBIdentProducer implements IdentProducer {

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
				ident[x * 3 + y * (width * 3) + 0] = (new Integer(p.getRed())).byteValue();
				ident[x * 3 + y * (width * 3) + 1] = (new Integer(p.getGreen())).byteValue();
				ident[x * 3 + y * (width * 3) + 2] = (new Integer(p.getBlue())).byteValue();
			}
		}
		return new Identity(ident);
	}

}
