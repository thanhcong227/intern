package viettelsoftware.intern.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import viettelsoftware.intern.dto.response.BorrowingBookResponse;
import viettelsoftware.intern.dto.request.BorrowingBookRequest;
import viettelsoftware.intern.entity.BookEntity;
import viettelsoftware.intern.entity.BorrowingBook;

@Mapper(componentModel = "spring")
public interface BorrowingBookMapper {

    @Mapping(source = "book.bookId", target = "borrowingBookId")
    @Mapping(source = "book.title", target = "bookTitle")
    BorrowingBookResponse toBorrowingBookResponse(BorrowingBook borrowingBook);

    @Mapping(target = "borrowing", ignore = true)
    @Mapping(source = "bookId", target = "book")
    BorrowingBook toBorrowingBook(BorrowingBookRequest request);

    default BookEntity mapBookEntity(String bookId) {
        if (bookId == null) {
            return null;
        }
        BookEntity book = new BookEntity();
        book.setBookId(bookId);
        return book;
    }
}