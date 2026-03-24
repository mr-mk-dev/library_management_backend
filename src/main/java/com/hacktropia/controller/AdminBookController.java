package com.hacktropia.controller;

import com.hacktropia.exception.BookException;
import com.hacktropia.payload.dto.BookDTO;
import com.hacktropia.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/books")
@Tag(name = "Admin - Books", description = "Admin-only book management operations (requires ROLE_ADMIN)")
public class AdminBookController {

    private final BookService bookService;

    @PostMapping()
    @Operation(summary = "Create a new book", description = "Creates a single book entry in the catalog. Requires ADMIN role.")
    public ResponseEntity<BookDTO> createBook(
            @Valid @RequestBody BookDTO bookDTO) throws BookException {

        BookDTO createdBook=bookService.createBook(bookDTO);
        return ResponseEntity.ok(createdBook);
    }
}
