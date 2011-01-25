package nl.bneijt.videosaic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.io.File;
import java.net.UnknownHostException;

import com.mongodb.MongoException;

import nl.bneijt.videosaic.FrameGenerator;
import nl.bneijt.videosaic.Frame;

class App 
{
	static void main(String args[]) throws InterruptedException, MongoException, UnknownHostException {
		//TODO Use real option parsing library (maybe paulp's optional)
		if(args.length < 2) {
			System.out.printf("Usage: %s <command> <input file>", args[0]);
		}
		String command = args[0];
		ArrayList<File> files = new ArrayList<File>();
		for(String arg : args)
		{
			files.add(new File(arg));
		}
		files.remove(0); //Drop command from file-list
		System.out.printf("Command %s\n", command);
		System.out.printf("Files: %s\n", files.toString());
		IdentStorage identStorage = new MongoDBIdentStorage();
		if(command == "target")
		{
			//Load frame idents as targets into the database (documents)
			//Create index of output movie
			File targetFile = files.get(0);
			BlockingQueue<Frame> queue = new SynchronousQueue<Frame>();
			FrameGenerator fg = new FrameGenerator(queue, targetFile);
			fg.run();
			Frame f = queue.take();
			while(f != null)
			{
				System.out.println(f.frameNumber());
				FrameLocation location = new FrameLocation(targetFile.getAbsolutePath(), f.frameNumber());
				f = queue.take();
				//Create ident
				IdentProducer ident = new MeanIdentProducer();
				//Store ident in database
				
				identStorage.storeTargetIdent(ident.identify(f), location);
			}
		}
		else if (command == "source") {

			//Load frame idents and see if they are in the database
		}
		else if (command == "store") {
			//Store video into the given output file

		}
	}
}

