package nl.bneijt.videosaic;

import java.awt.image.BufferedImage;

public class ZeroIdentProducer implements IdentProducer {

	@Override
	public String identify(BufferedImage img) {
		return "0.0.0.0";

	}

}
