package viettelsoftware.intern.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import viettelsoftware.intern.dto.request.BorrowingRequest;
import viettelsoftware.intern.dto.response.BorrowingResponse;
import viettelsoftware.intern.entity.BorrowingBook;
import viettelsoftware.intern.entity.BorrowingEntity;
import viettelsoftware.intern.entity.UserEntity;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BorrowingMapper {

    @Mapping(target = "users", source = "user")
    BorrowingResponse toBorrowingResponse(BorrowingEntity borrowingEntity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "borrowings", ignore = true)
    BorrowingEntity toBorrowing(BorrowingRequest request);

    default Set<String> mapBorrowingBooksToIds(Set<BorrowingBook> borrowingBooks) {
        return borrowingBooks != null
                ? borrowingBooks.stream()
                .map(BorrowingBook::getBorrowingBookId)
                .collect(Collectors.toSet())
                : Collections.emptySet();
    }

    default String mapUserToFullName(UserEntity user) {
        return user != null ? user.getFullName() : null;
    }
}
