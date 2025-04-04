package viettelsoftware.intern.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import viettelsoftware.intern.dto.GenreStats;
import viettelsoftware.intern.repository.BookRepository;
import viettelsoftware.intern.repository.GenreRepository;
import viettelsoftware.intern.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashBoardServiceImpl {

    GenreRepository genreRepository;
    PostRepository postRepository;
    BookRepository bookRepository;

    // Thống kê số lượng sách theo thể loại
    public List<GenreStats> getBooksByGenreStats() {
        log.info("Fetching statistics of books by genre");

        // Lấy dữ liệu từ genreRepository
        List<Object[]> result = genreRepository.countBooksByGenre();

        // Chuyển đổi Object[] thành GenreStats
        List<GenreStats> stats = new ArrayList<>();
        for (Object[] row : result) {
            String genreName = (String) row[0]; // Tên thể loại
            long bookCount = (long) row[1]; // Số lượng sách
            stats.add(new GenreStats(genreName, bookCount));
        }

        return stats;
    }

    public List<Object[]> getTop5Posts() {
        Pageable pageable = PageRequest.of(0, 5);  // Lấy 5 bài viết đầu tiên
        Page<Object[]> topPostsPage = postRepository.getTop5MostLikedPosts(pageable);
        return topPostsPage.getContent();
    }
}
