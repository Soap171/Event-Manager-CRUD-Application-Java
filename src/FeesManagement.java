import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.*;

public class FeesManagement extends JFrame {

    private JTextField destinationField, firstClassFeeField, secondClassFeeField, thirdClassFeeField;
    private JTextField searchField;
    private JButton addButton, updateButton, deleteButton, searchButton;
    private JTable feeTable; // Declare feeTable as an instance variable
    private Connection connection;

    public FeesManagement() {
        super("Fees Management");

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swiftrail", "root", "200434");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the database", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        JLabel destinationLabel = new JLabel("Destination:");
        JLabel firstClassFeeLabel = new JLabel("First Class Fee:");
        JLabel secondClassFeeLabel = new JLabel("Second Class Fee:");
        JLabel thirdClassFeeLabel = new JLabel("Third Class Fee:");

        destinationField = new JTextField(20);
        firstClassFeeField = new JTextField(10);
        secondClassFeeField = new JTextField(10);
        thirdClassFeeField = new JTextField(10);
        searchField = new JTextField(20);

        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        searchButton = new JButton("Search");
        JButton backToDashboard = new JButton("Dashboard");

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
                addFee();
                populateTable();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFee();
                populateTable();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteFee();
                populateTable();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchFee();
            }
        });

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Fee ID");
        tableModel.addColumn("Destination");
        tableModel.addColumn("First Class Fee");
        tableModel.addColumn("Second Class Fee");
        tableModel.addColumn("Third Class Fee");

        feeTable = new JTable(tableModel); // Initialize feeTable
        JScrollPane tableScrollPane = new JScrollPane(feeTable);

        feeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = feeTable.getSelectedRow();
                if (selectedRow != -1) {
                    int feeId = (int) feeTable.getValueAt(selectedRow, 0);
                    String destination = (String) feeTable.getValueAt(selectedRow, 1);
                    BigDecimal firstClassFee = (BigDecimal) feeTable.getValueAt(selectedRow, 2);
                    BigDecimal secondClassFee = (BigDecimal) feeTable.getValueAt(selectedRow, 3);
                    BigDecimal thirdClassFee = (BigDecimal) feeTable.getValueAt(selectedRow, 4);

                    // Set the values in text fields
                    destinationField.setText(destination);
                    firstClassFeeField.setText(firstClassFee.toString());
                    secondClassFeeField.setText(secondClassFee.toString());
                    thirdClassFeeField.setText(thirdClassFee.toString());
                }
            }
        });

        JPanel formPanel = new JPanel(new GridLayout(8, 2));
        formPanel.add(destinationLabel);
        formPanel.add(destinationField);
        formPanel.add(firstClassFeeLabel);
        formPanel.add(firstClassFeeField);
        formPanel.add(secondClassFeeLabel);
        formPanel.add(secondClassFeeField);
        formPanel.add(thirdClassFeeLabel);
        formPanel.add(thirdClassFeeField);
        formPanel.add(addButton);
        formPanel.add(updateButton);
        formPanel.add(deleteButton);
        formPanel.add(backToDashboard);

        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        formPanel.add(searchPanel);

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        populateTable();
    }

    private void addFee() {
        String destination = destinationField.getText();
        BigDecimal firstClassFee = new BigDecimal(firstClassFeeField.getText());
        BigDecimal secondClassFee = new BigDecimal(secondClassFeeField.getText());
        BigDecimal thirdClassFee = new BigDecimal(thirdClassFeeField.getText());

        try {
            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO Fees (destination, firstClassFee, secondClassFee, thirdClassFee) VALUES (?, ?, ?, ?)");
            insertStatement.setString(1, destination);
            insertStatement.setBigDecimal(2, firstClassFee);
            insertStatement.setBigDecimal(3, secondClassFee);
            insertStatement.setBigDecimal(4, thirdClassFee);
            insertStatement.executeUpdate();

            insertStatement.close();
            clearFields();
            JOptionPane.showMessageDialog(this, "Fee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding fee to the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateFee() {
        int selectedRow = feeTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a fee to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int feeId = (int) feeTable.getValueAt(selectedRow, 0);
        String destination = destinationField.getText();
        BigDecimal firstClassFee = new BigDecimal(firstClassFeeField.getText());
        BigDecimal secondClassFee = new BigDecimal(secondClassFeeField.getText());
        BigDecimal thirdClassFee = new BigDecimal(thirdClassFeeField.getText());

        try {
            PreparedStatement updateStatement = connection.prepareStatement("UPDATE Fees SET destination = ?, firstClassFee = ?, secondClassFee = ?, thirdClassFee = ? WHERE fee_id = ?");
            updateStatement.setString(1, destination);
            updateStatement.setBigDecimal(2, firstClassFee);
            updateStatement.setBigDecimal(3, secondClassFee);
            updateStatement.setBigDecimal(4, thirdClassFee);
            updateStatement.setInt(5, feeId);
            updateStatement.executeUpdate();

            updateStatement.close();
            clearFields();
            JOptionPane.showMessageDialog(this, "Fee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating fee in the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteFee() {
        int selectedRow = feeTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a fee to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmDialogResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this fee?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirmDialogResult != JOptionPane.YES_OPTION) {
            return;
        }

        int feeId = (int) feeTable.getValueAt(selectedRow, 0);

        try {
            PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Fees WHERE fee_id = ?");
            deleteStatement.setInt(1, feeId);
            deleteStatement.executeUpdate();

            deleteStatement.close();
            clearFields();
            JOptionPane.showMessageDialog(this, "Fee deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting fee from the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchFee() {
        String searchDestination = searchField.getText().trim();

        if (searchDestination.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a destination to search", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel tableModel = (DefaultTableModel) feeTable.getModel();
        tableModel.setRowCount(0);

        int selectedRow = -1; // Added to keep track of the selected row

        try {
            PreparedStatement searchStatement = connection.prepareStatement("SELECT * FROM Fees WHERE LOWER(destination) LIKE LOWER(?)");
            searchStatement.setString(1, "%" + searchDestination + "%");

            ResultSet resultSet = searchStatement.executeQuery();

            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getInt("fee_id"),
                        resultSet.getString("destination"),
                        resultSet.getBigDecimal("firstClassFee"),
                        resultSet.getBigDecimal("secondClassFee"),
                        resultSet.getBigDecimal("thirdClassFee")
                };
                tableModel.addRow(rowData);

                // Check if the current row matches the search criteria
                if (resultSet.getString("destination").toLowerCase().contains(searchDestination.toLowerCase())) {
                    selectedRow = tableModel.getRowCount() - 1; // Set the selected row to the last row added
                }
            }

            resultSet.close();
            searchStatement.close();

            searchField.setText("");

            // Set the selection to the matching row
            if (selectedRow != -1) {
                feeTable.setRowSelectionInterval(selectedRow, selectedRow);
                feeTable.scrollRectToVisible(feeTable.getCellRect(selectedRow, 0, true));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching fees in the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void populateTable() {
        DefaultTableModel tableModel = (DefaultTableModel) feeTable.getModel();
        tableModel.setRowCount(0);

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Fees");

            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getInt("fee_id"),
                        resultSet.getString("destination"),
                        resultSet.getBigDecimal("firstClassFee"),
                        resultSet.getBigDecimal("secondClassFee"),
                        resultSet.getBigDecimal("thirdClassFee")
                };
                tableModel.addRow(rowData);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching fees from the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        destinationField.setText("");
        firstClassFeeField.setText("");
        secondClassFeeField.setText("");
        thirdClassFeeField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FeesManagement();
            }
        });
    }
}
