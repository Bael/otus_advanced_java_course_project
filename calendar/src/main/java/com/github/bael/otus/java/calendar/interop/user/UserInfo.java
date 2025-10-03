package com.github.bael.otus.java.calendar.interop.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    UUID userId;
    String firstName;
    String lastName;
    String email;

    public String fullname() {
        return firstName + " " + lastName;
    }
}
