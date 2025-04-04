package viettelsoftware.intern.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import viettelsoftware.intern.config.locale.Translator;
import viettelsoftware.intern.config.response.GeneralResponse;
import viettelsoftware.intern.config.response.ResponseStatus;
import viettelsoftware.intern.constant.ErrorCode;
import viettelsoftware.intern.dto.response.ApiResponse;

@ControllerAdvice
@Slf4j
public class GlobalExcepionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse> handingRuntimeException(RuntimeException e) {
        log.error("Error", e);
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode(ErrorCode.UNKNOWN.getCode());
        apiResponse.setMessage(ErrorCode.UNKNOWN.getMessages());
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse> handlingEntityNotFoundException(EntityNotFoundException e) {
        log.error("Error:", e);
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode(ErrorCode.USER_NOT_FOUND.getCode());
        apiResponse.setMessage(ErrorCode.USER_NOT_FOUND.getMessages());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse> handlingAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessages());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(apiResponse);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<GeneralResponse<Object>> handlingCustomException(CustomException e) {
        log.error("CustomException: {}", e.getMessage());

        GeneralResponse<Object> response = new GeneralResponse<>(null);
        response.setStatus(e.getResponseStatus());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GeneralResponse<String>> handleValidation(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String code = fieldError.getDefaultMessage();
        log.info("error: {}", e.getMessage());

        String message = Translator.toLocale(code);

        ResponseStatus status = new ResponseStatus(code, false);
        status.setMessage(message);

        GeneralResponse<String> response = new GeneralResponse<>(null);
        response.setStatus(status);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
