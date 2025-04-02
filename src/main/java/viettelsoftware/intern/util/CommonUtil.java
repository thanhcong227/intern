package viettelsoftware.intern.util;

import lombok.experimental.UtilityClass;
import viettelsoftware.intern.constant.AppConstant;

@UtilityClass
public class CommonUtil {

    public boolean isValidEmail(String email) {
        if (email == null)
            return false;
        else
            return email.matches(AppConstant.REGEX_EMAIL);
    }
}


