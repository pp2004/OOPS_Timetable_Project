package view;

import model.Course;
import model.Instructor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class InstructorPanel extends JPanel {
    private List<Instructor> instructors;
    private List<Course> courses;
    private JTextArea display;

    public InstructorPanel(List<Instructor> instructors, List<Course> courses) {
        this.instructors = instructors;
        this.courses = courses;
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Manage Instructors", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        display = new JTextArea();
        display.setEditable(false);
        updateInstructorDisplay(); // Initial population
        add(new JScrollPane(display), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Instructor");
        JButton editButton = new JButton("Edit Instructor");
        JButton deleteButton = new JButton("Delete Instructor");
        JButton importButton = new JButton("Import from CSV"); // New button

        editButton.addActionListener(e -> showEditInstructorDialog());
        deleteButton.addActionListener(e -> deleteSelectedInstructor());
        addButton.addActionListener(e -> showAddInstructorDialog());
        importButton.addActionListener(e -> importInstructorsFromCSV()); // New listener
       
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(importButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }
    private void importInstructorsFromCSV() {
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
                    if (data.length == 2) {
                        try {
                            int id = Integer.parseInt(data[0].trim());
                            String name = data[1].trim();
                            instructors.add(new Instructor(id, name));
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                            JOptionPane.showMessageDialog(this, "Error parsing line: " + line + "\nEnsure format is: id (integer),name.", "CSV Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (!line.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Invalid number of columns in line: " + line + "\nEnsure format is: id (integer),name.", "CSV Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                updateInstructorDisplay();
                JOptionPane.showMessageDialog(this, "Instructors imported successfully from " + file.getName(), "Import Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading CSV file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void updateInstructorDisplay() {
        display.setText("");
        for (Instructor inst : instructors) {
            display.append(inst.toString() + "\n");
            for (Course c : courses) {
                if ((c.hashCode() + inst.getId()) % 50 == 0) { // pseudo-assignment
                    display.append("  ↳ " + c.getCourseNumber() + " - " + c.getName() + "\n");
                }
            }
            display.append("\n");
        }
    }
    private void showEditInstructorDialog() {
        int selectedIndex = display.getSelectionStart();
        int row = 0;
        int charCount = 0;
        Instructor selectedInstructor = null;
        for (Instructor inst : instructors) {
            int instructorLength = inst.toString().length() + 1; // +1 for newline
            if (selectedIndex >= charCount && selectedIndex < charCount + instructorLength) {
                selectedInstructor = inst;
                break;
            }
            charCount += instructorLength;
            for (Course c : courses) {
                if ((c.hashCode() + inst.getId()) % 50 == 0) {
                    charCount += ("  ↳ " + c.getCourseNumber() + " - " + c.getName() + "\n").length();
                }
            }
            charCount++; // For the extra newline after each instructor's details
            row++;
        }
    
        if (selectedInstructor == null) {
            JOptionPane.showMessageDialog(this, "Please select an instructor to edit (click on their name).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        final Instructor instructorToUpdate = selectedInstructor;

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Edit Instructor", true);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));
        dialog.setPreferredSize(new Dimension(300, 150));
    
        JTextField idField = new JTextField(String.valueOf(selectedInstructor.getId()), 10);
        idField.setEditable(false);
        JTextField nameField = new JTextField(selectedInstructor.getName(), 20);
        JButton updateButton = new JButton("Update");
    
        dialog.add(new JLabel("ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("")); // Spacer
        dialog.add(updateButton);
    
        updateButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                instructorToUpdate.setName(name);
                updateInstructorDisplay();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Instructor Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deleteSelectedInstructor() {
        int selectedIndex = display.getSelectionStart();
        Instructor instructorToDelete = null;
        int charCount = 0;
        for (Instructor inst : instructors) {
            int instructorLength = inst.toString().length() + 1;
            if (selectedIndex >= charCount && selectedIndex < charCount + instructorLength) {
                instructorToDelete = inst;
                break;
            }
            charCount += instructorLength;
            for (Course c : courses) {
                if ((c.hashCode() + inst.getId()) % 50 == 0) {
                    charCount += ("  ↳ " + c.getCourseNumber() + " - " + c.getName() + "\n").length();
                }
            }
            charCount++;
        }
    
        if (instructorToDelete == null) {
            JOptionPane.showMessageDialog(this, "Please select an instructor to delete (click on their name).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + instructorToDelete.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            instructors.remove(instructorToDelete);
            updateInstructorDisplay();
        }
    }
    private void showAddInstructorDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Add New Instructor", true);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));
        dialog.setPreferredSize(new Dimension(300, 150));

        JTextField idField = new JTextField(10);
        JTextField nameField = new JTextField(20);
        JButton addButton = new JButton("Add");

        dialog.add(new JLabel("ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("")); // Spacer
        dialog.add(addButton);

        addButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText().trim();

                if (!name.isEmpty()) {
                    instructors.add(new Instructor(id, name));
                    updateInstructorDisplay();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Instructor Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input for Instructor ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public List<Instructor> getInstructors() {
        return instructors;
    }
}