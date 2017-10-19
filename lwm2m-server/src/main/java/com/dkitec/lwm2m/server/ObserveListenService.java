package com.dkitec.lwm2m.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.server.observation.ObservationListener;
import org.eclipse.leshan.server.registration.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.dkitec.lwm2m.common.code.ComCode;
import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.domain.CoapResponseVO;
import com.dkitec.lwm2m.domain.message.MessageRefInfoVO;
import com.dkitec.lwm2m.server.message.MessageTracer;
import com.dkitec.lwm2m.service.message.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

/***
 * Observation Listener 단말에서 observation이 발생 되었을때 사용한다. 저장된 구독 정보를 notify 처리한다.
 */
@Service
public class ObserveListenService implements ObservationListener {

	private static final Logger logger = LoggerFactory.getLogger(ObserveListenService.class);

	@Autowired
	DefaultLwm2mServer leshanServer;

	@Autowired
	MessageService messageService;

	@PostConstruct
	public void regObservListenService() {
		leshanServer.getLwm2mServer().getObservationService().addListener(this);
	}

	@Override
	public void newObservation(Observation observation, Registration registration) {
		// TODO Auto-generated method stub
		logger.debug("### create Observation");
		// logger.debug("reg : "+new Gson().toJson(registration));
		logger.debug("obser : " + new Gson().toJson(observation));
	}

	@Override
	public void cancelled(Observation observation) {
		// TODO Auto-generated method stub
		logger.debug("### cancel Observation");
	}

	@Override
	public void onResponse(Observation observation, Registration registration, ObserveResponse response) {
		// TODO Auto-generated method stub
		logger.debug("### observation Response");
		logger.debug("observation : " + new Gson().toJson(observation));
		sendSubscription(observation, response);
	}

	@Override
	public void onError(Observation observation, Registration registration, Exception error) {
		// TODO Auto-generated method stub

	}

	private void sendSubscription(Observation observation, ObserveResponse response) {

		Map<String, String> context = observation.getContext();
		String subUrl = context.get(ComCode.Lwm2mKey.subscriptionURL.getValue());

		if (!CommonUtil.isEmpty(subUrl)) {
			logger.debug("[구독 전달] 주소 : " + subUrl);
			try {
				CloseableHttpClient httpclient = HttpClients.createDefault();
				int responseCode = 0;
				String payload = null;
				HttpPost httpPost = new HttpPost(subUrl);
				HttpEntity entity = null;
				try {
					httpPost.setHeader("Accept", "application/xml;charset=UTF-8");
					httpPost.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);

					payload = new Gson().toJson(response.getContent());
					if (!CommonUtil.isEmpty(payload)) {
						StringEntity requestEntity = null;
						try {
							requestEntity = new StringEntity(payload);
						} catch (UnsupportedEncodingException e) {
							LoggerPrint.printErrorLogExceptionrMsg(logger, e, "Observe Notify");
						}
						requestEntity.setChunked(true);
						httpPost.setEntity(requestEntity);
					}
					CloseableHttpResponse httpResponse = null;
					try {
						httpResponse = httpclient.execute(httpPost);
						System.out.println(httpResponse.getStatusLine());
						entity = httpResponse.getEntity();
						responseCode = httpResponse.getStatusLine().getStatusCode();
						EntityUtils.consume(entity);
					} catch (Exception e) {
						LoggerPrint.printErrorLogExceptionrMsg(logger, e, "Observe Notify");
					} finally {
						httpResponse.close();
					}
				} finally {
					httpclient.close();
					System.out.println("notify info : " + new Gson().toJson(response));
					try {
						Object obj = response.getCoapResponse();
						ObjectMapper mapper = new ObjectMapper();
						Response respvo = mapper.convertValue(obj, Response.class);
						String tId = CommonUtil.makeMessageTid(respvo.getSource().getHostAddress(), respvo.getMID()+"",
								respvo.getTokenString());
						String deviceId = leshanServer.getLwm2mServer().getRegistrationService().getById(response.getObservation().getRegistrationId()).getEndpoint();
						SimpleDateFormat sf = new SimpleDateFormat(ComCode.DataFormat.DateStrFormat.getValue());
						String creDatm = sf.format(new Date());

						MessageRefInfoVO msgRef = new MessageRefInfoVO();
						msgRef.setCoapResType(respvo.getType().toString());
						msgRef.setTokenId(respvo.getTokenString());
						String resbody = (entity == null)? null : entity.toString();
						messageService.insertHttpNotifyMsg(tId, deviceId, creDatm, httpPost, payload, responseCode,
							resbody, msgRef);
					} catch (IllegalArgumentException e) {
						LoggerPrint.printErrorLogExceptionrMsg(logger, e);
					}
				}
			} catch (IOException e) {
				LoggerPrint.printErrorLogExceptionrMsg(logger, e, "Observe Notify");
			}
		}
	}
}
