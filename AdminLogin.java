import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

// AdminLogin class
public class AdminLogin {

    private ReservationSystem system;

    public AdminLogin(ReservationSystem system) {
        this.system = system;
        createLoginGUI();
    }

    private void createLoginGUI() {
        JFrame frame = new JFrame("EZRes Admin Login");
        frame.setSize(400, 200);
        frame.setLayout(new GridLayout(3,2,10,10));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Login");

        loginBtn.setFocusPainted(false);
        loginBtn.setBackground(Color.decode("#FF6F61"));
        loginBtn.setForeground(Color.white);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            if (user.equals("admin") && pass.equals("password")) {
                frame.dispose();
                showAdminDashboard();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(userLabel); frame.add(userField);
        frame.add(passLabel); frame.add(passField);
        frame.add(new JLabel()); frame.add(loginBtn);

        frame.setVisible(true);
    }

    private void showAdminDashboard() {
        JFrame frame = new JFrame("EZRes Admin Dashboard");
        frame.setSize(950, 500);
        frame.setLayout(new BorderLayout(10,10));

        String[] columns = {"Room No","Guest Name","Room Type","Guests","Check-in","Check-out","Total Amount","Action"};
        DefaultTableModel model = new DefaultTableModel(columns,0);

        List<Room> bookedRooms = system.getAllBookedRooms();
        for (Room r : bookedRooms) {
            model.addRow(new Object[]{
                    r.getRoomNumber(),
                    r.getGuestName(),
                    r.getRoomType(),
                    r.getGuests(),
                    r.getCheckInDate(),
                    r.getCheckOutDate(),
                    "₹" + String.format("%.2f", r.getTotalAmount()),
                    "Delete"
            });
        }

        JTable table = new JTable(model);

        // Set custom renderer and editor for "Delete" button column
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), system, this, table));

        JScrollPane scroll = new JScrollPane(table);
        frame.add(scroll, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    // ===== Inner class: ButtonRenderer =====
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText((value == null) ? "Delete" : value.toString());
            setBackground(Color.RED);
            setForeground(Color.WHITE);
            return this;
        }
    }

    // ===== Inner class: ButtonEditor =====
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private boolean clicked;
        private ReservationSystem system;
        private AdminLogin admin;
        private JTable table;

        public ButtonEditor(JCheckBox checkBox, ReservationSystem system, AdminLogin admin, JTable table) {
            super(checkBox);
            this.system = system;
            this.admin = admin;
            this.table = table;
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(Color.RED);
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            button.setText((value == null) ? "Delete" : value.toString());
            clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) {
                int row = table.getSelectedRow();
                int roomNo = (int) table.getValueAt(row, 0);
                for (Hotel hotel : system.hotels) {
                    for (Room room : hotel.getRooms()) {
                        if (room.getRoomNumber() == roomNo) {
                            room.cancelBooking();
                            ((DefaultTableModel)table.getModel()).removeRow(row);
                            break;
                        }
                    }
                }
            }
            clicked = false;
            return "Delete";
        }

        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
