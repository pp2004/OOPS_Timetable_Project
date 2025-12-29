package view;

import model.Course;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CoursePanel extends JPanel {
    private List<Course> courses;
    private DefaultListModel<String> compulsoryListModel;
    private DefaultListModel<String> disciplineListModel;
    private DefaultListModel<String> openListModel;
    private DefaultListModel<String> humanitiesListModel;
    private JTabbedPane tabs;

    public CoursePanel(List<Course> courseList) {
        this.courses = courseList;
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Manage Courses", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        tabs = new JTabbedPane();
        compulsoryListModel = new DefaultListModel<>();
        disciplineListModel = new DefaultListModel<>();
        openListModel = new DefaultListModel<>();
        humanitiesListModel = new DefaultListModel<>();

        updateCourseLists(); // Initial population

        JPanel compulsory = new JPanel(new GridLayout(0, 1));
        compulsory.add(new JScrollPane(new JList<>(compulsoryListModel)));
        JPanel discipline = new JPanel(new GridLayout(0, 1));
        discipline.add(new JScrollPane(new JList<>(disciplineListModel)));
        JPanel open = new JPanel(new GridLayout(0, 1));
        open.add(new JScrollPane(new JList<>(openListModel)));
        JPanel humanities = new JPanel(new GridLayout(0, 1));
        humanities.add(new JScrollPane(new JList<>(humanitiesListModel)));

        tabs.addTab("Compulsory", compulsory);
        tabs.addTab("Discipline Electives", discipline);
        tabs.addTab("Open Electives (Minor)", open);
        tabs.addTab("Humanities Electives", humanities);

        add(tabs, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("Edit Course");
        JButton deleteButton = new JButton("Delete Course");
        JButton addButton = new JButton("Add Course");
        JButton importButton = new JButton("Import from CSV"); // New button
        addButton.addActionListener(e -> showAddCourseDialog());
        importButton.addActionListener(e -> importCoursesFromCSV()); // New listener
        editButton.addActionListener(e -> showEditCourseDialog());
        deleteButton.addActionListener(e -> deleteSelectedCourse());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        buttonPanel.add(importButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }
    private void importCoursesFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                boolean firstLine = true;
                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }
                    String[] data = line.split(",");
                    if (data.length == 6) {
                        try {
                            String courseNumber = data[0].trim();
                            String name = data[1].trim();
                            int numOfStudents = Integer.parseInt(data[2].trim());
                            int lectureHours = Integer.parseInt(data[3].trim());
                            int labHours = Integer.parseInt(data[4].trim());
                            String sectionName = data[5].trim();
                            courses.add(new Course(courseNumber, name, numOfStudents, lectureHours, labHours, sectionName));
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                            JOptionPane.showMessageDialog(this, "Error parsing line: " + line + "\nEnsure format is: courseNumber,name,numOfStudents,lectureHours,labHours,sectionName.", "CSV Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (!line.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Invalid number of columns in line: " + line + "\nEnsure format is: courseNumber,name,numOfStudents,lectureHours,labHours,sectionName.", "CSV Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                updateCourseLists();
                JOptionPane.showMessageDialog(this, "Courses imported successfully from " + file.getName(), "Import Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading CSV file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateCourseLists() {
        compulsoryListModel.clear();
        disciplineListModel.clear();
        openListModel.clear();
        humanitiesListModel.clear();
        for (Course c : courses) {
            String id = c.getCourseNumber();
            String label = c.toString();
            if (id.startsWith("EEE") || id.startsWith("MATH")) {
                compulsoryListModel.addElement(label);
            } else if (id.startsWith("CS") || id.startsWith("BITS")) {
                if (id.contains("F453") || id.contains("F454") || id.contains("F320")) {
                    openListModel.addElement(label);
                } else {
                    disciplineListModel.addElement(label);
                }
            } else if (id.startsWith("HSS")) {
                humanitiesListModel.addElement(label);
            }
        }
    }

    private void showAddCourseDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Add New Course", true);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));
        dialog.setPreferredSize(new Dimension(400, 300));

        JTextField courseNumberField = new JTextField(10);
        JTextField nameField = new JTextField(20);
        JTextField numOfStudentsField = new JTextField(5);
        JTextField lectureHoursField = new JTextField(5);
        JTextField labHoursField = new JTextField(5);
        JTextField sectionNameField = new JTextField(5);
        JButton addButton = new JButton("Add");

        dialog.add(new JLabel("Course Number:"));
        dialog.add(courseNumberField);
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("No. of Students:"));
        dialog.add(numOfStudentsField);
        dialog.add(new JLabel("Lecture Hours:"));
        dialog.add(lectureHoursField);
        dialog.add(new JLabel("Lab Hours:"));
        dialog.add(labHoursField);
        dialog.add(new JLabel("Section Name:"));
        dialog.add(sectionNameField);
        dialog.add(new JLabel("")); // Spacer
        dialog.add(addButton);

        addButton.addActionListener(e -> {
            try {
                String courseNumber = courseNumberField.getText().trim();
                String name = nameField.getText().trim();
                int numOfStudents = Integer.parseInt(numOfStudentsField.getText());
                int lectureHours = Integer.parseInt(lectureHoursField.getText());
                int labHours = Integer.parseInt(labHoursField.getText());
                String sectionName = sectionNameField.getText().trim();

                if (!courseNumber.isEmpty() && !name.isEmpty()) {
                    courses.add(new Course(courseNumber, name, numOfStudents, lectureHours, labHours, sectionName));
                    updateCourseLists();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Course Number and Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input for number of students or hours.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    private void showEditCourseDialog() {
        int selectedIndex = -1;
        JList<?> selectedList = null;
    
        if (tabs.getSelectedIndex() == 0) {
            selectedList = (JList<?>) ((JScrollPane) tabs.getComponentAt(0)).getViewport().getView();
        } else if (tabs.getSelectedIndex() == 1) {
            selectedList = (JList<?>) ((JScrollPane) tabs.getComponentAt(1)).getViewport().getView();
        } else if (tabs.getSelectedIndex() == 2) {
            selectedList = (JList<?>) ((JScrollPane) tabs.getComponentAt(2)).getViewport().getView();
        } else if (tabs.getSelectedIndex() == 3) {
            selectedList = (JList<?>) ((JScrollPane) tabs.getComponentAt(3)).getViewport().getView();
        }
    
        if (selectedList != null) {
            selectedIndex = selectedList.getSelectedIndex();
        }
    
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        Course selectedCourse = null;
        int courseIndex = -1;
        int count = 0;
        for (Course c : courses) {
            String id = c.getCourseNumber();
            if (tabs.getSelectedIndex() == 0 && (id.startsWith("EEE") || id.startsWith("MATH"))) {
                if (count == selectedIndex) {
                    selectedCourse = c;
                    courseIndex = courses.indexOf(c);
                    break;
                }
                count++;
            } else if (tabs.getSelectedIndex() == 1 && (id.startsWith("CS") || id.startsWith("BITS")) && !(id.contains("F453") || id.contains("F454") || id.contains("F320"))) {
                if (count == selectedIndex) {
                    selectedCourse = c;
                    courseIndex = courses.indexOf(c);
                    break;
                }
                count++;
            } else if (tabs.getSelectedIndex() == 2 && (id.startsWith("CS") || id.startsWith("BITS")) && (id.contains("F453") || id.contains("F454") || id.contains("F320"))) {
                if (count == selectedIndex) {
                    selectedCourse = c;
                    courseIndex = courses.indexOf(c);
                    break;
                }
                count++;
            } else if (tabs.getSelectedIndex() == 3 && id.startsWith("HSS")) {
                if (count == selectedIndex) {
                    selectedCourse = c;
                    courseIndex = courses.indexOf(c);
                    break;
                }
                count++;
            }
        }
    
        if (selectedCourse == null || courseIndex == -1) {
            JOptionPane.showMessageDialog(this, "Error retrieving selected course.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Edit Course", true);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));
        dialog.setPreferredSize(new Dimension(400, 300));
    
        JTextField courseNumberField = new JTextField(selectedCourse.getCourseNumber(), 10);
        courseNumberField.setEditable(false);
        JTextField nameField = new JTextField(selectedCourse.getName(), 20);
        JTextField numOfStudentsField = new JTextField(String.valueOf(selectedCourse.getNumOfStudents()), 5);
        JTextField lectureHoursField = new JTextField(String.valueOf(selectedCourse.getLectureHours()), 5);
        JTextField labHoursField = new JTextField(String.valueOf(selectedCourse.getLabHours()), 5);
        JTextField sectionNameField = new JTextField(selectedCourse.getSectionName(), 5);
        JButton updateButton = new JButton("Update");
    
        dialog.add(new JLabel("Course Number:"));
        dialog.add(courseNumberField);
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("No. of Students:"));
        dialog.add(numOfStudentsField);
        dialog.add(new JLabel("Lecture Hours:"));
        dialog.add(lectureHoursField);
        dialog.add(new JLabel("Lab Hours:"));
        dialog.add(labHoursField);
        dialog.add(new JLabel("Section Name:"));
        dialog.add(sectionNameField);
        dialog.add(new JLabel("")); // Spacer
        dialog.add(updateButton);
    
        int finalCourseIndex = courseIndex;
        updateButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int numOfStudents = Integer.parseInt(numOfStudentsField.getText());
                int lectureHours = Integer.parseInt(lectureHoursField.getText());
                int labHours = Integer.parseInt(labHoursField.getText());
                String sectionName = sectionNameField.getText().trim();
    
                courses.get(finalCourseIndex).setName(name);
                courses.get(finalCourseIndex).setNumOfStudents(numOfStudents);
                courses.get(finalCourseIndex).setLectureHours(lectureHours);
                courses.get(finalCourseIndex).setLabHours(labHours);
                courses.get(finalCourseIndex).setSectionName(sectionName);
    
                updateCourseLists();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input for number of students or hours.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deleteSelectedCourse() {
        int selectedIndex = -1;
        JList<?> selectedList = null;
    
        if (tabs.getSelectedIndex() == 0) {
            selectedList = (JList<?>) ((JScrollPane) tabs.getComponentAt(0)).getViewport().getView();
        } else if (tabs.getSelectedIndex() == 1) {
            selectedList = (JList<?>) ((JScrollPane) tabs.getComponentAt(1)).getViewport().getView();
        } else if (tabs.getSelectedIndex() == 2) {
            selectedList = (JList<?>) ((JScrollPane) tabs.getComponentAt(2)).getViewport().getView();
        } else if (tabs.getSelectedIndex() == 3) {
            selectedList = (JList<?>) ((JScrollPane) tabs.getComponentAt(3)).getViewport().getView();
        }
    
        if (selectedList != null) {
            selectedIndex = selectedList.getSelectedIndex();
        }
    
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        Course courseToDelete = null;
        int count = 0;
        for (Course c : courses) {
            String id = c.getCourseNumber();
            if (tabs.getSelectedIndex() == 0 && (id.startsWith("EEE") || id.startsWith("MATH"))) {
                if (count == selectedIndex) {
                    courseToDelete = c;
                    break;
                }
                count++;
            } else if (tabs.getSelectedIndex() == 1 && (id.startsWith("CS") || id.startsWith("BITS")) && !(id.contains("F453") || id.contains("F454") || id.contains("F320"))) {
                if (count == selectedIndex) {
                    courseToDelete = c;
                    break;
                }
                count++;
            } else if (tabs.getSelectedIndex() == 2 && (id.startsWith("CS") || id.startsWith("BITS")) && (id.contains("F453") || id.contains("F454") || id.contains("F320"))) {
                if (count == selectedIndex) {
                    courseToDelete = c;
                    break;
                }
                count++;
            } else if (tabs.getSelectedIndex() == 3 && id.startsWith("HSS")) {
                if (count == selectedIndex) {
                    courseToDelete = c;
                    break;
                }
                count++;
            }
        }
    
        if (courseToDelete == null) {
            JOptionPane.showMessageDialog(this, "Error finding course to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected course?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            courses.remove(courseToDelete);
            updateCourseLists();
        }
    }
    public List<Course> getCourses() {
        return courses;
    }
}