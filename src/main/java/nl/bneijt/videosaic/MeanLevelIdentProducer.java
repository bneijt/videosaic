package nl.bneijt.videosaic;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public class MeanLevelIdentProducer implements IdentProducer {

	private final MeanIdentProducer meanIdentProducer;

	public MeanLevelIdentProducer()
	{
		this.meanIdentProducer = new MeanIdentProducer();
	}
	public static int minValue(int[] numbers){  
		Arrays.sort(numbers);  
		return numbers[0];  
	}  
	@Override
	public List<String> identify(BufferedImage img) {
		int[] levels = meanIdentProducer.quadrantMeans(img);
		
		int min = minValue(levels);
		String[] l = {
				String.format("%d", levels[0] - min),
				String.format("%d", levels[1] - min),
				String.format("%d", levels[2] - min),
				String.format("%d", levels[3] - min),
		};
        return  Arrays.asList(l);
	}

}
