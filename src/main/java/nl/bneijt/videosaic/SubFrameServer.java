package nl.bneijt.videosaic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * HTTP Server for image database
 * 
 * @author A. Bram Neijt <bneijt@gmail.com>
 * 
 */
/*
 * import javax.servlet.http.HttpServletRequest; import
 * javax.servlet.http.HttpServletResponse; import
 * javax.servlet.ServletException; import java.io.IOException; import
 * org.eclipse.jetty.server.Server; import org.eclipse.jetty.server.Request;
 * import org.eclipse.jetty.server.handler.AbstractHandler;
 */
public class SubFrameServer {

	static final Logger LOG = Logger.getLogger(App.class);
	private SubFrameStorage storage;

	public SubFrameServer(List<File> subFiles) throws InterruptedException {
		storage = new SubFrameStorage();
		storage.loadFiles(subFiles);
	}

	public void serve() throws IOException {

		int port = 8080;
		ServerSocket server = null;
		for (port = 8080; port < 9000; port++) {
			try {
				server = new ServerSocket(port);
				LOG.info("Started server at port: " + port);
				break;
			} catch (IOException e) {
			}
		}

		if (server == null) {
			LOG.error("Unable to get a port for the server");
			return;
		}
		while (true) {
			final Socket client = server.accept();
			// Handle requests in thread
			LOG.info("Started to serve: " + client.getRemoteSocketAddress());
			try {
				InputStream in = client.getInputStream();
				OutputStream out = client.getOutputStream();
				byte[] query = storage.block();
				in.read(query);
				byte[] best = storage.bestMatchFor(query);
				LOG.info("Sending response to "
						+ client.getRemoteSocketAddress());
				out.write(best);
				out.flush();
				out.close();
				LOG.debug("Socket closed");
			} catch (IOException e) {
				LOG.warn("Socket IO Exception for: "
						+ client.getRemoteSocketAddress());
			}

		}
	}

	public static void main(String[] args) throws InterruptedException,
			IOException {
		// Load the files given on the commandline into memory
		// Handle queries to this list

		SubFrameServer server = new SubFrameServer(Lists.transform(Arrays
				.asList(args), new Function<String, File>() {
			@Override
			public File apply(final String name) {
				return new File(name);
			}
		}));
		server.serve();
	}
}
