package com.dkitec.lwm2m.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.leshan.core.model.DDFFileParser;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.model.ResourceModel;
import org.eclipse.leshan.core.model.json.ObjectModelDeserializer;
import org.eclipse.leshan.core.model.json.ResourceModelDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.dao.ServerInitDao;
import com.dkitec.lwm2m.domain.ObjectModelInfoVO;
import com.dkitec.lwm2m.service.intf.ServerInitService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ServerInitServiceImpl implements ServerInitService {

	Logger logger = LoggerFactory.getLogger(ServerInitServiceImpl.class);
	
	@Autowired
	ServerInitDao serverInitDao;
	
	@Override
	public void insertDefaultModelInfo() {
		try {
			serverInitDao.insertDefaultModelInfo();
		} catch (Exception e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}
	}

	@Override
	public List<ObjectModel> selectLoadObjects() {
		List<ObjectModel> resultModles = new ArrayList<>();
		try {
			List<ObjectModelInfoVO> additionObjectModel = serverInitDao.selectAdditionObjects();
			
			/*objectModel을 json으로 저장하는경우* 
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(ObjectModel.class, new ObjectModelDeserializer());
			gsonBuilder.registerTypeAdapter(ResourceModel.class, new ResourceModelDeserializer());
			Gson gson = gsonBuilder.create();
			 * for(ObjectModelInfoVO model : additionObjectModel){
				logger.debug("## parse model : " + model.getObjId());
				ObjectModel[] objectModels = gson.fromJson(model.getObjCont(), ObjectModel[].class);
			    List<ObjectModel> models = new ArrayList<>();
			    Collections.addAll(models, objectModels);	
			}*/

			DDFFileParser ddfFileParser = new DDFFileParser();
			for(ObjectModelInfoVO model : additionObjectModel){
				logger.debug("## parse model : " + model.getObjNm());
				InputStream inputStream = null;
				if(!CommonUtil.isEmpty(model.getObjCont())){
					try {
						inputStream = new ByteArrayInputStream(model.getObjCont().getBytes("UTF-8"));
						resultModles.add(ddfFileParser.parse(inputStream, model.getObjNm()));
					} catch (UnsupportedEncodingException e) {
						LoggerPrint.printErrorLogExceptionrMsg(logger, e);
					}
				}			
			}
		} catch (Exception e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}
		return resultModles;
	}

}
