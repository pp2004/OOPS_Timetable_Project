package view;

import model.Course;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.List;

public class AutoSuggestTimetable extends JPanel {
    private final JButton nextButton = new JButton("Next Suggestion");
    private final JButton prevButton = new JButton("Previous Suggestion");
    private final JButton saveButton = new JButton("Save Timetable");
    private final JButton backButton = new JButton("Close");
    private JTable suggestionTable;
    private DefaultTableModel tableModel;
    private JLabel infoLabel;

    private List<Map<Course, String[]>> validSuggestions = new ArrayList<>();
    private int currentIndex = 0;

    public AutoSuggestTimetable(List<Course> selectedCourses) {
        setLayout(new BorderLayout());

        String[] columns = {"Course Code", "Course Name", "Instructor", "Day", "Start", "End"};
        tableModel = new DefaultTableModel(columns, 0);
        suggestionTable = new JTable(tableModel);
        suggestionTable.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(suggestionTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        infoLabel = new JLabel("Generating timetable suggestions...", SwingConstants.CENTER);
        add(infoLabel, BorderLayout.NORTH);

        // Use SwingWorker to generate suggestions in the background
        generateSuggestionsInBackground(selectedCourses);

        nextButton.addActionListener(e -> {
            if (!validSuggestions.isEmpty()) {
                currentIndex = (currentIndex + 1) % validSuggestions.size();
                showCurrentSuggestion();
            }
        });

        prevButton.addActionListener(e -> {
            if (!validSuggestions.isEmpty()) {
                currentIndex = (currentIndex - 1 + validSuggestions.size()) % validSuggestions.size();
                showCurrentSuggestion();
            }
        });

        saveButton.addActionListener(this::saveCurrentSuggestion);

        backButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        });
    }

    private void generateSuggestionsInBackground(List<Course> selectedCourses) {
        // Create a separate progress dialog without requiring a parent window
        JDialog progressDialog = new JDialog();
        progressDialog.setTitle("Generating Timetables");
        progressDialog.setModal(true);
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(null); // Center on screen
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        JLabel statusLabel = new JLabel("Processing timetable options...", SwingConstants.CENTER);
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(statusLabel, BorderLayout.NORTH);
        contentPanel.add(progressBar, BorderLayout.CENTER);
        
        progressDialog.add(contentPanel);
        
        // Use SwingWorker to process in background
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                generateAllSuggestions(selectedCourses);
                return null;
            }
            
            @Override
            protected void done() {
                progressDialog.dispose();
                if (validSuggestions.isEmpty()) {
                    infoLabel.setText("No valid timetable found for selected courses");
                    JOptionPane.showMessageDialog(AutoSuggestTimetable.this,
                        "No valid timetable combinations found. Try selecting different courses.",
                        "No Suggestions", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    infoLabel.setText("Found " + validSuggestions.size() + " timetable suggestion(s)");
                    showCurrentSuggestion();
                }
            }
        };
        
        // Start worker thread
        worker.execute();
        
        // Show dialog on EDT after worker starts
        SwingUtilities.invokeLater(() -> progressDialog.setVisible(true));
    }

    private void generateAllSuggestions(List<Course> courses) {
        Map<String, List<String[]>> courseOptions = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("data/courses_details.csv"))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) { // Only need 6 columns now
                    String code = parts[0].trim();
                    String instructor = parts[1].trim();
                    String day = parts[3].trim(); // Changed from 9 to 3
                    String start = parts[4].trim(); // Changed from 10 to 4
                    String end = parts[5].trim(); // Changed from 11 to 5
                    
                    // Convert time format from "HH:MM" to integer
                    String startInt = start.replace(":", "");
                    String endInt = end.replace(":", "");
                    
                    courseOptions.putIfAbsent(code, new ArrayList<>());
                    courseOptions.get(code).add(new String[]{instructor, day, startInt, endInt});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, 
                    "Error reading course details: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            });
            return;
        }

        // Add debug info to see which courses have options
        for (Course course : courses) {
            String code = course.getCourseNumber();
            List<String[]> options = courseOptions.get(code);
            if (options == null || options.isEmpty()) {
                System.out.println("Warning: No time slots found for course " + code);
            } else {
                System.out.println("Found " + options.size() + " options for course " + code);
            }
        }

        backtrackSuggestions(courses, courseOptions, new HashMap<>(), 0);
    }

    private void backtrackSuggestions(List<Course> courses, Map<String, List<String[]>> options, Map<Course, String[]> current, int index) {
        if (index == courses.size()) {
            validSuggestions.add(new HashMap<>(current));
            return;
        }

        Course course = courses.get(index);
        List<String[]> instructors = options.get(course.getCourseNumber());
        if (instructors == null || instructors.isEmpty()) {
            // No available time slots for this course, skip it
            backtrackSuggestions(courses, options, current, index + 1);
            return;
        }

        for (String[] details : instructors) {
            if (!hasConflict(current, details)) {
                current.put(course, details);
                backtrackSuggestions(courses, options, current, index + 1);
                current.remove(course);
            }
        }
    }

    private boolean hasConflict(Map<Course, String[]> current, String[] newEntry) {
        String newDay = newEntry[1];
        int newStart = Integer.parseInt(newEntry[2]);
        int newEnd = Integer.parseInt(newEntry[3]);

        for (String[] val : current.values()) {
            if (val[1].equals(newDay)) {
                int start = Integer.parseInt(val[2]);
                int end = Integer.parseInt(val[3]);
                if (Math.max(start, newStart) < Math.min(end, newEnd)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void showCurrentSuggestion() {
        tableModel.setRowCount(0);

        if (validSuggestions.isEmpty()) {
            tableModel.addRow(new Object[]{"No valid timetable found", "", "", "", "", ""});
            return;
        }

        Map<Course, String[]> suggestion = validSuggestions.get(currentIndex);
        for (Map.Entry<Course, String[]> entry : suggestion.entrySet()) {
            Course c = entry.getKey();
            String[] d = entry.getValue();
            
            // Convert time back to HH:MM format for display
            String start = d[2];
            String end = d[3];
            if (start.length() == 3) {
                start = "0" + start.substring(0, 1) + ":" + start.substring(1);
            } else {
                start = start.substring(0, 2) + ":" + start.substring(2);
            }
            
            if (end.length() == 3) {
                end = "0" + end.substring(0, 1) + ":" + end.substring(1);
            } else {
                end = end.substring(0, 2) + ":" + end.substring(2);
            }
            
            tableModel.addRow(new Object[]{c.getCourseNumber(), c.getName(), d[0], d[1], start, end});
        }
    }

    private void saveCurrentSuggestion(ActionEvent e) {
        if (validSuggestions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No suggestion to save.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(chooser.getSelectedFile()))) {
                writer.write("COURSE_CODE,COURSE_NAME,INSTRUCTOR,DAY,START,END\n");
                for (Map.Entry<Course, String[]> entry : validSuggestions.get(currentIndex).entrySet()) {
                    Course c = entry.getKey();
                    String[] d = entry.getValue();
                    
                    // Convert time back to HH:MM format for saving
                    String start = d[2];
                    String end = d[3];
                    if (start.length() == 3) {
                        start = "0" + start.substring(0, 1) + ":" + start.substring(1);
                    } else {
                        start = start.substring(0, 2) + ":" + start.substring(2);
                    }
                    
                    if (end.length() == 3) {
                        end = "0" + end.substring(0, 1) + ":" + end.substring(1);
                    } else {
                        end = end.substring(0, 2) + ":" + end.substring(2);
                    }
                    
                    writer.write(c.getCourseNumber() + "," + c.getName() + "," + d[0] + "," + d[1] + "," + start + "," + end + "\n");
                }
                JOptionPane.showMessageDialog(this, "Timetable saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save timetable: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}