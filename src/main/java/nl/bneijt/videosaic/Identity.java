package nl.bneijt.videosaic;

import java.util.Arrays;
/**
 * Base type wrapper (sounds nice, but you have to admit this is stupid ;) )
 * @author A. Bram Neijt <bneijt@gmail.com>
 *
 */
public class Identity {
	final public byte[] data;
	public Identity(byte[] data) {
		this.data = Arrays.copyOf(data, data.length);
	}
	public long distance(Identity identity) {
		long d = 0;
		for(int i = 0; i < identity.data.length; ++i)
		{
			d += Math.abs(this.data[i] - identity.data[i]);
		}
		return d;
	}
	@Override
	public String toString() {
		return super.toString() + "[" +  data[0] + ", " + data[1] + " .. " + data[data.length -2] + ", " + data[data.length -1] + "]";
	}
}
