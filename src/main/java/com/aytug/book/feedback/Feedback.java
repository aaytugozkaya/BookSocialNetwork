package com.aytug.book.feedback;

import com.aytug.book.book.Book;
import com.aytug.book.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Feedback extends BaseEntity {

    private Double note;
    private String comment;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
