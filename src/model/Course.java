package model;

public class Course {
    private String courseNumber;
    private String name;
    private int numOfStudents;
    private int lectureHours;
    private int labHours;
    private String sectionName;
    private String midSemExamDate;
    private String midSemExamTime;
    private String compreExamDate;
    private String compreExamTime;

    public Course(String courseNumber, String name, int numOfStudents, int lectureHours, int labHours, String sectionName) {
        this.courseNumber = courseNumber;
        this.name = name;
        this.numOfStudents = numOfStudents;
        this.lectureHours = lectureHours;
        this.labHours = labHours;
        this.sectionName = sectionName;
    }

    public String getCourseNumber() { return courseNumber; }
    public String getName() { return name; }
    public int getNumOfStudents() { return numOfStudents; }
    public int getLectureHours() { return lectureHours; }
    public int getLabHours() { return labHours; }
    public String getSectionName() { return sectionName; }
    public String getMidSemExamDate() { return midSemExamDate; }
    public void setMidSemExamDate(String date) { this.midSemExamDate = date; }
    public String getMidSemExamTime() { return midSemExamTime; }
    public void setMidSemExamTime(String time) { this.midSemExamTime = time; }
    public String getCompreExamDate() { return compreExamDate; }
    public void setCompreExamDate(String date) { this.compreExamDate = date; }
    public String getCompreExamTime() { return compreExamTime; }
    public void setCompreExamTime(String time) { this.compreExamTime = time; }

    public int getTotalHours() {
        return lectureHours + labHours;
    }
// --- Added Setter Methods for editable fields ---
public void setName(String name) { this.name = name; }
public void setNumOfStudents(int numOfStudents) { this.numOfStudents = numOfStudents; }
public void setLectureHours(int lectureHours) { this.lectureHours = lectureHours; }
public void setLabHours(int labHours) { this.labHours = labHours; }
public void setSectionName(String sectionName) { this.sectionName = sectionName; }
    @Override
    public String toString() {
        return courseNumber + ": " + name + " [" + lectureHours + "L, " + labHours + "P] with " + numOfStudents + " students";
    }
}
