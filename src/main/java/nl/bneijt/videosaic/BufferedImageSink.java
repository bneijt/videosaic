
package nl.bneijt.videosaic;
/*
    Based on code from
http://groups.google.com/group/gstreamer-java/browse_thread/thread/fc0f85def933867c
 */
import nl.bneijt.videosaic.Frame;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.concurrent.TimeUnit;

import org.gstreamer.Gst;
import org.gstreamer.elements.PlayBin;
import org.gstreamer.elements.FakeSink;
import org.gstreamer.elements.RGBDataSink;
import java.util.concurrent.BlockingQueue;

class BufferedImageSink implements RGBDataSink.Listener {
	private final BlockingQueue<Frame> queue;

	public BufferedImageSink(BlockingQueue<Frame> queue)
	{
		super();
		this.queue = queue;
	}
	private long frameNumber = 0;
	public void rgbFrame(int w, int h, IntBuffer rgbPixels) {
		Frame frame = new Frame(
				w, h,
				BufferedImage.TYPE_INT_ARGB,
				frameNumber++);
		frame.setRGB(0, 0, w, h, rgbPixels.array(), 0, w);
		try {
			queue.put(frame);
		} catch (java.lang.InterruptedException e){
			//player.stop();
			//TODO LOG error/warning
		}
	};


}

