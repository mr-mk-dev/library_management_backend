package com.hacktropia.service.impl;

import com.hacktropia.mapper.WishlistMapper;
import com.hacktropia.modal.Book;
import com.hacktropia.modal.User;
import com.hacktropia.modal.Wishlist;
import com.hacktropia.payload.dto.WishlistDTO;
import com.hacktropia.payload.response.PageResponse;
import com.hacktropia.repository.BookRepository;
import com.hacktropia.repository.WishlistRepository;
import com.hacktropia.service.UserService;
import com.hacktropia.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserService userService;
    private final BookRepository bookRepository;
    private final WishlistMapper wishlistMapper;

    @Override
    public WishlistDTO addToWishlist(Long bookId, String notes) throws Exception {
        User user=userService.getCurrentUser();

        Book book=bookRepository.findById(bookId)
                .orElseThrow(()-> new Exception("Book not found"));

        if(wishlistRepository.existsByUserIdAndBookId(user.getId(),bookId)){
            throw new Exception("book is already in your wishlist");
        }

        Wishlist wishlist=new Wishlist();
        wishlist.setUser(user);
        wishlist.setBook(book);
        wishlist.setNotes(notes);
        Wishlist saved=wishlistRepository.save(wishlist);

        return wishlistMapper.toDTO(saved);
    }

    @Override
    public void removeFromWishlist(Long bookId) throws Exception {
        User user=userService.getCurrentUser();
        Wishlist wishlist=wishlistRepository.findByUserIdAndBookId(user.getId(), bookId);
        if(wishlist==null){
            throw new Exception("book is not in your wishlist");
        }
        wishlistRepository.delete(wishlist);

    }

    @Override
    public PageResponse<WishlistDTO> getMyWishlist(int page, int size) throws Exception {
        Long userId=userService.getCurrentUser().getId();
        Pageable pageable= PageRequest.of(page,size, Sort.by("addedAt").descending());
        Page<Wishlist> wishlistPage=wishlistRepository.findByUserId(userId,pageable);
        return convertToPageResponse(wishlistPage);
    }

    private PageResponse<WishlistDTO> convertToPageResponse(Page<Wishlist> wishlistPage){
        List<WishlistDTO> wishlistDTOs=wishlistPage.getContent()
                .stream()
                .map(wishlistMapper::toDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                wishlistDTOs,
                wishlistPage.getNumber(),
                wishlistPage.getSize(),
                wishlistPage.getTotalElements(),
                wishlistPage.getTotalPages(),
                wishlistPage.isLast(),
                wishlistPage.isFirst(),
                wishlistPage.isEmpty()
        );
    }
}
