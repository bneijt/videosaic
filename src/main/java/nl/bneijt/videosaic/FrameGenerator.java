package nl.bneijt.videosaic;

/*
 Based on code from
 http://groups.google.com/group/gstreamer-java/browse_thread/thread/fc0f85def933867c
 */
import java.io.File;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.gstreamer.Gst;
import org.gstreamer.elements.FakeSink;
import org.gstreamer.elements.PlayBin2;
import org.gstreamer.elements.RGBDataSink;

public class FrameGenerator implements Runnable {

	private final PlayBin2 player;
	private final static Logger LOG = Logger.getLogger(FrameGenerator.class);
	
	/**
	 * Sets up a GstreamerPlayer and load RGB frames from the inputVideoFile and
	 * place the Frame s on the outputQueue. The frames only start running when
	 * "run" is called, after that there is no stopping it.
	 * 
	 * @param outputQueue
	 * @param inputVideoFile
	 */
	public FrameGenerator(final BlockingQueue<Frame> outputQueue,
			File inputVideoFile) {
		String[] args = {};
		args = Gst.init("FrameGenerator", args);
		player = new PlayBin2("FrameGenerator");
		RGBDataSink.Listener listener1 = new BufferedImageSink(outputQueue);
		RGBDataSink videoSink = new RGBDataSink("rgb", listener1);
		player.setVideoSink(videoSink);
		player.setAudioSink(new FakeSink("AudioFlush"));
		player.setInputFile(inputVideoFile);
	}

	public void run() {
		LOG.debug("Starting FrameGenerator");
		player.play();
	}

	
}
