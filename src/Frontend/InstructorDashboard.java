package Frontend;

import Backend.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class InstructorDashboard extends javax.swing.JFrame {

    private Instructor currentInstructor;
    private DatabaseManager db = new DatabaseManager();
    private DefaultListModel<String> courseListModel;
    private DefaultListModel<String> lessonListModel;

    // GUI Components
    private JList<String> courseJList;
    private JList<String> lessonJList;
    private JLabel welcomeLabel;
    private JTextArea descriptionArea;

    public InstructorDashboard(Instructor instructor) {
        this.currentInstructor = instructor;
        initComponents();
        refreshCourseList();
        setTitle("Instructor Dashboard - " + currentInstructor.getUsername());
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 600));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- TOP: Welcome Message ---
        welcomeLabel = new JLabel("Welcome, Instructor " + currentInstructor.getUsername());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // --- LEFT: Course List ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Your Courses"));
        courseListModel = new DefaultListModel<>();
        courseJList = new JList<>(courseListModel);
        courseJList.addListSelectionListener(e -> {
            updateDescription();
            refreshLessonList();
        });
        leftPanel.add(new JScrollPane(courseJList), BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(200, 0));
        mainPanel.add(leftPanel, BorderLayout.WEST);

        // --- CENTER: Lessons & Description Split ---
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        // Top Center: Description
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBorder(BorderFactory.createTitledBorder("Course Description"));
        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        // Bottom Center: Lesson List
        JPanel lessonPanel = new JPanel(new BorderLayout());
        lessonPanel.setBorder(BorderFactory.createTitledBorder("Lessons in Selected Course"));
        lessonListModel = new DefaultListModel<>();
        lessonJList = new JList<>(lessonListModel);
        lessonPanel.add(new JScrollPane(lessonJList), BorderLayout.CENTER);

        centerPanel.add(descPanel);
        centerPanel.add(lessonPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- RIGHT: Action Buttons ---
        JPanel rightPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        rightPanel.setPreferredSize(new Dimension(180, 0));

        JButton btnCreate = new JButton("Create Course");
        JButton btnDeleteCourse = new JButton("Delete Course");
        JButton btnAddLesson = new JButton("Add Lesson");
        JButton btnDeleteLesson = new JButton("Delete Lesson");
        JButton btnViewStudents = new JButton("View Students");
        JButton btnLogout = new JButton("Logout");

        btnCreate.addActionListener(evt -> onCreateCourse());
        btnDeleteCourse.addActionListener(evt -> onDeleteCourse());
        btnAddLesson.addActionListener(evt -> onAddLesson());
        btnDeleteLesson.addActionListener(evt -> onDeleteLesson());
        btnViewStudents.addActionListener(evt -> onViewStudents());
        btnLogout.addActionListener(evt -> {
            new WelcomePage().setVisible(true);
            this.dispose();
        });

        rightPanel.add(btnCreate);
        rightPanel.add(btnDeleteCourse);
        rightPanel.add(new JSeparator());
        rightPanel.add(btnAddLesson);
        rightPanel.add(btnDeleteLesson);
        rightPanel.add(new JSeparator());
        rightPanel.add(btnViewStudents);
        rightPanel.add(btnLogout);

        mainPanel.add(rightPanel, BorderLayout.EAST);
        add(mainPanel);
    }

    private void refreshCourseList() {
        courseListModel.clear();
        ArrayList<Course> allCourses = db.loadCourses();
        for (Course c : allCourses) {
            if (c.getInstructorId().equals(currentInstructor.getUserid())) {
                courseListModel.addElement(c.getCourseId() + " - " + c.getTitle());
            }
        }
    }

    private void refreshLessonList() {
        lessonListModel.clear();
        String courseId = getSelectedCourseId();
        if (courseId != null) {
            Course c = db.getCourseById(courseId);
            if (c != null && c.getLessons() != null) {
                for (Lesson l : c.getLessons()) {
                    lessonListModel.addElement(l.getLessonId() + " - " + l.getTitle());
                }
            }
        }
    }

    private String getSelectedCourseId() {
        String selected = courseJList.getSelectedValue();
        return (selected == null) ? null : selected.split(" - ")[0];
    }

    private String getSelectedLessonId() {
        String selected = lessonJList.getSelectedValue();
        return (selected == null) ? null : selected.split(" - ")[0];
    }

    private void updateDescription() {
        String id = getSelectedCourseId();
        if (id != null) {
            Course c = db.getCourseById(id);
            descriptionArea.setText(c.getDescription());
        } else {
            descriptionArea.setText("");
        }
    }

    // --- NEW ACTIONS ---

    private void onDeleteLesson() {
        String lessonId = getSelectedLessonId();
        if (lessonId == null) {
            JOptionPane.showMessageDialog(this, "Please select a lesson to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete lesson " + lessonId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // This calls your existing method in Instructor.java
            if (currentInstructor.deleteLesson(lessonId)) {
                JOptionPane.showMessageDialog(this, "Lesson Deleted.");
                refreshLessonList(); // Update the UI
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting lesson.");
            }
        }
    }

    private void onCreateCourse() {
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextArea descField = new JTextArea(3, 20);
        Object[] message = {"ID:", idField, "Title:", titleField, "Desc:", new JScrollPane(descField)};
        if (JOptionPane.showConfirmDialog(null, message, "Create Course", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (currentInstructor.createCourse(idField.getText(), titleField.getText(), descField.getText())) {
                refreshCourseList();
            }
        }
    }

    private void onDeleteCourse() {
        String id = getSelectedCourseId();
        if (id != null && currentInstructor.deleteCourse(id)) {
            refreshCourseList();
            lessonListModel.clear();
        }
    }

    private void onAddLesson() {
        String courseId = getSelectedCourseId();
        if (courseId == null) return;

        JTextField lId = new JTextField();
        JTextField lTitle = new JTextField();
        JTextArea lContent = new JTextArea(5, 20);
        Object[] message = {"Lesson ID:", lId, "Title:", lTitle, "Content:", new JScrollPane(lContent)};

        if (JOptionPane.showConfirmDialog(null, message, "Add Lesson", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (currentInstructor.addLesson(courseId, lId.getText(), lTitle.getText(), lContent.getText())) {
                refreshLessonList();
            }
        }
    }

    private void onViewStudents() {
        String courseId = getSelectedCourseId();
        if (courseId == null) return;
        ArrayList<String> students = currentInstructor.viewEnrolledStudents(courseId);
        JOptionPane.showMessageDialog(this, students.isEmpty() ? "No students." : "Students:\n" + String.join("\n", students));
    }
}