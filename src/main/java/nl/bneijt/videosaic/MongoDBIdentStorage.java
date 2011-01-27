package nl.bneijt.videosaic;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
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
	private final Logger LOG = Logger.getLogger(MongoDBIdentStorage.class);
	
	public MongoDBIdentStorage() throws MongoException, UnknownHostException {
		mongo = new Mongo();
		DB db = mongo.getDB("videosaic");
		collection = db.getCollection("frames");
	}

	@Override
	public void storeSubIdent(String ident, FrameLocation location) {
		BasicDBObject query = new BasicDBObject();
		query.append("ident", ident);

		BasicDBObject update = new BasicDBObject();
		BasicDBObject sub = new BasicDBObject();
		sub.append("sub", location.toString());
		update.append("$push", sub);
		WriteResult result = collection.update(query, update, false, true);
		if(result.getN() > 0){
			LOG.info("Updated ident " + ident);			
		}
	}

	@Override
	public void storeSuperIdent(List<String> idents, FrameLocation location) {
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


}
