package viettelsoftware.intern.config.response;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GeneralResponse<T> {
    private ResponseStatus status;
    private T data;

    public GeneralResponse(T data) {
        this.data = data;
    }
}
