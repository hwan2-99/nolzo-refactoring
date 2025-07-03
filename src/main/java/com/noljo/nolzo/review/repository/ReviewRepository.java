package com.noljo.nolzo.review.repository;

import com.noljo.nolzo.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
