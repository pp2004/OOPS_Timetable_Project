package view;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StudentDashboard extends JPanel {
    private MainWindow parent;
    private Map<String, Course> allCourses = new HashMap<>();
    private Map<String, Integer> courseCredits = new HashMap<>();
    private Map<String, JCheckBox> checkboxes = new HashMap<>();
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JLabel creditSummary = new JLabel("Total selected credits: 0 / 25");
    private JProgressBar creditProgress = new JProgressBar(0, 25);
    private List<Course> selectedCoursesList = new ArrayList<>();
    private final int MAX_CREDITS = 25;
    private boolean creditWarningShown = false;

    public StudentDashboard() {
        this(null);
    }

    public StudentDashboard(MainWindow parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Student Dashboard", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        add(header, BorderLayout.NORTH);

        creditSummary.setHorizontalAlignment(SwingConstants.CENTER);
        creditSummary.setToolTipText("Reminder: You can select up to 25 credits only.");
        creditProgress.setStringPainted(true);
        creditProgress.setToolTipText("Shows your selected credits out of maximum 25 allowed.");

        JPanel creditPanel = new JPanel(new BorderLayout());
        creditPanel.add(creditSummary, BorderLayout.NORTH);
        creditPanel.add(creditProgress, BorderLayout.SOUTH);

        add(creditPanel, BorderLayout.BEFORE_FIRST_LINE);
        add(tabbedPane, BorderLayout.CENTER);

        JButton loadButton = new JButton("Select Semester and Load Courses");
        JButton displayButton = new JButton("Display Selected Courses");
        JButton exportButton = new JButton("Export Selected to CSV");
        JButton suggestButton = new JButton("Auto Suggest Timetable");
        JButton logoutButton = new JButton("Logout");

        JPanel buttons = new JPanel();
        buttons.add(loadButton);
        buttons.add(displayButton);
        buttons.add(exportButton);
        buttons.add(suggestButton);
        buttons.add(logoutButton);

        add(buttons, BorderLayout.SOUTH);

        loadButton.addActionListener(this::loadCourses);
        displayButton.addActionListener(this::displaySelectedCourses);
        exportButton.addActionListener(this::exportSelectedCourses);
        suggestButton.addActionListener(this::suggestTimetable);
        logoutButton.addActionListener(e -> {
            if (parent != null) {
                parent.switchTo("login");
            } else {
                JOptionPane.showMessageDialog(this, "Logout functionality only available when used with MainWindow", 
                                             "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        loadCourseDetailsFromFile("data/courses_details.csv");
    }

    private void loadCourseDetailsFromFile(String filename) {
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(filename))) {
            String line;
            String headerLine = br.readLine(); // Read the header row
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 12) {
                    try {
                        String courseCode = parts[0].trim();
                        String courseName = parts[1].trim(); // Assuming INSTRUCTOR is the name
                        int lectureHours = Integer.parseInt(parts[7].trim()); // Assuming HOURS is lecture hours
                        int labHours = 0; // Assuming no separate lab hours in this context
                        Course course = new Course(courseCode, courseName, 0, lectureHours, labHours, "");
                        allCourses.put(courseCode, course);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        System.err.println("Error parsing line in CSV: " + line + " - " + e.getMessage());
                    }
                } else if (!line.trim().isEmpty()) {
                    System.err.println("Invalid number of columns in CSV line: " + line);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading course details file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCourses(ActionEvent e) {
        tabbedPane.removeAll();
        allCourses.clear();
        checkboxes.clear();
        courseCredits.clear();
        creditWarningShown = false;
        updateCreditSummary();

        String[] semesters = {"1st Semester", "2nd Semester"};
        String sem = (String) JOptionPane.showInputDialog(this, "Select Semester:", "Semester", JOptionPane.QUESTION_MESSAGE, null, semesters, semesters[0]);
        if (sem == null) return;

        String[] minors = {"CNI", "DS"};
        String minor = (String) JOptionPane.showInputDialog(this, "Select Minor:", "Minor", JOptionPane.QUESTION_MESSAGE, null, minors, minors[0]);
        if (minor == null) return;

        Map<String, JPanel> sections = new LinkedHashMap<>();
        String[] titles = {"Compulsory Courses", "Discipline Electives", "Open Electives (Minor)", "Humanities Electives"};
        for (String title : titles) {
            JPanel panel = new JPanel(new GridLayout(0, 1));
            sections.put(title, panel);
            tabbedPane.addTab(title, new JScrollPane(panel));
        }

        if (sem.equals("1st Semester")) {
            addCourse("EEE F311", "Communication Systems", 3, 0, 4, sections.get("Compulsory Courses"));
            addCourse("MATH F212", "Optimization", 3, 0, 3, sections.get("Compulsory Courses"));
            addCourse("EEE F313", "Analog & Digital VLSI Design", 3, 1, 4, sections.get("Compulsory Courses"));
        } else {
            addCourse("EEE F312", "Power Systems", 3, 0, 3, sections.get("Compulsory Courses"));
            addCourse("EEE F342", "Power Electronics", 3, 1, 4, sections.get("Compulsory Courses"));
            addCourse("EEE F341", "Analog Electronics", 3, 1, 4, sections.get("Compulsory Courses"));
        }

        // Discipline electives
        addCourse("BITS F312", "Neural Networks and Fuzzy Logic", 3, 0, 3, sections.get("Discipline Electives"));
        addCourse("BITS F415", "Introduction To MEMS", 3, 1, 4, sections.get("Discipline Electives"));
        addCourse("CS F213", "Object Oriented Programming", 3, 1, 4, sections.get("Discipline Electives"));
        addCourse("CS F342", "Computer Architecture", 3, 1, 4, sections.get("Discipline Electives"));
        addCourse("CS F372", "Operating Systems", 3, 0, 3, sections.get("Discipline Electives"));
        addCourse("EEE F245", "Control System Lab", 0, 1, 1, sections.get("Discipline Electives"));
        addCourse("EEE F246", "Electrical and Electronic Circuits Lab", 0, 2, 2, sections.get("Discipline Electives"));

        if (minor.equals("CNI")) {
            addCourse("BITS F232", "Foundations of Data Structures", 3, 1, 4, sections.get("Open Electives (Minor)"));
            addCourse("CS F372", "Operating Systems", 3, 0, 3, sections.get("Open Electives (Minor)"));
            addCourse("CS F407", "Artificial Intelligence", 3, 0, 3, sections.get("Open Electives (Minor)"));
            addCourse("BITS F464", "Machine Learning", 3, 0, 3, sections.get("Open Electives (Minor)"));
            addCourse("CS F212", "Database Systems", 3, 1, 4, sections.get("Open Electives (Minor)"));
            addCourse("CS F301", "Programming Languages", 2, 0, 2, sections.get("Open Electives (Minor)"));
            addCourse("CS F303", "Computer Networks", 3, 1, 4, sections.get("Open Electives (Minor)"));
        } else {
            addCourse("BITS F464", "Machine Learning", 3, 0, 3, sections.get("Open Electives (Minor)"));
            addCourse("CS F320", "Foundations of Data Science", 3, 0, 3, sections.get("Open Electives (Minor)"));
            addCourse("MATH F432", "Applied Statistical Methods", 3, 0, 3, sections.get("Open Electives (Minor)"));
            addCourse("BITS F453", "Computational Learning Theory", 3, 0, 3, sections.get("Open Electives (Minor)"));
            addCourse("BITS F454", "Bio-Inspired Intelligence", 3, 0, 3, sections.get("Open Electives (Minor)"));
            addCourse("CS F317", "Reinforcement Learning", 3, 0, 3, sections.get("Open Electives (Minor)"));
            addCourse("CS F407", "Artificial Intelligence", 3, 0, 3, sections.get("Open Electives (Minor)"));
        }

        String[] humCourses = {
                "HSS F334 - Srimad Bhagavad Gita",
                "HSS F335 - Literary Criticism",
                "HSS F336 - Modern Fiction",
                "HSS F337 - English Literary Forms",
                "HSS F338 - Comparative Indian Literature",
                "HSS F365 - Sustainable Happiness",
                "HSS F368 - Asian Cinemas and Cultures",
                "HSS F369 - Caste and Gender in India"
        };

        for (String c : humCourses) {
            String code = c.split(" - ")[0];
            String name = c.split(" - ")[1];
            addCourse(code, name, 3, 0, 3, sections.get("Humanities Electives"));
        }

        revalidate();
        repaint();
    }

    private void addCourse(String code, String title, int l, int p, int u, JPanel targetPanel) {
        Course c = new Course(code, title, 0, l, p, "");
        JCheckBox cb = new JCheckBox(code + " - " + title + " (" + u + " credits)");
        cb.setToolTipText("This course is worth " + u + " credits.");
        cb.addItemListener(e -> updateCreditSummary());
        allCourses.put(code, c);
        courseCredits.put(code, u);
        checkboxes.put(code, cb);
        targetPanel.add(cb);
    }

    private void updateCreditSummary() {
        int total = checkboxes.entrySet().stream()
                .filter(entry -> entry.getValue().isSelected())
                .mapToInt(entry -> courseCredits.get(entry.getKey())).sum();
        
        // Update the credit display
        creditSummary.setText("Total selected credits: " + total + " / " + MAX_CREDITS);
        creditProgress.setValue(total);
        
        // Change color based on credit status
        if (total > MAX_CREDITS) {
            creditSummary.setForeground(Color.RED);
            creditProgress.setForeground(Color.RED);
            
            // Show warning dialog if credits exceed limit and warning hasn't been shown
            if (!creditWarningShown) {
                JOptionPane.showMessageDialog(this,
                    "Warning: You have selected " + total + " credits, which exceeds the maximum of " + MAX_CREDITS + " credits.\n" +
                    "Please deselect some courses before proceeding.",
                    "Credit Limit Exceeded", JOptionPane.WARNING_MESSAGE);
                creditWarningShown = true;
            }
        } else {
            // Reset colors and warning flag when credits are within limit
            creditSummary.setForeground(Color.BLACK);
            creditProgress.setForeground(UIManager.getColor("ProgressBar.foreground"));
            creditWarningShown = false;
        }

        // Update selected courses list
        selectedCoursesList.clear();
        for (Map.Entry<String, JCheckBox> entry : checkboxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                Course selectedCourse = allCourses.get(entry.getKey());
                if (selectedCourse != null) {
                    selectedCoursesList.add(selectedCourse);
                }
            }
        }
    }

    private void displaySelectedCourses(ActionEvent e) {
        StringBuilder sb = new StringBuilder("Selected Courses:\n");
        if (selectedCoursesList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses selected.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int totalCredits = 0;
        for (Course course : selectedCoursesList) {
            String courseCode = course.getCourseNumber();
            int credits = courseCredits.get(courseCode);
            totalCredits += credits;
            sb.append(courseCode).append(" - ").append(course.getName())
              .append(" (").append(credits).append(" credits)\n");
        }
        
        sb.append("\nTotal Credits: ").append(totalCredits);
        if (totalCredits > MAX_CREDITS) {
            sb.append(" - EXCEEDS MAXIMUM LIMIT OF ").append(MAX_CREDITS).append(" CREDITS");
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Selected Courses", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportSelectedCourses(ActionEvent e) {
        if (selectedCoursesList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses selected to export.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int totalCredits = selectedCoursesList.stream()
                .mapToInt(course -> courseCredits.get(course.getCourseNumber()))
                .sum();
                
        if (totalCredits > MAX_CREDITS) {
            int option = JOptionPane.showConfirmDialog(this, 
                "You have selected " + totalCredits + " credits, which exceeds the maximum of " + MAX_CREDITS + ".\n" +
                "Do you still want to export these selections?", 
                "Credit Limit Exceeded", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Selected Courses to CSV");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write("COURSE_NUMBER,COURSE_NAME,CREDITS\n"); // Write header
                for (Course course : selectedCoursesList) {
                    String courseCode = course.getCourseNumber();
                    int credits = courseCredits.get(courseCode);
                    writer.write(courseCode + "," + course.getName() + "," + credits + "\n");
                }
                writer.write("TOTAL CREDITS," + totalCredits + "\n");
                JOptionPane.showMessageDialog(this, "Selected courses exported to " + fileToSave.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting courses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void suggestTimetable(ActionEvent e) {
        if (selectedCoursesList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select courses first.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int totalCredits = selectedCoursesList.stream()
                .mapToInt(course -> courseCredits.get(course.getCourseNumber()))
                .sum();
                
        if (totalCredits > MAX_CREDITS) {
            int option = JOptionPane.showConfirmDialog(this, 
                "You have selected " + totalCredits + " credits, which exceeds the maximum of " + MAX_CREDITS + ".\n" +
                "Do you still want to generate a timetable for these selections?", 
                "Credit Limit Exceeded", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        // Create a frame for the timetable suggestion
        JFrame timetableFrame = new JFrame("Auto Suggest Timetable");
        timetableFrame.setSize(800, 600);
        timetableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Create and add the AutoSuggestTimetable panel
        AutoSuggestTimetable timetablePanel = new AutoSuggestTimetable(selectedCoursesList);
        timetableFrame.getContentPane().add(timetablePanel);
        
        timetableFrame.setLocationRelativeTo(this);
        timetableFrame.setVisible(true);
    }
}