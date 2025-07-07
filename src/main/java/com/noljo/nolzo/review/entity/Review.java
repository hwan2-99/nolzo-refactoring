package com.noljo.nolzo.review.entity;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.global.BaseEntity;
import com.noljo.nolzo.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    private String content;

    private int rating;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public Review(String content, int rating, Event event, Member member) {
        this.content = content;
        this.rating = rating;
        this.event = event;
        this.member = member;
    }

    public boolean isOwner(Long memberId) {
        return this.member.getId().equals(memberId);
    }

    public void update(String content, int rating) {
        this.content = content;
        this.rating = rating;
    }
}
