package com.example.checkscam.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;


@Data
public class UpdateUserRequest {
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @Email(message = "Email should be valid")
    private String email;
    
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;
    
    private String avatar;
}
