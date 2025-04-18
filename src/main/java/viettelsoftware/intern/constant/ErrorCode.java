package viettelsoftware.intern.constant;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(101,"User not found"),
    USER_EXISTED(105,"User existed"),
    USERNAME_OR_PASSWORD_INCORRECT(100, "Username or password incorrect"),
    ROLE_NOT_FOUND(102,"Role not found"),
    ROLE_EXISTED(104,"Role existed"),
    PERMISSION_NOT_FOUND(107,"Permission not found"),
    PERMISSION_EXISTED(106,"Permission existed"),
    BORROWING_NOT_FOUND(108,"Borrowing not found"),
    BORROWING_EXISTED(109,"Borrowing existed"),
    BOOK_NOT_FOUND(110,"Book not found"),
    BOOK_EXISTED(111,"Book existed"),
    POST_NOT_FOUND(112,"Post not found"),
    POST_EXISTED(113,"Post existed"),
    COMMENT_NOT_FOUND(114,"Comment not found"),
    COMMENT_EXISTED(115,"Comment existed"),
    GENRE_EXISTED(117,"Genre existed"),
    GENRE_NOT_FOUND(116,"Genre not found"),
    BORROWING_BOOK_CREATION_FAILED(118,"Borrowing book creation failed"),
    BORROWING_BOOK_NOT_FOUND(119,"Borrowing book not found"),
    UNKNOWN(555, "UNKNOWN"),
    DATABASE_ERROR(123, "Database error"),
    EMAIL_INVALID(124, "Invalid email"),
    TOKEN_INVALID(125, "Token invalid"),
    TOKEN_EXPIRED(126, "Token expired"),
    BOOK_NOT_ENOUGH(127, "Book not enough"),
    BOOK_QUANTITY_INVALID(128, "Book quantity invalid"),
    FILE_INVALID_FORMAT(121, "Invalid file format. Only .xlsx and .xls files are allowed"),
    FILE_PROCESSING_ERROR(120,"Failed to process the file"),
    FILE_INVALID(122, "File name is null"),

    ;

    final int code;
    final String messages;

    ErrorCode (int code, String messages) {
        this.code = code;
        this.messages = messages;
    }
}
