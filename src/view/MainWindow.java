package view;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel containerPanel;

    public MainWindow() {
        setTitle("Time Table Builder - EEE Branch");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen

        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);

        // Add all panels here
        containerPanel.add(new LoginPanel(this), "login");
        containerPanel.add(new AdminDashboard(this), "admin");
        containerPanel.add(new TeacherDashboard(this), "teacher");
        containerPanel.add(new StudentDashboard(this), "student");

        add(containerPanel);
        cardLayout.show(containerPanel, "login");
    }

    public void switchTo(String panelName) {
        cardLayout.show(containerPanel, panelName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow app = new MainWindow();
            app.setVisible(true);
        });
    }
}

