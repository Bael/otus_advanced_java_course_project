package com.github.bael.otus.java.calendar.data;

import com.github.bael.otus.java.calendar.entity.SchedulingPoll;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface SchedulingPollRepository extends CrudRepository<SchedulingPoll, Long> {
}
