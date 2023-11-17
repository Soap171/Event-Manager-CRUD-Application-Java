import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventCRUDApp extends JFrame {
    private Connection connection;
    private JList<String> eventList;
    private DefaultListModel<String> eventListModel;
    private JTextField eventNameField;
    private JTextField eventDateField;
    private JTextArea descriptionField;
    private JTextField phoneNumberField;
    private JCheckBox notifiedCheckBox;

    public EventCRUDApp() {
        // Initialize Swing components
        eventListModel = new DefaultListModel<>();
        eventList = new JList<>(eventListModel);
        eventNameField = new JTextField(20); // Increased size
        eventDateField = new JTextField(20); // Increased size
        descriptionField = new JTextArea(5, 20); // Increased rows and columns
        phoneNumberField = new JTextField(20); // Increased size
        notifiedCheckBox = new JCheckBox("Notified");

        JButton deleteButton = new JButton("Delete");
        JButton updateButton = new JButton("Update");
        JButton newButton = new JButton("New");

        // Set layout for rightPanel
        JPanel rightPanel = new JPanel(new GridBagLayout());

        // Create GridBagConstraints for consistent layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add components with labels in a single row
        addRow(rightPanel, gbc, new JLabel("Event Name:"), eventNameField);
        addRow(rightPanel, gbc, new JLabel("Event Date:"), eventDateField);
        addRow(rightPanel, gbc, new JLabel("Description:"), new JScrollPane(descriptionField));
        addRow(rightPanel, gbc, new JLabel("Phone Number:"), phoneNumberField);
        addRow(rightPanel, gbc, new JLabel("Notified:"), notifiedCheckBox);

        // Add buttons at the bottom
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Use REMAINDER to span the remaining columns
        gbc.fill = GridBagConstraints.HORIZONTAL;
        rightPanel.add(deleteButton, gbc);
        gbc.gridy++;
        rightPanel.add(updateButton, gbc);
        gbc.gridy++;
        rightPanel.add(newButton, gbc);

        add(new JScrollPane(eventList), BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Set up event listeners
        eventList.addListSelectionListener(e -> displaySelectedEvent());

        deleteButton.addActionListener(e -> deleteEvent());
        updateButton.addActionListener(e -> updateEvent());
        newButton.addActionListener(e -> newEvent());

        // Connect to the database (replace with your database details)
        String url = "jdbc:mysql://localhost:3306/EventManager";
        String user = "root";
        String password = "200434";

        try {
            connection = DriverManager.getConnection(url, user, password);
            loadEvents();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the database", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Set frame properties
        setTitle("Event CRUD App");
        setSize(1000, 600); // Increased width
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, JComponent label, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.gridy--;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);

        gbc.gridy++;
    }




    private void loadEvents() {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM events");

            while (resultSet.next()) {
                int eventId = resultSet.getInt("event_id");
                String eventName = resultSet.getString("event_name");
                String eventInfo = eventId + " - " + eventName;
                eventListModel.addElement(eventInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading events", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displaySelectedEvent() {
        int selectedIndex = eventList.getSelectedIndex();
        if (selectedIndex != -1) {
            int eventId = getEventId(selectedIndex);

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM events WHERE event_id=?")) {

                preparedStatement.setInt(1, eventId);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    eventNameField.setText(resultSet.getString("event_name"));
                    eventDateField.setText(resultSet.getString("event_date"));
                    descriptionField.setText(resultSet.getString("description"));
                    phoneNumberField.setText(resultSet.getString("phone_number"));
                    notifiedCheckBox.setSelected(resultSet.getBoolean("notified"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading event details", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getEventId(int selectedIndex) {
        String eventInfo = eventListModel.getElementAt(selectedIndex);

        // Use regular expression to extract numeric part (event_id)
        Pattern pattern = Pattern.compile("\\b(\\d+)\\b");
        Matcher matcher = pattern.matcher(eventInfo);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        // Handle the case where event_id is not found
        return -1; // or another appropriate value
    }

    private void deleteEvent() {
        int selectedIndex = eventList.getSelectedIndex();
        if (selectedIndex != -1) {
            int eventId = getEventId(selectedIndex);

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM events WHERE event_id=?")) {

                preparedStatement.setInt(1, eventId);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Event deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    eventListModel.remove(selectedIndex);
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "No event deleted", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting event", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateEvent() {
        int selectedIndex = eventList.getSelectedIndex();
        if (selectedIndex != -1) {
            int eventId = getEventId(selectedIndex);

            String eventName = eventNameField.getText();
            String eventDate = eventDateField.getText();
            String description = descriptionField.getText();
            String phoneNumber = phoneNumberField.getText();
            boolean notified = notifiedCheckBox.isSelected();

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE events SET event_name=?, event_date=?, description=?, phone_number=?, notified=? WHERE event_id=?")) {

                preparedStatement.setString(1, eventName);
                preparedStatement.setString(2, eventDate);
                preparedStatement.setString(3, description);
                preparedStatement.setString(4, phoneNumber);
                preparedStatement.setBoolean(5, notified);
                preparedStatement.setInt(6, eventId);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Event updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    eventListModel.setElementAt(eventId + " - " + eventName, selectedIndex);
                } else {
                    JOptionPane.showMessageDialog(this, "No event updated", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating event", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void newEvent() {
        JTextField eventNameField = new JTextField(20);
        JTextField eventDateField = new JTextField(20);
        JTextArea descriptionField = new JTextArea(8, 20); // Increased rows
        JTextField phoneNumberField = new JTextField(20);
        JCheckBox notifiedCheckBox = new JCheckBox("Notified");


        Object[] message = {
                "Event Name:", eventNameField,
                "Event Date:", eventDateField,
                "Description:", new JScrollPane(descriptionField),
                "Phone Number:", phoneNumberField,
                "Notified:", notifiedCheckBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "New Event", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String eventName = eventNameField.getText();
            String eventDate = eventDateField.getText();
            String description = descriptionField.getText();
            String phoneNumber = phoneNumberField.getText();
            boolean notified = notifiedCheckBox.isSelected();

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO events (event_name, event_date, description, phone_number, notified) VALUES (?, ?, ?, ?, ?)")) {

                preparedStatement.setString(1, eventName);
                preparedStatement.setString(2, eventDate);
                preparedStatement.setString(3, description);
                preparedStatement.setString(4, phoneNumber);
                preparedStatement.setBoolean(5, notified);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Event added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    loadEvents(); // Refresh the event list
                } else {
                    JOptionPane.showMessageDialog(this, "No event added", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding event", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        eventNameField.setText("");
        eventDateField.setText("");
        descriptionField.setText("");
        phoneNumberField.setText("");
        notifiedCheckBox.setSelected(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EventCRUDApp::new);
    }
}
