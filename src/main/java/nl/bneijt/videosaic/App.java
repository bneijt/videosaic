package nl.bneijt.videosaic;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.mongodb.MongoException;

class App 
{
	
	final static void println(String a) { System.out.println(a);}
	public static void main(String args[]) throws InterruptedException, MongoException, UnknownHostException {
		
    	Options options = new Options();

    	// add t option
    	options.addOption("v", false, "Return version information and exit") ;
    	CommandLineParser parser = new PosixParser();
    	CommandLine cmd = null;
		try {
			cmd = parser.parse( options, args);
		} catch (ParseException e) {
			System.out.println("Unable to parse commandline options");
			System.out.printf("Usage: %s <command> <input file>", args[0]);
			System.exit(1);
		}
    	if(cmd.hasOption("v")) {
    	    System.out.println("Version... hmm....");
    	    System.exit(0);
    	}
		
		List<String> fileArguments = Arrays.asList(cmd.getArgs()); //cmd.getArgList sucks because it does not contain a type
		String command = fileArguments.remove(0);
		ArrayList<File> files = new ArrayList<File>();
		
		for(String arg : fileArguments) //TODO This is just a map, find a nice mapping system to handle this
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
				println(String.format("Frame number %i", f.frameNumber()));
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

