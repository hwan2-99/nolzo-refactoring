package com.noljo.nolzo.review.adapter.out.persistence;

import com.noljo.nolzo.review.application.port.out.ReviewPersistencePort;
import com.noljo.nolzo.review.entity.Review;
import com.noljo.nolzo.review.repository.ReviewRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewPersistenceAdapter implements ReviewPersistencePort {

    private final ReviewRepository reviewRepository;

    @Override
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public <S extends Review> S save(S review) {
        return reviewRepository.save(review);
    }

    @Override
    public void delete(Review review) {
        reviewRepository.delete(review);
    }

    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Override
    public List<Review> findByMemberId(Long memberId) {
        return reviewRepository.findByMemberId(memberId);
    }

    @Override
    public Optional<Review> findByMemberIdAndEventId(Long memberId, Long eventId) {
        return reviewRepository.findByMemberIdAndEventId(memberId, eventId);
    }

    @Override
    public Page<Review> findPageByEventId(Long eventId, Pageable pageable) {
        return reviewRepository.findPageByEventId(eventId, pageable);
    }

    @Override
    public Double getAverageByEventId(Long eventId) {
        return reviewRepository.getAverageByEventId(eventId);
    }
}
