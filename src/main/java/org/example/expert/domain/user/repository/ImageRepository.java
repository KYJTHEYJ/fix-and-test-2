package org.example.expert.domain.user.repository;


import org.example.expert.domain.user.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByUserId(Long userId);
    long countByUserId(Long userId);
}
