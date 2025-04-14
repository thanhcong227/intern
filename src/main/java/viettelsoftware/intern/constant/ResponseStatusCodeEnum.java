package viettelsoftware.intern.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseStatusCodeEnum {
    public static final ResponseStatusCode SUCCESS = ResponseStatusCode.builder().code("2000").httpCode(200).build();
    public static final ResponseStatusCode USER_NOT_FOUND = ResponseStatusCode.builder().code("101").httpCode(404).build();
    public static final ResponseStatusCode USER_EXISTED = ResponseStatusCode.builder().code("105").httpCode(400).build();
    public static final ResponseStatusCode USERNAME_OR_PASSWORD_INCORRECT = ResponseStatusCode.builder().code("100").httpCode(400).build();
    public static final ResponseStatusCode ROLE_NOT_FOUND = ResponseStatusCode.builder().code("102").httpCode(404).build();
    public static final ResponseStatusCode ROLE_EXISTED = ResponseStatusCode.builder().code("104").httpCode(400).build();
    public static final ResponseStatusCode PERMISSION_NOT_FOUND = ResponseStatusCode.builder().code("107").httpCode(404).build();
    public static final ResponseStatusCode PERMISSION_EXISTED = ResponseStatusCode.builder().code("106").httpCode(400).build();
    public static final ResponseStatusCode BORROWING_NOT_FOUND = ResponseStatusCode.builder().code("108").httpCode(404).build();
    public static final ResponseStatusCode BORROWING_EXISTED = ResponseStatusCode.builder().code("109").httpCode(400).build();
    public static final ResponseStatusCode BOOK_NOT_FOUND = ResponseStatusCode.builder().code("110").httpCode(404).build();
    public static final ResponseStatusCode BOOK_EXISTED = ResponseStatusCode.builder().code("111").httpCode(400).build();
    public static final ResponseStatusCode POST_NOT_FOUND = ResponseStatusCode.builder().code("112").httpCode(404).build();
    public static final ResponseStatusCode POST_EXISTED = ResponseStatusCode.builder().code("113").httpCode(400).build();
    public static final ResponseStatusCode COMMENT_NOT_FOUND = ResponseStatusCode.builder().code("114").httpCode(404).build();
    public static final ResponseStatusCode COMMENT_EXISTED = ResponseStatusCode.builder().code("115").httpCode(400).build();
    public static final ResponseStatusCode GENRE_EXISTED = ResponseStatusCode.builder().code("117").httpCode(400).build();
    public static final ResponseStatusCode GENRE_NOT_FOUND = ResponseStatusCode.builder().code("116").httpCode(404).build();
    public static final ResponseStatusCode BORROWING_BOOK_CREATION_FAILED = ResponseStatusCode.builder().code("118").httpCode(400).build();
    public static final ResponseStatusCode BORROWING_BOOK_NOT_FOUND = ResponseStatusCode.builder().code("119").httpCode(404).build();
    public static final ResponseStatusCode FILE_PROCESSING_ERROR = ResponseStatusCode.builder().code("120").httpCode(400).build();
    public static final ResponseStatusCode UNKNOWN = ResponseStatusCode.builder().code("555").httpCode(400).build();
    public static final ResponseStatusCode INVALID_FILE_FORMAT = ResponseStatusCode.builder().code("121").httpCode(400).build();
    public static final ResponseStatusCode DATABASE_ERROR = ResponseStatusCode.builder().code("123").httpCode(400).build();
    public static final ResponseStatusCode INVALID_EMAIL = ResponseStatusCode.builder().code("124").httpCode(400).build();
    public static final ResponseStatusCode TOKEN_INVALID = ResponseStatusCode.builder().code("125").httpCode(400).build();
    public static final ResponseStatusCode TOKEN_EXPIRED = ResponseStatusCode.builder().code("126").httpCode(400).build();
    public static final ResponseStatusCode BOOK_NOT_ENOUGH = ResponseStatusCode.builder().code("127").httpCode(400).build();
    public static final ResponseStatusCode BOOK_QUANTITY_INVALID = ResponseStatusCode.builder().code("128").httpCode(400).build();
    public static final ResponseStatusCode INVALID_FILE = ResponseStatusCode.builder().code("122").httpCode(400).build();
    public static final ResponseStatusCode BOOK_OUT_OF_STOCK = ResponseStatusCode.builder().code("131").httpCode(400).build();

}