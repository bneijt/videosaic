package nl.bneijt.videosaic;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Utility class to handle allocating and storing a collection of sub-frames.
 * This contains the matching to find the best frame
 * 
 * @author A. Bram Neijt <bneijt@gmail.com>
 * 
 */
public class SubFrameStorage {
	private byte[][] frames; // /Each frame with the last byte being the index
	// of the name of the source file in the names
	// array
	private final int width;
	private final int height;

	public SubFrameStorage(List<File> subFiles) throws InterruptedException {
		width = 32;
		height = 24;
		System.out.println("Opening superframeserver with " + subFiles);
		// Load each frame of each file into memory
		int frameCount = 0;
		for (File targetFile : subFiles) {
			// Count frames in a terribly stupid fashion
			BlockingQueue<Frame> queue = App.frameQueue(targetFile);
			Frame f = queue.poll(10, TimeUnit.SECONDS);
			while (f != null) {
				frameCount = f.frameNumber();
				f = queue.poll(5, TimeUnit.SECONDS);
			}
			frameCount += 1; // Add one for the 0-index of frameNumber
		}
		// Allocate
		frames = new byte[frameCount][];
		int frameIdx = 0;
		// Process
		for (File targetFile : subFiles) {
			// Start loading frames
			BlockingQueue<Frame> queue = App.frameQueue(targetFile);
			Frame f = queue.poll(10, TimeUnit.SECONDS);
			while (f != null) {

				BufferedImage scaled = Frame.scale(f, width, height);
				byte[] ident = SubFrameStorage.bytesFromBufferedImage(scaled);

				frames[frameIdx] = ident;
				frameIdx += 1;
				// Next frame
				f = queue.poll(2, TimeUnit.SECONDS);// TODO UGLY CODE!
			}
		}
	}
	static public byte[] bytesFromBufferedImage(BufferedImage scaled) {
		// Create HSB byte array
		final int width = scaled.getWidth();
		final int height = scaled.getHeight();
		byte[] ident = new byte[width * height * 3];
		for (int x = 0; x < scaled.getWidth(); ++x) {
			for (int y = 0; y < scaled.getHeight(); ++y) {
				Color p = new Color(scaled.getRGB(x, y));
				float[] hsb = Color.RGBtoHSB(p.getRed(), p.getGreen(),
						p.getBlue(), null);
				float h = hsb[0];
				float s = hsb[1];
				float b = hsb[2];
				              
				assert (hsb[0] <= 1.0);
				assert (hsb[1] <= 1.0);
				assert (hsb[2] <= 1.0);
				assert (hsb[0] >= 0);
				assert (hsb[1] >= 0);
				assert (hsb[2] >= 0);
				System.out.println("H" + h + "s"+s+"b"+b);
				ident[(x + y * width) * 3 + 0] = float2byte(h);
				ident[(x + y * width) * 3 + 1] = float2byte(s);
				ident[(x + y * width) * 3 + 2] = float2byte(b);
			}
		}
		return ident;
	}
	/**
	 * Convert 0-1.0 float into Byte.MIN_VALUE-Byte.MAX_VALUE byte
	 * @param f
	 * @return
	 */
	private static byte float2byte(float f) {
		assert(Byte.MIN_VALUE < 0);
		assert(Byte.MAX_VALUE > 0);
		return new Float(f * (float)(Byte.MAX_VALUE - Byte.MIN_VALUE) + (float)Byte.MIN_VALUE).byteValue();
		
	}
	public byte[] block() {
		return new byte[width * height * 3];
	}

	public byte[] bestMatchFor(byte[] query) {
		byte[] best = frames[0];
		long bestDistance = distance(best, query);
		for (byte[] frame : frames) {
			long distance = distance(best, query);//TODO Optimize by adding bestDistance ??
			if(distance < bestDistance)
			{
				best = frame;
				bestDistance = distance;
			}
		}
		//TODO Add logging
		return best;
	}

	private long distance(byte[] a, byte[] b) {
		long d = 0;
		assert(a.length == b.length);
		for(int i = 0; i < a.length; ++i)
		{
			d += Math.abs((int)a[i] - (int)b[i]);
		}
		return d;
		
	}
	public static BufferedImage bufferedImageFromBytes(byte[] subFrameBytes, final int width, final int height) {
		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		if(subFrameBytes.length != width * height * 3)
			throw new RuntimeException("Wrong size frmae fgiansgd");
		for(int pixel = 0; pixel < subFrameBytes.length / 3; ++pixel)
		{
			int i = pixel * 3;
			
			float h = byte2float(subFrameBytes[i]);
			float s = byte2float(subFrameBytes[i + 1]);
			float b = byte2float(subFrameBytes[i + 2]);
			System.out.println("BYTE H" + subFrameBytes[i] + "s"+subFrameBytes[i+1]+"b"+subFrameBytes[i+2]);
			System.out.println("OUT H" + h + "s"+s+"b"+b);
			int rgb = Color.HSBtoRGB(h, s, b);
			int x = pixel % width;
			int y = pixel / width;
			bi.setRGB(x, y, rgb);
		}
		return bi;
	}
	/**
	 * Convert a byte to a [0.0-1.0] float
	 * @param b
	 * @return
	 */
	private static float byte2float(byte b) {
		assert(Byte.MIN_VALUE < 0);
		return (float) ((int) b - (int) Byte.MIN_VALUE) / (float) ((int) Byte.MAX_VALUE - (int) Byte.MIN_VALUE);
	}

}