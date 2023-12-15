import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.*;

public class ParcelCRUDApp extends JFrame {

    private java.sql.Connection connection;
    private JList<String> parcelList;
    private DefaultListModel<String> parcelListModel;
    private JTextField destinationField;
    private JTextField senderContactField;
    private JTextField receiverContactField;
    private JTextField feeField;
    private JTextField weightField;
    private JComboBox<String> statusComboBox;
    private JButton deleteParcelButton;
    private JButton updateParcelButton;
    private JButton updateStatusButton;
    private JButton addParcelButton;  // New button for adding a parcel

    private JPanel rightPanel;  // Assuming you have a JPanel named rightPanel
    private JTextField searchField;
    private JButton searchButton;
    private JButton backToDashboard;
    public ParcelCRUDApp() {
        rightPanel = new JPanel(); // Instantiate rightPanel
        setIcon();

        // Initialize parcels components
        parcelListModel = new DefaultListModel<>();
        parcelList = new JList<>(parcelListModel);
        destinationField = new JTextField(20);
        senderContactField = new JTextField(20);
        receiverContactField = new JTextField(20);
        feeField = new JTextField(20);
        weightField = new JTextField(20);
        statusComboBox = new JComboBox<>(new String[]{"Pending", "Shipped", "Delivered", "Failed"});
        deleteParcelButton = new JButton("Delete Parcel");
        updateParcelButton = new JButton("Update Parcel");
        updateStatusButton = new JButton("Update Status");
        addParcelButton = new JButton("Add Parcel");
        backToDashboard = new JButton("Dashboard");

        // Set layout for the right panel
        rightPanel.setLayout(new GridBagLayout());

        // Initialize GridBagConstraints for parcels components
        GridBagConstraints gbcParcels = new GridBagConstraints();
        gbcParcels.anchor = GridBagConstraints.WEST;
        gbcParcels.insets = new Insets(5, 5, 5, 5);

        // Add parcels components with labels and input fields in a single row
        addRow(rightPanel, gbcParcels, new JLabel("Destination:"), destinationField);
        addRow(rightPanel, gbcParcels, new JLabel("Sender Contact:"), senderContactField);
        addRow(rightPanel, gbcParcels, new JLabel("Receiver Contact:"), receiverContactField);
        addRow(rightPanel, gbcParcels, new JLabel("Fee:"), feeField);
        addRow(rightPanel, gbcParcels, new JLabel("Weight:"), weightField);
        addRow(rightPanel, gbcParcels, new JLabel("Status:"), statusComboBox);

        // Add buttons at the bottom
        gbcParcels.gridx = 0;
        gbcParcels.gridy++;
        gbcParcels.gridwidth = GridBagConstraints.REMAINDER;
        gbcParcels.fill = GridBagConstraints.HORIZONTAL;
        rightPanel.add(deleteParcelButton, gbcParcels);
        gbcParcels.gridy++;
        rightPanel.add(updateParcelButton, gbcParcels);
        gbcParcels.gridy++;
        rightPanel.add(updateStatusButton, gbcParcels);
        gbcParcels.gridy++;
        rightPanel.add(addParcelButton, gbcParcels);

        // Set up event listeners for parcels
        parcelList.addListSelectionListener(e -> displaySelectedParcel());
        deleteParcelButton.addActionListener(e -> deleteParcel());
        updateParcelButton.addActionListener(e -> updateParcel());
        updateStatusButton.addActionListener(e -> updateParcelStatus());
        addParcelButton.addActionListener(e -> addNewParcel());

        backToDashboard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Dashboard dashboard = new Dashboard();
                setVisible(false);
            }
        });

        searchField = new JTextField(10);
        searchButton = new JButton("Search");
        searchButton.setFocusPainted(false);

        // Add search components to the right panel
        addRow(rightPanel, gbcParcels, new JLabel("Search Parcel by ID:"), searchField);
        addRow(rightPanel, gbcParcels, new JLabel(""), searchButton); // Empty label for spacing
        addRow(rightPanel,gbcParcels,new JLabel(""),backToDashboard);

        // Set up event listener for the search button
        searchButton.addActionListener(e -> searchParcel());

        // Other initialization code...

        // Set layout for the main frame
        setLayout(new BorderLayout());

        // Set the preferred width for the JScrollPane containing the JList
        int parcelTableWidth = 500; // Adjust the width as needed
        JScrollPane scrollPane = new JScrollPane(parcelList);
        scrollPane.setPreferredSize(new Dimension(parcelTableWidth, getHeight()));

        add(scrollPane, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Set up the frame properties
        setTitle("SwiftRail (Railway Management)");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        if (connectToDatabase()) {
            loadParcels();
        }
    }



    private void addRow(JPanel panel, GridBagConstraints gbc, JComponent label, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1; // Reset gridwidth to default value
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
    }

    public class SmsSender {

        private static final String SMS_API_URL = "https://api.smshub.lk/api/v1/send/single";
        private static final String API_TOKEN = "zw8uyfSJObgc3gL7n5bvhNthk7xrrY52nsXJTaNG28pBc79KpBdlOYUozxTGR9eB3drbtQDOYv9w43hQlc5KtbnRSiPATZ3Ic8rCO64EDqR6HL7LQYtrvX1DdI7jHTPlK3a4qrsKHxtmJ7sLd3GYFhmA9T7UVGDf9lpYsr56s0SSgyrli3cShCT9xUE05ahZlH2DbRNLoKVkSV16ZMj6rrpVgFv83RncAH2yE8mDUc3seDEnCBMuDYp9dkWHTlw20pKIAmgHHeBwPRy53ezaqi5kpOLIVe0wNuFcTn0uBQm04XCBtx5uGz1S2igOMOspx24Sn810Y8bCUYYMV4xBDTEz0IaY0fxrkDfFybiRV3VoozBt5ZOT1Slk2L2YcmSnzc7WVCbLDsamUSdZWMjDzMm2O0xV86NpmVGvqnENDujgvjcQiNKmQx5XNvGMvBkpf6rNkYekHdM09gRlZV9SrdWjT94zQIVgehwYIWN5TPyIrGIRX1PSPDx19MFLsI5kmihy2C9fLshwyOstFRR52l5hXeYE2MCyKRbZWt3dbkkDlEdxrqEW5veajq16Egn2ENjwi8CoK2I1v1u0OWhoQ6Xg2X3xGJNdmkwfdqlanDbwig1LS5L4oboeDmMu53kfoaQ0907WIOSnNjSqgffIbg6lVxoOST6wY2Weker4IgRg57ie9Xo0eFnY3kSnhrBZ9prYtGDMV8u8CXFFVCoo7hJSR8x6FU0p3gXr5MtSwxbVNHnSo2SEWH3K4E1twyzr0NQIR3P3X4LqFLXeEFX3GBiHzoww87LMmucZgudelDiYW8jfTDpfz9aVMLxxGIfglaK3ThuFDheFTxptAnv8IUD7V0AZsOSN4jRCPMsNr0vGBecxFjVQEhdoArtNHd5NxrAFuSTJH7thgKhmXD6KNecIqP6sY7QKOqgXL58zEBvkBp8ucker3PitrvYasEBmdSwPoQqjt8ORFRX7at1BvibGzZ8OeWE64Ypfemzsu3lO0XfOyNpphbBrkiTOnXSm";

        public static void sendSms(String phoneNumber, String message) {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");

            String smsBody = String.format("{\n \"message\":\"%s\",\n \"phoneNumber\":\"%s\"\n}", message, phoneNumber);

            RequestBody body = RequestBody.create(mediaType, smsBody);

            Request request = new Request.Builder()
                    .url(SMS_API_URL)
                    .method("POST", body)
                    .addHeader("Authorization", API_TOKEN)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                // Handle the response if needed
                System.out.println(response.body().string());
            } catch (Exception e) {
                e.printStackTrace();
                // Handle the exception, e.g., log the error or show a message
            }
        }
    }

    private void notifySenderReceiver(int parcelId, String newStatus) {
        String senderContact = senderContactField.getText();
        String receiverContact = receiverContactField.getText();

        // Notify sender
        if (!senderContact.isEmpty()) {
            SmsSender.sendSms(senderContact, String.format("Parcel ID %d status updated to: %s", parcelId, newStatus));
        }

        // Notify receiver
        if (!receiverContact.isEmpty() && !receiverContact.equals(senderContact)) {
            SmsSender.sendSms(receiverContact, String.format("Parcel ID %d status updated to: %s", parcelId, newStatus));
        }
    }



    // Database connection setup
    private boolean connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/swiftrail";
        String user = "root";
        String password = "2004";

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the database", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void searchParcel() {
        String searchIdText = searchField.getText();

        // Check if the search ID is a valid integer
        try {
            int searchId = Integer.parseInt(searchIdText);

            // Find the index of the parcel with the specified ID in the parcel list
            int index = findParcelIndexById(searchId);

            if (index != -1) {
                // Set the selection to the found index
                parcelList.setSelectedIndex(index);
                parcelList.ensureIndexIsVisible(index); // Scroll to the selected index if needed
            } else {
                JOptionPane.showMessageDialog(this, "Parcel with ID " + searchId + " not found", "Not Found", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid parcel ID", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private int findParcelIndexById(int searchId) {
        for (int i = 0; i < parcelListModel.size(); i++) {
            int parcelId = getParcelId(i);
            if (parcelId == searchId) {
                return i; // Found the parcel with the specified ID
            }
        }
        return -1; // Parcel not found
    }

    private void deleteParcel() {
        int selectedIndex = parcelList.getSelectedIndex();
        if (selectedIndex != -1) {
            int parcelId = getParcelId(selectedIndex);

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM parcel_table WHERE parcel_id=?")) {

                preparedStatement.setInt(1, parcelId);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Parcel deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    parcelListModel.remove(selectedIndex);
                    clearParcelFields();
                } else {
                    JOptionPane.showMessageDialog(this, "No parcel deleted", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting parcel", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateParcel() {
        int selectedIndex = parcelList.getSelectedIndex();
        if (selectedIndex != -1) {
            int parcelId = getParcelId(selectedIndex);

            String destination = destinationField.getText();
            String senderContact = senderContactField.getText();
            String receiverContact = receiverContactField.getText();
            String fee = feeField.getText();
            String weight = weightField.getText();

            if (destination.isEmpty() || senderContact.isEmpty() || receiverContact.isEmpty() || fee.isEmpty() || weight.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE parcel_table SET destination=?, sender_contact=?, receiver_contact=?, fee=?, weight=? WHERE parcel_id=?")) {

                    preparedStatement.setString(1, destination);
                    preparedStatement.setString(2, senderContact);
                    preparedStatement.setString(3, receiverContact);
                    preparedStatement.setString(4, fee);
                    preparedStatement.setString(5, weight);
                    preparedStatement.setInt(6, parcelId);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Parcel updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        parcelListModel.setElementAt(parcelId + " - " + destination, selectedIndex);
                        loadParcels();
                        clearParcelFields();
                    } else {
                        JOptionPane.showMessageDialog(this, "No parcel updated", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error updating parcel", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void updateParcelStatus() {
        int selectedIndex = parcelList.getSelectedIndex();
        if (selectedIndex != -1) {
            int parcelId = getParcelId(selectedIndex);
            String newStatus = (String) statusComboBox.getSelectedItem();

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE parcel_table SET status=? WHERE parcel_id=?")) {

                preparedStatement.setString(1, newStatus);
                preparedStatement.setInt(2, parcelId);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Parcel status updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    notifySenderReceiver(parcelId, newStatus);
                    loadParcels();
                    clearParcelFields();
                } else {
                    JOptionPane.showMessageDialog(this, "No parcel status updated", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating parcel status", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getParcelId(int selectedIndex) {
        String parcelInfo = parcelListModel.getElementAt(selectedIndex);

        // Use regular expression to extract numeric part (parcel_id)
        Pattern pattern = Pattern.compile("\\b(\\d+)\\b");
        Matcher matcher = pattern.matcher(parcelInfo);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        // Handle the case where parcel_id is not found
        return -1; // or another appropriate value
    }

    private void clearParcelFields() {
        destinationField.setText("");
        senderContactField.setText("");
        receiverContactField.setText("");
        feeField.setText("");
        weightField.setText("");
        statusComboBox.setSelectedIndex(0);
    }

    private void displaySelectedParcel() {
        int selectedIndex = parcelList.getSelectedIndex();
        if (selectedIndex != -1) {
            int parcelId = getParcelId(selectedIndex);

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM parcel_table WHERE parcel_id=?")) {

                preparedStatement.setInt(1, parcelId);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    destinationField.setText(resultSet.getString("destination"));
                    senderContactField.setText(resultSet.getString("sender_contact"));
                    receiverContactField.setText(resultSet.getString("receiver_contact"));
                    feeField.setText(resultSet.getString("fee"));
                    weightField.setText(resultSet.getString("weight"));
                    String status = resultSet.getString("status");
                    statusComboBox.setSelectedItem(status);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading parcel details", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }




    private void loadParcels() {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM parcel_table");

            // Clear the existing data from the DefaultListModel
            parcelListModel.clear();

            while (resultSet.next()) {
                int parcelId = resultSet.getInt("parcel_id");
                String destination = resultSet.getString("destination");
                String senderContact = resultSet.getString("sender_contact");
                String receiverContact = resultSet.getString("receiver_contact");
                String status = resultSet.getString("status");

                // Build the information string, including all details
                String parcelInfo = String.format("%d - %s - Sender Contact: %s - Receiver Contact: %s - %s", parcelId, destination, senderContact, receiverContact, status);

                // Add the string to the DefaultListModel
                parcelListModel.addElement(parcelInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading parcels", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

// ...


    private void addNewParcel() {
        String destination = destinationField.getText();
        String senderContact = senderContactField.getText();
        String receiverContact = receiverContactField.getText();
        String fee = feeField.getText();
        String weight = weightField.getText();
        String status = (String) statusComboBox.getSelectedItem();

        if (destination.isEmpty() || senderContact.isEmpty() || receiverContact.isEmpty() || fee.isEmpty() || weight.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO parcel_table (destination, sender_contact, receiver_contact, fee, weight, status) " +
                            "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

                preparedStatement.setString(1, destination);
                preparedStatement.setString(2, senderContact);
                preparedStatement.setString(3, receiverContact);
                preparedStatement.setString(4, fee);
                preparedStatement.setString(5, weight);
                preparedStatement.setString(6, status);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Parcel added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearParcelFields();
                    loadParcels();  // Refresh the list of parcels
                } else {
                    JOptionPane.showMessageDialog(this, "No parcel added", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding parcel", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setIcon() {
        ImageIcon frameIcon = new ImageIcon("img/SwiftRail.png");
        setIconImage(frameIcon.getImage());
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(ParcelCRUDApp::new);
    }
}
