import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class SchedulesForm extends JFrame {

    private JComboBox<String> trainComboBox;
    private JTextField arrivalTimeField;
    private JTextField departureTimeField;
    private JTextField dayField;
    private JTable schedulesTable;

    public SchedulesForm() {
        super("Schedules Management");

        JLabel trainLabel = new JLabel("Train:");
        JLabel arrivalTimeLabel = new JLabel("Arrival Time:");
        JLabel departureTimeLabel = new JLabel("Departure Time:");
        JLabel dayLabel = new JLabel("Day:");

        trainComboBox = new JComboBox<>();
        populateTrainComboBox();

        arrivalTimeField = new JTextField(20);
        departureTimeField = new JTextField(20);
        dayField = new JTextField(20);

        JButton addButton = new JButton("Add Schedule");
        JButton updateButton = new JButton("Update Schedule");
        JButton deleteButton = new JButton("Delete Schedule");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSchedule();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSchedule();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSchedule();
            }
        });

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Train");
        tableModel.addColumn("Arrival Time");
        tableModel.addColumn("Departure Time");
        tableModel.addColumn("Day");

        schedulesTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(schedulesTable);

        schedulesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = schedulesTable.getSelectedRow();
                if (selectedRow != -1) {
                    String trainName = (String) schedulesTable.getValueAt(selectedRow, 0);
                    String arrivalTime = (String) schedulesTable.getValueAt(selectedRow, 1);
                    String departureTime = (String) schedulesTable.getValueAt(selectedRow, 2);
                    String day = (String) schedulesTable.getValueAt(selectedRow, 3);

                    trainComboBox.setSelectedItem(trainName);
                    arrivalTimeField.setText(arrivalTime);
                    departureTimeField.setText(departureTime);
                    dayField.setText(day);
                }
            }
        });

        JPanel formPanel = new JPanel(new GridLayout(6, 2));
        formPanel.add(trainLabel);
        formPanel.add(trainComboBox);
        formPanel.add(arrivalTimeLabel);
        formPanel.add(arrivalTimeField);
        formPanel.add(departureTimeLabel);
        formPanel.add(departureTimeField);
        formPanel.add(dayLabel);
        formPanel.add(dayField);
        formPanel.add(addButton);
        formPanel.add(updateButton);
        formPanel.add(deleteButton);

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        populateSchedulesTable();
    }

    private void populateTrainComboBox() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swiftrail", "root", "200434");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT name FROM Trains");

            while (resultSet.next()) {
                trainComboBox.addItem(resultSet.getString("name"));
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching train names from the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateSchedulesTable() {
        DefaultTableModel tableModel = (DefaultTableModel) schedulesTable.getModel();
        tableModel.setRowCount(0);

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swiftrail", "root", "200434");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Trains.name, Schedules.arrivalTime, Schedules.departureTime, Schedules.day FROM Schedules JOIN Trains ON Schedules.train_id = Trains.trainId");

            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getString("name"),
                        resultSet.getString("arrivalTime"),
                        resultSet.getString("departureTime"),
                        resultSet.getString("day")
                };
                tableModel.addRow(rowData);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching schedules from the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSchedule() {
        String selectedTrainName = (String) trainComboBox.getSelectedItem();
        String arrivalTime = arrivalTimeField.getText();
        String departureTime = departureTimeField.getText();
        String day = dayField.getText();

        if (selectedTrainName == null || selectedTrainName.isEmpty() || arrivalTime.isEmpty() || departureTime.isEmpty() || day.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swiftrail", "root", "200434");
            // Get the trainId based on the selected train name
            PreparedStatement selectTrainIdStatement = connection.prepareStatement("SELECT trainId FROM Trains WHERE name = ?");
            selectTrainIdStatement.setString(1, selectedTrainName);
            ResultSet resultSet = selectTrainIdStatement.executeQuery();

            int trainId = -1;
            if (resultSet.next()) {
                trainId = resultSet.getInt("trainId");
            }

            // Insert the schedule into the Schedules table
            PreparedStatement insertScheduleStatement = connection.prepareStatement("INSERT INTO Schedules (train_id, arrivalTime, departureTime, day) VALUES (?, ?, ?, ?)");
            insertScheduleStatement.setInt(1, trainId);
            insertScheduleStatement.setString(2, arrivalTime);
            insertScheduleStatement.setString(3, departureTime);
            insertScheduleStatement.setString(4, day);
            insertScheduleStatement.executeUpdate();

            resultSet.close();
            selectTrainIdStatement.close();
            insertScheduleStatement.close();
            populateSchedulesTable();
            connection.close();

            // Clear the fields after adding a schedule
            arrivalTimeField.setText("");
            departureTimeField.setText("");
            dayField.setText("");

            JOptionPane.showMessageDialog(this, "Schedule added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding schedule to the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSchedule() {
        // ... (unchanged code for updating)

        populateSchedulesTable();
    }

    private void deleteSchedule() {
        String selectedTrainName = (String) trainComboBox.getSelectedItem();
        String arrivalTime = arrivalTimeField.getText();
        String departureTime = departureTimeField.getText();
        String day = dayField.getText();

        if (selectedTrainName == null || selectedTrainName.isEmpty() || arrivalTime.isEmpty() || departureTime.isEmpty() || day.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmDialogResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this schedule?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirmDialogResult != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swiftrail", "root", "200434");

            // Get the trainId based on the selected train name
            PreparedStatement selectTrainIdStatement = connection.prepareStatement("SELECT trainId FROM Trains WHERE name = ?");
            selectTrainIdStatement.setString(1, selectedTrainName);
            ResultSet resultSet = selectTrainIdStatement.executeQuery();

            int trainId = -1;
            if (resultSet.next()) {
                trainId = resultSet.getInt("trainId");
            }

            // Delete the schedule from the Schedules table
            PreparedStatement deleteScheduleStatement = connection.prepareStatement("DELETE FROM Schedules WHERE train_id = ? AND arrivalTime = ? AND departureTime = ? AND day = ?");
            deleteScheduleStatement.setInt(1, trainId);
            deleteScheduleStatement.setString(2, arrivalTime);
            deleteScheduleStatement.setString(3, departureTime);
            deleteScheduleStatement.setString(4, day);
            deleteScheduleStatement.executeUpdate();

            resultSet.close();
            selectTrainIdStatement.close();
            deleteScheduleStatement.close();
            populateSchedulesTable();
            connection.close();

            // Clear the fields after deleting a schedule
            arrivalTimeField.setText("");
            departureTimeField.setText("");
            dayField.setText("");

            JOptionPane.showMessageDialog(this, "Schedule deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting schedule from the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SchedulesForm();
            }
        });
    }
}
