package com.aytug.book.book;

import com.aytug.book.file.FileUtils;
import com.aytug.book.history.BookTransationHistory;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {
    public Book toBook(BookRequest request) {
        return Book.builder().
                id(request.id()).
                title(request.title()).
                isbn(request.isbn()).
                authorName(request.authorName()).
                synopsis(request.synopsis()).
                archived(false).
                shareable(request.shareable()).
                build();
    }

    public BookResponse  toBookResponse(Book book) {
        return  BookResponse.builder().
                id(book.getId()).
                title(book.getTitle()).
                authorName(book.getAuthorName()).
                isbn(book.getIsbn()).
                synopsis(book.getSynopsis()).
                owner(book.getOwner().getFullName()).
                cover(FileUtils.readFileFromLocation(book.getBookCover())).
                rate(book.getRate()).
                shareable(book.isShareable()).
                archived(book.isArchived()).
                build();
    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransationHistory history) {
        return  BorrowedBookResponse.builder().
                id(history.getBook().getId()).
                title(history.getBook().getTitle()).
                authorName(history.getBook().getAuthorName()).
                isbn(history.getBook().getIsbn()).
                rate(history.getBook().getRate()).
                returned(history.isReturned()).
                returnApproved(history.isReturnApproved()).
                build();
    }
}
