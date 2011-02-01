package nl.bneijt.videosaic;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MemoryIdentStorage implements IdentStorage {

	class Item{
		public FrameLocation location;
		public List<String> identity;
		
	}
	
	private List<Item> storage;
	
	@Override
	public FrameLocation bestMatchFor(final List<String> ident) {
		Collections.sort(storage,
        new Comparator<Item>()
        {
            public int compare(Item a, Item b)
            {
            	//Match both with ident, best matching wins
            	//Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
            	if(a.identity.equals(b.identity))
            		return 0;
            	//TODO Match a to a depth, and b to a depth and return if their depths are equal or not
            	int aMatchLength = 1;
            	int bMatchLength = 2;
                return aMatchLength - bMatchLength;
            }        
        });
		return storage.get(0).location;
	}

	@Override
	public void clear() {
		throw new RuntimeException("Can not clear a memory ident storage (yet), restart the program to clear.");
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
