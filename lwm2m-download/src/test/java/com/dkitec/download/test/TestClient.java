package com.dkitec.download.test;

import java.io.FileOutputStream;
import java.util.Arrays;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.Request;
import org.junit.Ignore;
import org.junit.Test;

public class TestClient {
	
	private String requestUri = "coap://localhost:15683/firmware/13";
	private String resultFileName = "C:\\test.txt";
	
	@Test
	@Ignore
	public void testCoapDownload() {
		CoapClient client = new CoapClient(requestUri);
		
		Request request = new Request(Code.GET, Type.CON);
		CoapResponse response = client.advanced(request);
		
		if(response != null) {
			byte[] payload = response.getPayload();
			
			synchronized(payload) {
				System.out.println("payload: " + Arrays.toString(payload));
				
				if(response.getOptions() != null) {
					System.out.println("BlockOption2: " + response.getOptions().getBlock2());
				}
				
				// file로 조회
				try(FileOutputStream fos = new FileOutputStream(resultFileName)) {
					fos.write(payload);	
				}
				catch(Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		else {
			System.out.println("response is null");
		}
	}
}