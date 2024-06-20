package com.nashtech.rookies.assetmanagement.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (AccessDeniedException e) {
            log.error("Error occurred: {}", e.getMessage());
            var authenticationInstance = SecurityContextHolder.getContext().getAuthentication();
            log.info(authenticationInstance.toString());
            if (authenticationInstance instanceof AnonymousAuthenticationToken) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());

            } else {
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }

            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(
                    ResponseDto.<Void>builder()
                            .message("You don't have permission to perform this action.")
                            .build()
            ));
        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage());

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(
                    ResponseDto.<Void>builder()
                            .message("Some errors occur. Please check the server log.")
                            .build()
            ));
        }
    }
}
