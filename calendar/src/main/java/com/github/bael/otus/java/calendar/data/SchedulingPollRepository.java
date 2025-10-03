package com.github.bael.otus.java.calendar.data;

import com.github.bael.otus.java.calendar.entity.SchedulingPoll;
import com.github.bael.otus.java.calendar.entity.SchedulingPollStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

public interface SchedulingPollRepository extends CrudRepository<SchedulingPoll, Long> {
    List<SchedulingPoll> findAllByDeadlineAfterAndStatus(Instant deadlineAfter, SchedulingPollStatus status);
}
