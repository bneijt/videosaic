package nl.bneijt.videosaic;

import java.util.ArrayList;
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
		assert (storage.size() > 0);
		Item best = storage.get(0);
		int bestMatchCount = 0;
		for (Item i : storage) {
			int iMatchCount = matchCount(i, ident);
			if(iMatchCount > bestMatchCount)
			{
				best = i;
				bestMatchCount = iMatchCount;
			}
		}
		LOG.debug("Found " + best.identity + " as best match for " + ident);
		return best.location;
	}

	/**
	 * Return the total length of prefix matches in the elements of the ident and item.identity
	 * @param item
	 * @param ident
	 * @return
	 */
	private int matchCount(final Item item, final List<String> ident) {
		int matchLen = (item.identity.size() < ident.size() ? item.identity.size() : ident.size());
		int count = 0;
		for(int i = 0; i < ident.size(); ++i)
		{
			count += matchingPrefixLength(item.identity.get(i), ident.get(i));
		}
		return count;
	}


	private int matchingPrefixLength(final String a, final String b) {
		int len = (a.length() < b.length() ? a.length() : b.length());
		int i = 0;
		for(; i < len; ++i)
		{
			if(a.charAt(i) != b.charAt(i))
				return i;
		}
		return i;
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
