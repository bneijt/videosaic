
package net.logfish.videosaic;
/*
    Based on code from
http://groups.google.com/group/gstreamer-java/browse_thread/thread/fc0f85def933867c
*/
import net.logfish.videosaic.Frame;
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

public class FrameGenerator implements Runnable {

        private final BlockingQueue<Frame> queue;
        private final PlayBin player;

        public FrameGenerator(BlockingQueue<Frame> outputQueue, File inputVideoFile) {
            this.queue = outputQueue;
            String[] args = {};
            args = Gst.init("FrameGenerator", args);
            player = new PlayBin("FrameGenerator");

            RGBDataSink.Listener listener1 = new RGBDataSink.Listener() {
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
                            queue.put(null);
                            player.stop();
                        }
                        };
            };

            RGBDataSink videoSink = new RGBDataSink("rgb", listener1);
            player.setVideoSink(videoSink);
            player.setAudioSink(new FakeSink("AudioFlush"));
            player.setInputFile(inputVideoFile);
        }
        public void run() {
            player.play();
        }

/*
    TODO: Move to test cases
        public static void main(String[] args) throws IOException {
                long start = System.currentTimeMillis();
                FrameGenerator catcher = new FrameGenerator();
                File videoFile = new File("C:\\dartmoor.ogv");
                catcher.setInputVideoFile(videoFile);
                long end = System.currentTimeMillis();
                long duration = (end - start);
                System.out.println("Image Catcher Initialization duration = " +
duration+" ms.");

                start = System.currentTimeMillis();
                BufferedImage curImage = catcher.catchFrameAt(10, TimeUnit.SECONDS);
                end = System.currentTimeMillis();
                duration = (end - start);
                System.out.println("Image Capture duration = " + duration+" ms.");
        }
        */
}
