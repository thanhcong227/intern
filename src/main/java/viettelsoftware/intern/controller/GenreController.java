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
import org.springframework.web.bind.annotation.*;
import viettelsoftware.intern.dto.request.GenreRequest;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.dto.response.GenreResponse;
import viettelsoftware.intern.entity.GenreEntity;
import viettelsoftware.intern.service.impl.GenreServiceImpl;

@RestController
@RequestMapping("/genre")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreController {
    GenreServiceImpl genreServiceImpl;

    @PostMapping()
    ApiResponse<GenreResponse> create(@RequestBody GenreRequest request) {
        return ApiResponse.<GenreResponse>builder()
                .result(genreServiceImpl.create(request))
                .build();
    }

    @PutMapping("/{genreId}")
    ApiResponse<GenreResponse> update(@PathVariable String genreId, @RequestBody GenreRequest request) {
        return ApiResponse.<GenreResponse>builder()
                .result(genreServiceImpl.update(genreId, request))
                .build();
    }

    @DeleteMapping("/{genreId}")
    ApiResponse<Void> delete(@PathVariable String genreId) {
        genreServiceImpl.delete(genreId);
        return ApiResponse.<Void>builder()
                .message("Genre deleted successfully")
                .build();
    }

    @GetMapping("/{genreId}")
    ApiResponse<GenreResponse> getGenre(@PathVariable String genreId) {
        return ApiResponse.<GenreResponse>builder()
                .result(genreServiceImpl.getGenre(genreId))
                .build();
    }

    @GetMapping
    ApiResponse<Page<GenreResponse>> getAllGenres(@PageableDefault(size = 5) Pageable pageable) {
        return ApiResponse.<Page<GenreResponse>>builder()
                .result(genreServiceImpl.getAllGenres(pageable))
                .build();
    }

    @GetMapping("/export/excel")
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
