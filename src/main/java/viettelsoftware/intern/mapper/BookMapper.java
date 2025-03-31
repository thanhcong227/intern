package viettelsoftware.intern.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import viettelsoftware.intern.dto.request.BookRequest;
import viettelsoftware.intern.dto.response.BookResponse;
import viettelsoftware.intern.entity.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "genres", source = "genres")
    @Mapping(target = "borrowingBooks", source = "borrowingBooks")
    BookResponse toBookResponse(BookEntity bookEntity);

    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "borrowingBooks", ignore = true)
    BookEntity toBook(BookRequest request);

    default Set<String> mapGenres(Set<GenreEntity> genres) {
        return genres != null ? genres.stream().map(GenreEntity::getName).collect(Collectors.toSet()) : Collections.emptySet();
    }

    default Set<String> mapBorrowingBooks(Set<BorrowingBook> borrowingBooks) {
        return borrowingBooks != null ? borrowingBooks.stream().map(BorrowingBook::getBorrowingBookId).collect(Collectors.toSet()) : Collections.emptySet();
    }
}
