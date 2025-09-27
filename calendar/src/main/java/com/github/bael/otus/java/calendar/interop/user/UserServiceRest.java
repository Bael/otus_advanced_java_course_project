package com.github.bael.otus.java.calendar.interop.user;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * todo add cache
 */
public class UserServiceRest implements UserService {
    @Override
    public UserInfo findUser(UUID userId) {
        return null;
    }

    @Override
    public List<UserInfo> findUsers(Set<UUID> userIds) {
        return Collections.emptyList();
    }
}
