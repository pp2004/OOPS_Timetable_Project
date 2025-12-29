package view;

import model.Classroom;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ClassroomPanel extends JPanel {
    private List<Classroom> classrooms;
    private DefaultListModel<String> classroomListModel;
    private JList<String> list;

    public ClassroomPanel(List<Classroom> classroomList) {
        this.classrooms = classroomList;
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Manage Classrooms", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        classroomListModel = new DefaultListModel<>();
        updateClassroomList(); // Populate the list initially

        list = new JList<>(classroomListModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow single selection
        
        add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Classroom");
        JButton importButton = new JButton("Import from CSV"); // New button
        JButton editButton = new JButton("Edit Classroom"); // For future implementation
        JButton deleteButton = new JButton("Delete Classroom"); // For future implementation

        addButton.addActionListener(e -> showAddClassroomDialog());
        importButton.addActionListener(e -> importClassroomsFromCSV()); // New listener
        editButton.addActionListener(e -> showEditClassroomDialog());
        deleteButton.addActionListener(e -> deleteSelectedClassroom());

        buttonPanel.add(addButton);
        buttonPanel.add(importButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateClassroomList() {
        classroomListModel.clear();
        for (Classroom c : classrooms) {
            classroomListModel.addElement(c.toString());
        }
    }
//added below part
private void importClassroomsFromCSV() {
    JFileChooser fileChooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
    fileChooser.setFileFilter(filter);
    int returnVal = fileChooser.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true; // Skip header row (optional)
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length == 5) {
                    try {
                        String id = data[0].trim();
                        int capacity = Integer.parseInt(data[1].trim());
                        boolean avRequired = Boolean.parseBoolean(data[2].trim());
                        int computers = Integer.parseInt(data[3].trim());
                        boolean isLab = Boolean.parseBoolean(data[4].trim());
                        classrooms.add(new Classroom(id, capacity, avRequired, computers, isLab));
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                        JOptionPane.showMessageDialog(this, "Error parsing line: " + line + "\nEnsure format is: id,capacity,av_required,num_computers,is_lab (boolean).", "CSV Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (!line.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Invalid number of columns in line: " + line + "\nEnsure format is: id,capacity,av_required,num_computers,is_lab (boolean).", "CSV Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            updateClassroomList();
            JOptionPane.showMessageDialog(this, "Classrooms imported successfully from " + file.getName(), "Import Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading CSV file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

    private void showAddClassroomDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Add New Classroom", true);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));
        dialog.setPreferredSize(new Dimension(300, 200));

        JTextField idField = new JTextField(10);
        JTextField capacityField = new JTextField(5);
        JCheckBox avRequiredCheck = new JCheckBox();
        JTextField computersField = new JTextField(5);
        JCheckBox isLabCheck = new JCheckBox();
        JButton addButton = new JButton("Add");

        dialog.add(new JLabel("ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Capacity:"));
        dialog.add(capacityField);
        dialog.add(new JLabel("AV Required:"));
        dialog.add(avRequiredCheck);
        dialog.add(new JLabel("No. of Computers:"));
        dialog.add(computersField);
        dialog.add(new JLabel("Is Lab:"));
        dialog.add(isLabCheck);
        dialog.add(new JLabel("")); // Spacer
        dialog.add(addButton);

        addButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                int capacity = Integer.parseInt(capacityField.getText());
                boolean avRequired = avRequiredCheck.isSelected();
                int computers = Integer.parseInt(computersField.getText());
                boolean isLab = isLabCheck.isSelected();

                if (!id.isEmpty()) {
                    classrooms.add(new Classroom(id, capacity, avRequired, computers, isLab));
                    updateClassroomList();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Classroom ID cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input for capacity or computers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    private void showEditClassroomDialog() {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a classroom to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        Classroom selectedClassroom = classrooms.get(selectedIndex);
    
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Edit Classroom", true);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));
        dialog.setPreferredSize(new Dimension(300, 200));
    
        JTextField idField = new JTextField(selectedClassroom.getId(), 10);
        idField.setEditable(false); // ID should likely not be editable
        JTextField capacityField = new JTextField(String.valueOf(selectedClassroom.getCapacity()), 5);
        JCheckBox avRequiredCheck = new JCheckBox("AV Required", selectedClassroom.isAudioVideoRequired()); // Added label
        JTextField computersField = new JTextField(String.valueOf(selectedClassroom.getNumOfComputers()), 5);
        JCheckBox isLabCheck = new JCheckBox("Is Lab", selectedClassroom.isLab()); // Added label
        JButton updateButton = new JButton("Update");
    
        dialog.add(new JLabel("ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Capacity:"));
        dialog.add(capacityField);
        dialog.add(avRequiredCheck); // Now adding the JCheckBox directly
        dialog.add(new JLabel("No. of Computers:"));
        dialog.add(computersField);
        dialog.add(isLabCheck); // Now adding the JCheckBox directly
        dialog.add(new JLabel("")); // Spacer
        dialog.add(updateButton);
    
        updateButton.addActionListener(e -> {
            try {
                int capacity = Integer.parseInt(capacityField.getText());
                boolean avRequired = avRequiredCheck.isSelected();
                int computers = Integer.parseInt(computersField.getText());
                boolean isLab = isLabCheck.isSelected();
    
                selectedClassroom.setCapacity(capacity);
                selectedClassroom.setAudioVideoRequired(avRequired);
                selectedClassroom.setNumOfComputers(computers);
                selectedClassroom.setLab(isLab);
    
                updateClassroomList();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input for capacity or computers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    private void deleteSelectedClassroom() {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a classroom to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected classroom?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            classrooms.remove(selectedIndex);
            updateClassroomList();
        }
    }

    public List<Classroom> getClassrooms() {
        return classrooms;
    }
}