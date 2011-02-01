package nl.bneijt.videosaic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

public class MemoryIdentStorage implements IdentStorage {

	class Item {
		public FrameLocation location;
		public List<String> identity;

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
	public FrameLocation bestMatchFor(final List<String> ident) {
		LOG.debug("Find best match for " + ident);
		
		assert (storage.size() > 0);
		Collections.sort(storage, new Comparator<Item>() {
			/**
			 * Compare items based on their matching length to the given super ident
			 * @param a
			 * @param b
			 * @return
			 */
			public int compare(Item a, Item b) {
				int aMatchLength = matchLength(a.identity, ident);
				int bMatchLength = matchLength(b.identity, ident);
				if(aMatchLength + bMatchLength > 0)
					return aMatchLength - bMatchLength;
				int aMatchCount = matchCount(a.identity, ident);
				int bMatchCount = matchCount(b.identity, ident);
				return aMatchCount - bMatchCount;
			}

			private int matchCount(List<String> a, List<String> b) {
				int count = 0;
				for(int i = 0; i < a.size(); ++i)
					if(a.get(i).equals(b.get(i)))
						count += 1;
				return count;
				
			}

			private int matchLength(List<String> a, List<String> b) {
				assert(a.size() == b.size());
				int i = 0;
				for(;i < a.size(); ++i)
					if(! a.get(i).equals(b.get(i)) )
						break;
				return i;
			}
		});
		LOG.debug("Head " + storage.subList(0, 10));
		LOG.debug("Tail " + storage.subList(storage.size() - 10, storage.size()));
		Item best = storage.get(storage.size() -1);
		LOG.debug("Found " + best.identity);
		return best.location;
	}

	@Override
	public void clear() {
		throw new RuntimeException(
				"Can not clear a memory ident storage (yet), restart the program to clear.");
	}

	@Override
	public void storeSubIdent(List<String> ident, FrameLocation location) {
		// TODO Auto-generated method stub
		Item i = new Item();
		i.location = location;
		i.identity = ident;

		storage.add(i);
	}

}
