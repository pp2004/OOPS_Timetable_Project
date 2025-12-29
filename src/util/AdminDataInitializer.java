package util;

import model.*;

import java.time.LocalTime;
import java.util.*;

public class AdminDataInitializer {
    public static List<Course> generateCourses() {
        List<Course> courseList = new ArrayList<>();

        courseList.add(new Course("EEE F311", "Communication Systems", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("MATH F212", "Optimization", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("EEE F313", "Analog & Digital VLSI Design", rand(80,180), 3, 1, "A1"));

        courseList.add(new Course("EEE F312", "Power Systems", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("EEE F342", "Power Electronics", rand(80,180), 3, 1, "A1"));
        courseList.add(new Course("EEE F341", "Analog Electronics", rand(80,180), 3, 1, "A1"));

        courseList.add(new Course("BITS F312", "Neural Networks", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("BITS F415", "Introduction To MEMS", rand(80,180), 3, 1, "A1"));
        courseList.add(new Course("CS F213", "Object Oriented Programming", rand(80,180), 3, 1, "A1"));
        courseList.add(new Course("CS F342", "Computer Architecture", rand(80,180), 3, 1, "A1"));
        courseList.add(new Course("CS F372", "Operating Systems", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("EEE F245", "Control System Lab", rand(80,180), 0, 1, "A1"));
        courseList.add(new Course("EEE F246", "Circuits Lab", rand(80,180), 0, 2, "A1"));

        courseList.add(new Course("BITS F232", "Data Structures", rand(80,180), 3, 1, "A1"));
        courseList.add(new Course("CS F407", "Artificial Intelligence", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("BITS F464", "Machine Learning", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("CS F212", "Database Systems", rand(80,180), 3, 1, "A1"));
        courseList.add(new Course("CS F301", "Programming Languages", rand(80,180), 2, 0, "A1"));
        courseList.add(new Course("CS F303", "Computer Networks", rand(80,180), 3, 1, "A1"));

        courseList.add(new Course("CS F320", "Data Science", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("MATH F432", "Applied Stats", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("BITS F453", "Comp Learning Theory", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("BITS F454", "Bio-Inspired AI", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("CS F317", "Reinforcement Learning", rand(80,180), 3, 0, "A1"));

        courseList.add(new Course("HSS F334", "Bhagavad Gita", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("HSS F335", "Literary Criticism", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("HSS F336", "Modern Fiction", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("HSS F337", "Literary Forms", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("HSS F338", "Indian Literature", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("HSS F365", "Sustainable Happiness", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("HSS F368", "Asian Cinemas", rand(80,180), 3, 0, "A1"));
        courseList.add(new Course("HSS F369", "Caste and Gender", rand(80,180), 3, 0, "A1"));

        return courseList;
    }

    public static List<Instructor> generateInstructors(int count) {
        List<Instructor> instructors = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            instructors.add(new Instructor(100 + i, "Instructor " + i));
        }
        return instructors;
    }

    public static List<Classroom> generateClassrooms() {
        List<Classroom> rooms = new ArrayList<>();
        char[] theoryBlocks = {'D', 'G', 'F'};
        char[] labBlocks = {'I', 'J'};

        for (char block : theoryBlocks) {
            for (int floor = 0; floor <= 2; floor++) {
                for (int room = 1; room <= 6; room++) {
                    String roomId = block + String.valueOf(floor) + "0" + room;
                    rooms.add(new Classroom(roomId, rand(80,180), true, rand(20,50), false));
                }
            }
        }
        for (char block : labBlocks) {
            for (int floor = 0; floor <= 2; floor++) {
                for (int room = 1; room <= 6; room++) {
                    String roomId = block + String.valueOf(floor) + "0" + room;
                    rooms.add(new Classroom(roomId, rand(80,180), false, rand(40,100), true));
                }
            }
        }
        return rooms;
    }

    private static int rand(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }
}
