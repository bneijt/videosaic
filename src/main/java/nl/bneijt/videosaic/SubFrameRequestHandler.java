package nl.bneijt.videosaic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

public class SubFrameRequestHandler extends Thread {
	private final SubFrameStorage storage;
	private final Socket client;
	static final Logger LOG = Logger.getLogger(SubFrameRequestHandler.class);

	public SubFrameRequestHandler(final SubFrameStorage storage,
			final Socket client) {
		this.storage = storage;
		this.client = client;

	}

	public void run() {
		LOG.info("Started to serve: " + client.getRemoteSocketAddress());
		while (client.isConnected()) {
			try {
				InputStream in = client.getInputStream();
				OutputStream out = client.getOutputStream();
				byte[] query = storage.block();
				int read = in.read(query);
				if (read < query.length) {
					LOG.error("Could not read complete query from: "
							+ client.getRemoteSocketAddress());
					client.close();
					return;
				}
				byte[] best = storage.bestMatchFor(query);
				out.write(best);
				out.flush();
				LOG.info("Served: " + client.getRemoteSocketAddress());
			} catch (IOException e) {
				LOG.warn("Socket IO Exception for: "
						+ client.getRemoteSocketAddress());
			}
		}

	}
}
