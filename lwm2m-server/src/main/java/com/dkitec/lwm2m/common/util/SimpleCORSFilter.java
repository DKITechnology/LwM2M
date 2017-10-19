package com.dkitec.lwm2m.common.util;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class SimpleCORSFilter implements Filter {
 
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    	HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		
		if(request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
			response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
			response.addHeader("Access-Control-Allow-Headers", "Authorization, X-Request-With, Content-Type, Accept"
					+ ", loginToken, adminId, menuAuthCd, menuId, CoapResultCd");
			response.addHeader("Access-Control-Expose-Headers", "Content-Disposition, CoapResultCd");
			response.addHeader("Access-Control-Max-Age", "1728000");
		}
		chain.doFilter(request, response);
    }
    public void init(FilterConfig filterConfig) {}
    public void destroy() {}
}