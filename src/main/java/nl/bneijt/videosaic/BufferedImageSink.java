
package nl.bneijt.videosaic;
/*
    Based on code from
http://groups.google.com/group/gstreamer-java/browse_thread/thread/fc0f85def933867c
 */
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.util.concurrent.BlockingQueue;

import org.gstreamer.elements.RGBDataSink;

/**
 * Implements an RGBDataSink.Listener and creates Frame s from each of the rgbPixel buffers that come in.
 * The bufferes are placed on the queue
 * @author A. Bram Neijt <bneijt@gmail.com>
 *
 */
class BufferedImageSink implements RGBDataSink.Listener {
	private final BlockingQueue<Frame> queue;
	private final String meta;

	public BufferedImageSink(BlockingQueue<Frame> queue, String meta)
	{
		super();
		
		this.queue = queue;
		this.meta = meta;
	}
	private long frameNumber = 0;
	public void rgbFrame(int w, int h, IntBuffer rgbPixels) {
		Frame frame = new Frame(
				w, h,
				BufferedImage.TYPE_INT_ARGB,
				frameNumber++, meta);
		frame.setRGB(0, 0, w, h, rgbPixels.array(), 0, w);
		try {
			queue.put(frame);
		} catch (java.lang.InterruptedException e){
			//player.stop();
			//TODO LOG error/warning
		}
	};


}

