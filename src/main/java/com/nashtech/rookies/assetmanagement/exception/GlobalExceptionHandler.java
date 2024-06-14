package com.nashtech.rookies.assetmanagement.exception;

import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidUserCredentialException.class)
    public ResponseEntity<ResponseDto<Void>> handleInvalidUserCredentialException(InvalidUserCredentialException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ResponseDto.<Void>builder()
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler({BadCredentialsException.class})
    protected ResponseEntity<ResponseDto<Void>> handleBadCredentialsException(BadCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ResponseDto.<Void>builder()
                        .message("Invalid username or password.")
                        .build()
        );
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<ResponseDto<Void>> handleException(Exception exception) {
        log.info("Exception occurred....", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ResponseDto.<Void>builder()
                        .message("Some errors occur. Please check the server log.")
                        .build()
        );
    }
}