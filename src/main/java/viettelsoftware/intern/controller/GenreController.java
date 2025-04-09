package viettelsoftware.intern.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import viettelsoftware.intern.config.response.GeneralResponse;
import viettelsoftware.intern.config.response.ResponseFactory;
import viettelsoftware.intern.dto.request.GenreRequest;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.dto.response.GenreResponse;
import viettelsoftware.intern.service.impl.GenreServiceImpl;

@RestController
@RequestMapping("/genre")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreController {
    GenreServiceImpl genreServiceImpl;
    ResponseFactory responseFactory;

    @PostMapping()
    @PreAuthorize("hasAuthority('GENRE_MANAGE')")
    ResponseEntity<GeneralResponse<GenreResponse>> create(@RequestBody GenreRequest request) {
        return responseFactory.success(genreServiceImpl.create(request));
    }

    @PutMapping("/{genreId}")
    @PreAuthorize("hasAuthority('GENRE_MANAGE')")
    ResponseEntity<GeneralResponse<GenreResponse>> update(@PathVariable String genreId, @RequestBody GenreRequest request) {
        return responseFactory.success(genreServiceImpl.update(genreId, request));
    }

    @DeleteMapping("/{genreId}")
    @PreAuthorize("hasAuthority('GENRE_MANAGE')")
    ResponseEntity<GeneralResponse<Object>> delete(@PathVariable String genreId) {
        genreServiceImpl.delete(genreId);
        return responseFactory.successNoData();
    }

    @GetMapping("/{genreId}")
    @PreAuthorize("hasAuthority('GENRE_VIEW')")
    ResponseEntity<GeneralResponse<GenreResponse>> getGenre(@PathVariable String genreId) {
        return responseFactory.success(genreServiceImpl.getGenre(genreId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('GENRE_VIEW')")
    ResponseEntity<GeneralResponse<Page<GenreResponse>>> getAllGenres(@PageableDefault(size = 9) Pageable pageable) {
        return responseFactory.success(genreServiceImpl.getAllGenres(pageable));
    }

    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('EXPORT_DATA')")
    public ResponseEntity<byte[]> exportGenresToExcel() {
        byte[] excelData = genreServiceImpl.exportGenresToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=genres.xlsx");
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(excelData);
    }
}
