import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class Hotel {
    private String name;
    private List<Room> rooms;

    public Hotel(String name) {
        this.name = name;
        rooms = new ArrayList<>();
        initializeRooms();
    }

    private void initializeRooms() {
        int roomNo = 1;
        for (int i = 0; i < 50; i++) rooms.add(new Room(roomNo++, "Single", 1000));
        for (int i = 0; i < 50; i++) rooms.add(new Room(roomNo++, "Double", 1800));
        for (int i = 0; i < 30; i++) rooms.add(new Room(roomNo++, "Deluxe", 2500));
        for (int i = 0; i < 20; i++) rooms.add(new Room(roomNo++, "Suite", 4000));
    }

    public String getName() { return name; }
    public List<Room> getRooms() { return rooms; }

    public Room getAvailableRoom(String type, LocalDateTime checkIn, LocalDateTime checkOut) {
        for (Room r : rooms)
            if (r.getRoomType().equals(type) && r.isAvailable(checkIn, checkOut))
                return r;
        return null;
    }
}
