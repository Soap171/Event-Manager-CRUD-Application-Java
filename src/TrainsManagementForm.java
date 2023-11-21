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

        JLabel nameLabel = new JLabel("Train Name:");
        trainNameField = new JTextField(20);

        JButton addButton = new JButton("Add Train");
        JButton deleteButton = new JButton("Delete Train");

        tableModel = new DefaultTableModel();
        trainsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(trainsTable);

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

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swiftrail", "root", "200434");
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
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swiftrail", "root", "200434");
            String insertQuery = "INSERT INTO Trains (name) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, trainName);
                preparedStatement.executeUpdate();

                // Refresh the table after adding a train
                fetchAndPopulateTable();

                JOptionPane.showMessageDialog(this, "Train added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swiftrail", "root", "200434");
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



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TrainsManagementForm();
            }
        });
    }
}




