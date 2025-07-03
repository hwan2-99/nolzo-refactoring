package com.noljo.nolzo.review.service;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.review.dto.request.ReviewCreateRequest;
import com.noljo.nolzo.review.dto.response.ReviewResponse;
import com.noljo.nolzo.review.entity.Review;
import com.noljo.nolzo.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    public ReviewResponse create(Long memberId, ReviewCreateRequest request) {
        Member member = memberRepository.getOrThrow(memberId);

        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다."));

        Review review = new Review(request.content(), request.rating(), event, member);

        return ReviewResponse.from(reviewRepository.save(review));
    }
}
