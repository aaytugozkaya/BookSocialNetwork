package com.aytug.book.feedback;

import com.aytug.book.book.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/feedbacks")
@Tag(name = "Feedback", description = "Feedback API")
public class FeedbackController {
    private final FeedbackService service;

    @PostMapping()
    public ResponseEntity<Integer> saveFeedback(FeedbackRequest request, Authentication connectedUser) {
        return ResponseEntity.ok(service.save(request, connectedUser));
    }

    @GetMapping("/book/{book-id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbackByBook(
            @PathVariable("book-id") Integer bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.findAllFeedbackByBook(bookId, page, size, connectedUser));
    }
}

