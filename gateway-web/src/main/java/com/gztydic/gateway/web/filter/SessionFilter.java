package com.gztydic.gateway.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gztydic.gateway.core.common.constant.SessionConstant;

public class SessionFilter implements Filter{
	
	private static String[] ignoreUrls;	//不需要过滤的路径
	
	public boolean isContains(String container, String[] regx) {
        boolean result = false;
        for (int i = 0; i < regx.length; i++) {
            if (container.indexOf(regx[i]) != -1) {
                return true;
            }
        }
        return result;
    }

	public void init(FilterConfig filterConfig) throws ServletException {
		String ignoreUrl = filterConfig.getInitParameter("ignoreUrl");
		ignoreUrls = ignoreUrl.split(";");
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse)servletResponse;
		
		if (!this.isContains(request.getRequestURI(), ignoreUrls)) {	//对指定页面不进行过滤
			HttpSession session = ((HttpServletRequest)request).getSession();
			//未登录或session已失效
			if(session.getAttribute(SessionConstant.SESSION_ATTRIBUTE_USER_INFO) == null){
				// 如果是ajax请求响应头会有，x-requested-with；  
	            if (request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")){
	            	response.setStatus(999);//表示session timeout
	            }else{
	                request.getRequestDispatcher("/toLogin.jsp").forward(servletRequest, servletResponse);
	            }
	            return;
			}
		}
		filterChain.doFilter(request, response);
	}
	
	public void destroy() {
		
	}
}
