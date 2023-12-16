import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame {

    private JTextField userNameField;
    private JPasswordField passwordField;

    public Login() {
        super("Login");


        // Load an image and create a JLabel to display it
        ImageIcon userIcon = new ImageIcon("img/user.png"); // Replace with the actual path to your image
        Image image = userIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(image));

        JLabel userNameLabel = new JLabel(" Your Username");
        JLabel passwordLabel = new JLabel(" Your Password");

        userNameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        // Use GridBagLayout for better control of the layout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        // Place the image label
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(0, 60, 10, 0); // Add some space below the image
        panel.add(imageLabel, constraints);

        // Reset insets
        constraints.insets = new Insets(0, 0, 0, 0);

        // Place the username label and field
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(userNameLabel, constraints);

        constraints.gridx = 1;
        panel.add(userNameField, constraints);

        // Place the password label and field
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(passwordLabel, constraints);

        constraints.gridx = 1;
        panel.add(passwordField, constraints);

        // Place the login button
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(10, 60, 0, 0); // Add some space above the login button
        panel.add(loginButton, constraints);

        add(panel);

        setSize(300, 300); // Adjusted height to accommodate the image and provide some spacing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    private void setIcon() {
        ImageIcon frameIcon = new ImageIcon("img/SwiftRail.png");
        setIconImage(frameIcon.getImage());
    }


    private boolean connectToDatabase() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            System.out.println("Connected to the database");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the database", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void login() {
        if (connectToDatabase()) {
            try {
                String selectQuery = "SELECT * FROM Admin WHERE userName = ? and password = ?";
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

                    String username = userNameField.getText();
                    char[] passwordChars = passwordField.getPassword();
                    String password = new String(passwordChars);

                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);

                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        Dashboard dashboard = new Dashboard();
                        setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(this, "Login failed. Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error executing SQL query", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            login.setIcon();
        });
    }


}
