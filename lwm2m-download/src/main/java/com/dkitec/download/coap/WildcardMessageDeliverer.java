package com.dkitec.download.coap;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.observe.ObserveManager;
import org.eclipse.californium.core.observe.ObserveRelation;
import org.eclipse.californium.core.observe.ObservingEndpoint;
import org.eclipse.californium.core.server.MessageDeliverer;
import org.eclipse.californium.core.server.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request Path의 와일드카드(/*)를 담당하는 Coap Resource로 넘기는 MessageDeliverer 
 */
public class WildcardMessageDeliverer implements MessageDeliverer {
	
	/**
	 * logger
	 */
	private final Logger logger = LoggerFactory.getLogger(WildcardMessageDeliverer.class);
	
	/**
	 *  The root of all resources 
	 */
	private final Resource root;
	
	/** 
	 * wildcard rootpath 
	 */
	private final String path;
	
	/**
	 * The manager of the observe mechanism for this server 
	 */
	private final ObserveManager observeManager = new ObserveManager();
	
	/**
	 * 생성자
	 * @param root
	 * @param path
	 */
	public WildcardMessageDeliverer(final Resource root, final String path) {
		this.root = root;
		this.path = path;
	}

	/**
	 * 
	 */
	@Override
	public void deliverRequest(final Exchange exchange) {
		Request request = exchange.getRequest();
		List<String> reqPaths = request.getOptions().getUriPath();
		final Resource resource = findResource(reqPaths);
		
		if(resource != null) {
			checkForObserveOption(exchange, resource);
			
			Executor executor = resource.getExecutor();
			if(executor != null) {
				exchange.setCustomExecutor();
				executor.execute(new Runnable() {
					@Override
					public void run() {
						resource.handleRequest(exchange);
					}
				});
			}
			else {
				resource.handleRequest(exchange);
			}
		}
		else {
			logger.info("Did not find resource {} requested by {}:{}",
					new Object[]{reqPaths, request.getSource(), request.getSourcePort()});
			exchange.sendResponse(new Response(ResponseCode.NOT_FOUND));
		}
	}

	/**
	 * 
	 * @param exchange
	 * @param resource
	 */
	private void checkForObserveOption(Exchange exchange, Resource resource) {
		Request request = exchange.getRequest();
		if(request.getCode() != Code.GET) {
			return;
		}
		InetSocketAddress source = new InetSocketAddress(request.getSource(),  request.getSourcePort());
		
		// observe 옵션 존재 여부 확인
		if(request.getOptions().hasObserve() && resource.isObservable()) {
			
			// observe 설정
			if(request.getOptions().getObserve() == 0) {
				// Requests wants to observe and resource allows it :-)
				logger.debug("Initiate an observe relation between {}:{} and resource {}",
						new Object[]{request.getSource(), request.getSourcePort(), resource.getURI()});
				
				ObservingEndpoint remote = observeManager.findObservingEndpoint(source);
				ObserveRelation relation = new ObserveRelation(remote, resource, exchange);
				remote.addObserveRelation(relation);
				exchange.setRelation(relation);
			}
			// observe 해제
			else if(request.getOptions().getObserve() == 1) {
				ObserveRelation relation = observeManager.getRelation(source, request.getToken());
				if(relation != null) relation.cancel();
			}
		}
	}

	/**
	 * 
	 * @param paths
	 * @return
	 */
	private Resource findResource(final List<String> paths) {
		LinkedList<String> path = new LinkedList<String>(paths);
		Resource current = root;
		
		if(!"".equals(root.getName())) {
			path.removeFirst();
		}
		
		while(!path.isEmpty() && current != null) {
			String name = path.removeFirst();
			current = current.getChild(name);
			
			if(this.path.equals(name)) {
				return root.getChild(this.path);
			}
		}
		return current;
	}

	/**
	 * 
	 */
	@Override
	public void deliverResponse(Exchange exchange, Response response) {
		if (response == null) {
			throw new NullPointerException("Response must not be null");
		} 
		else if (exchange == null) {
			throw new NullPointerException("Exchange must not be null");
		} 
		else if (exchange.getRequest() == null) {
			throw new IllegalArgumentException("Exchange does not contain request");
		} 
		else {
			exchange.getRequest().setResponse(response);
		}
	}

}
