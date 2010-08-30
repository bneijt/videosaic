package net.logfish.videosaic;
import java.util.concurrent.{BlockingQueue,SynchronousQueue};
import scala.collection.mutable.ArrayOps;
import org.gstreamer.Gst;
import java.io.File;
import net.logfish.videosaic.{FrameGenerator, Frame};
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

object Application
{
  def main(args: Array[String]) = {
    //TODO Use real option parsing library (maybe paulp's optional)
    if(args.length < 2) {
        printf("Usage: %s <output> <input <input <input ...>>>", args(0));
    }
    val files: Array[File] = args.map {new File(_)};
    printf("Files: %s\n", files.mkString)
    val outputVideo = files.head
    if(outputVideo.exists()) {
        printf("Output video already exists, exiting");
        System.exit(1);
    }
    val inputVideos = files.tail
    printf("Output: %s\nInput: %s", outputVideo.toString, inputVideos.mkString)
    //Create index of output movie
    val testFile = inputVideos.head
    var queue: BlockingQueue[Frame] = new SynchronousQueue[Frame]();
    val fg = new FrameGenerator(queue, testFile);
    fg.run();
    while(true)
    {
        val f: Frame = queue.take();
        println(f.frameNumber());
    }
    //Create index of output video;s
    //Match input movie parts to output movie
    //Create render definition
    //Render output movie
  }
}



/* http://groups.google.com/group/gstreamer-java/browse_thread/thread/fc0f85def933867c

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.concurrent.TimeUnit;

import org.gstreamer.Gst;
import org.gstreamer.elements.PlayBin;
import org.gstreamer.elements.RGBDataSink;

public class VideoFrameCatcher {

        private BufferedImage currentImage = null;
        private PlayBin player = null;

        public VideoFrameCatcher() {
                String[] args = {};
                args = Gst.init("VideoFrameCatcher", args);
                player = new PlayBin("VideoFrameCatcher");

                RGBDataSink.Listener listener1 = new RGBDataSink.Listener() {
                        public void rgbFrame(int w, int h, IntBuffer rgbPixels) {
                                System.out.println("    -> Got a frame !");
                                BufferedImage curImage = new BufferedImage(w, h,
BufferedImage.TYPE_INT_ARGB);
                                curImage.setRGB(0, 0, w, h, rgbPixels.array(), 0, w);
                                currentImage = curImage;
                        }
                };

                RGBDataSink videoSink = new RGBDataSink("rgb", listener1);
                player.setVideoSink(videoSink);
        }

        public VideoFrameCatcher(File inputVideoFile) {
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

        while(currentImage==null);
        player.stop();

                return currentImage;
        }

        public static void main(String[] args) throws IOException {
                long start = System.currentTimeMillis();
                VideoFrameCatcher catcher = new VideoFrameCatcher();
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
