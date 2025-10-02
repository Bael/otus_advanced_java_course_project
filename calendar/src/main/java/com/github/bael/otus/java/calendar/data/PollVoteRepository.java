package com.github.bael.otus.java.calendar.data;

import com.github.bael.otus.java.calendar.entity.PollVote;
import org.springframework.data.domain.Pageable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

public interface PollVoteRepository extends CrudRepository<PollVote, Long> {

    List<PollVote> findByPollIdAndTimeSlotId(Long pollId, Long timeSlotId, Pageable pageable);

    List<PollVote> findByPollIdAndUserId(Long pollId, UUID userId, Pageable pageable);
    List<PollVote> findByPollId(Long pollId);
}