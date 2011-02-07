package nl.bneijt.videosaic;

/*
 Based on code from
 http://groups.google.com/group/gstreamer-java/browse_thread/thread/fc0f85def933867c
 */
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.IntBuffer;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.gstreamer.elements.RGBDataSink;

/**
 * Implements an RGBDataSink.Listener and creates Frame s from each of the
 * rgbPixel buffers that come in. The bufferes are placed on the queue
 * 
 * @author A. Bram Neijt <bneijt@gmail.com>
 * 
 */
class BufferedImageSink implements RGBDataSink.Listener {
	private final BlockingQueue<Frame> queue;
	static final Logger LOG = Logger.getLogger(BufferedImageSink.class);

	public BufferedImageSink(BlockingQueue<Frame> queue) {
		super();

		this.queue = queue;
	}

	private int frameNumber = 0;

	public void rgbFrame(int w, int h, IntBuffer rgbPixels) {
		Frame frame = new Frame(w, h, BufferedImage.TYPE_INT_BGR, //TODO fix this using something smart?
				frameNumber++);
		int[] pixels = ((DataBufferInt)frame.getRaster().getDataBuffer()).getData();
        rgbPixels.get(pixels, 0, w * h);
		try {
			queue.put(frame);
		} catch (java.lang.InterruptedException e1) {
			// player.stop();
			LOG.info("Interrupted queue PUT for frame");
		}
	};

}
