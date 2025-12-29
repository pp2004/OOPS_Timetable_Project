package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TimeTable {
    private List<ScheduleEntry> entries;

    public TimeTable() {
        this.entries = new ArrayList<>();
    }

    public List<ScheduleEntry> getEntries() {
        return entries;
    }

    public boolean addEntry(ScheduleEntry entry) {
        for (ScheduleEntry existing : entries) {
            if (existing.getTimeSlot().overlapsWith(entry.getTimeSlot())) {
                if (existing.getCourseComponent().getInstructor().getId() == entry.getCourseComponent().getInstructor().getId())
                    return false;// instructoor conflict
                if (existing.getClassroom().getId().equals(entry.getClassroom().getId()))
                    return false;// classroom conflict
                if (existing.getCourseComponent().getCourse().getCourseNumber()
                        .equals(entry.getCourseComponent().getCourse().getCourseNumber()))
                    return false;//Course conflict (assuming one section per course)
            }
        }
        entries.add(entry);
        return true;
    }

    public boolean checkConflictForUpdate(ScheduleEntry entry, int indexToIgnore) {
        for (int i = 0; i < entries.size(); i++) {
            if (i == indexToIgnore) continue; // Skip the entry being updated
            ScheduleEntry existing = entries.get(i);
            if (existing.getTimeSlot().overlapsWith(entry.getTimeSlot())) {
                if (existing.getCourseComponent().getInstructor().getId() == entry.getCourseComponent().getInstructor().getId()) {
                    return true; // Instructor conflict
                }
                if (existing.getClassroom().getId().equals(entry.getClassroom().getId())) {
                    return true; // Classroom conflict
                }
                if (existing.getCourseComponent().getCourse().getCourseNumber()
                        .equals(entry.getCourseComponent().getCourse().getCourseNumber())) {
                    return true; // Course conflict
                }
            }
        }
        return false; // No conflict
    }    
    public boolean saveToFile(String filePath) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath)))) {
            writer.println("# My Custom Timetable Format"); // Header
            for (ScheduleEntry entry : entries) {
                CourseComponent cc = entry.getCourseComponent();
                TimeSlot ts = entry.getTimeSlot();
                writer.printf("COURSE=%s\n", cc.getCourse().getCourseNumber());
                writer.printf("INSTRUCTOR=%d\n", cc.getInstructor().getId());
                writer.printf("CLASSROOM=%s\n", entry.getClassroom().getId());
                writer.printf("DAY=%s\n", ts.getDay());
                writer.printf("START=%s\n", ts.getStartTime());
                writer.printf("END=%s\n", ts.getEndTime());
                writer.printf("TYPE=%s\n", cc.getType());
                writer.printf("HOURS=%d\n", cc.getHours());
                writer.println("---ENTRY_END---");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}