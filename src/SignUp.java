import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUp extends JFrame {

    private JTextField userNameField;
    private JPasswordField passwordField;

    public SignUp() {
        super("Sign Up");

        JLabel userNameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        userNameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        JButton signUpButton = new JButton("Sign Up");
        JButton backButton = new JButton("Back to Login");

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signUp();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToLogin();
            }
        });

        JPanel panel = new JPanel();
        panel.add(userNameLabel);
        panel.add(userNameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(signUpButton);
        panel.add(backButton);

        add(panel);

        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private boolean connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/swiftrail";
        String user = "root";
        String password = "200434";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the database", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void signUp() {
        if (connectToDatabase()) {
            try {
                String insertQuery = "INSERT INTO Admin (userName, password) VALUES (?, ?)";
                try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/swiftrail", "root", "200434");
                     PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

                    String username = userNameField.getText();
                    char[] passwordChars = passwordField.getPassword();
                    String password = new String(passwordChars);

                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Sign up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Sign up failed.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error executing SQL query", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void goBackToLogin() {
        // Hide the current signup page
        setVisible(false);

        // Open the login page
        new Login();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SignUp();
            }
        });
    }
}
