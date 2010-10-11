package nl.bneijt.videosaic;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MongoDBIdentStorage implements IdentStorage {
	private final Mongo mongo;

	public MongoDBIdentStorage() throws MongoException,
	UnknownHostException {
		mongo = new Mongo();
	}
	private DBCollection collection()
	{
		DB db = mongo.getDB(null);
		return db.getCollection("videosaic");
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean storeSourceIdent(String ident, FrameLocation location) {
		BasicDBObject query = new BasicDBObject();
		query.put("ident", ident);
		com.mongodb.DBCursor cur = collection().find(query);
		if (!cur.hasNext())
			return false;
		com.mongodb.DBObject document = cur.next();
		ArrayList<String> sources = (ArrayList<String>) document.get("sources");
		sources.add(location.toString());
		document.put("sources", sources);
		collection().insert(document);
		return true;
	}

	@Override
	public void storeTargetIdent(String ident, FrameLocation location) {
		BasicDBObject document = new BasicDBObject();
		document.put("ident", ident);
		document.put("location", location);
		document.put("sources", new ArrayList<String>());
		collection().insert(document);
		throw new RuntimeException("Bram has not implemented this method yet.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FrameLocation> sourceLocations(String ident) {
		BasicDBObject query = new BasicDBObject();
		query.put("ident", ident);
		com.mongodb.DBCursor cur = collection().find(query);
		if (!cur.hasNext())
			return new ArrayList<FrameLocation>();
		com.mongodb.DBObject document = cur.next();
		ArrayList<String> sources = (ArrayList<String>) document.get("sources");
		ArrayList<FrameLocation> locs = new ArrayList<FrameLocation>();
		for(String l : sources)
			locs.add(new FrameLocation(l));
		return locs;
	}

}
