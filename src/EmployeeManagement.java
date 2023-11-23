import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.*;

public class EmployeeManagement extends JFrame {

    private JTextField nicField, nameField, contactField, addressField, salaryField;
    private JTable employeeTable;
    private JTextField searchField;
    private JButton searchButton;

    public EmployeeManagement() {
        super("Employee Management");

        JLabel nicLabel = new JLabel("NIC:");
        JLabel nameLabel = new JLabel("Name:");
        JLabel contactLabel = new JLabel("Contact:");
        JLabel addressLabel = new JLabel("Address:");
        JLabel salaryLabel = new JLabel("Salary:");

        nicField = new JTextField(20);
        nameField = new JTextField(20);
        contactField = new JTextField(15);
        addressField = new JTextField(255);
        salaryField = new JTextField(15);

        JButton addButton = new JButton("Add Employee");
        JButton updateButton = new JButton("Update Employee");
        JButton deleteButton = new JButton("Delete Employee");
        searchField = new JTextField(20);
        searchButton = new JButton("Search by NIC");

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchEmployee();
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEmployee();
                populateEmployeeTable();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEmployee();
                populateEmployeeTable();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEmployee();
                populateEmployeeTable();
            }
        });




        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("NIC");
        tableModel.addColumn("Name");
        tableModel.addColumn("Contact");
        tableModel.addColumn("Address");
        tableModel.addColumn("Salary");

        employeeTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(employeeTable);

        employeeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow != -1) {
                    String nic = (String) employeeTable.getValueAt(selectedRow, 0);
                    String name = (String) employeeTable.getValueAt(selectedRow, 1);
                    String contact = (String) employeeTable.getValueAt(selectedRow, 2);
                    String address = (String) employeeTable.getValueAt(selectedRow, 3);
                    BigDecimal salary = (BigDecimal) employeeTable.getValueAt(selectedRow, 4);

                    nicField.setText(nic);
                    nameField.setText(name);
                    contactField.setText(contact);
                    addressField.setText(address);
                    salaryField.setText(salary.toString()); // Convert BigDecimal to String
                }
            }
        });


        JPanel formPanel = new JPanel(new GridLayout(10, 2));
        formPanel.add(nicLabel);
        formPanel.add(nicField);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(contactLabel);
        formPanel.add(contactField);
        formPanel.add(addressLabel);
        formPanel.add(addressField);
        formPanel.add(salaryLabel);
        formPanel.add(salaryField);
        formPanel.add(addButton);
        formPanel.add(updateButton);
        formPanel.add(deleteButton);
        formPanel.add(searchButton);
        formPanel.add(searchField);


        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        populateEmployeeTable();
    }

    private void searchEmployee() {
        String searchNic = searchField.getText();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swiftrail", "root", "200434");
            PreparedStatement searchStatement = connection.prepareStatement("SELECT * FROM Employee WHERE NIC = ?");
            searchStatement.setString(1, searchNic);
            ResultSet resultSet = searchStatement.executeQuery();

            DefaultTableModel tableModel = (DefaultTableModel) employeeTable.getModel();
            tableModel.setRowCount(0);

            int selectedRow = -1; // Added to keep track of the selected row

            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getString("NIC"),
                        resultSet.getString("name"),
                        resultSet.getString("contact"),
                        resultSet.getString("address"),
                        resultSet.getBigDecimal("salary")
                };
                tableModel.addRow(rowData);

                // Check if the current row matches the search criteria
                if (resultSet.getString("NIC").equals(searchNic)) {
                    selectedRow = tableModel.getRowCount() - 1; // Set the selected row to the last row added
                }
            }

            resultSet.close();
            searchStatement.close();
            connection.close();

            // Set the selection to the matching row
            if (selectedRow != -1) {
                employeeTable.setRowSelectionInterval(selectedRow, selectedRow);
                employeeTable.scrollRectToVisible(employeeTable.getCellRect(selectedRow, 0, true));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching for employee", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void addEmployee() {
        String nic = nicField.getText();
        String name = nameField.getText();
        String contact = contactField.getText();
        String address = addressField.getText();
        String salary = salaryField.getText();

        if (nic.isEmpty() || name.isEmpty() || contact.isEmpty() || address.isEmpty() || salary.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swiftrail", "root", "200434");
            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO Employee (NIC, name, contact, address, salary) VALUES (?, ?, ?, ?, ?)");
            insertStatement.setString(1, nic);
            insertStatement.setString(2, name);
            insertStatement.setString(3, contact);
            insertStatement.setString(4, address);
            insertStatement.setBigDecimal(5, new BigDecimal(salary));
            insertStatement.executeUpdate();

            insertStatement.close();
            connection.close();

            clearFields();
            JOptionPane.showMessageDialog(this, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding employee to the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployee() {
        String nic = nicField.getText();
        String name = nameField.getText();
        String contact = contactField.getText();
        String address = addressField.getText();
        String salary = salaryField.getText();

        if (nic.isEmpty() || name.isEmpty() || contact.isEmpty() || address.isEmpty() || salary.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swiftrail", "root", "200434");
            PreparedStatement updateStatement = connection.prepareStatement("UPDATE Employee SET name = ?, contact = ?, address = ?, salary = ? WHERE NIC = ?");
            updateStatement.setString(1, name);
            updateStatement.setString(2, contact);
            updateStatement.setString(3, address);
            updateStatement.setBigDecimal(4, new BigDecimal(salary));
            updateStatement.setString(5, nic);
            updateStatement.executeUpdate();

            updateStatement.close();
            connection.close();

            clearFields();
            JOptionPane.showMessageDialog(this, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating employee in the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        String nic = nicField.getText();

        if (nic.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter NIC for deletion", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmDialogResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this employee?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirmDialogResult != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swiftrail", "root", "200434");
            PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Employee WHERE NIC = ?");
            deleteStatement.setString(1, nic);
            deleteStatement.executeUpdate();

            deleteStatement.close();
            connection.close();

            clearFields();
            JOptionPane.showMessageDialog(this, "Employee deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting employee from the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateEmployeeTable() {
        DefaultTableModel tableModel = (DefaultTableModel) employeeTable.getModel();
        tableModel.setRowCount(0);

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swiftrail", "root", "200434");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Employee");

            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getString("NIC"),
                        resultSet.getString("name"),
                        resultSet.getString("contact"),
                        resultSet.getString("address"),
                        resultSet.getBigDecimal("salary")
                };
                tableModel.addRow(rowData);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching employees from the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        nicField.setText("");
        nameField.setText("");
        contactField.setText("");
        addressField.setText("");
        salaryField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EmployeeManagement();
            }
        });
    }
}
