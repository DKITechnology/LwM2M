package com.dkitec.lwm2m.server;

import java.net.BindException;
import java.security.KeyStore;
import java.util.List;

import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.leshan.LwM2m;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.codec.DefaultLwM2mNodeDecoder;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.cluster.RedisRegistrationEventPublisher;
import org.eclipse.leshan.server.cluster.RedisRegistrationStore;
import org.eclipse.leshan.server.cluster.RedisRequestResponseHandler;
import org.eclipse.leshan.server.cluster.RedisSecurityStore;
import org.eclipse.leshan.server.cluster.RedisTokenHandler;
import org.eclipse.leshan.server.model.LwM2mModelProvider;
import org.eclipse.leshan.server.model.StaticModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.server.message.MessageTracer;
import com.dkitec.lwm2m.service.intf.ServerInitService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.Pool;

/**
 * Eclipse LESHAN SEVER BUILD 
 * 2017.06
 */
public class DefaultLwm2mServer {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultLwm2mServer.class);
	
	//LeshanSever Instance
	private LeshanServer lwm2mServer;	
	public LeshanServer getLwm2mServer() {
		return lwm2mServer;
	}
	public void setLwm2mServer(LeshanServer lwServer) {
		this.lwm2mServer = lwServer;
	}

	//LeshanServer Config Value;
	private String clusterInstanceId = "1";
	private String[] modelPaths;
	private String modelsFolderPath;
	private JedisPool jedisPool;	
	private String serverAddress;
	private String secureAddress;
	private int coapServerPort = LwM2m.DEFAULT_COAP_PORT;
    private int secureCaopServerPort = LwM2m.DEFAULT_COAP_SECURE_PORT; 

    // Get keystore parameters
    private String keyStorePath;
    private String keyStoreType = KeyStore.getDefaultType();
    private String keyStorePass;
    private String keyStoreAlias;
    private String keyStoreAliasPass;
    
    //messageInfo
    MessageTracer messageTracer;
    
    private ServerInitService serverInitService;

	public String getClusterInstanceId() {
		return clusterInstanceId;
	}
	public void setClusterInstanceId(String clusterInstanceId) {
		this.clusterInstanceId = clusterInstanceId;
	}
	public String[] getModelPaths() {
		return modelPaths;
	}
	public void setModelPaths(String[] modelPaths) {
		this.modelPaths = modelPaths;
	}
	public String getModelsFolderPath() {
		return modelsFolderPath;
	}
	public void setModelsFolderPath(String modelsFolderPath) {
		this.modelsFolderPath = modelsFolderPath;
	}
	public JedisPool getJedisPool() {
		return jedisPool;
	}
	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	public String getServerAddress() {
		return serverAddress;
	}
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	public String getSecureAddress() {
		return secureAddress;
	}
	public void setSecureAddress(String secureAddress) {
		this.secureAddress = secureAddress;
	}
	public int getCoapServerPort() {
		return coapServerPort;
	}
	public void setCoapServerPort(int coapServerPort) {
		this.coapServerPort = coapServerPort;
	}
	public int getSecureCaopServerPort() {
		return secureCaopServerPort;
	}
	public void setSecureCaopServerPort(int secureCaopServerPort) {
		this.secureCaopServerPort = secureCaopServerPort;
	}
	public String[] getModelpaths() {
		return modelPaths;
	}
	
	//key Store Info
    public String getKeyStorePath() {
		return keyStorePath;
	}
	public void setKeyStorePath(String keyStorePath) {
		this.keyStorePath = keyStorePath;
	}
	public String getKeyStoreType() {
		return keyStoreType;
	}
	public void setKeyStoreType(String keyStoreType) {
		this.keyStoreType = keyStoreType;
	}
	public String getKeyStorePass() {
		return keyStorePass;
	}
	public void setKeyStorePass(String keyStorePass) {
		this.keyStorePass = keyStorePass;
	}
	public String getKeyStoreAlias() {
		return keyStoreAlias;
	}
	public void setKeyStoreAlias(String keyStoreAlias) {
		this.keyStoreAlias = keyStoreAlias;
	}
	public String getKeyStoreAliasPass() {
		return keyStoreAliasPass;
	}
	public void setKeyStoreAliasPass(String keyStoreAliasPass) {
		this.keyStoreAliasPass = keyStoreAliasPass;
	}

    public MessageTracer getMessageTracer() {
		return messageTracer;
	}
	public void setMessageTracer(MessageTracer messageTracer) {
		this.messageTracer = messageTracer;
	}
	
	public void setServerInitService(ServerInitService serverInitService) {
		this.serverInitService = serverInitService;
	}
	public void statrLeshanServer() {
    	logger.info("###### Leshan Start Server : " + serverAddress);
        try {
            createAndStartClusterServer(clusterInstanceId, serverAddress, coapServerPort, secureAddress, secureCaopServerPort, modelsFolderPath);
        } catch (BindException e) {
        	LoggerPrint.printErrorLogExceptionrMsg(logger, e);
        } catch (Exception e) {
            LoggerPrint.printErrorLogExceptionrMsg(logger, e);
        }
    }
    
	public void createAndStartClusterServer(String clusterInstanceId, String localAddress, int localPort,
            String secureLocalAddress, int secureLocalPort, String modelsFolderPath) throws Exception {
    	
    	Pool<Jedis> jedis = jedisPool;
    	
        // Prepare LWM2M server.
        LeshanServerBuilder builder = new LeshanServerBuilder();
        builder.setLocalAddress(localAddress, localPort);
        builder.setLocalSecureAddress(secureLocalAddress, secureLocalPort);
        DefaultLwM2mNodeDecoder decoder = new DefaultLwM2mNodeDecoder();
        builder.setDecoder(decoder);
        builder.setNetworkConfig(NetworkConfig.getStandard());

        List<ObjectModel> models = ObjectLoader.loadDefault();
        
        //classpath에 저장하는 경우
        /*if(modelPaths != null)
        	models.addAll(ObjectLoader.loadDdfResources("/models/", modelPaths));
        if (modelsFolderPath != null) {
            models.addAll(ObjectLoader.loadObjectsFromDir(new File(modelsFolderPath)));
        }*/
        
        try {
			List<ObjectModel> addtionModels = serverInitService.selectLoadObjects();
			if(addtionModels != null && addtionModels.size() > 0)
				models.addAll(addtionModels);
		} catch (Exception e) { LoggerPrint.printErrorLogExceptionrMsg(logger, e);}
        
        LwM2mModelProvider modelProvider = new StaticModelProvider(models);
        builder.setObjectModelProvider(modelProvider);

        builder.setRegistrationStore(new RedisRegistrationStore(jedis));
        builder.setSecurityStore(new RedisSecurityStore(jedis));
        
        // Create and start LWM2M server
        lwm2mServer = builder.build();

        // Create Clustering support
        RedisTokenHandler tokenHandler = new RedisTokenHandler(jedis, clusterInstanceId);
        new RedisRequestResponseHandler(jedis, lwm2mServer, lwm2mServer.getRegistrationService(), tokenHandler,
                lwm2mServer.getObservationService());
        lwm2mServer.getRegistrationService().addListener(tokenHandler);
        lwm2mServer.getRegistrationService().addListener(new RedisRegistrationEventPublisher(jedis));

        //
        //Message Interceptor ( MessageInfo Save )        
        if(messageTracer != null){
        	for (Endpoint endpoint : lwm2mServer.getCoapServer().getEndpoints()) {
                endpoint.addInterceptor(messageTracer);
            }	
        }
        
        lwm2mServer.start();
    }
}
