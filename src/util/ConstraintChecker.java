package util;

import model.ScheduleEntry;
import model.TimeSlot;

public class ConstraintChecker {

    public static boolean checkInstructorConflict(ScheduleEntry entry1, ScheduleEntry entry2) {
        if (!entry1.getTimeSlot().overlapsWith(entry2.getTimeSlot())) {
            return false;
        }

        return entry1.getCourseComponent().getInstructor().getId()
                == entry2.getCourseComponent().getInstructor().getId();
    }

    public static boolean checkClassroomConflict(ScheduleEntry entry1, ScheduleEntry entry2) {
        if (!entry1.getTimeSlot().overlapsWith(entry2.getTimeSlot())) {
            return false;
        }

        return entry1.getClassroom().getId().equals(entry2.getClassroom().getId());
    }

    public static boolean checkCourseOverlap(ScheduleEntry entry1, ScheduleEntry entry2) {
        if (!entry1.getTimeSlot().overlapsWith(entry2.getTimeSlot())) {
            return false;
        }

        // Here you may define actual course-group-based overlap rules like OS vs OOP
        String c1 = entry1.getCourseComponent().getCourse().getCourseNumber();
        String c2 = entry2.getCourseComponent().getCourse().getCourseNumber();

        return isClashingPair(c1, c2);
    }

    private static boolean isClashingPair(String course1, String course2) {
        // Example: OOP and OS should not overlap
        return (course1.equals("CS F213") && course2.equals("CS F372"))
            || (course1.equals("CS F372") && course2.equals("CS F213"));
    }
}

