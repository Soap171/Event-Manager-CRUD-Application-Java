import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Dashboard extends JFrame {

    JFrame frame = new JFrame();

    public Dashboard() {
        super("Dashboard");
        setIcon();

        // Create a label for the image
        JLabel imageLabel = new JLabel();
        ImageIcon originalImageIcon = new ImageIcon("img/dashboard.png");  // Update the path

        int imageWidth = 800;  // Replace with your desired width
        int imageHeight = 500; // Replace with your desired height
        ImageIcon scaledImageIcon = new ImageIcon(originalImageIcon.getImage().getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH));
        imageLabel.setIcon(scaledImageIcon);


        // Create a panel for the image label
        JPanel imagePanel = new JPanel();
        imagePanel.add(imageLabel);




        JButton parcelManagementButton = new JButton("Parcel Management");
        parcelManagementButton.setFocusPainted(false);
        parcelManagementButton.setBorderPainted(false);
        parcelManagementButton.setBackground(new Color(14, 12, 168));
        parcelManagementButton.setForeground(new Color(255,255,255));
        parcelManagementButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton feesButton = new JButton("Fees Management");
        feesButton.setFocusPainted(false);
        feesButton.setBorderPainted(false);
        feesButton.setBackground(new Color(14, 12, 168));
        feesButton.setForeground(new Color(255,255,255));
        feesButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton trainsButton = new JButton("Trains Management");
        trainsButton.setFocusPainted(false);
        trainsButton.setBorderPainted(false);
        trainsButton.setBackground(new Color(14, 12, 168));
        trainsButton.setForeground(new Color(255,255,255));
        trainsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton scheduleManagementButton = new JButton("Schedule Management");
        scheduleManagementButton.setFocusPainted(false);
        scheduleManagementButton.setBorderPainted(false);
        scheduleManagementButton.setBackground(new Color(14, 12, 168));
        scheduleManagementButton.setForeground(new Color(255,255,255));
        scheduleManagementButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton employeeManagementButton = new JButton("Employee Management");
        employeeManagementButton.setFocusPainted(false);
        employeeManagementButton.setBorderPainted(false);
        employeeManagementButton.setBackground(new Color(14, 12, 168));
        employeeManagementButton.setForeground(new Color(255,255,255));
        employeeManagementButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));




        parcelManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openParcelManagement();
                setVisible(false);
            }
        });

        feesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openFees();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                setVisible(false);
            }
        });

        trainsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openTrains();
                setVisible(false);
            }
        });

        scheduleManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openScheduleManagement();
                setVisible(false);
            }
        });

        employeeManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    openEmployeeManagement();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                setVisible(false);
            }
        });




        JPanel panel = new JPanel();
        panel.add(parcelManagementButton);
        panel.add(feesButton);
        panel.add(trainsButton);
        panel.add(scheduleManagementButton);
        panel.add(employeeManagementButton);





        frame.add(panel,BorderLayout.SOUTH);
        frame.add(imagePanel, BorderLayout.CENTER);  // Adjust the layout as needed

        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    private void openParcelManagement() {

        ParcelCRUDApp parcel = new ParcelCRUDApp();



    }

    private void openFees() throws SQLException {
        FeesManagement fees = new FeesManagement();




    }

    private void openTrains() {
        TrainsManagementForm trains = new TrainsManagementForm();

    }

    private void openScheduleManagement() {

        SchedulesForm form = new SchedulesForm();

    }

    private void setIcon() {
        ImageIcon frameIcon = new ImageIcon("img/SwiftRail.png");
        frame.setIconImage(frameIcon.getImage());
    }

    private void openEmployeeManagement() throws SQLException {
        EmployeeManagement emp = new EmployeeManagement();

    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Dashboard();

            }
        });
    }
}
