package viettelsoftware.intern.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import viettelsoftware.intern.config.response.GeneralResponse;
import viettelsoftware.intern.config.response.ResponseFactory;
import viettelsoftware.intern.dto.request.BorrowingBookRequest;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.dto.response.BorrowingBookResponse;
import viettelsoftware.intern.service.impl.BorrowingBookServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/borrowing-book")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BorrowingBookController {

    BorrowingBookServiceImpl borrowingBookServiceImpl;
    private final ResponseFactory responseFactory;

    @PostMapping("/{borrowingBookId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LIBRARIAN')")
    ResponseEntity<GeneralResponse<BorrowingBookResponse>> create(@PathVariable String borrowingBookId, @RequestBody List<BorrowingBookRequest> requests) {
        return responseFactory.success(borrowingBookServiceImpl.create(borrowingBookId, requests));
    }

    @PutMapping("/{borrowingBookId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LIBRARIAN')")
    ResponseEntity<GeneralResponse<BorrowingBookResponse>> updateBorrowingBook(
            @PathVariable String borrowingBookId,
            @RequestBody BorrowingBookRequest request) {
        return responseFactory.success(borrowingBookServiceImpl.update(borrowingBookId, request));
    }

    @DeleteMapping("/{borrowingBookId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LIBRARIAN')")
    ResponseEntity<GeneralResponse<Object>> delete(@PathVariable String borrowingBookId) {
        borrowingBookServiceImpl.delete(borrowingBookId);
        return responseFactory.successNoData();
    }

    @GetMapping("/{borrowingBookId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LIBRARIAN')")
    ResponseEntity<GeneralResponse<BorrowingBookResponse>> getBorrowing(@PathVariable String borrowingBookId) {
        return responseFactory.success(borrowingBookServiceImpl.getBorrowingBook(borrowingBookId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LIBRARIAN')")
    ResponseEntity<GeneralResponse<Page<BorrowingBookResponse>>> getAllBorrowing(@PageableDefault(size = 5) Pageable pageable) {
        return responseFactory.success(borrowingBookServiceImpl.getAllBorrowingBooks(pageable));
    }
}
