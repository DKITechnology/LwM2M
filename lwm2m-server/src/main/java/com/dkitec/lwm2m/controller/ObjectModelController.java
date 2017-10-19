package com.dkitec.lwm2m.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.leshan.core.model.DDFFileParser;
import org.eclipse.leshan.core.model.ObjectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.common.util.json.JsonResult;
import com.dkitec.lwm2m.domain.ObjectModelInfoVO;

@RestController
public class ObjectModelController {
	
	Logger logger = LoggerFactory.getLogger(ObjectModelController.class);

	@RequestMapping(value="/objectModel/valid", method=RequestMethod.POST)
	public JsonResult checkObectModel(@RequestBody ObjectModelInfoVO objectModel, HttpServletRequest req, HttpServletResponse res){
		JsonResult resultvo = new JsonResult();
		String result = "fail";
		
		try {
			if(!CommonUtil.isEmpty(objectModel.getObjCont())){
				InputStream inputStream = null;
				validDom(objectModel.getObjCont());
				inputStream = new ByteArrayInputStream(objectModel.getObjCont().getBytes("UTF-8"));
				DDFFileParser ddfFileParser = new DDFFileParser();
				ObjectModel objMd = ddfFileParser.parse(inputStream, objectModel.getObjNm());
				if(objMd == null)
					throw new ObejctModelExption("ObjectModel로 변환 할 수 없는 데이터 입니다.");
			}else{
				throw new ObejctModelExption("XML 데이터가 입력 되있지 않습니다.");
			}
			result = "success";
		} catch (ObejctModelExption obe) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resultvo.setErrorMsg(obe.getMessage());
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resultvo.setErrorMsg(e.getMessage());
		}
		resultvo.setResult(result);
		return resultvo;
	}
	
	private class ObejctModelExption extends Exception {
		private static final long serialVersionUID = 1L;

		ObejctModelExption(String msg){
			super(msg);
		}
		
		@Override
		public String getMessage() {		
			return super.getMessage();
		}
	}
	
	private void validDom(String content) throws ObejctModelExption{
		try {
			InputStream inputStream = null;
			inputStream = new ByteArrayInputStream(content.getBytes("UTF-8"));
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);
		} catch (Exception e) {
			throw new ObejctModelExption("유효하지 않는 XML 형식입니다.");
		}
	}
}
