package viettelsoftware.intern.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import viettelsoftware.intern.dto.request.GenreRequest;
import viettelsoftware.intern.dto.response.GenreResponse;
import viettelsoftware.intern.entity.GenreEntity;

public interface GenreService {
    GenreResponse create(GenreRequest request);

    GenreResponse update(String genreId, GenreRequest request);

    void delete(String genreId);
    GenreResponse getGenre(String genreId);
    Page<GenreResponse> getAllGenres(Pageable pageable);
    byte[] exportGenresToExcel();
}
