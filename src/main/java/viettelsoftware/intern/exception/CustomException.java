package viettelsoftware.intern.exception;

import lombok.Getter;
import lombok.Setter;
import viettelsoftware.intern.config.locale.Translator;
import viettelsoftware.intern.config.response.ResponseStatus;

@Getter
@Setter
public class CustomException extends RuntimeException {

    private final ResponseStatus responseStatus;

    public CustomException(String code) {
        super(Translator.toLocale(code));
        this.responseStatus = new ResponseStatus(code, true);
    }

    public CustomException(String code, String customMessage) {
        super(customMessage);
        this.responseStatus = new ResponseStatus(code, false);
        this.responseStatus.setMessage(customMessage);
    }
}
