import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Dashboard extends JFrame {

    JFrame frame = new JFrame();

    public Dashboard() {
        super("Dashboard");

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

        JLabel label = new JLabel("Choose the Path you want to Manage with SwiftRail !!");


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
                openFees();
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

                openEmployeeManagement();
                setVisible(false);
            }
        });


        JPanel panel = new JPanel();
        panel.add(parcelManagementButton);
        panel.add(feesButton);
        panel.add(trainsButton);
        panel.add(scheduleManagementButton);
        panel.add(employeeManagementButton);

        JPanel panel2 = new JPanel();
        panel2.add(label);



        frame.add(panel,BorderLayout.CENTER);
        frame.add(panel2, BorderLayout.NORTH);

        frame.setSize(500, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    private void openParcelManagement() {

        ParcelCRUDApp parcel = new ParcelCRUDApp();
        frame.setVisible(false);


    }

    private void openFees() {
        FeesManagement fees = new FeesManagement();
        frame.setVisible(false);



    }

    private void openTrains() {
        TrainsManagementForm trains = new TrainsManagementForm();
        frame.setVisible(false);

    }

    private void openScheduleManagement() {

        SchedulesForm form = new SchedulesForm();
        frame.setVisible(false);

    }

    private void openEmployeeManagement(){
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
