package nl.bneijt.videosaic;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;

/**
 * This will store sub and super frames in the mongodb It will store document
 * super frame idents as: { ident: ident, location: location, index: index
 * (location within super frame) sub: [] (Matching sub-frames) }
 * 
 * @author A. Bram Neijt <bneijt@gmail.com>
 * 
 */
public class MongoDBIdentStorage implements IdentStorage {
	private final Mongo mongo;
	private final DBCollection collection;
	private final DB db;
	private final Logger LOG = Logger.getLogger(MongoDBIdentStorage.class);
	
	public MongoDBIdentStorage() throws MongoException, UnknownHostException {
		mongo = new Mongo();
		this.db = mongo.getDB("videosaic");
		collection = db.getCollection("frames");
	}

	@Override
	public boolean storeSubIdent(List<String> ident, FrameLocation location) {
		BasicDBObject query = new BasicDBObject();
		query.append("ident", ident);

		BasicDBObject update = new BasicDBObject();
		BasicDBObject sub = new BasicDBObject();
		sub.append("sub", location.toString());
		update.append("$push", sub);
		WriteResult result = collection.update(query, update, false, true);
		if(result.getN() > 0){
			LOG.info("Updated ident " + ident);
			return true;
		}
		return false;
	}

	@Override
	public void storeSuperIdent(List< List<String> > idents, FrameLocation location) {
		LOG.debug("Storring " + idents.size() + " idents");
		for (int i = 0; i < idents.size(); ++i) {
			BasicDBObject document = new BasicDBObject();
			document.append("ident", idents.get(i));
			document.append("location", location.toString());
			document.append("index", i);
			document.append("sub", new ArrayList<String>());
			document.append("_id", UUID.randomUUID().toString());
			assert(!document.isPartialObject());
			collection.insert(document);
		}
	}

	@Override
	public String information() {
		// TODO Auto-generated method stub
		String count = "Record count: " + collection.find().count();
		//Find number without sub-records
		return count;
		
	}

	@Override
	public ArrayList<FrameLocation> loadSubFrames(FrameLocation location) {
		LOG.debug(String.format("Loading subframes for: %s", location));
		BasicDBObject query = new BasicDBObject();
		query.append("location", location.toString());
		DBCursor result = collection.find(query);
		TreeMap<Integer, FrameLocation> subframes = new TreeMap<Integer, FrameLocation>();
		while(result.hasNext())
		{
			DBObject frame = result.next();
			assert(frame.containsField("index"));
			assert(frame.containsField("sub"));
			BasicDBList subs = (BasicDBList) frame.get("sub");
			LOG.debug(String.format("Found %d subs for frame %s", subs.size(), location.toString()));
			if(subs.size() <= 0)
			{
				subframes.put((Integer) frame.get("index"), location); //Self reference when there are no sub-frames (Should be black in the future)
				continue;
			}
			assert(subs.size() > 0);//Simply get the first sub-frame as a frame
			String sub = (String) subs.get(0);
			subframes.put((Integer) frame.get("index"), new FrameLocation(sub));
		}
		return new ArrayList<FrameLocation>(subframes.values());
	}

	@Override
	public void clear() {
		LOG.debug("Dropping database");
		this.db.dropDatabase();
	}




}
