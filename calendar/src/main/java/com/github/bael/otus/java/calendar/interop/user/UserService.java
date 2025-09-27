package com.github.bael.otus.java.calendar.interop.user;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserService {
    UserInfo findUser(UUID userId);

    List<UserInfo> findUsers(Set<UUID> userIds);
}
