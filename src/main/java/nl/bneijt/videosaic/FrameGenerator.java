
package nl.bneijt.videosaic;
/*
    Based on code from
http://groups.google.com/group/gstreamer-java/browse_thread/thread/fc0f85def933867c
*/
import java.io.File;
import java.util.concurrent.BlockingQueue;

import org.gstreamer.Gst;
import org.gstreamer.elements.FakeSink;
import org.gstreamer.elements.PlayBin;
import org.gstreamer.elements.RGBDataSink;

public class FrameGenerator implements Runnable {

        private final PlayBin player;

        /**
         * Sets up a GstreamerPlayer and load RGB frames from the inputVideoFile and place the Frame s on the outputQueue.
         * The frames only start running when "run" is called, after that there is no stopping it.
         * @param outputQueue
         * @param inputVideoFile
         */
        public FrameGenerator(BlockingQueue<Frame> outputQueue, File inputVideoFile) {
            String[] args = {};
            args = Gst.init("FrameGenerator", args);
            player = new PlayBin("FrameGenerator");
            RGBDataSink.Listener listener1 = new BufferedImageSink(outputQueue, inputVideoFile.getName());
            RGBDataSink videoSink = new RGBDataSink("rgb", listener1);
            player.setVideoSink(videoSink);
            player.setAudioSink(new FakeSink("AudioFlush"));
            player.setInputFile(inputVideoFile);
        }
        public void run() {
            player.play();
        }
}
