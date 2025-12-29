package view;

import javax.swing.*;
import java.awt.*;

public class TeacherDashboard extends JPanel {
    private MainWindow parent;

    public TeacherDashboard(MainWindow parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Teacher Dashboard", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        add(header, BorderLayout.NORTH);

        // Placeholder timetable (empty table for now)
        String[] columns = {"Course", "Type", "Day", "Start Time", "End Time", "Room"};
        String[][] data = {
            {"EEE F311", "Lecture", "Monday", "9:00", "10:00", "D001"},
            {"CS F213", "Lab", "Wednesday", "14:00", "16:00", "J102"}
        };

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> parent.switchTo("login"));
        add(logoutBtn, BorderLayout.SOUTH);
    }
}

