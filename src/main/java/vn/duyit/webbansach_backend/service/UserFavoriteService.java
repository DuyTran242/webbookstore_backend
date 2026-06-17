package vn.duyit.webbansach_backend.service;

import vn.duyit.webbansach_backend.dto.FavoriteProductDto;
import vn.duyit.webbansach_backend.entity.Product;
import vn.duyit.webbansach_backend.entity.User;
import vn.duyit.webbansach_backend.entity.UserFavorite;
import vn.duyit.webbansach_backend.repository.ProductRepository;
import vn.duyit.webbansach_backend.repository.UserFavoriteRepository;
import vn.duyit.webbansach_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserFavoriteService {

    private final UserFavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public UserFavoriteService(UserFavoriteRepository favoriteRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public List<FavoriteProductDto> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(UserFavorite::getProduct)
                .map(product -> new FavoriteProductDto(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        (product.getImages() != null && !product.getImages().isEmpty()) ? product.getImages().get(0).getImageUrl() : null
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addFavorite(Long userId, Long productId) {
        if (favoriteRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            return; // Already favorited
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        UserFavorite favorite = new UserFavorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Long userId, Long productId) {
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }
}
