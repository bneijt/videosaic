package nl.bneijt.videosaic;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

public class SubFrameFileDumper {
	static final Logger LOG = Logger.getLogger(App.class);

	public static void main(String[] args) throws InterruptedException,
			IOException {
		// Load the files given on the commandline into memory
		// Handle queries to this list
		for(String arg : args)
		{
			SubFrameStorage storage = new SubFrameStorage();
			storage.loadFile(new File(arg));
			storage.dumpFile(new File(arg + ".bin"));
			
		}
	}
}
