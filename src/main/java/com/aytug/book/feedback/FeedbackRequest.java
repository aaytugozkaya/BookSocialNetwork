package com.aytug.book.feedback;

import jakarta.validation.constraints.*;

public record FeedbackRequest (
        @Positive(message = "Note must be positive")
                @Min(value = 0, message = "201.Note must be greater than 0")
                @Max(value = 5, message = "202.Note must be less than 5")
        Double note,
        @NotNull(message = "203.Comment cannot be null")
        @NotEmpty(message = "203.Comment cannot be empty")
        @NotBlank(message = "203.Comment cannot be blank")
        String comment,
        @NotNull(message = "204.Book id cannot be null")
        Integer bookId
){
}
