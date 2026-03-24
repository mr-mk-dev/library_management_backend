package com.hacktropia.controller;

import com.hacktropia.exception.GenreException;
import com.hacktropia.payload.dto.GenreDTO;
import com.hacktropia.payload.response.ApiResponse;
import com.hacktropia.service.GenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genres")
@Tag(name = "Genres", description = "Genre CRUD with hierarchical parent-child structure")
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    @Operation(summary = "Create a genre", description = "Creates a new genre. Supports hierarchical genres via parentGenre.")
    public ResponseEntity<?> createGenre(
            @RequestBody GenreDTO genre) {
        GenreDTO genres = genreService.createGenre(genre);
        return ResponseEntity.ok(genres);
    }

    @GetMapping()
    @Operation(summary = "Get all genres", description = "Returns all genres (active and inactive)")
    public ResponseEntity<?> getAllGenres() {
        List<GenreDTO> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{genreId}")
    @Operation(summary = "Get genre by ID")
    public ResponseEntity<?> getGenreById(
            @Parameter(description = "Genre ID") @PathVariable("genreId") Long genreId
    ) throws GenreException {
        GenreDTO genres = genreService.getGenreById(genreId);
        return ResponseEntity.ok(genres);
    }

    @PutMapping("/{genreId}")
    @Operation(summary = "Update a genre", description = "Updates genre details by ID")
    public ResponseEntity<?> updateGenre(
            @Parameter(description = "Genre ID") @PathVariable("genreId") Long genreId,
            @RequestBody GenreDTO genre
    ) throws GenreException {
        GenreDTO genres = genreService.updateGenre(genreId, genre);
        return ResponseEntity.ok(genres);
    }

    @DeleteMapping("/{genreId}")
    @Operation(summary = "Soft delete a genre", description = "Marks the genre as inactive (soft delete)")
    public ResponseEntity<?> deleteGenre(
            @Parameter(description = "Genre ID") @PathVariable("genreId") Long genreId
    ) throws GenreException {
        genreService.deleteGenre(genreId);
        ApiResponse response = new ApiResponse("genre deleted- soft delete", true);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{genreId}/hard")
    @Operation(summary = "Hard delete a genre", description = "Permanently removes the genre from the database")
    public ResponseEntity<?> hardDeleteGenre(
            @Parameter(description = "Genre ID") @PathVariable("genreId") Long genreId
    ) throws GenreException {
        genreService.hardDeleteGenre(genreId);
        ApiResponse response = new ApiResponse("genre deleted- hard delete", true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-level")
    @Operation(summary = "Get top-level genres", description = "Returns genres that have no parent (root genres)")
    public ResponseEntity<?> getTopLevelGenres() {
        List<GenreDTO> genres = genreService.getTopLevelGenres();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/count")
    @Operation(summary = "Get total active genres count")
    public ResponseEntity<?> getTotalActiveGenres() {
        Long genres = genreService.getTotalActiveGenres();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}/book-count")
    @Operation(summary = "Get book count by genre", description = "Returns the number of books in a specific genre")
    public ResponseEntity<?> getBookCountByGenres(
            @Parameter(description = "Genre ID") @PathVariable Long id) {
        Long count = genreService.getBookCountByGenre(id);
        return ResponseEntity.ok(count);
    }
}
