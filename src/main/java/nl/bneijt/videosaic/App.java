package nl.bneijt.videosaic;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.mongodb.MongoException;

/**
 * The main application. Currently way to large.
 * 
 * @author A. Bram Neijt <bneijt@gmail.com>
 * 
 */
class App {
	static final int N_TILES_PER_SIDE = 10;
	static final int WIDTH = 320;
	static final int HEIGHT = 240;

	static final Logger LOG = Logger.getLogger(App.class);
	static final DiskFrameStorage frameStorage = new DiskFrameStorage(WIDTH
			/ N_TILES_PER_SIDE, HEIGHT / N_TILES_PER_SIDE);

	public static void main(String args[]) throws InterruptedException,
			MongoException, IOException {

		Options options = new Options();

		// add t option
		options.addOption("v", false, "Return version information and exit");
		options.addOption("s", "sub", true, "Add video for sub frames");
		options.addOption("S", "super", true, "Add video for super frames");
		options.addOption("i", "identify", true, "Identify a single image");
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println("Unable to parse commandline options");
			System.out.printf("Usage: %s <command> <input file>", args[0]);
			System.exit(1);
		}
		if (cmd.hasOption("v")) {
			System.out.println("Version... hmm....");
			System.exit(0);
		}
		if(cmd.getArgs().length > 0)
		{
			System.out.println("Found unparsed arguments, exitting");
			System.out.println("Arguments where: " + cmd.getArgList());
			System.exit(1);
			
		}
		
		
		Function<String, File> fileCreator = new Function<String, File>() {
			@Override
			public File apply(final String name) {
				return new File(name);
			}
		};

		List<File> subFiles = Lists.transform(cmdList(cmd, "s"), fileCreator);
		List<File> superFiles = Lists.transform(cmdList(cmd, "S"), fileCreator);
		List<File> identFiles = Lists.transform(cmdList(cmd, "i"), fileCreator);
		System.out.println("Sub: " + subFiles.toString());
		System.out.println("Super: " + superFiles.toString());
		System.out.println("Identify: " + identFiles.toString());
		System.out.println("Rest: " + cmd.getArgList().toString());
		
		IdentStorage identStorage = new MemoryIdentStorage();
		IdentProducer identifier = new LargeIdentProducer();

		for (File targetFile : subFiles) {
			BlockingQueue<Frame> queue = frameQueue(targetFile);
			Frame f = queue.poll(10, TimeUnit.SECONDS);
			while (f != null) {
				FrameLocation location = new FrameLocation(targetFile
						.getAbsolutePath(), f.frameNumber());
				List<String> ident = identifier.identify(f);

				System.out.println(String.format("Frame number %d ident %s", f
						.frameNumber(), ident.toString()));
				identStorage.storeSubIdent(ident, location);

				//Store sub-frame on disk
				frameStorage.storeFrame(f, location);
				// Next frame
				f = queue.poll(2, TimeUnit.SECONDS);// TODO UGLY CODE!
			}

		}

		for (File targetFile : superFiles) {
			BlockingQueue<Frame> queue = frameQueue(targetFile);
			Frame f = queue.poll(10, TimeUnit.SECONDS);

			while (f != null) {
				FrameLocation location = new FrameLocation(targetFile
						.getAbsolutePath(), f.frameNumber());
				LOG.debug("Starting match for: " + location.toString());
				// Create idents for each piece of the puzzel, match to a good
				// location
				int w = f.getWidth() / N_TILES_PER_SIDE;
				int h = f.getHeight() / N_TILES_PER_SIDE;
				ArrayList<FrameLocation> locations = new ArrayList<FrameLocation>();
				for (int xOffset = 0; xOffset < f.getWidth(); xOffset += w)
					for (int yOffset = 0; yOffset < f.getHeight(); yOffset += h) {
						List<String> ident = identifier.identify(f.getSubimage(
								xOffset, yOffset, w, h));
						locations.add(identStorage.bestMatchFor(ident));
					}

				// Collapse match into file on disk
				BufferedImage outputFrame = generateFrame(locations);
				File outputFile = new File(String.format("/tmp/image_%05d.png", f.frameNumber()));
				LOG.debug(String.format("Outputting to %s", outputFile
						.toString()));
				ImageIO.write(outputFrame, "png", outputFile);
				
				// Next frame
				f = queue.poll(2, TimeUnit.SECONDS);// TODO UGLY CODE!
			}
		}

		for(File identify: identFiles){
			BufferedImage img = ImageIO.read(identify);
			System.out.println(String.format("%s:\t%s", identify.getName(), identifier.identify(img)));
		}
	}

	/**
	 * Get the option out of the CommandLine and always return a List (even if
	 * CommandLine would say null)
	 * 
	 * @param cmd
	 * @param option
	 * @return
	 */
	private static List<String> cmdList(CommandLine cmd, String option) {
		if (cmd.getOptionValues(option) != null)
			return Arrays.asList(cmd.getOptionValues(option));
		return Collections.emptyList();

	}

	private static BufferedImage generateFrame(
			ArrayList<FrameLocation> subFrames) throws InterruptedException,
			IOException {
		assert (subFrames.size() == N_TILES_PER_SIDE * N_TILES_PER_SIDE);
		// Collapse all the sub-frames of the current frame into it's
		// parts.
		// Use a default of black if the frame is missing
		BufferedImage outputFrame = new BufferedImage(WIDTH, HEIGHT,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D outputGraphics = outputFrame.createGraphics();
		for (int i = 0; i < subFrames.size(); ++i) {
			FrameLocation fl = subFrames.get(i);
			LOG.debug(String.format("Loading subimage %d from %s", i, fl
					.toString()));
			BufferedImage scaledSubframe = frameStorage.loadFrame(fl);
			// Scale to fit
			int tileWidth = WIDTH / N_TILES_PER_SIDE;
			int tileHeight = HEIGHT / N_TILES_PER_SIDE;
			int x = tileWidth * (i % N_TILES_PER_SIDE);
			int y = tileHeight * (i / N_TILES_PER_SIDE);
			// Store the subframe in the bufferedImage
			assert (x < outputFrame.getWidth());
			assert (y < outputFrame.getHeight());
			//LOG.debug(String.format("Putting sub frame at %d,%d", x, y));
			outputGraphics.drawImage(scaledSubframe, x, y, null);
		}
		outputGraphics.dispose();
		return outputFrame;
	}

	private static BlockingQueue<Frame> frameQueue(File targetFile) {
		BlockingQueue<Frame> queue = new SynchronousQueue<Frame>();
		FrameGenerator fg = new FrameGenerator(queue, targetFile);
		fg.run();
		return queue;

	}

}