package viettelsoftware.intern.service.impl;

import org.springframework.stereotype.Service;
import viettelsoftware.intern.service.ErrorService;

@Service
public class ErrorServiceImpl implements ErrorService {
    @Override
    @SuppressWarnings("all")
    public String getErrorDetail(String errorCode, String language) {
        switch (language) {
            case "vi": {
                switch (errorCode) {
                    case "00": {
                        return "Thành công";
                    }
                    default: {
                        return null;
                    }
                }
            }
            default: {
                switch (errorCode) {
                    case "00": {
                        return "success";
                    }
                    default: {
                        return null;
                    }
                }
            }
        }
    }
}
