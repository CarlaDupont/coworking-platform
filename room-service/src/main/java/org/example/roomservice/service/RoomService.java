package org.example.roomservice.service;

import org.example.roomservice.entity.Room;
import org.example.roomservice.producer.RoomEventProducer;
import org.example.roomservice.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomEventProducer roomEventProducer;

    public RoomService(RoomRepository roomRepository, RoomEventProducer roomEventProducer) {
        this.roomRepository = roomRepository;
        this.roomEventProducer = roomEventProducer;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public Room createRoom(Room room) {
        room.setAvailable(true);
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room updatedRoom) {
        return roomRepository.findById(id)
                .map(room -> {
                    room.setName(updatedRoom.getName());
                    room.setCity(updatedRoom.getCity());
                    room.setCapacity(updatedRoom.getCapacity());
                    room.setType(updatedRoom.getType());
                    room.setHourlyRate(updatedRoom.getHourlyRate());
                    room.setAvailable(updatedRoom.isAvailable());
                    return roomRepository.save(room);
                })
                .orElseThrow(() -> new NoSuchElementException("Room not found"));
    }

    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Room not found"));

        roomRepository.delete(room);
        roomEventProducer.publishRoomDeleted(id);
    }

    public boolean isRoomAvailable(Long id) {
        return roomRepository.findById(id)
                .map(Room::isAvailable)
                .orElse(false);
    }

    public Room updateAvailability(Long id, boolean available) {
        return roomRepository.findById(id)
                .map(room -> {
                    room.setAvailable(available);
                    return roomRepository.save(room);
                })
                .orElseThrow(() -> new NoSuchElementException("Room not found"));
    }
}