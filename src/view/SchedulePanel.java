package view;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.util.List;

public class SchedulePanel extends JPanel {
    private List<Course> courses;
    private List<Instructor> instructors;
    private List<Classroom> classrooms;
    private TimeTable timetable;
    private DefaultListModel<String> scheduleListModel;
    private JList<String> scheduleList;

    public SchedulePanel(List<Course> courses, List<Instructor> instructors, List<Classroom> classrooms) {
        this.courses = courses;
        this.instructors = instructors;
        this.classrooms = classrooms;
        this.timetable = new TimeTable();
        this.scheduleListModel = new DefaultListModel<>();

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Manual Timetable Entry", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Course dropdown
        JComboBox<Course> courseBox = new JComboBox<>(courses.toArray(new Course[0]));
        JComboBox<Instructor> instructorBox = new JComboBox<>(instructors.toArray(new Instructor[0]));
        JComboBox<Classroom> classroomBox = new JComboBox<>(classrooms.toArray(new Classroom[0]));
        JComboBox<String> dayBox = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"});
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"LECTURE", "LAB"});
        JTextField startField = new JTextField("09:00");
        JTextField endField = new JTextField("10:00");
        JTextField hoursField = new JTextField("1");

        JPanel form = new JPanel(new GridLayout(0, 2));
        form.add(new JLabel("Course:")); form.add(courseBox);
        form.add(new JLabel("Instructor:")); form.add(instructorBox);
        form.add(new JLabel("Classroom:")); form.add(classroomBox);
        form.add(new JLabel("Day:")); form.add(dayBox);
        form.add(new JLabel("Start Time (HH:mm):")); form.add(startField);
        form.add(new JLabel("End Time (HH:mm):")); form.add(endField);
        form.add(new JLabel("Component Type:")); form.add(typeBox);
        form.add(new JLabel("Hours:")); form.add(hoursField);

        add(form, BorderLayout.WEST);

        scheduleList = new JList<>(scheduleListModel);
        add(new JScrollPane(scheduleList), BorderLayout.CENTER);

        JButton addBtn = new JButton("Add Entry");
        JButton deleteBtn = new JButton("Delete Entry");
        JButton exportBtn = new JButton("Export to CSV");

        addBtn.addActionListener((ActionEvent e) -> {
            try {
                Course c = (Course) courseBox.getSelectedItem();
                Instructor i = (Instructor) instructorBox.getSelectedItem();
                Classroom cls = (Classroom) classroomBox.getSelectedItem();
                String day = (String) dayBox.getSelectedItem();
                String typeStr = (String) typeBox.getSelectedItem();
                LocalTime start = LocalTime.parse(startField.getText().trim());
                LocalTime end = LocalTime.parse(endField.getText().trim());
                int hours = Integer.parseInt(hoursField.getText().trim());

                TimeSlot slot = new TimeSlot(day, start, end, typeStr.equals("LAB"));
                CourseComponent cc = new CourseComponent(c, i, TimeSlotType.valueOf(typeStr), hours);
                ScheduleEntry newEntry = new ScheduleEntry(cc, cls, slot);

                if (timetable.addEntry(newEntry)) {
                    scheduleListModel.addElement(newEntry.toString());
                    //clearInputFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Conflict detected, not added.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            int selectedIndex = scheduleList.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Please select an entry to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this entry?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                timetable.getEntries().remove(selectedIndex);
                scheduleListModel.remove(selectedIndex);
            }
        });

        exportBtn.addActionListener((ActionEvent e) -> {
            String path = "timetable.csv";
            boolean saved = timetable.saveToFile(path);
            if (saved) {
                JOptionPane.showMessageDialog(this, "Saved to " + path, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn);
        btnPanel.add(exportBtn);
        btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    

    public TimeTable getTimeTable() {
        return timetable;
    }
}