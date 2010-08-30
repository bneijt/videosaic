
package net.logfish.videosaic;
/*
    Based on code from
http://groups.google.com/group/gstreamer-java/browse_thread/thread/fc0f85def933867c
*/
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

public class FrameGenerator {

        private BufferedImage currentImage = null;
        private PlayBin player = null;

        public FrameGenerator() {
                String[] args = {};
                args = Gst.init("FrameGenerator", args);
                player = new PlayBin("FrameGenerator");

                RGBDataSink.Listener listener1 = new RGBDataSink.Listener() {
                        public void rgbFrame(int w, int h, IntBuffer rgbPixels) {
                                System.out.println("    -> Got a frame !");
                                BufferedImage curImage = new BufferedImage(w, h,
BufferedImage.TYPE_INT_ARGB);
                                System.out.println("Creating image from raw pixels");
                                curImage.setRGB(0, 0, w, h, rgbPixels.array(), 0, w);
                                System.out.println("Setting currentImage");
                                currentImage = curImage;
                                System.out.println("Done");
                                try {
                                    ImageIO.write(curImage, "png", new File("out.png"));
                                } catch(Exception e) {
                                }

                        }
                };

                RGBDataSink videoSink = new RGBDataSink("rgb", listener1);
                player.setVideoSink(videoSink);
                player.setAudioSink(new FakeSink("AudioFlush"));
        }

        public FrameGenerator(File inputVideoFile) {
                this();
                this.setInputVideoFile(inputVideoFile);
        }

        public void setInputVideoFile(File inputVideoFile) {
                player.setInputFile(inputVideoFile);
        }

        public BufferedImage catchFrameAt(long time, TimeUnit unit){

                currentImage = null;

                player.pause();
                player.getState();
                player.seek(time, unit);
                player.play();
                System.out.println("Entering while loop");
                while(currentImage == null);
                System.out.println("Exiting while loop");
                player.stop();
                System.out.println("Stoppped player");

                return currentImage;
        }

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
}
