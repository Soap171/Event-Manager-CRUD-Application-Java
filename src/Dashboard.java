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

        parcelManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openParcelManagement();
            }
        });

        feesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFees();
            }
        });

        trainsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openTrains();
            }
        });

        scheduleManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openScheduleManagement();
            }
        });

        JPanel panel = new JPanel();
        panel.add(parcelManagementButton);
        panel.add(feesButton);
        panel.add(trainsButton);
        panel.add(scheduleManagementButton);

        add(panel);

        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void openParcelManagement() {
        ParcelCRUDApp parcel = new ParcelCRUDApp();

    }

    private void openFees() {
        // Implement the logic to open the Fees section
        JOptionPane.showMessageDialog(this, "Opening Fees", "Fees", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openTrains() {
        // Implement the logic to open the Trains section
        TrainsManagementForm trains = new TrainsManagementForm();
    }

    private void openScheduleManagement() {

        SchedulesForm form = new SchedulesForm();
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
