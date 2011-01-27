package nl.bneijt.videosaic;

import java.awt.image.BufferedImage;
import java.util.Arrays;

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
	public String identify(BufferedImage img) {
		int[] levels = meanIdentProducer.quadrantMeans(img);
		
		int min = minValue(levels);
		
        return (levels[0] - min) + "." + (levels[1] - min) + "." + (levels[2] - min) + "." + (levels[3] - min);
	}

}
