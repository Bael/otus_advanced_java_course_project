package com.github.bael.otus.java.calendar.data;

import com.github.bael.otus.java.calendar.entity.PollTimeSlot;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PollTimeSlotRepository extends CrudRepository<PollTimeSlot, Long> {

    List<PollTimeSlot> findByPollId(Long pollId);
}