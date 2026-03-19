package org.example.roomservice.controller;

import org.example.roomservice.entity.Room;
import org.example.roomservice.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping({"/rooms", "/api/rooms"})
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{id}")
    public Room getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id)
                .orElseThrow(() -> new NoSuchElementException("Room not found"));
    }

    @PostMapping
    public Room createRoom(@RequestBody Room room) {
        return roomService.createRoom(room);
    }

    @PutMapping("/{id}")
    public Room updateRoom(@PathVariable Long id, @RequestBody Room room) {
        return roomService.updateRoom(id, room);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
    }

    @GetMapping("/{id}/availability")
    public boolean isRoomAvailable(@PathVariable Long id) {
        return roomService.isRoomAvailable(id);
    }

    @RequestMapping(value = "/{id}/availability", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public Room updateAvailability(@PathVariable Long id, @RequestParam boolean available) {
        return roomService.updateAvailability(id, available);
    }
}