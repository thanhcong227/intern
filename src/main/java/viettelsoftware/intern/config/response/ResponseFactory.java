package viettelsoftware.intern.config.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import viettelsoftware.intern.constant.ResponseStatusCode;
import viettelsoftware.intern.constant.ResponseStatusCodeEnum;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.service.ErrorService;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class ResponseFactory {

    private final ErrorService errorService;

    public ResponseFactory(ErrorService errorService) {
        this.errorService = errorService;
    }

    private ResponseStatus parseResponseStatus(String code) {
        ResponseStatus responseStatus = new ResponseStatus(code, true);

        if (Objects.nonNull(this.errorService)) {
            String errorDetail = this.errorService.getErrorDetail(code, LocaleContextHolder.getLocale().getLanguage());
            if (Objects.nonNull(errorDetail)) {
                responseStatus.setMessage(errorDetail);
            }
        }

        log.debug(responseStatus.toString());
        return responseStatus;
    }

    public <T> ResponseEntity<GeneralResponse<T>> success(T data) {
        GeneralResponse<T> response = new GeneralResponse<>();
        response.setData(data);
        return this.success(response);
    }

    public <T> ResponseEntity<GeneralResponse<T>> success(GeneralResponse<T> response) {
        ResponseStatus responseStatus =this.parseResponseStatus(ResponseStatusCodeEnum.SUCCESS.getCode());
        response.setStatus(responseStatus);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<GeneralResponse<Object>> successNoData() {
        GeneralResponse<Object> responseObject = new GeneralResponse<>();
        return this.success(responseObject);
    }

    public <T> ResponseEntity<GeneralResponse<T>> successWithHeader(MultiValueMap<String, String> header, T data) {
        GeneralResponse<T> responseObject = new GeneralResponse<>();
        responseObject.setData(data);
        ResponseStatus responseStatus = this.parseResponseStatus(ResponseStatusCodeEnum.SUCCESS.getCode());
        responseObject.setStatus(responseStatus);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.addAll(header);
        return (ResponseEntity.ok().headers(responseHeaders)).body(responseObject);
    }

    public <T> ResponseEntity<GeneralResponse<T>> fail(T data, ResponseStatusCode code) {
        GeneralResponse<T> responseObject = new GeneralResponse<>();
        responseObject.setData(data);
        return this.fail(responseObject, code);
    }

    public <T> ResponseEntity<GeneralResponse<T>> fail(ResponseStatusCode code) {
        GeneralResponse<T> responseObject = new GeneralResponse<>();
        return this.fail(responseObject, code);
    }

    public <T> ResponseEntity<GeneralResponse<T>> fail(GeneralResponse<T> responseObject, ResponseStatusCode code) {
        ResponseStatus responseStatus = this.parseResponseStatus(code.getCode());
        if (Objects.isNull(responseObject)) {
            responseObject = new GeneralResponse<>();
        }

        responseObject.setStatus(responseStatus);
        return ResponseEntity.ok().body(responseObject);
    }

}
