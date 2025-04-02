package viettelsoftware.intern.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import viettelsoftware.intern.constant.ErrorCode;
import viettelsoftware.intern.dto.request.BorrowingBookRequest;
import viettelsoftware.intern.dto.response.BorrowingBookResponse;
import viettelsoftware.intern.entity.BookEntity;
import viettelsoftware.intern.entity.BorrowingBook;
import viettelsoftware.intern.entity.BorrowingEntity;
import viettelsoftware.intern.exception.AppException;
import viettelsoftware.intern.mapper.BorrowingBookMapper;
import viettelsoftware.intern.repository.BookRepository;
import viettelsoftware.intern.repository.BorrowingBookRepository;
import viettelsoftware.intern.repository.BorrowingRepository;
import viettelsoftware.intern.service.BorrowingBookService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BorrowingBookServiceImpl implements BorrowingBookService {

    BorrowingBookRepository borrowingBookRepository;
    BookRepository bookRepository;
    BorrowingBookMapper borrowingBookMapper;
    BorrowingRepository borrowingRepository;

    @Override
    public BorrowingBookResponse create(String borrowingId, List<BorrowingBookRequest> requests) {
        BorrowingEntity borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new AppException(ErrorCode.BORROWING_NOT_FOUND));

        Set<BorrowingBook> borrowingBooks = requests.stream().map(request -> {
            BookEntity book = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
            if (request.getQuantity() < 0) {
                throw new AppException(ErrorCode.BOOK_QUANTITY_INVALID);
            } else if (book.getQuantity() < request.getQuantity()) {
                throw new AppException(ErrorCode.BOOK_NOT_ENOUGH);
            } else {
                book.setQuantity(book.getQuantity() - request.getQuantity());
                bookRepository.save(book);
            }
            return BorrowingBook.builder()
                    .borrowing(borrowing)
                    .book(book)
                    .quantity(request.getQuantity())
                    .build();
        }).collect(Collectors.toSet());

        borrowing.setBorrowings(borrowingBooks);
        borrowingRepository.save(borrowing);

        return borrowingBooks.stream().findFirst()
                .map(borrowingBookMapper::toBorrowingBookResponse)
                .orElseThrow(() -> new AppException(ErrorCode.BORROWING_BOOK_CREATION_FAILED));
    }

    @Override
    public BorrowingBookResponse update(String borrowingBookId, BorrowingBookRequest request) {
        BorrowingBook borrowingBook = borrowingBookRepository.findById(borrowingBookId)
                .orElseThrow(() -> new AppException(ErrorCode.BORROWING_NOT_FOUND));

        if (request.getQuantity() < 0) {
            throw new AppException(ErrorCode.BOOK_QUANTITY_INVALID);
        } else if (request.getQuantity() == 0) {
            borrowingBookRepository.deleteById(borrowingBookId);
            return borrowingBookMapper.toBorrowingBookResponse(borrowingBook);
        } else if (request.getQuantity() > borrowingBook.getBook().getQuantity()) {
            throw new AppException(ErrorCode.BOOK_NOT_ENOUGH);
        }
        BookEntity book = borrowingBook.getBook();
        book.setQuantity(book.getQuantity() + borrowingBook.getQuantity() - request.getQuantity());
        bookRepository.save(book);
        borrowingBook.setQuantity(request.getQuantity());
        borrowingBookRepository.save(borrowingBook);

        return borrowingBookMapper.toBorrowingBookResponse(borrowingBook);
    }



    @Override
    public void delete(String borrowingBookId) {
        if (!borrowingBookRepository.existsById(borrowingBookId)) {
            throw new AppException(ErrorCode.BORROWING_BOOK_NOT_FOUND);
        }
        borrowingBookRepository.deleteById(borrowingBookId);
    }

    @Override
    public BorrowingBookResponse getBorrowingBook(String borrowingBookId) {
        BorrowingBook borrowingBook = borrowingBookRepository.findById(borrowingBookId)
                .orElseThrow(() -> new AppException(ErrorCode.BORROWING_BOOK_NOT_FOUND));

        return borrowingBookMapper.toBorrowingBookResponse(borrowingBook);
    }

    @Override
    public Page<BorrowingBookResponse> getAllBorrowingBooks(Pageable pageable) {
        return borrowingBookRepository.findAll(pageable).map(borrowingBookMapper::toBorrowingBookResponse);
    }
}

