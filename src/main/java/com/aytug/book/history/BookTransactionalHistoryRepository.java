package com.aytug.book.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookTransactionalHistoryRepository extends JpaRepository<BookTransationHistory, Integer>{
   @Query("""
            SELECT history
            FROM BookTransationHistory history
            WHERE history.user.id = :id
            """)
    Page<BookTransationHistory> findAllBorrowedBooks(Pageable pageable, Integer id);


    @Query("""
            SELECT history
            FROM BookTransationHistory history
            WHERE history.book.owner.id = :id
            """)
    Page<BookTransationHistory> findAllReturnedBooks(Pageable pageable, Integer id);

    @Query("""
    SELECT(COUNT (*)>0) AS isBorrowed
    FROM BookTransationHistory history
    WHERE history.user.id = :userId
    AND history.book.id = :bookId
    AND history.returnApproved = false
    """)
    boolean isAlreadyBorrowedByUser(Integer bookId, Integer userId);

    @Query("""
    SELECT transaction
    FROM BookTransationHistory transaction
    WHERE transaction.user.id = :userId
    AND transaction.book.id = :bookId
    AND transaction.returnApproved = false
    AND transaction.returned = false
    """)
    Optional<BookTransationHistory> findByBookIdAndUserId(Integer bookId, Integer userId);
    @Query("""
    SELECT transaction
    FROM BookTransationHistory transaction
    WHERE transaction.book.owner.id = :userId
    AND transaction.book.id = :bookId
    AND transaction.returnApproved = false
    AND transaction.returned = true
    """)
    Optional<BookTransationHistory> findByBookIdAndOwnerId(Integer bookId, Integer id);
}
