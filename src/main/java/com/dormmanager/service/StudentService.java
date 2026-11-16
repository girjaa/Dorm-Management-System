package com.dormmanager.service;

import com.dormmanager.exception.StudentNotFoundException;
import com.dormmanager.model.Room;
import com.dormmanager.model.Student;
import com.dormmanager.validation.InputValidator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing students.
 */
public class StudentService {

    private static final String STUDENTS_FILE = "students.dat";

    private final List<Student> students;
    private final FileStorageService fileStorage;

    public StudentService(FileStorageService fileStorage) {
        this.students = new ArrayList<>();
        this.fileStorage = fileStorage;
    }

    /**
     * Registers a new student and assigns them to a room.
     */
    public Student registerStudent(String studentId, String name, String email, Room room) {
        InputValidator.validateStudentId(studentId);
        InputValidator.validateNotEmpty(name, "Student name");
        InputValidator.validateEmail(email);

        Student student = new Student(studentId, name, email, room);
        if (!student.validate()) {
            throw new IllegalArgumentException("Student validation failed.");
        }

        students.add(student);
        if (room != null) {
            room.addOccupant(student);
        }
        return student;
    }

    /**
     * Finds a student by ID.
     *
     * @throws StudentNotFoundException if not found
     */
    public Student findStudentById(String id) {
        return students.stream()
                .filter(s -> s.getStudentId().equals(id))
                .findFirst()
                .orElseThrow(() -> new StudentNotFoundException("Student not found: " + id));
    }

    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(students);
    }

    /**
     * Removes a student by ID.
     */
    public boolean removeStudent(String id) {
        Student student = findStudentById(id);
        if (student.getRoom() != null) {
            student.getRoom().removeOccupant(id);
        }
        return students.remove(student);
    }

    public void saveAll() {
        List<String> lines = students.stream().map(Student::toCsv).collect(Collectors.toList());
        fileStorage.saveToFile(STUDENTS_FILE, lines);
    }

    public void loadAll(Map<String, Room> roomMap) {
        students.clear();
        List<String> lines = fileStorage.loadFromFile(STUDENTS_FILE);
        for (String line : lines) {
            if (!line.isBlank()) {
                String[] parts = line.split("\\|");
                String roomId = parts.length > 3 ? parts[3].trim() : "NONE";
                Room room = roomId.equals("NONE") ? null : roomMap.get(roomId);
                Student student = Student.fromCsv(line, room);
                students.add(student);
                if (room != null) {
                    room.addOccupant(student);
                }
            }
        }
    }
}
