package com.nashtech.rookies.assetmanagement.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;


//@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final HandlerExceptionResolver handleExceptionResolver;

    public CustomAccessDeniedHandler(@Qualifier("handlerExceptionResolver")HandlerExceptionResolver handleExceptionResolver) {
        this.handleExceptionResolver = handleExceptionResolver;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        this.handleExceptionResolver.resolveException(request, response, null, accessDeniedException);
    }

}