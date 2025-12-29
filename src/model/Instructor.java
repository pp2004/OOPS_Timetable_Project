package model;

import java.util.ArrayList;
import java.util.List;

public class Instructor {
    private int id;
    private String name;
    private List<TeachingActivity> assignedCourses;

    public Instructor(int id, String name) {
        this.id = id;
        this.name = name;
        this.assignedCourses = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public List<TeachingActivity> getAssignedCourses() { return assignedCourses; }
    public void setName(String name) { this.name = name; }
    public void assignCourse(TeachingActivity activity) {
        assignedCourses.add(activity);
    }

    public boolean isAvailable(TimeSlot slot) {
        for (TeachingActivity activity : assignedCourses) {
            if (activity.getAssignedSlot() != null && activity.getAssignedSlot().overlapsWith(slot)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return name + " [ID: " + id + "]";
    }
}
