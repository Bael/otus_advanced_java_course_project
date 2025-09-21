package com.github.bael.otus.java.userservice.rest.dto;


import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserAccountUpdateRequest {
    private String firstName;
    private String lastName;

    @Email(message = "Email should be valid")
    private String email;

    private String timezone;
    private Boolean enabled;
}
