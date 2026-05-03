package com.axeldelacanal.usermanager.dto;

import com.axeldelacanal.usermanager.domain.User;

public class UserResponse {

    public Long id;
    public String username;
    public String email;

    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.id = user.getId();
        response.username = user.getUsername();
        response.email = user.getEmail();
        return response;
    }
}
