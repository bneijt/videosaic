package nl.bneijt.videosaic;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * Generate a large vector of almost uselesss bug ordered features
 * TODO Re-order feature index by growing variance!
 * @author A. Bram Neijt <bneijt@gmail.com>
 * 
 */
public class LargeIdentProducer implements IdentProducer {
	public static int[] rgb2hsv(int r, int g, int b) {

		int min; // Min. value of RGB
		int max; // Max. value of RGB
		int delMax; // Delta RGB value

		if (r > g) {
			min = g;
			max = r;
		} else {
			min = r;
			max = g;
		}
		if (b > max)
			max = b;
		if (b < min)
			min = b;

		delMax = max - min;

		float H = 0, S;
		float V = max;

		if (delMax == 0) {
			H = 0;
			S = 0;
		} else {
			S = delMax / 255f;
			if (r == max)
				H = ((g - b) / (float) delMax) * 60;
			else if (g == max)
				H = (2 + (b - r) / (float) delMax) * 60;
			else if (b == max)
				H = (4 + (r - g) / (float) delMax) * 60;
		}

		int[] hsv = { (int) (H), (int) (S * 100), (int) (V * 100) };
		return hsv;
	}

	/**
	 * Calculate real hue From http://en.wikipedia.org/wiki/HSL_and_HSV
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static int hue(final int[] rgb) {
		final int r = rgb[0];
		final int g = rgb[1];
		final int b = rgb[2];
		double alpha = 0.5 * (2 * r - g - b);
		double sqrt3by2 = 0.8660254037844386;
		double beta = sqrt3by2 * (g - b);
		double H = Math.atan2(beta, alpha);
		return (int) ((Math.PI + H) * 255.0 / (2 * Math.PI));
	}

	@Override
	public List<String> identify(BufferedImage img) {

		int intensity = MeanIdentProducer.meanIntensity(img);
		int[] meanColor = MeanColorIdentProducer.meanRGB(img);
		int[] meanHSV = LargeIdentProducer.rgb2hsv(meanColor[0], meanColor[1],
				meanColor[2]);
		int[] levels = MeanIdentProducer.quadrantMeans(img);
		int hue = LargeIdentProducer.hue(meanColor);

		// Build features: intensity, meanColor, quadrantMeans,
		List<String> features = new ArrayList<String>();
		features.add(reversedBinary(hue));
		features.add(reversedBinary(intensity));
		if (reversedBinary(intensity).length() > 10)
			System.out.println("Converting " + intensity + " resulted in " + reversedBinary(intensity));
		//features.add(String.valueOf(intensity));
		//features.add(String.valueOf(hue));

		//features.add(String.valueOf(intensity / 50)); //Split up the intensity value into [0,1,2]

		//features.add(String.valueOf(intensity)); //Split up the intensity value into [0,1,2]
		//features.add(String.valueOf(meanHSV[2]));
		//features.add(String.valueOf(meanHSV[1])); // Who cares for saturation, I
													// don't
		for (int i : levels)
			features.add(reversedBinary(i));


		//for (int i : levels)
		//	features.add(String.valueOf(i));
		return features;
	}

	/**
	 * Return the reversed binary string notation of the interger value
	 * @param hue
	 * @return
	 */
	private String reversedBinary(final int value) {
		return StringUtils.reverse(Integer.toBinaryString(value));
	}

}
