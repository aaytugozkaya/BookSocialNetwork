package com.aytug.book.book;

import com.aytug.book.common.BaseEntity;
import com.aytug.book.feedback.Feedback;
import com.aytug.book.history.BookTransationHistory;
import com.aytug.book.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor@Entity@SuperBuilder
public class Book extends BaseEntity {

    private String title;
    private String authorName;
    private  String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "book")
    private List<BookTransationHistory> histories;

    @Transient
    public double getRate(){
        if(feedbacks== null || feedbacks.isEmpty()){
            return 0.0;
        }
        var rate = this.feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);
        double rounderRate = Math.round(rate * 10) / 10.0;
        return rounderRate;
    }

}
