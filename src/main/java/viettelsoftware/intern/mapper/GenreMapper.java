package viettelsoftware.intern.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import viettelsoftware.intern.dto.request.GenreRequest;
import viettelsoftware.intern.dto.response.GenreResponse;
import viettelsoftware.intern.entity.BookEntity;
import viettelsoftware.intern.entity.GenreEntity;
import viettelsoftware.intern.entity.RoleEntity;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    GenreResponse toGenreResponse(GenreEntity genreEntity);

    @Mapping(target = "books", ignore = true)
    GenreEntity toGenre(GenreRequest request);

    default Set<String> mapBooks(Set<BookEntity> books) {
        return books != null ? books.stream().map(BookEntity::getTitle).collect(Collectors.toSet()) : Collections.emptySet();
    }
}
