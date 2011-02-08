package nl.bneijt.videosaic;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class SubFrameClient {
	private static final Logger LOG = Logger.getLogger(SubFrameClient.class);

	class Peer {
		public int connectCount;

		public Peer(String hostName, int portNumber)
				throws UnknownHostException {
			host = InetAddress.getByName(hostName);
			port = portNumber;
			connectCount = 0;
		}

		public Socket openSocket() throws IOException {
			LOG.info("Connecting to " + host + ":" + port);
			_socket = new Socket(host, port);
			connectCount++;
			return _socket;
		}

		public Socket socket() throws IOException {
			if (_socket == null)
				return openSocket();
			if (!_socket.isConnected()) {
				_socket.close();
				return openSocket();
			}
			return _socket;
		}

		private Socket _socket;
		public final InetAddress host;
		public final int port;

		public void close() {
			if (_socket != null) {
				try {
					_socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				_socket = null;
			}
		}
	}

	private Peer[] peers;

	public SubFrameClient(String[] hosts) throws UnknownHostException,
			IOException {
		peers = new Peer[hosts.length];
		for (int i = 0; i < peers.length; ++i) {
			String host = hosts[i];
			int colonIndex = host.indexOf(":") > 0 ? host.indexOf(":") : host
					.length();
			peers[i] = new Peer(host.substring(0, colonIndex), Integer
					.parseInt(host.substring(colonIndex + 1)));
		}

	}

	public static void main(String[] args) throws UnknownHostException,
			IOException, InterruptedException {
		// Open the target file and start looking for frames at the given
		// servers
		File targetFile = new File(args[args.length - 1]);
		SubFrameClient sfc = new SubFrameClient(Arrays.copyOf(args,
				args.length - 1));

		BlockingQueue<Frame> queue = App.frameQueue(targetFile);
		Frame f = queue.poll(10, TimeUnit.SECONDS);

		while (f != null) {
			BufferedImage outputFrame = new BufferedImage(App.WIDTH,
					App.HEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics2D outputGraphics = outputFrame.createGraphics();
			int w = f.getWidth() / App.N_TILES_PER_SIDE;
			int h = f.getHeight() / App.N_TILES_PER_SIDE;

			for (int yOffset = 0; yOffset < f.getHeight(); yOffset += h) {
				for (int xOffset = 0; xOffset < f.getWidth(); xOffset += w) {
					byte[] query = SubFrameStorage.bytesFromBufferedImage(f
							.getSubimage(xOffset, yOffset, w, h));
					byte[] subFrameBytes = sfc.bestMatchFor(query);
					BufferedImage subframe = SubFrameStorage
							.bufferedImageFromBytes(subFrameBytes, w, h);
					outputGraphics.drawImage(subframe, xOffset, yOffset, null);
				}
			}
			outputGraphics.dispose();

			// Collapse match into file on disk
			File outputFile = new File(String.format("/tmp/image_%05d.png", f
					.frameNumber()));
			LOG.debug(String.format("Outputting to %s", outputFile.toString()));
			ImageIO.write(outputFrame, "png", outputFile);
			// Next frame
			f = queue.poll(2, TimeUnit.SECONDS);// TODO UGLY CODE!
		}
		sfc.close();
	}

	private void close() {
		for (Peer p : peers)
			p.close();
	}

	private byte[] bestMatchFor(byte[] query) {
		if (peers.length == 0)
			throw new RuntimeException("No hosts left to query");
		SubFrameStorage storage = new SubFrameStorage();
		List<InputStream> ins = new ArrayList<InputStream>();
		// Fire queries
		for (Peer peer : peers) {
			if (peer.connectCount > 10)
				continue; // Skip hosts that fail to often

				Socket s;
				try {
					s = peer.socket();
					LOG.debug("Contacting " + s.getRemoteSocketAddress());
					OutputStream out = s.getOutputStream();
					out.write(query);
					out.flush();
					ins.add(s.getInputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}
		// Collect responses
		for (InputStream in : ins) {
			byte[] response = new byte[query.length];
			int read;
			try {
				read = in.read(response);
				if (read != response.length) {
					LOG.error("Could not get full response, throwing away result");
					continue;
				}
				LOG.info("Storing response");
				storage.add(response);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return storage.bestMatchFor(query);
	}

}
