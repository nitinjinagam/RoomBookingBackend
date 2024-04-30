package com.example.demo.Request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddRoomRequest {
    private String roomName;
    private int roomCapacity;
}
