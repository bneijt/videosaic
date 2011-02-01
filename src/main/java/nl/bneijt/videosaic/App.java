package nl.bneijt.videosaic;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

	public static void main(String args[]) throws InterruptedException,
			MongoException, IOException {

		Options options = new Options();

		// add t option
		options.addOption("v", false, "Return version information and exit");
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
		if (cmd.getArgList().size() < 2) {
			System.out.println("Not enough arguments given");
			System.out.println("Usage: videosaic <sub|super|store> <input file>");
			System.exit(1);
		}

		ArrayList<String> fileArguments = new ArrayList<String>();
		fileArguments.addAll(Arrays.asList(cmd.getArgs())); // cmd.getArgList
		// sucks because it
		// does not contain
		// a type
		System.out.println(fileArguments);
		String command = fileArguments.remove(0);
		ArrayList<File> files = new ArrayList<File>();

		for (String arg : fileArguments) // TODO This is just a map, find a nice
		// mapping system to handle this
		{
			files.add(new File(arg));
		}

		System.out.printf("Command '%s'\n", command);
		System.out.printf("Files: %s\n", files.toString());
		IdentStorage identStorage = new MemoryIdentStorage();
		IdentProducer identifier = new MeanLevelIdentProducer();
		if (command.equals("sub")) {
			LOG.debug("Storing sub-idents");
			for(File targetFile : files)
			{
				BlockingQueue<Frame> queue = frameQueue(targetFile);
				Frame f = queue.poll(10, TimeUnit.SECONDS);
				while (f != null) {
					FrameLocation location = new FrameLocation(targetFile
							.getAbsolutePath(), f.frameNumber());
					List<String> ident = identifier.identify(f);

					System.out.println(String.format("Frame number %d ident %s", f
							.frameNumber(), ident.toString()));
					identStorage.storeSubIdent(ident, location);
					
					//Next frame
					f = queue.poll(2, TimeUnit.SECONDS);// TODO UGLY CODE!
				}
				
			}

		} else if (command.equals("super")) {
			for(File targetFile: files)
			{
				BlockingQueue<Frame> queue = frameQueue(targetFile);
				Frame f = queue.poll(10, TimeUnit.SECONDS);

				while (f != null) {
					FrameLocation location = new FrameLocation(targetFile
							.getAbsolutePath(), f.frameNumber());
					LOG.debug("Starting match for: " + location.toString());
					// Create idents for each piece of the puzzel, match to a good location
					int w = f.getWidth() / N_TILES_PER_SIDE;
					int h = f.getHeight() / N_TILES_PER_SIDE;
					ArrayList< FrameLocation > locations = new ArrayList< FrameLocation >();
					for (int xOffset = 0; xOffset < f.getWidth(); xOffset += w)
						for (int yOffset = 0; yOffset < f.getHeight(); yOffset += h) {
							List<String> ident = identifier.identify(f.getSubimage(
									xOffset, yOffset, w, h));
							locations.add(identStorage.bestMatchFor(ident));
						}

					//Collapse match into file on disk
					BufferedImage outputFrame = generateFrame(locations);
					File outputFile = new File(String.format(
							"/tmp/image_%d.png", f.frameNumber()));
					LOG.debug(String.format("Outputting to %s", outputFile.toString()));
					ImageIO.write(outputFrame, "png", outputFile);
				}
				
			}

		} else if (command.equals("clear")) {
			identStorage.clear();
		} else {
			System.out.println("Unknown command: " + command);
			System.exit(1);
			}

	}

	private static BufferedImage generateFrame(
			ArrayList<FrameLocation> subFrames) throws InterruptedException, IOException {
		DiskFrameStorage frameStorage = new DiskFrameStorage(WIDTH / N_TILES_PER_SIDE, HEIGHT / N_TILES_PER_SIDE);
		assert(subFrames.size() == N_TILES_PER_SIDE * N_TILES_PER_SIDE);
		// Collapse all the sub-frames of the current frame into it's
			// parts.
			// Use a default of black if the frame is missing
			BufferedImage outputFrame = new BufferedImage(WIDTH, HEIGHT,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D outputGraphics = outputFrame.createGraphics();
			for (int i = 0; i < subFrames.size(); ++i) {
				FrameLocation fl = subFrames.get(i);
				LOG.debug(String.format("Loading subimage %d from %s", i,
						fl.toString()));
				BufferedImage scaledSubframe = frameStorage.loadFrame(fl);
				// Scale to fit
				int tileWidth = WIDTH / N_TILES_PER_SIDE;
				int tileHeight = HEIGHT / N_TILES_PER_SIDE;
				int x = tileWidth * (i % N_TILES_PER_SIDE);
				int y = tileHeight * (i / N_TILES_PER_SIDE);
				// Store the subframe in the bufferedImage
				assert (x < outputFrame.getWidth());
				assert (y < outputFrame.getHeight());
				LOG
						.debug(String.format("Putting sub frame at %d,%d",
								x, y));
				outputGraphics.drawImage(scaledSubframe, x, y, null);

			}
			return outputFrame;
	}

	private static BlockingQueue<Frame> frameQueue(File targetFile) {
		BlockingQueue<Frame> queue = new SynchronousQueue<Frame>();
		FrameGenerator fg = new FrameGenerator(queue, targetFile);
		fg.run();
		return queue;

	}

}