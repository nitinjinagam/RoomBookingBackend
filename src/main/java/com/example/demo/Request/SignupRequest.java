package com.example.demo.Request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignupRequest {
    private String email;
    private String name;
    private String password;

}
