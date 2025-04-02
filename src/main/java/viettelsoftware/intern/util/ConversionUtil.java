package viettelsoftware.intern.util;


import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public class ConversionUtil {

    public static <T, R> List<R> convertList(List<T> list, Function<T, R> func) {
        return list.stream().map(func).toList();
    }

    public static <T, R> R convertObject(T t, Function<T, R> func) {
        return func.apply(t);
    }

    public static <T, R> Page<R> convertPage(Page<T> page, Function<T, R> func) {
        return page.map(func);
    }

}
