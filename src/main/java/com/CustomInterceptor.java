package com;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomInterceptor implements HandlerInterceptor {
	
	private final String[] arr = {
			"/logout",
			"/login",
			"/signup"
	};

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		try {
			String uri = request.getRequestURI();
			if(request.getSession(false)==null && !omitSession(uri)) {
				return false;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean omitSession(String url) {
		for(String a: arr) {
			if(a.equals(url)) {
				return true;
			}
		}
		return false;
	}
}