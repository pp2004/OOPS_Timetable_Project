package view;

import model.Classroom;
import model.Course;
import model.Instructor;
import util.AdminDataInitializer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JPanel {
    private MainWindow parent;
    private List<Course> courseList;
    private List<Instructor> instructorList;
    private List<Classroom> classroomList;

    public AdminDashboard(MainWindow parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        add(header, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));

        JButton classroomBtn = new JButton("Manage Classrooms");
        JButton courseBtn = new JButton("Manage Courses");
        JButton instructorBtn = new JButton("Manage Instructors");
        JButton scheduleBtn = new JButton("Manual Timetable Entry");
        JButton logoutBtn = new JButton("Logout");

        buttonPanel.add(classroomBtn);
        buttonPanel.add(courseBtn);
        buttonPanel.add(instructorBtn);
        buttonPanel.add(scheduleBtn);
        buttonPanel.add(logoutBtn);

        add(buttonPanel, BorderLayout.CENTER);

        // ✅ Load default values from AdminDataInitializer
        courseList = AdminDataInitializer.generateCourses();
        instructorList = AdminDataInitializer.generateInstructors(60);
        classroomList = AdminDataInitializer.generateClassrooms();

        // Linked panels
        ClassroomPanel classroomPanel = new ClassroomPanel(classroomList);
        CoursePanel coursePanel = new CoursePanel(courseList);
        InstructorPanel instructorPanel = new InstructorPanel(instructorList, courseList);

        // Build schedule panel only after some data exists
        scheduleBtn.addActionListener(e -> {
            if (courseList.isEmpty() || instructorList.isEmpty() || classroomList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add courses, instructors, and classrooms first.");
            } else {
                showPanel(new SchedulePanel(courseList, instructorList, classroomList), "Schedule Entry");
            }
        });

        classroomBtn.addActionListener(e -> showPanel(classroomPanel, "Classroom Manager"));
        courseBtn.addActionListener(e -> showPanel(coursePanel, "Course Manager"));
        instructorBtn.addActionListener(e -> showPanel(instructorPanel, "Instructor Manager"));
        logoutBtn.addActionListener(e -> parent.switchTo("login"));
    }

    private void showPanel(JPanel panel, String title) {
        JFrame popup = new JFrame(title);
        popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popup.setSize(800, 500);
        popup.add(panel);
        popup.setLocationRelativeTo(null);
        popup.setVisible(true);
    }
}
