import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EZResGUI {

    private ReservationSystem system;
    private JPanel reservationsPanel;
    private JPanel availableRoomsPanel;

    private int guestCount = 1;
    private JLabel guestCountLabel;
    private JComboBox<String> roomTypeCombo;

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public EZResGUI(ReservationSystem system) {
        this.system = system;
        createGUI();
    }

    private void createGUI() {
        JFrame frame = new JFrame("EZRes Hotel Reservation");
        frame.setSize(950, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        // Top Panel with Gradient
        JPanel topPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, Color.decode("#FF6F61"),
                        0, getHeight(), Color.decode("#FFA07A"));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel hotelLabel = new JLabel("Hotel: EZRes");
        hotelLabel.setFont(new Font("Arial", Font.BOLD, 20));
        hotelLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        topPanel.add(hotelLabel, gbc);
        gbc.gridwidth = 1;

        // Check-in & Check-out
        JLabel checkInLabel = new JLabel("Check-in:");
        SpinnerDateModel checkInModel = new SpinnerDateModel();
        JSpinner checkInSpinner = new JSpinner(checkInModel);
        checkInSpinner.setEditor(new JSpinner.DateEditor(checkInSpinner, "yyyy-MM-dd HH:mm"));

        JLabel checkOutLabel = new JLabel("Check-out:");
        SpinnerDateModel checkOutModel = new SpinnerDateModel();
        JSpinner checkOutSpinner = new JSpinner(checkOutModel);
        checkOutSpinner.setEditor(new JSpinner.DateEditor(checkOutSpinner, "yyyy-MM-dd HH:mm"));

        // Guests
        JLabel guestsLabel = new JLabel("Guests:");
        JPanel guestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        guestPanel.setOpaque(false);
        JButton minusBtn = new JButton("-");
        JButton plusBtn = new JButton("+");
        guestCountLabel = new JLabel(String.valueOf(guestCount));
        guestPanel.add(minusBtn); guestPanel.add(guestCountLabel); guestPanel.add(plusBtn);

        minusBtn.addActionListener(e -> {
            if (guestCount > 1) guestCount--;
            guestCountLabel.setText(String.valueOf(guestCount));
            refreshAvailableRooms(checkInSpinner, checkOutSpinner);
        });
        plusBtn.addActionListener(e -> {
            if (guestCount < 10) guestCount++;
            guestCountLabel.setText(String.valueOf(guestCount));
            refreshAvailableRooms(checkInSpinner, checkOutSpinner);
        });

        // Room Type Dropdown
        JLabel roomTypeLabel = new JLabel("Room Type:");
        String[] types = {"Single", "Double", "Deluxe", "Suite"};
        roomTypeCombo = new JComboBox<>(types);

        // Buttons
        JButton checkBtn = new JButton("Check Availability");
        styleButton(checkBtn, "#FF6F61", "#FF3B2E");

        JButton adminBtn = new JButton("Admin Login");
        styleButton(adminBtn, "#666666", "#444444");
        adminBtn.addActionListener(e -> new AdminLogin(system));

        // Layout top panel
        gbc.gridx = 0; gbc.gridy = 1; topPanel.add(checkInLabel, gbc);
        gbc.gridx = 1; topPanel.add(checkInSpinner, gbc);
        gbc.gridx = 0; gbc.gridy = 2; topPanel.add(checkOutLabel, gbc);
        gbc.gridx = 1; topPanel.add(checkOutSpinner, gbc);
        gbc.gridx = 0; gbc.gridy = 3; topPanel.add(guestsLabel, gbc);
        gbc.gridx = 1; topPanel.add(guestPanel, gbc);
        gbc.gridx = 0; gbc.gridy = 4; topPanel.add(roomTypeLabel, gbc);
        gbc.gridx = 1; topPanel.add(roomTypeCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 5; topPanel.add(adminBtn, gbc);
        gbc.gridx = 1; topPanel.add(checkBtn, gbc);

        frame.add(topPanel, BorderLayout.NORTH);

        // Reservations Panel (bottom)
        reservationsPanel = new JPanel();
        reservationsPanel.setLayout(new BoxLayout(reservationsPanel, BoxLayout.Y_AXIS));
        reservationsPanel.setBorder(BorderFactory.createTitledBorder("Reservations"));
        JScrollPane reservationScroll = new JScrollPane(reservationsPanel);
        reservationScroll.setPreferredSize(new Dimension(920, 150));
        frame.add(reservationScroll, BorderLayout.SOUTH);

        // Available Rooms Panel (center)
        availableRoomsPanel = new JPanel();
        availableRoomsPanel.setLayout(new GridLayout(0, 3, 10, 10));
        JScrollPane availableRoomsScroll = new JScrollPane(availableRoomsPanel);
        availableRoomsScroll.setPreferredSize(new Dimension(920, 350));
        frame.add(availableRoomsScroll, BorderLayout.CENTER);

        // Check Availability Button
        checkBtn.addActionListener(e -> refreshAvailableRooms(checkInSpinner, checkOutSpinner));

        frame.setVisible(true);
    }

    private void refreshAvailableRooms(JSpinner checkInSpinner, JSpinner checkOutSpinner) {
        LocalDateTime checkIn = LocalDateTime.parse(
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(((SpinnerDateModel)checkInSpinner.getModel()).getDate()),
                dtf);
        LocalDateTime checkOut = LocalDateTime.parse(
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(((SpinnerDateModel)checkOutSpinner.getModel()).getDate()),
                dtf);
        String roomType = (String) roomTypeCombo.getSelectedItem();

        if (!checkOut.isAfter(checkIn)) {
            JOptionPane.showMessageDialog(null, "Check-out must be after Check-in!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        availableRoomsPanel.removeAll();

        for (Hotel hotel : system.hotels) {
            for (Room room : hotel.getRooms()) {
                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));

                JLabel roomNo = new JLabel("Room " + room.getRoomNumber());
                JLabel type = new JLabel(room.getRoomType());
                JLabel price;

                if (!room.isAvailable(checkIn, checkOut) || room.isBooked()) {
                    card.setBackground(Color.LIGHT_GRAY);
                    price = new JLabel("Booked");
                    price.setForeground(Color.RED.darker());
                    card.setEnabled(false);
                } else {
                    long hours = java.time.temporal.ChronoUnit.HOURS.between(checkIn, checkOut);
                    double total = room.getPricePerNight() * (hours / 24.0) * guestCount * 1.12;
                    price = new JLabel("Total: ₹" + String.format("%.2f", total));
                    card.setBackground(Color.WHITE);

                    card.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseEntered(java.awt.event.MouseEvent evt) { card.setBackground(Color.decode("#FFE0B2")); }
                        public void mouseExited(java.awt.event.MouseEvent evt) { card.setBackground(Color.WHITE); }
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            String guestName = null;
                            boolean valid = false;

                            while (!valid) {
                                guestName = JOptionPane.showInputDialog(null, "Enter Guest Name:");
                                if (guestName == null) return; // Cancel
                                else if (guestName.trim().isEmpty())
                                    JOptionPane.showMessageDialog(null, "Name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                                else valid = true;
                            }

                            room.book(guestName, guestCount, checkIn, checkOut);
                            system.makeReservation(hotel.getName(), guestName, checkIn, checkOut, guestCount, roomType);
                            updateReservations();
                            refreshAvailableRooms(checkInSpinner, checkOutSpinner);
                            showBookingMessage("Booking Successful for " + guestName + "!");
                        }
                    });
                }

                card.add(roomNo);
                card.add(type);
                card.add(price);
                availableRoomsPanel.add(card);
            }
        }

        availableRoomsPanel.revalidate();
        availableRoomsPanel.repaint();
    }

    private void styleButton(JButton btn, String colorNormal, String colorHover) {
        btn.setFocusPainted(false);
        btn.setBackground(Color.decode(colorNormal));
        btn.setForeground(Color.white);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(Color.decode("#333333"), 1, true));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(Color.decode(colorHover)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(Color.decode(colorNormal)); }
        });
    }

    private void updateReservations() {
        reservationsPanel.removeAll();
        for (Hotel hotel : system.hotels)
            for (Room room : hotel.getRooms())
                if (room.isBooked())
                    reservationsPanel.add(new JLabel(room.toString()));
        reservationsPanel.revalidate();
        reservationsPanel.repaint();
    }

    private void showBookingMessage(String msgText) {
        JLabel msg = new JLabel(msgText);
        msg.setForeground(Color.GREEN.darker());
        msg.setFont(new Font("Arial", Font.BOLD, 16));
        reservationsPanel.add(msg);
        reservationsPanel.revalidate();
        reservationsPanel.repaint();

        new Timer(2000, ev -> {
            reservationsPanel.remove(msg);
            reservationsPanel.revalidate();
            reservationsPanel.repaint();
        }).start();
    }

    public static void main(String[] args) {
        ReservationSystem system = new ReservationSystem();
        system.addHotel(new Hotel("EZRes"));
        SwingUtilities.invokeLater(() -> new EZResGUI(system));
    }
}
