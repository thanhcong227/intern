package viettelsoftware.intern.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/{borrowingBookId}")
    ApiResponse<BorrowingBookResponse> create(@PathVariable String borrowingBookId, @RequestBody List<BorrowingBookRequest> requests) {
        return ApiResponse.<BorrowingBookResponse>builder()
                .result(borrowingBookServiceImpl.create(borrowingBookId, requests))
                .build();
    }

    @PutMapping("/{borrowingBookId}")
    public ApiResponse<BorrowingBookResponse> updateBorrowingBook(
            @PathVariable String borrowingBookId,
            @RequestBody BorrowingBookRequest request) {
        return ApiResponse.<BorrowingBookResponse>builder()
                .result(borrowingBookServiceImpl.update(borrowingBookId, request))
                .build();
    }

    @DeleteMapping("/{borrowingBookId}")
    ApiResponse<Void> delete(@PathVariable String borrowingBookId) {
        borrowingBookServiceImpl.delete(borrowingBookId);
        return ApiResponse.<Void>builder()
                .message("Borrowing deleted successfully")
                .build();
    }

    @GetMapping("/{borrowingBookId}")
    ApiResponse<BorrowingBookResponse> getBorrowing(@PathVariable String borrowingBookId) {
        return ApiResponse.<BorrowingBookResponse>builder()
                .result(borrowingBookServiceImpl.getBorrowingBook(borrowingBookId))
                .build();
    }

    @GetMapping
    ApiResponse<Page<BorrowingBookResponse>> getAllBorrowing(@PageableDefault(size = 5) Pageable pageable) {
        return ApiResponse.<Page<BorrowingBookResponse>>builder()
                .result(borrowingBookServiceImpl.getAllBorrowingBooks(pageable))
                .build();
    }
}
