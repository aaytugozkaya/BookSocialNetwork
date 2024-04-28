package com.aytug.book.book;

import com.aytug.book.book.exception.OperationNotPermittedException;
import com.aytug.book.file.FileStorageService;
import com.aytug.book.history.BookTransactionalHistoryRepository;
import com.aytug.book.history.BookTransationHistory;
import com.aytug.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class BookService {
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionalHistoryRepository bookTransactionalHistoryRepository;
    private final FileStorageService fileStorageService;
    public Integer save(BookRequest request, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book = bookMapper.toBook(request);
        book.setOwner(user );
        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer id){
        return bookRepository.findById(id)
                .map(bookMapper::toBookResponse)
                .orElseThrow(()-> new EntityNotFoundException("Book not found with id: "+id));
    }

    public PageResponse<BookResponse> findAllBooks(int page,int size,Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable,user.getId());
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(bookResponse,books.getNumber(),
        books.getSize(),
        books.getTotalElements(),
        books.getTotalPages(),
        books.isFirst(),
        books.isLast());
    }

    public PageResponse<BookResponse> findAllBooksByOwner(Integer page, Integer size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()),pageable);

        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(bookResponse,books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(Integer page, Integer size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransationHistory> allBorrowedBooks = bookTransactionalHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(bookResponse,allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(Integer page, Integer size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransationHistory> allBorrowedBooks = bookTransactionalHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(bookResponse,allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast());
    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You are not the owner of the book");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You are not the owner of the book.archived");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
       if(book.isArchived()||!book.isShareable()){
           throw new OperationNotPermittedException("Book is not available for borrowing");
       }
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cant borrow your own book");
        }
        final boolean isAlreadyBorrowed = bookTransactionalHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if(isAlreadyBorrowed){
            throw new OperationNotPermittedException("Book is already borrowed this book");
        }

        BookTransationHistory history = BookTransationHistory.builder()
                .book(book)
                .user(user)
                .returned(false)
                .returnApproved(false)
                .build();
        bookTransactionalHistoryRepository.save(history);
        return bookId;
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
        if(book.isArchived()||!book.isShareable()){
            throw new OperationNotPermittedException("Book is not available for borrowing");
        }
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cant borrow or return your own book");
        }
        BookTransationHistory bookTransationHistory = bookTransactionalHistoryRepository.findByBookIdAndUserId(bookId,user.getId())
                .orElseThrow(()->new EntityNotFoundException("Book is not borrowed by you"));
        bookTransationHistory.setReturned(true);
        return bookTransactionalHistoryRepository.save(bookTransationHistory).getId();

    }

    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
        if(book.isArchived()||!book.isShareable()){
            throw new OperationNotPermittedException("Book is not available for borrowing");
        }
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cant borrow or return your own book");
        }
        BookTransationHistory bookTransationHistory = bookTransactionalHistoryRepository.findByBookIdAndOwnerId(bookId,user.getId())
                .orElseThrow(()->new EntityNotFoundException("Book is not returned yet"));
        bookTransationHistory.setReturnApproved(true);
        return bookTransactionalHistoryRepository.save(bookTransationHistory).getId();

    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        var bookCover = fileStorageService.saveFile(file,user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);

    }
}
