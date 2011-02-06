package nl.bneijt.videosaic;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class MemoryIdentStorage implements IdentStorage {

	class Item {
		public FrameLocation location;
		public Identity identity;

		public String toString() {
			return identity.toString();
		}
	}

	private List<Item> storage;
	static final Logger LOG = Logger.getLogger(MemoryIdentStorage.class);

	public MemoryIdentStorage() {
		storage = new ArrayList<Item>();
	}

	@Override
	public FrameLocation bestMatchFor(final Identity ident) {
		assert (storage.size() > 0);
		assert (ident.data.length > 0);
		
		Item best = storage.get(0);
		long bestDistance = ident.distance(best.identity);
		for (Item i : storage) {
			long distance = ident.distance(i.identity);
			if(distance < bestDistance)
			{
				best = i;
				bestDistance = distance;
			}
		}
		LOG.debug("Found " + best.identity + " as best match for " + ident + " with distance " +  bestDistance);
		return best.location;
	}

	
	@Override
	public void clear() {
		throw new RuntimeException(
				"Can not clear a memory ident storage (yet), restart the program to clear.");
	}

	@Override
	public void storeSubIdent(Identity ident, FrameLocation location) {
		Item i = new Item();
		i.location = location;
		i.identity = ident;
		storage.add(i);
	}

}
