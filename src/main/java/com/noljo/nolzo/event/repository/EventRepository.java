package com.noljo.nolzo.event.repository;

import com.noljo.nolzo.event.dto.EventResponse;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByEventCategory(EventCategory eventCategory);


    @Query("""
            select e
            from Event e
            where e.id in(
                select min(e2.id)
                from Event e2
                where e2.eventCategory = :category
                group by e2.title
            )
            """)
    List<Event> findDistinctEventByCategory(@Param("category")EventCategory category);

    List<Event> findAllByTitle(String title);
}
