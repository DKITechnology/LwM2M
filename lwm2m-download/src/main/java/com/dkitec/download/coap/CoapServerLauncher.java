package com.dkitec.download.coap;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CoapServerLauncher {
	
	private final Logger logger = LoggerFactory.getLogger(CoapServerLauncher.class);
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Value("#{commonProps['coap.port']}")
	private String COAP_PORT;
	
	@Value("#{commonProps['coap.downloadpath']}")
	private String DOWNLOAD_PATH;
	
	@Value("#{commonProps['coap.blocksize']}")
	private String PREFERRED_BLOCK_SIZE;
	
	private CoapServer coapServer;

	/**
	 * 서버 시작
	 */
	@PostConstruct
	public void startServer() {
		
		// CoAP 서버 디폴트 네트워크 설정
		NetworkConfig networkConfig = NetworkConfig.getStandard();
		
		// BLOCK SIZE 설정
		networkConfig.setInt(NetworkConfig.Keys.PREFERRED_BLOCK_SIZE, Integer.parseInt(PREFERRED_BLOCK_SIZE));
		coapServer = new CoapServer(networkConfig);
		
		// 서버 엔드포인트 설정
		addEndpoints(coapServer);
		
		// download시 사용할 path 설정
		coapServer.setMessageDeliverer(new WildcardMessageDeliverer(coapServer.getRoot(), DOWNLOAD_PATH));
		
		// download CoapResource 추가
		for(CoapResource resource : applicationContext.getBeansOfType(CoapResource.class).values()) {
			coapServer.add(resource);
		}
		
		// 서버 시작
		coapServer.start();
		logger.info("\n CoAP server started."
			+ "\n - PORT                 : " + COAP_PORT
			+ "\n - DOWNLOAD_PATH        : " + DOWNLOAD_PATH
			+ "\n - PREFERRED_BLOCK_SIZE : " + PREFERRED_BLOCK_SIZE
		);
	}
	
	/**
	 * 서버 정지
	 */
	@PreDestroy
	public void stopServer() {
		if(coapServer != null) {
			coapServer.stop();
		}
	}
	
	/**
	 * coap 서버 EndPoint 설정
	 * @param coapServer
	 */
	private void addEndpoints(CoapServer coapServer) {
		for(InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
			if(addr instanceof Inet4Address || addr.isLoopbackAddress()) {
				InetSocketAddress bindToAddress = new InetSocketAddress(addr, Integer.parseInt(COAP_PORT));
				coapServer.addEndpoint(new CoapEndpoint(bindToAddress));
			}
		}
	}

}
