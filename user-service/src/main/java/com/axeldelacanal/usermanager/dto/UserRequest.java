package com.axeldelacanal.usermanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequest {

    @NotBlank
    @Size(min = 3, max = 64)
    public String username;

    @NotBlank
    @Email
    @Size(max = 255)
    public String email;

    @NotBlank
    @Size(min = 8, max = 128)
    public String password;
}
