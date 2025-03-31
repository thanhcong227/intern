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
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.dto.response.BorrowingResponse;
import viettelsoftware.intern.service.impl.BorrowingServiceImpl;

import java.util.Set;

@RestController
@RequestMapping("/borrowing")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BorrowingController {

    BorrowingServiceImpl borrowingServiceImpl;

    @PostMapping()
    @PreAuthorize("hasAuthority('BORROWING_MANAGE')")
    ApiResponse<BorrowingResponse> create(@RequestParam String userId, @RequestBody Set<String> bookIds) {
        return ApiResponse.<BorrowingResponse>builder()
                .result(borrowingServiceImpl.create(userId, bookIds))
                .build();
    }

    @PutMapping("/{borrowingId}")
    @PreAuthorize("hasAuthority('BORROWING_MANAGE')")
    ApiResponse<BorrowingResponse> update(@PathVariable String borrowingId, @RequestBody Set<String> bookIds) {
        return ApiResponse.<BorrowingResponse>builder()
                .result(borrowingServiceImpl.update(borrowingId, bookIds))
                .build();
    }

    @DeleteMapping("/{borrowingId}")
    @PreAuthorize("hasAuthority('BORROWING_MANAGE')")
    ApiResponse<Void> delete(@PathVariable String borrowingId) {
        borrowingServiceImpl.delete(borrowingId);
        return ApiResponse.<Void>builder()
                .message("Borrowing deleted successfully")
                .build();
    }

    @GetMapping("/{borrowingId}")
    @PreAuthorize("hasAuthority('BORROWING_VIEW')")
    ApiResponse<BorrowingResponse> getBorrowing(@PathVariable String borrowingId) {
        return ApiResponse.<BorrowingResponse>builder()
                .result(borrowingServiceImpl.getBorrowing(borrowingId))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('BORROWING_VIEW')")
    ApiResponse<Page<BorrowingResponse>> getAllBorrowing(@PageableDefault(size = 5) Pageable pageable) {
        return ApiResponse.<Page<BorrowingResponse>>builder()
                .result(borrowingServiceImpl.getAllBorrowing(pageable))
                .build();
    }

    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('EXPORT_DATA')")
    public ResponseEntity<byte[]> exportBorrowingsToExcel() {
        byte[] excelData = borrowingServiceImpl.exportBorrowingsToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=borrowings.xlsx");
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(excelData);
    }
}
