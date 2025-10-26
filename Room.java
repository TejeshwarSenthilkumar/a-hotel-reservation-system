import java.time.LocalDateTime;

public class Room {
    private int roomNumber;
    private String roomType;
    private double pricePerNight;
    private boolean booked;
    private String guestName;
    private int guests;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;

    public Room(int roomNumber, String roomType, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.booked = false;
    }

    public int getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public double getPricePerNight() { return pricePerNight; }
    public boolean isBooked() { return booked; }
    public String getGuestName() { return guestName; }
    public int getGuests() { return guests; }
    public LocalDateTime getCheckInDate() { return checkInDate; }
    public LocalDateTime getCheckOutDate() { return checkOutDate; }

    public boolean isAvailable(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (!booked) return true;
        return checkOut.isBefore(checkInDate) || checkIn.isAfter(checkOutDate);
    }

    public void book(String guestName, int guests, LocalDateTime checkIn, LocalDateTime checkOut) {
        this.guestName = guestName;
        this.guests = guests;
        this.checkInDate = checkIn;
        this.checkOutDate = checkOut;
        this.booked = true;
    }

    public void cancelBooking() {
    this.booked = false;
    this.guestName = null;
    this.guests = 0;
    this.checkInDate = null;
    this.checkOutDate = null;
    }
   
    public double getTotalAmount() {
        long hours = java.time.temporal.ChronoUnit.HOURS.between(checkInDate, checkOutDate);
        double amount = pricePerNight * (hours / 24.0) * guests;
        return amount * 1.12;
    }

    @Override
    public String toString() {
        if (!booked) return "Room " + roomNumber + " (" + roomType + ") - Available";
        return "Room " + roomNumber + " (" + roomType + ") - " + guestName + " | " +
                guests + " guests | " + checkInDate + " to " + checkOutDate +
                " | Total: ₹" + String.format("%.2f", getTotalAmount());
    }
}
