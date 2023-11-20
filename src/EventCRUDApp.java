import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.*;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class EventCRUDApp extends JFrame {
    private Connection connection;
    private JList<String> eventList;
    private DefaultListModel<String> eventListModel;
    private JTextField eventNameField;
    private JTextField eventDateField;
    private JTextArea descriptionField;
    private JTextField phoneNumberField;
    private JCheckBox notifiedCheckBox;
    // Inside the EventCRUDApp class
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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
        deleteButton.setFocusPainted(false);
        JButton updateButton = new JButton("Update");
        updateButton.setFocusPainted(false);
        JButton newButton = new JButton("ADD New Event");
        newButton.setFocusPainted(false);
        newButton.setBackground(new Color(2, 9, 69));
        newButton.setForeground(new Color(252, 252, 252));
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

        // Modify the eventList and its container
        eventList = new JList<>(eventListModel);
        eventList.setVisibleRowCount(10); // Set the number of visible rows as needed

        // Set the preferred width of the JScrollPane containing the eventList
        JScrollPane scrollPane = new JScrollPane(eventList);
        scrollPane.setPreferredSize(new Dimension(500, getHeight())); // Adjust the width as needed

        // Add the modified JScrollPane to the left side of the BorderLayout
        add(scrollPane, BorderLayout.WEST);

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
        setTitle("Event Manager");
        setSize(1000, 600); // Increased width
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        scheduler.scheduleAtFixedRate(this::sendNotifications, 0, 1, TimeUnit.DAYS);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, JComponent label, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);

        gbc.gridy++;
    }


    private void sendNotifications() {
        // Get the current date
        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.toString();

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM events WHERE notified=true AND event_date='" + formattedDate + "'");

            while (resultSet.next()) {
                String eventName = resultSet.getString("event_name");
                String eventDate = resultSet.getString("event_date");
                String phoneNumber = resultSet.getString("phone_number");

                // Check if the event date is today
                if (eventDate.equals(currentDate.toString())) {
                    // Send SMS notification
                    String notificationMessage = "Reminder: Event today - " + eventName;
                    SmsSender.sendSms(phoneNumber, notificationMessage);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as needed
            JOptionPane.showMessageDialog(this, "Error sending notifications", "Error", JOptionPane.ERROR_MESSAGE);
        }

        System.out.println("Sending notifications task completed at: " + LocalDateTime.now());
    }



    private void loadEvents() {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM events");

            // Clear the existing data from the DefaultListModel
            eventListModel.clear();

            while (resultSet.next()) {
                int eventId = resultSet.getInt("event_id");
                String eventName = resultSet.getString("event_name");
                String eventDate = resultSet.getString("event_date");
                String eventDescription = resultSet.getString("description");
                boolean notified = resultSet.getBoolean("notified");

                // Build the information string, including the notified status
                String eventInfo = eventId + " - " + eventName + " (" + eventDate + ") - " + eventDescription
                        + (notified ? " - Notified" : " - Not Notified");

                // Add the string to the DefaultListModel
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
            if (eventName.isEmpty() || eventDate.isEmpty() || description.isEmpty() || phoneNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All the fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            }

            else{
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
                        loadEvents();
                        clearFields();
                    } else {
                        JOptionPane.showMessageDialog(this, "No event updated", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error updating event", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }


        }
    }

    private void newEvent() {
        JTextField eventNameField = new JTextField(20);
        JTextField eventDateField = new JTextField(20);
        JTextArea descriptionField = new JTextArea(8, 20);
        JTextField phoneNumberField = new JTextField(20);
        JCheckBox notifiedCheckBox = new JCheckBox("Notified");

        boolean validInput = false;

        while (!validInput) {
            Object[] message = {
                    "Event Name:", eventNameField,
                    "Event Date:", eventDateField,
                    "Description:", new JScrollPane(descriptionField),
                    "Phone Number:", phoneNumberField,
                    "Notified:", notifiedCheckBox
            };

            int option = JOptionPane.showConfirmDialog(this, message, "New Event", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String eventName = eventNameField.getText().trim();
                String eventDate = eventDateField.getText().trim();
                String description = descriptionField.getText().trim();
                String phoneNumber = phoneNumberField.getText().trim();
                boolean notified = notifiedCheckBox.isSelected();

                // Validate input
                if (eventName.isEmpty() || eventDate.isEmpty() || description.isEmpty() || phoneNumber.isEmpty())  {
                    JOptionPane.showMessageDialog(this, "All the fileds are required", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    validInput = true; // Set to true to exit the loop
                    // Additional validations and processing can be added here
                    try (PreparedStatement preparedStatement = connection.prepareStatement(
                            "INSERT INTO events (event_name, event_date, description, phone_number, notified) VALUES (?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS)) {

                        preparedStatement.setString(1, eventName);
                        preparedStatement.setString(2, eventDate);
                        preparedStatement.setString(3, description);
                        preparedStatement.setString(4, phoneNumber);
                        preparedStatement.setBoolean(5, notified);

                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            // Get the generated keys (if any)
                            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                    int eventId = generatedKeys.getInt(1);

                                    // Update the existing DefaultListModel
                                    String eventInfo = eventId + " - " + eventName + " (" + eventDate + ")";
                                    eventListModel.addElement(eventInfo);

                                    // Send SMS notification
                                    SmsSender.sendSms(phoneNumber, "New event added: " + eventName +" Date is "+ eventDate);
                                }
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error adding event", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                // User clicked Cancel or closed the dialog
                validInput = true; // Set to true to exit the loop without adding an event
            }
        }
    }


    public class SmsSender {

        private static final String API_URL = "https://doubtful-hare-sweatshirt.cyclic.app/send-sms";
        private static final String API_ID = "5967741";
        private static final String API_TOKEN =
                "zw8uyfSJObgc3gL7n5bvhNthk7xrrY52nsXJTaNG28pBc79KpBdlOYUozxTGR9eB3drbtQDOYv9w43hQlc5KtbnRSiPATZ3Ic8rCO64EDqR6HL7LQYtrvX1DdI7jHTPlK3a4qrsKHxtm" +
                        "J7sLd3GYFhmA9T7UVGDf9lpYsr56s0SSgyrli3cShCT9xUE05ahZlH2DbRNLoKVkSV16ZMj6rrpVgFv83RncAH2yE8mDUc3seDEnCBMuDYp9dkWHTlw20pKIAmgHHeBwPRy53ezaqi5kpOLIVe0wNuFcTn0uBQm04XCBtx5uGz1S" +
                        "2igOMOspx24Sn810Y8bCUYYMV4xBDTEz0IaY0fxrkDfFybiRV3VoozBt5ZOT1Slk2L2YcmSnzc7WVCbLDsamUSdZWMjDzMm2O0xV86NpmVGvqnENDujgvjcQiNKmQx5XNvGMvBkpf6rNkYekHdM09gRlZV9SrdWjT94zQIVgehwYI" +
                        "WN5TPyIrGIRX1PSPDx19MFLsI5kmihy2C9fLshwyOstFRR52l5hXeYE2MCyKRbZWt3dbkkDlEdxrqEW5veajq16Egn2ENjwi8CoK2I1v1u0OWhoQ6Xg2X3xGJNdmkwfdqlanDbwig1LS5L4oboeDmMu53kfoaQ0907WIOSnNjSqgffIbg6l" +
                        "VxoOST6wY2Weker4IgRg57ie9Xo0eFnY3kSnhrBZ9prYtGDMV8u8CXFFVCoo7hJSR8x6FU0p3gXr5MtSwxbVNHnSo2SEWH3K4E1twyzr0NQIR3P3X4LqFLXeEFX3GBiHzoww87LMmucZgudelDiYW8jfTDpfz9aVMLxxGIfglaK3ThuFDheFTx" +
                        "ptAnv8IUD7V0AZsOSN4jRCPMsNr0vGBecxFjVQEhdoArtNHd5NxrAFuSTJH7thgKhmXD6KNecIqP6sY7QKOqgXL58zEBvkBp8ucker3PitrvYasEBmdSwPoQqjt8ORFRX7at1BvibGzZ8OeWE64Ypfemzsu3lO0XfOyNpphbBrkiTOnXSm";


        public static void sendSms(String phoneNumber, String message) {
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"message\":\"" + message + "\",\"phoneNumber\":\"" + phoneNumber + "\"}");

            Request request = new Request.Builder()
                    .url(API_URL)
                    .method("POST", body)
                    .addHeader("Authorization", "Bearer " + API_TOKEN)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Api-Id", API_ID)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    System.out.println("SMS sent successfully");
                } else {
                    System.out.println("Failed to send SMS. HTTP Status Code: " + response.code());
                }
            } catch (Exception e) {
                e.printStackTrace();
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
