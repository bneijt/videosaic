package nl.bneijt.videosaic;


/**
 * IdentStorage is both a storage for identifying features of the sub-frames as a matcher for the matching of the super frame identities.
 * 
 * Probably want DI of a matcher/search algorithm later on.
 * 
 * @author A. Bram Neijt <bneijt@gmail.com>
 *
 */
public interface IdentStorage {
	/**
	 *  Store the sub ident for later matching
	 * @param ident
	 * @param location
	 * @return Whether or not the frame was stored.
	 */
	public void storeSubIdent(Identity ident, FrameLocation location);
	/**
	 * Find the best matching framelocation for the given identity
	 * @param ident
	 * @return
	 */
	public FrameLocation bestMatchFor(Identity ident);
	public void clear();

}
