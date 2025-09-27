package com.github.bael.otus.java.calendar.data;

import com.github.bael.otus.java.calendar.entity.PollAttendee;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PollAttendeeRepository extends CrudRepository<PollAttendee, Long> {

    List<PollAttendee> findByPoll_Id(Long pollId);

    boolean existsByPoll_IdAndUserId(Long pollId, UUID userId);
}