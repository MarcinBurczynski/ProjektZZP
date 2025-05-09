package org.example.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Slf4j
@Component
public class RequestLogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        long start = System.currentTimeMillis();
        try {
            chain.doFilter(req, res);
        }  finally {
            long ms = System.currentTimeMillis() - start;

            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            int status = response.getStatus();
            String clientIP = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");
            String sessionId = request.getRequestedSessionId();
            String contentType = response.getContentType();
            String fullUrl = uri + (queryString != null ? "?" + queryString : "");

            log.info("Request: {} {} -> Status: {} | Time: {} ms | IP: {} | UA: {} | Session: {} | Content-Type: {}",
                    method, fullUrl, status, ms, clientIP, userAgent, sessionId, contentType);
        }
    }
}
