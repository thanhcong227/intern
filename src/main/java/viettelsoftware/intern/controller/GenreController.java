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

    @PostMapping()
    @PreAuthorize("hasAuthority('GENRE_MANAGE')")
    ApiResponse<GenreResponse> create(@RequestBody GenreRequest request) {
        return ApiResponse.<GenreResponse>builder()
                .result(genreServiceImpl.create(request))
                .build();
    }

    @PutMapping("/{genreId}")
    @PreAuthorize("hasAuthority('GENRE_MANAGE')")
    ApiResponse<GenreResponse> update(@PathVariable String genreId, @RequestBody GenreRequest request) {
        return ApiResponse.<GenreResponse>builder()
                .result(genreServiceImpl.update(genreId, request))
                .build();
    }

    @DeleteMapping("/{genreId}")
    @PreAuthorize("hasAuthority('GENRE_MANAGE')")
    ApiResponse<Void> delete(@PathVariable String genreId) {
        genreServiceImpl.delete(genreId);
        return ApiResponse.<Void>builder()
                .message("Genre deleted successfully")
                .build();
    }

    @GetMapping("/{genreId}")
    @PreAuthorize("hasAuthority('GENRE_VIEW')")
    ApiResponse<GenreResponse> getGenre(@PathVariable String genreId) {
        return ApiResponse.<GenreResponse>builder()
                .result(genreServiceImpl.getGenre(genreId))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('GENRE_VIEW')")
    ApiResponse<Page<GenreResponse>> getAllGenres(@PageableDefault(size = 5) Pageable pageable) {
        return ApiResponse.<Page<GenreResponse>>builder()
                .result(genreServiceImpl.getAllGenres(pageable))
                .build();
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
