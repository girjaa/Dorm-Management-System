package com.dormmanager.model;

import com.dormmanager.interfaces.Manageable;

/**
 * Represents a student living in the dormitory.
 */
public class Student implements Manageable {

    private String studentId;
    private String name;
    private String email;
    private Room room;

    /**
     * Constructs a new Student.
     *
     * @param studentId the unique student ID
     * @param name      the student's full name
     * @param email     the student's email address
     * @param room      the room the student is assigned to (may be null)
     */
    public Student(String studentId, String name, String email, Room room) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.room = room;
    }

    // --- Manageable ---

    @Override
    public String getId() {
        return studentId;
    }

    @Override
    public boolean validate() {
        return studentId != null && !studentId.isBlank()
                && name != null && !name.isBlank();
    }

    // --- CSV serialization ---

    /**
     * Serializes to a pipe-delimited CSV line.
     * Format: studentId|name|email|roomId
     */
    public String toCsv() {
        String roomId = (room != null) ? room.getId() : "NONE";
        return studentId + "|" + name + "|" + email + "|" + roomId;
    }

    /**
     * Deserializes from a pipe-delimited CSV line.
     *
     * @param line the CSV line
     * @param room the Room object (resolved externally), may be null
     * @return the Student object
     */
    public static Student fromCsv(String line, Room room) {
        String[] parts = line.split("\\|");
        return new Student(parts[0].trim(), parts[1].trim(), parts[2].trim(), room);
    }

    // --- Getters and setters ---

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    @Override
    public String toString() {
        return "Student{id='" + studentId + "', name='" + name + "', room=" +
                (room != null ? room.getId() : "none") + "}";
    }
}
