package com.dkitec.lwm2m.dao.test;
import java.util.List;

public interface DefaultRedisDao<E> {
	
	public void putData(E obj);
	
	public void deleteData(String key);
	
	public List<E> getObjectList();

	public List<E> getObjectList(long offset, long count);

	public E getData(String id);	
}