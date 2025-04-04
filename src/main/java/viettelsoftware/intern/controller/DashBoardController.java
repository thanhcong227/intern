package viettelsoftware.intern.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import viettelsoftware.intern.dto.BorrowedBookInfo;
import viettelsoftware.intern.dto.GenreStats;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.entity.BorrowingEntity;
import viettelsoftware.intern.service.impl.BorrowingServiceImpl;
import viettelsoftware.intern.service.impl.DashBoardServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashBoardController {

    DashBoardServiceImpl dashBoardService;
    private final BorrowingServiceImpl borrowingServiceImpl;

    @GetMapping("/books/genre")
    public ResponseEntity<List<GenreStats>> getBooksByGenreStats() {
        List<GenreStats> stats = dashBoardService.getBooksByGenreStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/top-posts")
    public ResponseEntity<List<Object[]>> getTopPosts() {
        List<Object[]> topPosts = dashBoardService.getTop5Posts();
        return ResponseEntity.ok(topPosts);
    }

    @GetMapping("/my-borrowed-books")
    public ApiResponse<List<BorrowedBookInfo>> getMyBorrowedBooks() {
        return ApiResponse.<List<BorrowedBookInfo>>builder()
                .result(borrowingServiceImpl.getBorrowedBooksByCurrentUser())
                .build();
    }
}
