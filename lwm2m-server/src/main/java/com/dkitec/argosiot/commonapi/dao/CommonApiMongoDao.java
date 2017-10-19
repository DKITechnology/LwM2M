package com.dkitec.argosiot.commonapi.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.mongodb.DBObject;


@Component
public class CommonApiMongoDao {	

	protected String collectionName;
	
	protected Class<?> cls;
	
	protected final int INSERT_LIMIT = 1000; 
	
	@Autowired
	protected MongoTemplate mongoTemplate;

	/**
	 * upsert
	 * @param obj
	 * @throws Exception
	 */
	public void upsert(Object obj) throws Exception {
		mongoTemplate.save(obj, this.collectionName);
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public Class<?> getCls() {
		return cls;
	}

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}

	/**
	 * insert
	 * @param obj
	 * @throws Exception
	 */
	public void insert(Object obj) throws Exception {
		mongoTemplate.insert(obj, this.collectionName);
	}
	
	public void insertAllByLimit(List<Object> obj) throws Exception {
		Gson gson = new Gson();
		List<Object> objs = new ArrayList<Object>();
		int cnt = 0;
		
		// db lock 고려해 1000건씩 나누어 처리
		for (Object item : obj) {
			objs.add(gson.fromJson((String) item, Object.class));
			cnt++;

			if ( cnt >= INSERT_LIMIT) {
				mongoTemplate.insert(objs, this.collectionName);
				cnt = 0;
				objs.clear();
			}
		}
		
		mongoTemplate.insert(objs, this.collectionName);
	}
	
	public void insertAll(List<Object> obj) throws Exception {
		mongoTemplate.insert(obj, this.collectionName);
	}
	
	/**
	 * find
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public List<?> find(Query query) throws Exception {
		return  mongoTemplate.find(query, this.cls, this.collectionName);
	}
	
	/**
	 * findOne
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public Object findOne(Query query) throws Exception {
		return  mongoTemplate.findOne(query, this.cls, this.collectionName);
	}

	/**
	 * findAll
	 * @return
	 * @throws Exception
	 */
	public List<?> findAll() throws Exception {
		return  mongoTemplate.findAll(this.cls, this.collectionName);
	}

	/**
	 * count
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public long count(Query query) throws Exception { 
		return mongoTemplate.count(query, this.collectionName);
	}

	/**
	 * upsert
	 * @param query, update
	 * @param upsert
	 * @throws Exception
	 */
	public int upsert(Query query, Update update) throws Exception {
		return mongoTemplate.upsert(query, update, this.collectionName).getN();
	}

	/**
	 * remove
	 * @param query
	 * @throws Exception
	 */
	public int remove(Query query) throws Exception {
		mongoTemplate.remove(query, this.collectionName);
		return 1;
	}

	/**
	 * remove
	 * @param obj
	 * @throws Exception
	 */
	public int remove(Object obj) throws Exception {
		mongoTemplate.remove(obj, this.collectionName);
		return 1;
	}
	
	/**
	 * findAndModify
	 * @param query
	 * @param update
	 * @throws Exception
	 */
	public void findAndModify(Query query, Update update) throws Exception {
		mongoTemplate.findAndModify(query, update, this.cls, this.collectionName);
	}
	
	/**
	 * findOne
	 * @param query
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public AggregationResults aggregate(Aggregation aggregation) throws Exception {
		AggregationResults<Object> groupResults =  mongoTemplate.aggregate(aggregation, this.collectionName, Object.class);
		
		return groupResults;
	}
	
	public class CustomOperation implements AggregationOperation {
	    private DBObject operation;

	    public CustomOperation (DBObject operation) {
	        this.operation = operation;
	    }

	    @Override
	    public DBObject toDBObject(AggregationOperationContext context) {
	        return context.getMappedObject(operation);
	    }
	}
}
