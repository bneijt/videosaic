package nl.bneijt.videosaic;

import java.util.List;

public interface IdentStorage {
	/**
	 * Store the super ident. Any super ident which matches will be stored after this
	 * @param ident
	 * @param location
	 */
	public void storeSuperIdent(List<String> idents, FrameLocation location);
	/**
	 *  Try to store the sub ident in the datastore: this will simply connect the location to a superident if it can find one.
	 * @param ident
	 * @param location
	 * @return Whether or not the frame was stored.
	 */
	public boolean storeSubIdent(String ident, FrameLocation location);
	public String information();
	public List<FrameLocation> loadSubFrames(FrameLocation location);

}
