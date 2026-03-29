package com.hacktropia.controller;


import com.hacktropia.exception.BookException;
import com.hacktropia.payload.dto.BookDTO;
import com.hacktropia.payload.request.BookSearchRequest;
import com.hacktropia.payload.response.ApiResponse;
import com.hacktropia.payload.response.PageResponse;
import com.hacktropia.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Book catalog CRUD, search, and statistics")
public class BookController {

    private final BookService bookService;


    @PostMapping("/bulk")
    @Operation(summary = "Bulk create books", description = "Creates multiple books at once from a list of BookDTOs")
    public ResponseEntity<?>createBooksBulk(
            @Valid @RequestBody List<BookDTO> bookDTOS) throws BookException {

        List<BookDTO> createdBook=bookService.createBooksBulk(bookDTOS);
        return ResponseEntity.ok(createdBook);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Returns a single book by its ID")
    public ResponseEntity<BookDTO> getBookById(
            @Parameter(description = "Book ID") @PathVariable Long id) throws BookException {

        BookDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }
    @PutMapping("/{id}")
    @Operation(summary = "Update a book", description = "Updates book details by ID. Only non-null fields are updated.")
    public ResponseEntity<BookDTO>updateBook(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @RequestBody BookDTO bookDTO
    ) throws BookException{
        BookDTO updatedBook = bookService.updateBook(id,bookDTO);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a book", description = "Marks the book as inactive (soft delete)")
    public ResponseEntity<ApiResponse> deleteBook(
            @Parameter(description = "Book ID") @PathVariable Long id) throws BookException{
        bookService.deleteBook(id);
        return ResponseEntity.ok(new ApiResponse("Book deleted successfully", true));
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Permanently delete a book", description = "Permanently removes the book from the database (hard delete)")
    public ResponseEntity<ApiResponse> hardDeleteBook(
            @Parameter(description = "Book ID") @PathVariable Long id) throws BookException{
        bookService.hardDeleteBook(id);
        return ResponseEntity.ok(new ApiResponse("Book permanently deleted ", true));
    }

    @GetMapping
    @Operation(summary = "Search books with filters", description = "Paginated search with optional filters: genreId, availableOnly, activeOnly, sorting")
    public ResponseEntity<PageResponse<BookDTO>> searchBooks(
            @Parameter(description = "Filter by genre ID") @RequestParam(required = false) Long genreId,
            @Parameter(description = "Show only available books") @RequestParam(required = false, defaultValue ="false") Boolean availableOnly,
            @Parameter(description = "Show only active books") @RequestParam(defaultValue = "true") boolean activeOnly,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC") @RequestParam(defaultValue = "DESC") String sortDirection){

        BookSearchRequest searchRequest = new BookSearchRequest();
        searchRequest.setGenreId(genreId);
        searchRequest.setAvailableOnly(availableOnly);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);

        PageResponse<BookDTO> books= bookService.searchBooksWithFilters(searchRequest);
        return ResponseEntity.ok(books);
    }

    @PostMapping("/search")
    @Operation(summary = "Advanced book search", description = "Advanced search using a full BookSearchRequest body with all filter options")
    public ResponseEntity<PageResponse<BookDTO>> advancedSearch(
            @RequestBody BookSearchRequest searchRequest){
        PageResponse<BookDTO> books = bookService.searchBooksWithFilters(searchRequest);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get book statistics", description = "Returns total active books and total available books counts")
    public ResponseEntity<BookStatsResponse> getBookStats(){
        long totalActive=bookService.getTotalActiveBooks();
        long totalAvailable=bookService.getTotalAvailableBooks();
        BookStatsResponse stats = new BookStatsResponse(totalActive,totalAvailable);
        return ResponseEntity.ok(stats);
    }

    public static class BookStatsResponse{
        public long totalActiveBooks;
        public long totalAvailableBooks;

        public BookStatsResponse(long totalActiveBooks, long totalAvailableBooks){
            this.totalActiveBooks=totalActiveBooks;
            this.totalAvailableBooks=totalAvailableBooks;

        }
    }
}
