package nl.bneijt.videosaic;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.jetty.util.log.Log;

/**
 * Utility class to handle allocating and storing a collection of sub-frames.
 * This contains the matching to find the best frame
 * 
 * @author A. Bram Neijt <bneijt@gmail.com>
 * 
 */
public class SubFrameStorage {
	private List<byte[]> frames; // /Each frame with the last byte being the index
	// of the name of the source file in the names
	// array
	private final int width;
	private final int height;

	static final Logger LOG = Logger.getLogger(SubFrameStorage.class);
	
	public SubFrameStorage() {
		width = 32;
		height = 24;
		frames = new ArrayList<byte[]>();
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
		final float range = Byte.MAX_VALUE - Byte.MIN_VALUE;
		final float val = f * range + (float)Byte.MIN_VALUE;
		byte r = new Float(val).byteValue();
		return r;
	}
	public byte[] block() {
		byte[] b = new byte[width * height * 3];
		Arrays.fill(b, 0, b.length, (byte)0);
		return b;
	}

	public byte[] bestMatchFor(byte[] query) {
		if(frames.size() == 0)
		{
			Log.warn("NO FRAMES IN STORAGE, RETURNING EMPTY");
			return this.block();
		}
		byte[] best = frames.get(0);
		long bestDistance = distance(best, query);
		for (byte[] frame : frames)
		{
			long distance = distance(frame, query);
			if(distance < bestDistance)
			{
				best = frame;
				bestDistance = distance;
			}
		}
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
			throw new RuntimeException("Wrong number of sub-frame bytes for width/height combo");
		for(int pixel = 0; pixel < subFrameBytes.length / 3; ++pixel)
		{
			int i = pixel * 3;
			
			float h = byte2float(subFrameBytes[i]);
			float s = byte2float(subFrameBytes[i + 1]);
			float b = byte2float(subFrameBytes[i + 2]);
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
	public void add(byte[] frame)
	{
		frames.add(frame);
	}
	public void loadFiles(List<File> subFiles) throws InterruptedException {
		// Load each frame of each file into memory
		for (File targetFile : subFiles) {
			loadFile(targetFile);
		}
	}
	public void loadFile(File targetFile) throws InterruptedException {
		if(targetFile.getName().endsWith(".bin"))
		{
			LOG.info("Loading binary file: " + targetFile);
			try {
				FileInputStream in = new FileInputStream(targetFile);
				while(true)
				{
					byte[] block = this.block();
					int read = in.read(block);
					if(read < block.length)
						break;
					frames.add(block);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		LOG.info("Loading video: " + targetFile);
		// Start loading frames
		BlockingQueue<Frame> queue = App.frameQueue(targetFile);
		Frame f = queue.poll(10, TimeUnit.SECONDS);
		while (f != null) {

			BufferedImage scaled = Frame.scale(f, width, height);
			byte[] ident = SubFrameStorage.bytesFromBufferedImage(scaled);

			frames.add(ident);
			// Next frame
			f = queue.poll(2, TimeUnit.SECONDS);// TODO UGLY CODE!
		}
		Log.info("Done loading " + targetFile);		
	}
	public void dumpFile(File file) throws IOException {
		FileOutputStream f = new FileOutputStream(file);
		for(byte[] frame : frames)
		{
			f.write(frame);
		}
		f.flush();
		f.close();
	}

}