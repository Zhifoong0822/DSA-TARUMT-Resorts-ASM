package utility;

/**
 * Reusable validation methods for user-entered values.
 */
public final class ValidationHelper {

    private ValidationHelper() {
        // Utility class
    }

    public static boolean isNonBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isPositiveInt(int value) {
        return value > 0;
    }

    public static boolean isNonNegative(double value) {
        return value >= 0;
    }

    public static boolean isValidConfirmationNumber(String value) {
        return value != null && value.matches("\\d{8}");
    }

    public static boolean isValidIcNumber(String value) {
        return value != null && value.matches("\\d{12}");
    }

    public static boolean isValidRoomType(String value) {
        return value != null
                && (value.equalsIgnoreCase("Deluxe")
                || value.equalsIgnoreCase("Suite")
                || value.equalsIgnoreCase("Penthouse"));
    }
}
