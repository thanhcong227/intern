package viettelsoftware.intern.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import viettelsoftware.intern.config.response.GeneralResponse;
import viettelsoftware.intern.config.response.ResponseFactory;
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
    BorrowingServiceImpl borrowingServiceImpl;
    ResponseFactory responseFactory;

    @GetMapping("/books/genre")
    ResponseEntity<GeneralResponse<List<GenreStats>>> getBooksByGenreStats() {
        List<GenreStats> stats = dashBoardService.getBooksByGenreStats();
        return responseFactory.success(stats);
    }

    @GetMapping("/top-posts")
    ResponseEntity<GeneralResponse<List<Object[]>>> getTopPosts() {
        List<Object[]> topPosts = dashBoardService.getTop5Posts();
        return responseFactory.success(topPosts);
    }

    @GetMapping("/my-borrowed-books")
    ResponseEntity<GeneralResponse<List<BorrowedBookInfo>>> getMyBorrowedBooks() {
        List<BorrowedBookInfo> list = borrowingServiceImpl.getBorrowedBooksByCurrentUser();
        return responseFactory.success(list);
    }
}
