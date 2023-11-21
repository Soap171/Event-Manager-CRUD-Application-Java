import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SchedulesForm extends JFrame {

    private JComboBox<String> trainComboBox;
    private JTextField arrivalTimeField;
    private JTextField departureTimeField;
    private JTextField dayField;

    public SchedulesForm() {
        super("Schedules Management");

        JLabel trainLabel = new JLabel("Train:");
        JLabel arrivalTimeLabel = new JLabel("Arrival Time:");
        JLabel departureTimeLabel = new JLabel("Departure Time:");
        JLabel dayLabel = new JLabel("Day:");

        trainComboBox = new JComboBox<>();
        populateTrainComboBox(); // Populate the train names in the combo box

        arrivalTimeField = new JTextField(20);
        departureTimeField = new JTextField(20);
        dayField = new JTextField(20);

        JButton addButton = new JButton("Add Schedule");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSchedule();
            }
        });

        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        formPanel.add(trainLabel);
        formPanel.add(trainComboBox);
        formPanel.add(arrivalTimeLabel);
        formPanel.add(arrivalTimeField);
        formPanel.add(departureTimeLabel);
        formPanel.add(departureTimeField);
        formPanel.add(dayLabel);
        formPanel.add(dayField);
        formPanel.add(addButton);

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);

        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void populateTrainComboBox() {
        // Fetch train names from the database and populate the combo box
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SchedulesForm();
            }
        });
    }
}
