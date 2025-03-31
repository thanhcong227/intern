package viettelsoftware.intern.exception;

import lombok.Getter;
import lombok.Setter;
import viettelsoftware.intern.constant.ErrorCode;

@Setter
@Getter
public class AppException extends RuntimeException {
     private final ErrorCode errorCode;

     public AppException(ErrorCode errorCode) {
         super(errorCode.getMessages());
         this.errorCode = errorCode;
     }
}
