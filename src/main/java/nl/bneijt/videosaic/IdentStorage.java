package nl.bneijt.videosaic;

import java.util.List;

public interface IdentStorage {
	/**
	 * Store the target ident. Any source ident which matches will be stored after this
	 * @param ident
	 * @param location
	 */
	public void storeTargetIdent(String ident, FrameLocation location);
	/**
	 *  Try to store the ident in the datastore. If there is no target ident which matches, the ident will not be stored (and false will be returned)
	 * @param ident
	 * @param location
	 * @return Wether or not the frame was stored.
	 */
	public boolean storeSourceIdent(String ident, FrameLocation location);
	
	/**
	 * Return a list of locations for a given source ident
	 * @return A list of locations
	 */
	public List<FrameLocation> sourceLocations(String ident);
}
