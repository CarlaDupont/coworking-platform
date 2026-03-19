package org.example.roomservice.service;

import org.example.roomservice.entity.Room;
import org.example.roomservice.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public Room createRoom(Room room) {
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
                .orElseThrow(() -> new RuntimeException("Salle non trouvée avec l'id : " + id));
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
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
                .orElseThrow(() -> new RuntimeException("Salle non trouvée avec l'id : " + id));
    }
}