package vn.duyit.webbansach_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.dto.FavoriteProductDto;
import vn.duyit.webbansach_backend.service.UserFavoriteService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin
public class UserFavoriteController {

    private final UserFavoriteService userFavoriteService;

    public UserFavoriteController(UserFavoriteService userFavoriteService) {
        this.userFavoriteService = userFavoriteService;
    }

    @GetMapping
    public ResponseEntity<List<FavoriteProductDto>> getUserFavorites(@RequestParam Long userId) {
        try {
            if (userId == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            List<FavoriteProductDto> favorites = userFavoriteService.getUserFavorites(userId);
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public static class FavoriteRequest {
        public Long userId;
        public Long productId;
    }

    @PostMapping
    public ResponseEntity<?> addFavorite(@RequestBody FavoriteRequest request) {
        try {
            Long userId = request.userId;
            Long productId = request.productId;
            if (userId == null || productId == null) {
                return ResponseEntity.badRequest().body("userId and productId are required");
            }

            userFavoriteService.addFavorite(userId, productId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> removeFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        try {
            if (userId == null || productId == null) {
                return ResponseEntity.badRequest().body("userId and productId are required");
            }

            userFavoriteService.removeFavorite(userId, productId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
