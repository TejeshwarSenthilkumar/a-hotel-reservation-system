import java.time.LocalDateTime;
import java.util.*;
import java.io.*;

public class ReservationSystem {
    public List<Hotel> hotels;

    public ReservationSystem() {
        hotels = new ArrayList<>();
    }

    public void addHotel(Hotel hotel) {
        hotels.add(hotel);
    }

    public boolean makeReservation(String hotelName, String guestName, LocalDateTime checkIn,
                                   LocalDateTime checkOut, int guests, String roomType) {
        for (Hotel hotel : hotels) {
            if (hotel.getName().equalsIgnoreCase(hotelName)) {
                Room room = hotel.getAvailableRoom(roomType, checkIn, checkOut);
                if (room != null) {
                    room.book(guestName, guests, checkIn, checkOut);
                    saveInvoice(room, hotelName);
                    return true;
                }
            }
        }
        return false;
    }

    public List<Room> getAllBookedRooms() {
        List<Room> booked = new ArrayList<>();
        for (Hotel hotel : hotels) {
            for (Room room : hotel.getRooms()) {
                if (room.isBooked()) booked.add(room);
            }
        }
        return booked;
    }

    private void saveInvoice(Room room, String hotelName) {
        try {
            String filename = "Invoice_" + room.getGuestName().replaceAll("\\s+", "") +
                              "_" + System.currentTimeMillis() + ".txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write("----- EZRes Hotel Invoice -----\n");
            writer.write("Hotel: " + hotelName + "\n");
            writer.write("Guest Name: " + room.getGuestName() + "\n");
            writer.write("Room No: " + room.getRoomNumber() + "\n");
            writer.write("Room Type: " + room.getRoomType() + "\n");
            writer.write("Guests: " + room.getGuests() + "\n");
            writer.write("Check-in: " + room.getCheckInDate() + "\n");
            writer.write("Check-out: " + room.getCheckOutDate() + "\n");
            writer.write("Total Amount (Incl. 12% Tax): ₹" + room.getTotalAmount() + "\n");
            writer.write("-------------------------------\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAllBookings(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Hotel,Guest,RoomNo,Type,Guests,CheckIn,CheckOut,Total\n");
            for (Hotel hotel : hotels) {
                for (Room room : hotel.getRooms()) {
                    if (room.isBooked()) {
                        writer.write(String.format("%s,%s,%d,%s,%d,%s,%s,%.2f\n",
                                hotel.getName(),
                                room.getGuestName(),
                                room.getRoomNumber(),
                                room.getRoomType(),
                                room.getGuests(),
                                room.getCheckInDate(),
                                room.getCheckOutDate(),
                                room.getTotalAmount()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
