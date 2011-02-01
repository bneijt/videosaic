package nl.bneijt.videosaic;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Joiner;

/**
 * Store scaled versions of image on disk for later reference.
 * 
 * Caching proxy to real FrameLocation s
 * 
 * @author A. Bram Neijt <bneijt@gmail.com>
 * 
 */
public class DiskFrameStorage {
	private final static Logger LOG = Logger.getLogger(DiskFrameStorage.class);
	private final int width;
	private final int height;

	public DiskFrameStorage(int width, int height) {
		this.width = width;
		this.height = height;

	}

	public BufferedImage loadFrame(FrameLocation fl)
			throws InterruptedException, IOException {
		// Optimization: try cache
		File storageLocation = this.storageLocation(fl);
		if (storageLocation.exists()) {
			LOG.debug(String.format("Hit cache at: %s", storageLocation
					.getAbsolutePath()));
			return ImageIO.read(storageLocation);
		}
		// Fall back: return black image
		return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

	}

	public void storeFrame(Image image, FrameLocation location)
			throws IOException {
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
				image.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D bufImageGraphics = bufferedImage.createGraphics();
		bufImageGraphics.drawImage(image, 0, 0, null);
		this.storeFrame(bufferedImage, location);
	}

	public void storeFrame(BufferedImage image, FrameLocation location)
			throws IOException {
		File storageLocation = storageLocation(location);
		LOG.debug(String.format("Translated %s into file path %s", location
				.toString(), storageLocation.getAbsolutePath()));
		FileUtils.forceMkdir(storageLocation.getParentFile());

		// Scale to fit if needed
		if (image.getWidth() != width || image.getHeight() != height) {
			BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2D = scaledImage.createGraphics();
			graphics2D.drawImage(image, 0, 0, width, height, null);
			graphics2D.dispose();
			ImageIO.write(scaledImage, "png", storageLocation);
		} else
			ImageIO.write(image, "png", storageLocation);
	}

	private File storageLocation(FrameLocation location) {
		File storageLocation = new File(Joiner.on(File.separator).join(
				"/tmp/substorage", location.getFile().getName(),
				String.format("%d.png", location.getFrameNumber())));
		return storageLocation;
	}

}
