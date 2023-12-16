import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class TrainsManagementForm extends JFrame {

    private JTextField trainNameField;
    private JTable trainsTable;
    private DefaultTableModel tableModel;

    public TrainsManagementForm() {
        super("Trains Management");
        setIcon();

        JLabel nameLabel = new JLabel("Train Name:");
        trainNameField = new JTextField(20);

        JButton addButton = new JButton("Add Train");
        JButton deleteButton = new JButton("Delete Train");
        JButton backToDashboard = new JButton("Back To Dashboard");

        addButton.setFocusPainted(false);
        deleteButton.setFocusPainted(false);
        backToDashboard.setFocusPainted(false);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backToDashboard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        tableModel = new DefaultTableModel();
        trainsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(trainsTable);

        backToDashboard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Dashboard dashboard = new Dashboard();
                setVisible(false);
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTrain();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTrain();
            }
        });

        JPanel formPanel = new JPanel(new GridLayout(2, 2));
        formPanel.add(nameLabel);
        formPanel.add(trainNameField);
        formPanel.add(addButton);
        formPanel.add(deleteButton);

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(formPanel);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical space between buttons

        // Create a panel for the table and add space between the table and the buttons
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(backToDashboard, BorderLayout.SOUTH);

        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Initialize the table
        initializeTable();
    }
    private void initializeTable() {
        // Clear existing data
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);

        // Add column names
        tableModel.addColumn("Train ID");
        tableModel.addColumn("Train Name");

        // Fetch data from the database and populate the table
        fetchAndPopulateTable();
    }

    private void fetchAndPopulateTable() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Trains");

            // Clear existing data in the table
            tableModel.setRowCount(0);

            while (resultSet.next()) {
                Vector<Object> rowData = new Vector<>();
                rowData.add(resultSet.getInt("trainId"));
                rowData.add(resultSet.getString("name"));
                tableModel.addRow(rowData);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data from the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTrain() {
        String trainName = trainNameField.getText();

        if (trainName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a train name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection connection = DatabaseConnection.getConnection();
            String insertQuery = "INSERT INTO Trains (name) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, trainName);
                preparedStatement.executeUpdate();

                // Refresh the table after adding a train
                fetchAndPopulateTable();

                JOptionPane.showMessageDialog(this, "Train added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                trainNameField.setText("");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding train to the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTrain() {
        int selectedRow = trainsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int trainId = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            Connection connection = DatabaseConnection.getConnection();
            String deleteQuery = "DELETE FROM Trains WHERE trainId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setInt(1, trainId);
                preparedStatement.executeUpdate();
            }

            // Refresh the table after deleting a train
            fetchAndPopulateTable();

            JOptionPane.showMessageDialog(this, "Train deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting train from the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setIcon() {
        ImageIcon frameIcon = new ImageIcon("img/SwiftRail.png");
        setIconImage(frameIcon.getImage());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TrainsManagementForm();
            }
        });
    }
}


