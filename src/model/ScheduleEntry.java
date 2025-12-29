package model;

public class ScheduleEntry {
    private CourseComponent courseComponent;
    private Classroom classroom;
    private TimeSlot timeSlot;

    public ScheduleEntry(CourseComponent courseComponent, Classroom classroom, TimeSlot timeSlot) {
        this.courseComponent = courseComponent;
        this.classroom = classroom;
        this.timeSlot = timeSlot;
    }

    public CourseComponent getCourseComponent() { return courseComponent; }
    public Classroom getClassroom() { return classroom; }
    public TimeSlot getTimeSlot() { return timeSlot; }

    public boolean isValid() {
        if (courseComponent.getType() == TimeSlotType.LAB) {
            long duration = java.time.Duration.between(timeSlot.getStartTime(), timeSlot.getEndTime()).toMinutes();
            return duration == 120 && classroom.isLab();
        }
        return true;
    }

    @Override
    public String toString() {
        return courseComponent.toString() + " in Room: " + classroom.getId() + " at " + timeSlot.toString();
    }
}
