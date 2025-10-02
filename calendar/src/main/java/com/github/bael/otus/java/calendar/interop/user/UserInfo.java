package com.github.bael.otus.java.calendar.interop.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    String firstName;
    String lastName;
    String email;
}
