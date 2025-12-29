package model;

public class CourseComponent {
    private Course course;
    private Instructor instructor;
    private TimeSlotType type;
    private int hours;

    public CourseComponent(Course course, Instructor instructor, TimeSlotType type, int hours) {
        this.course = course;
        this.instructor = instructor;
        this.type = type;
        this.hours = hours;
    }

    public Course getCourse() { return course; }
    public Instructor getInstructor() { return instructor; }
    public TimeSlotType getType() { return type; }
    public int getHours() { return hours; }

    @Override
    public String toString() {
        return course.getCourseNumber() + " - " + type + " by " + instructor.getName() + " for " + hours + " hours";
    }
}

