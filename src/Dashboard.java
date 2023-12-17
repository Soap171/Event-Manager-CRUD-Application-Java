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


        JLabel imageLabel = new JLabel();
        ImageIcon originalImageIcon = new ImageIcon("D:/EAD/EventCRUDApp/img/dashboard.png");

        int imageWidth = 800;
        int imageHeight = 500;
        ImageIcon scaledImageIcon = new ImageIcon(originalImageIcon.getImage().getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH));
        imageLabel.setIcon(scaledImageIcon);


        // Create a panel for the image label
        JPanel imagePanel = new JPanel();
        imagePanel.add(imageLabel);




        JButton parcelManagementButton = new JButton("Parcel Management");
        parcelManagementButton.setFocusPainted(false);
        parcelManagementButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton feesButton = new JButton("Fees Management");
        feesButton.setFocusPainted(false);
        feesButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton trainsButton = new JButton("Trains Management");
        trainsButton.setFocusPainted(false);
        trainsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton scheduleManagementButton = new JButton("Schedule Management");
        scheduleManagementButton.setFocusPainted(false);
        scheduleManagementButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton employeeManagementButton = new JButton("Employee Management");
        employeeManagementButton.setFocusPainted(false);
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
        frame.add(imagePanel, BorderLayout.CENTER);

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
        ImageIcon frameIcon = new ImageIcon("D:/EAD/EventCRUDApp/img/SwiftRail.png");
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
