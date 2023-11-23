import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Dashboard extends JFrame {

    public Dashboard() {
        super("Dashboard");

        JButton parcelManagementButton = new JButton("Parcel Management");
        JButton feesButton = new JButton("Fees");
        JButton trainsButton = new JButton("Trains");
        JButton scheduleManagementButton = new JButton("Schedule Management");
        JButton employeeManagementButton = new JButton("Employee Management");

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

        add(panel);

        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void openParcelManagement() {

        ParcelCRUDApp parcel = new ParcelCRUDApp();
        setVisible(false);


    }

    private void openFees() {
        setVisible(false);



    }

    private void openTrains() {
        TrainsManagementForm trains = new TrainsManagementForm();
        setVisible(false);

    }

    private void openScheduleManagement() {

        SchedulesForm form = new SchedulesForm();
        setVisible(false);

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
