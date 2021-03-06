package dev.felipeaguiar.financeiro.infrastructure.config.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import dev.felipeaguiar.financeiro.infrastructure.config.AppProperty;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

	@Autowired
	private AppProperty appProperty;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse) response;
		res.setHeader("Access-Control-Allow-Origin", appProperty.getSeguranca().getCorsOrigin());

		if (HttpMethod.OPTIONS.name().equalsIgnoreCase(((HttpServletRequest) request).getMethod())) {
			res.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE, PATCH");
			res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
			res.setHeader("Access-Control-Max-Age", "3600");

			res.setStatus(HttpServletResponse.SC_OK);
		} else {
			chain.doFilter(request, res);
		}
	}


}
