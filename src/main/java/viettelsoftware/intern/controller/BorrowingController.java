package viettelsoftware.intern.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import viettelsoftware.intern.config.response.GeneralResponse;
import viettelsoftware.intern.config.response.ResponseFactory;
import viettelsoftware.intern.dto.PartialReturnRequest;
import viettelsoftware.intern.dto.request.BorrowingRequest;
import viettelsoftware.intern.dto.response.BorrowingResponse;
import viettelsoftware.intern.service.impl.BorrowingServiceImpl;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/borrowing")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BorrowingController {

    BorrowingServiceImpl borrowingServiceImpl;
    private final ResponseFactory responseFactory;

    @PostMapping()
    @PreAuthorize("hasAuthority('BORROWING_MANAGE')")
    public ResponseEntity<GeneralResponse<BorrowingResponse>> create(
            @RequestBody BorrowingRequest request,
            Authentication authentication) {
        return responseFactory.success(borrowingServiceImpl.create(request));
    }

    @PutMapping("/{borrowingId}")
    @PreAuthorize("hasAuthority('BORROWING_MANAGE')")
    ResponseEntity<GeneralResponse<BorrowingResponse>> update(@PathVariable String borrowingId, @RequestBody Set<BorrowingRequest.BorrowedBookItem> bookIds) {
        return responseFactory.success(borrowingServiceImpl.update(borrowingId, bookIds));
    }

    @DeleteMapping("/{borrowingId}")
    @PreAuthorize("hasAuthority('BORROWING_MANAGE')")
    ResponseEntity<GeneralResponse<Object>> delete(@PathVariable String borrowingId) {
        borrowingServiceImpl.delete(borrowingId);
        return responseFactory.successNoData();
    }

    @GetMapping("/{borrowingId}")
    @PreAuthorize("hasAuthority('BORROWING_VIEW')")
    ResponseEntity<GeneralResponse<BorrowingResponse>> getBorrowing(@PathVariable String borrowingId) {
        return responseFactory.success(borrowingServiceImpl.getBorrowing(borrowingId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('BORROWING_VIEW')")
    ResponseEntity<GeneralResponse<Page<BorrowingResponse>>> getAllBorrowing(@PageableDefault(size = 5) Pageable pageable) {
        return responseFactory.success(borrowingServiceImpl.getAllBorrowing(pageable));
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

    @PostMapping("returnAll")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GeneralResponse<Object>> returnBook(@RequestParam String borrowingId) {
        log.info("Request to return book with borrowingId: {}", borrowingId);
        borrowingServiceImpl.returnBooks(borrowingId);
        log.info("Successfully returned book with borrowingId: {}", borrowingId);
        return responseFactory.successNoData();
    }

    @PostMapping("return")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GeneralResponse<Object>> returnBook(@RequestBody PartialReturnRequest request) {
        borrowingServiceImpl.returnBooksPartially(request);
        return responseFactory.successNoData();
    }
}
