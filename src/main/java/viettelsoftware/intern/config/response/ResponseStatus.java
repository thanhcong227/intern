package viettelsoftware.intern.config.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import viettelsoftware.intern.config.locale.Translator;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponseStatus implements Serializable {
    private String code;
    private String message;

    public ResponseStatus(String code, boolean setMessageImplicitly) {
        this.setCode(code, setMessageImplicitly);
    }

    public void setCode(String code, boolean setMessageImplicitly) {
        this.code = code;
        if (setMessageImplicitly) {
            this.message = Translator.toLocale(code);
        }
    }

}
