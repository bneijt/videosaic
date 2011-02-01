package nl.bneijt.videosaic;

import java.awt.image.BufferedImage;
import java.util.List;

import com.google.common.collect.Lists;

public class ZeroIdentProducer implements IdentProducer {

	@Override
	public List<String> identify(BufferedImage img) {
		return Lists.newArrayList("0", "0");

	}

}
