package vn.duyit.webbansach_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.duyit.webbansach_backend.entity.QrLoginToken;

import java.util.Optional;

public interface QrLoginTokenRepository extends JpaRepository<QrLoginToken, Long> {

    Optional<QrLoginToken> findByToken(String token);

}