package viettelsoftware.intern.util;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Predicate;

public class UpdateHelper {

    // Dành cho String hoặc Object
    public static <T> void updateIfNotNull(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

    // Dành cho int, double, float,... (kiểu nguyên/thực)
    public static void updateIfPositive(IntConsumer setter, int value) {
        if (value > 0) {
            setter.accept(value);
        }
    }

    public static void updateIfNonNegative(IntConsumer setter, int value) {
        if (value >= 0) {
            setter.accept(value);
        }
    }

    public static void updateIfTrue(BooleanSupplier condition, Runnable action) {
        if (condition.getAsBoolean()) {
            action.run();
        }
    }

    // Dành cho Set/List/... nếu không null hoặc không empty
    public static <T> void updateIfNotEmpty(Consumer<T> setter, T value, Predicate<T> notEmptyCheck) {
        if (value != null && notEmptyCheck.test(value)) {
            setter.accept(value);
        }
    }
}