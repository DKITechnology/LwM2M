package com.dkitec.lwm2m.mdao.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.dkitec.lwm2m.common.util.Sequence;

@Repository 
public class SequenceDao {
	
	public enum SeqType{ MESSAGE };
	public enum MovType{ UP , DOWN };
		
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private final String COLLECTION_NAME = "sequence";

	/**
	 * insert
	 * @param item
	 */
	public void insert(Sequence item) {
		mongoTemplate.insert(item, COLLECTION_NAME);
	}

	/**
	 * findOne
	 * @param query
	 * @return
	 */
	public Sequence findOne(Query query) {
		return mongoTemplate.findOne(query, Sequence.class, COLLECTION_NAME);
	}

	/**
	 * sequence 번호 채번
	 * @param m_type
	 * @param s_type
	 * @return
	 */
	public synchronized long move(MovType m_type, SeqType s_type) {
		long move = (m_type == MovType.UP) ? 1:-1;
		String type = s_type.toString();
		
		Query query = new Query();
		query.addCriteria(Criteria.where("type").is(type));
				
		Update update = new Update();
		update.inc("sequence", move);
		
		FindAndModifyOptions option = new FindAndModifyOptions();
		option.upsert(true);
		
		Sequence findObj = mongoTemplate.findAndModify(query, update, option , Sequence.class, COLLECTION_NAME);
		
		return findObj.getSequence();
	}
}
