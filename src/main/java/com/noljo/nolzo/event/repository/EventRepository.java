package com.noljo.nolzo.event.repository;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByEventCategory(EventCategory eventCategory);

    @Query("""
        SELECT e FROM Event e
        WHERE (:keyword = 'all' OR e.title LIKE CONCAT('%', :keyword, '%'))
          AND e.id = (
                SELECT MIN(e2.id)
                FROM Event e2
                WHERE e2.title = e.title
          )
        ORDER BY e.title ASC
    """)
    List<Event> findOnePerTitle(@Param("keyword") String keyword);

    List<Event> findTop10ByEventCategoryOrderByViewCountDesc(EventCategory eventCategory);
}
