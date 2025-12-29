package model;

public abstract class TeachingActivity {
    protected Course course;
    protected Instructor instructor;
    protected int hours;
    protected TimeSlot assignedSlot;

    public TeachingActivity(Course course, Instructor instructor, int hours) {
        this.course = course;
        this.instructor = instructor;
        this.hours = hours;
    }

    public Course getCourse() { return course; }
    public Instructor getInstructor() { return instructor; }
    public int getHours() { return hours; }
    public TimeSlot getAssignedSlot() { return assignedSlot; }
    public void setAssignedSlot(TimeSlot slot) { this.assignedSlot = slot; }

    public abstract String getType();
}

