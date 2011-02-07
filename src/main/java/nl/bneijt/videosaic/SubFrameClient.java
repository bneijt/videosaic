package nl.bneijt.videosaic;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class SubFrameClient {
	private static final Logger LOG = Logger.getLogger(SubFrameClient.class);
	private Socket[] sockets;

	public SubFrameClient(String[] hosts) throws UnknownHostException, IOException {
		sockets = new Socket[hosts.length];
		for (int i = 0; i < sockets.length; ++i) {
			sockets[i] = new Socket(hosts[i], 8080);
		}

	}

	public static void main(String[] args) throws UnknownHostException,
			IOException, InterruptedException {
		// Open the target file and start looking for frames at the given
		// servers
		File targetFile = new File(args[args.length - 1]);
		SubFrameClient sfc = new SubFrameClient(Arrays.copyOf(args,
				args.length - 1));

		BlockingQueue<Frame> queue = App.frameQueue(targetFile);
		Frame f = queue.poll(10, TimeUnit.SECONDS);

		while (f != null) {
			BufferedImage outputFrame = new BufferedImage(App.WIDTH,
					App.HEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics2D outputGraphics = outputFrame.createGraphics();
			int w = f.getWidth() / App.N_TILES_PER_SIDE;
			int h = f.getHeight() / App.N_TILES_PER_SIDE;

			for (int yOffset = 0; yOffset < f.getHeight(); yOffset += h) {
				for (int xOffset = 0; xOffset < f.getWidth(); xOffset += w) {
					byte[] query = SubFrameStorage.bytesFromBufferedImage(f
							.getSubimage(xOffset, yOffset, w, h));
					byte[] subFrameBytes = sfc.bestMatchFor(query);
					BufferedImage subframe = SubFrameStorage
							.bufferedImageFromBytes(subFrameBytes, w, h);
					outputGraphics.drawImage(subframe, xOffset, yOffset, null);
				}
			}
			outputGraphics.dispose();

			// Collapse match into file on disk
			File outputFile = new File(String.format("/tmp/image_%05d.png", f
					.frameNumber()));
			LOG.debug(String.format("Outputting to %s", outputFile.toString()));
			ImageIO.write(outputFrame, "png", outputFile);
			// Next frame
			f = queue.poll(2, TimeUnit.SECONDS);// TODO UGLY CODE!
		}
	}

	private byte[] bestMatchFor(byte[] query) {
		System.err.println("BEST MATCH NOT IMPLEMENTED");
		return query;

	}

}
