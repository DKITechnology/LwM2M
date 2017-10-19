package com.dkitec.lwm2m.test;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dkitec.lwm2m.common.util.json.JsonResult;
import com.dkitec.lwm2m.controller.ObjectModelController;
import com.dkitec.lwm2m.domain.ObjectModelInfoVO;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( locations = { "/spring/root-context.xml", "/spring/appServlet/servlet-context.xml" })
public class ObjectModelValidTest extends TestCase{
	
	@Autowired
	private ObjectModelController objModelController;
		
	@Test
	public void objectModelValid(){
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addHeader("Authorization", "Basic ZGtpQWRtaW46M2I2YjIyY2MyM2M1M2M2YmVmNWViYmNkNmY0NDc2YzIyM2YyNjIyYjVlYmMxOTJmMDg3MGM1ZDgxNDNjMDgwOTpPQU0==");
		request.setContentType("application/json");
		
		String content = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<LWM2M  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://openmobilealliance.org/tech/profiles/LWM2M.xsd\">\r\n\t<Object ObjectType=\"MODefinition\">\r\n\t\t<Name>Temperature</Name>\r\n\t\t<Description1>Description: This IPSO object should be used with a temperature sensor to report a temperature measurement.  It also provides resources for minimum/maximum measured values and the minimum/maximum range that can be measured by the temperature sensor. An example measurement unit is degrees Celsius (ucum:Cel).</Description1>\r\n\t\t<ObjectID>3303</ObjectID>\r\n\t\t<ObjectURN>urn:oma:lwm2m:ext:3303</ObjectURN>\r\n\t\t<MultipleInstances>Multiple</MultipleInstances>\r\n\t\t<Mandatory>Optional</Mandatory>\r\n\t\t<Resources>\r\n\t\t\t<Item ID=\"5700\">\r\n\t\t\t\t<Name>Sensor Value</Name>\r\n\t\t\t\t<Operations>R</Operations>\r\n\t\t\t\t<MultipleInstances>Single</MultipleInstances>\r\n\t\t\t\t<Mandatory>Mandatory</Mandatory>\r\n\t\t\t\t<Type>Float</Type>\r\n\t\t\t\t<RangeEnumeration></RangeEnumeration>\r\n\t\t\t\t<Units>Defined by \u201CUnits\u201D resource.</Units>\r\n\t\t\t\t<Description>Last or Current Measured Value from the Sensor</Description>\r\n\t\t\t</Item>\r\n\t\t\t<Item ID=\"5601\">\r\n\t\t\t\t<Name>Min Measured Value</Name>\r\n\t\t\t\t<Operations>R</Operations>\r\n\t\t\t\t<MultipleInstances>Single</MultipleInstances>\r\n\t\t\t\t<Mandatory>Optional</Mandatory>\r\n\t\t\t\t<Type>Float</Type>\r\n\t\t\t\t<RangeEnumeration></RangeEnumeration>\r\n\t\t\t\t<Units>Defined by \u201CUnits\u201D resource.</Units>\r\n\t\t\t\t<Description>The minimum value measured by the sensor since power ON or reset</Description>\r\n\t\t\t</Item>\r\n\t\t\t<Item ID=\"5602\">\r\n\t\t\t\t<Name>Max Measured Value</Name>\r\n\t\t\t\t<Operations>R</Operations>\r\n\t\t\t\t<MultipleInstances>Single</MultipleInstances>\r\n\t\t\t\t<Mandatory>Optional</Mandatory>\r\n\t\t\t\t<Type>Float</Type>\r\n\t\t\t\t<RangeEnumeration></RangeEnumeration>\r\n\t\t\t\t<Units>Defined by \u201CUnits\u201D resource.</Units>\r\n\t\t\t\t<Description>The maximum value measured by the sensor since power ON or reset</Description>\r\n\t\t\t</Item>\r\n\t\t\t<Item ID=\"5603\">\r\n\t\t\t\t<Name>Min Range Value</Name>\r\n\t\t\t\t<Operations>R</Operations>\r\n\t\t\t\t<MultipleInstances>Single</MultipleInstances>\r\n\t\t\t\t<Mandatory>Optional</Mandatory>\r\n\t\t\t\t<Type>Float</Type>\r\n\t\t\t\t<RangeEnumeration></RangeEnumeration>\r\n\t\t\t\t<Units>Defined by \u201CUnits\u201D resource.</Units>\r\n\t\t\t\t<Description>The minimum value that can be measured by the sensor</Description>\r\n\t\t\t</Item>\r\n\t\t\t<Item ID=\"5604\">\r\n\t\t\t\t<Name>Max Range Value</Name>\r\n\t\t\t\t<Operations>R</Operations>\r\n\t\t\t\t<MultipleInstances>Single</MultipleInstances>\r\n\t\t\t\t<Mandatory>Optional</Mandatory>\r\n\t\t\t\t<Type>Float</Type>\r\n\t\t\t\t<RangeEnumeration></RangeEnumeration>\r\n\t\t\t\t<Units>Defined by \u201CUnits\u201D resource.</Units>\r\n\t\t\t\t<Description>The maximum value that can be measured by the sensor</Description>\r\n\t\t\t</Item>\r\n\t\t\t<Item ID=\"5701\">\r\n\t\t\t\t<Name>Sensor Units</Name>\r\n\t\t\t\t<Operations>R</Operations>\r\n\t\t\t\t<MultipleInstances>Single</MultipleInstances>\r\n\t\t\t\t<Mandatory>Optional</Mandatory>\r\n\t\t\t\t<Type>String</Type>\r\n\t\t\t\t<RangeEnumeration></RangeEnumeration>\r\n\t\t\t\t<Units></Units>\r\n\t\t\t\t<Description>Measurement Units Definition e.g. \u201CCel\u201D for Temperature in Celsius.</Description>\r\n\t\t\t</Item>\r\n\t\t\t<Item ID=\"5605\">\r\n\t\t\t\t<Name>Reset Min and Max Measured Values</Name>\r\n\t\t\t\t<Operations>E</Operations>\r\n\t\t\t\t<MultipleInstances>Single</MultipleInstances>\r\n\t\t\t\t<Mandatory>Optional</Mandatory>\r\n\t\t\t\t<Type>String</Type>\r\n\t\t\t\t<RangeEnumeration></RangeEnumeration>\r\n\t\t\t\t<Units></Units>\r\n\t\t\t\t<Description>Reset the Min and Max Measured Values to Current Value</Description>\r\n\t\t\t</Item>\r\n\t\t</Resources>\r\n\t\t<Description2></Description2>\r\n\t</Object>\r\n</LWM2M>";
		//HashMap<String, String> bodyMap = new HashMap<String, String>();
		//bodyMap.put("objCont", content);
		//request.setContent(new Gson().toJson(bodyMap).getBytes());
		ObjectModelInfoVO objectModel = new ObjectModelInfoVO();
		objectModel.setObjNm("test");
		objectModel.setObjCont(content);

		JsonResult result = objModelController.checkObectModel(objectModel, request, response);

		assertEquals(result.getResult(), "success");
	}
	
	@Test
	public void objectModelInValid(){
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addHeader("Authorization", "Basic ZGtpQWRtaW46M2I2YjIyY2MyM2M1M2M2YmVmNWViYmNkNmY0NDc2YzIyM2YyNjIyYjVlYmMxOTJmMDg3MGM1ZDgxNDNjMDgwOTpPQU0==");
		request.setContentType("application/json");
		
		String content = "version=\"1.0\" encoding=\"utf-8\"?>\r\n<LWM2M  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://openmobilealliance.org/tech/profiles/LWM2M.xsd\">\r\n\t<Object ObjectType=\"MODefinition\">\r\n\t\t<Name>Temperature</Name>\r\n\t\t<Description1>Description: This IPSO object should be used with a temperature sensor to report a temperature measurement.  It also provides resources for minimum/maximum measured values and the minimum/maximum range that can be measured by the temperature sensor. An example measurement unit is degrees Celsius (ucum:Cel).</Description1>\r\n\t\t<ObjectID>3303</ObjectID>\r\n\t\t<ObjectURN>urn:oma:lwm2m:ext:3303</ObjectURN>\r\n\t\t<MultipleInstances>Multiple</MultipleInstances>\r\n\t\t<Mandatory>Optional</Mandatory>\r\n\t\t<Resources>\r\n\t\t\t<Item ID=\"5700\">\r\n\t\t\t\t<Name>Sensor Value</Name>\r\n\t\t\t\t<Operations>R</Operations>\r\n\t\t\t\t<MultipleInstances>Single</MultipleInstances>\r\n\t\t\t\t<Mandatory>Mandatory</Mandatory>\r\n\t\t\t\t<Type>Float</Type>\r\n\t\t\t\t<RangeEnumeration></RangeEnumeration>\r\n\t\t\t\t<Units>Defined by \u201CUnits\u201D resource.</Units>\r\n\t\t\t\t<Description>Last or Current Measured Value from the Sensor</Description>\r\n\t\t\t</Item>\r\n\t\t\t<Item ID=\"5601\">\r\n\t\t\t\t<Name>Min Measured Value</Name>\r\n\t\t\t\t<Operations>R</Operations>\r\n\t\t\t\t<MultipleInstances>Single</MultipleInstances>\r\n\t\t\t\t<Mandatory>Optional</Mandatory>\r\n\t\t\t\t<Type>Float</Type>\r\n\t\t\t\t<RangeEnumeration></RangeEnumeration>\r\n\t\t\t\t<Units>Defined by \u201CUnits\u201D resource.</Units>\r\n\t\t\t\t<Description>The minimum value measured by the sensor since power ON or reset</Description>\r\n\t\t\t</Item>\r\n\t\t\t<Item ID=\"5602\">\r\n\t\t\t\t<Name>Max Measured Value</Name>\r\n\t\t\t\t<Operations>R</Operations>\r\n\t\t\t\t<MultipleInstances>Single</MultipleInstances>\r\n\t\t\t\t<Mandatory>Optional</Mandatory>\r\n\t\t\t\t<Type>Float</Type>\r\n\t\t\t\t<RangeEnumeration></RangeEnumeration>\r\n\t\t\t\t<Units>Defined by \u201CUnits\u201D resource.</Units>\r\n\t\t\t\t<Description>The maximum value measured by the sensor since power ON or reset</Description>\r\n\t\t\t</Item>\r\n\t\t\t<Item ID=\"5603\">\r\n\t\t\t\t<Name>Min Range Value</Name>\r\n\t\t\t\t<Operations>R</Operations>\r\n\t\t\t\t<MultipleInstances>Single</MultipleInstances>\r\n\t\t\t\t<Mandatory>Optional</Mandatory>\r\n\t\t\t\t<Type>Float</Type>\r\n\t\t\t\t<RangeEnumeration></RangeEnumeration>\r\n\t\t\t\t<Units>Defined by \u201CUnits\u201D resource.</Units>\r\n\t\t\t\t<Description>The minimum value that can be measured by the sensor</Description>\r\n\t\t\t</Item>\r\n\t\t\t<Item ID=\"5604\">\r\n\t\t\t\t<Name>Max Range Value</Name>\r\n\t\t\t\t<Operations>R</Operations>\r\n\t\t\t\t<MultipleInstances>Single</MultipleInstances>\r\n\t\t\t\t<Mandatory>Optional</Mandatory>\r\n\t\t\t\t<Type>Float</Type>\r\n\t\t\t\t<RangeEnumeration></RangeEnumeration>\r\n\t\t\t\t<Units>Defined by \u201CUnits\u201D resource.</Units>\r\n\t\t\t\t<Description>The maximum value that can be measured by the sensor</Description>\r\n\t\t\t</Item>\r\n\t\t\t<Item ID=\"5701\">\r\n\t\t\t\t<Name>Sensor Units</Name>\r\n\t\t\t\t<Operations>R</Operations>\r\n\t\t\t\t<MultipleInstances>Single</MultipleInstances>\r\n\t\t\t\t<Mandatory>Optional</Mandatory>\r\n\t\t\t\t<Type>String</Type>\r\n\t\t\t\t<RangeEnumeration></RangeEnumeration>\r\n\t\t\t\t<Units></Units>\r\n\t\t\t\t<Description>Measurement Units Definition e.g. \u201CCel\u201D for Temperature in Celsius.</Description>\r\n\t\t\t</Item>\r\n\t\t\t<Item ID=\"5605\">\r\n\t\t\t\t<Name>Reset Min and Max Measured Values</Name>\r\n\t\t\t\t<Operations>E</Operations>\r\n\t\t\t\t<MultipleInstances>Single</MultipleInstances>\r\n\t\t\t\t<Mandatory>Optional</Mandatory>\r\n\t\t\t\t<Type>String</Type>\r\n\t\t\t\t<RangeEnumeration></RangeEnumeration>\r\n\t\t\t\t<Units></Units>\r\n\t\t\t\t<Description>Reset the Min and Max Measured Values to Current Value</Description>\r\n\t\t\t</Item>\r\n\t\t</Resources>\r\n\t\t<Description2></Description2>\r\n\t</Object>\r\n</LWM2M>";
		//HashMap<String, String> bodyMap = new HashMap<String, String>();
		//bodyMap.put("objCont", content);
		//request.setContent(new Gson().toJson(bodyMap).getBytes());
		ObjectModelInfoVO objectModel = new ObjectModelInfoVO();
		objectModel.setObjNm("test");
		objectModel.setObjCont(content);

		JsonResult result = objModelController.checkObectModel(objectModel, request, response);

		assertEquals(result.getResult(), "fail");
		System.out.println("error : "+result.getErrorMsg());
	}
}
