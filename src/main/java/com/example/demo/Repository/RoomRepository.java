package com.example.demo.Repository;

import com.example.demo.Model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    Room findByRoomName(String roomName);
    Room findByRoomID(int roomId);
    List<Room> findAll();
    List<Room> findByRoomCapacityGreaterThanEqual(int capacity);
}