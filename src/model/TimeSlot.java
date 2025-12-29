package model;

import java.time.LocalTime;

public class TimeSlot {
    private String day;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isLabSlot;

    public TimeSlot(String day, LocalTime startTime, LocalTime endTime, boolean isLabSlot) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isLabSlot = isLabSlot;
    }

    public String getDay() { return day; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public boolean isLabSlot() { return isLabSlot; }

    public boolean overlapsWith(TimeSlot other) {
        if (!this.day.equalsIgnoreCase(other.day)) return false;
        return !(this.endTime.isBefore(other.startTime) || this.startTime.isAfter(other.endTime));
    }

    @Override
    public String toString() {
        return day + " " + startTime + "-" + endTime + (isLabSlot ? " [LAB]" : " [LECTURE]");
    }
}

