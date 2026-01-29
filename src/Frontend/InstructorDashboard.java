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
        setMinimumSize(new Dimension(1000, 700)); // Increased height for more buttons

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

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

        // --- CENTER: Lessons & Description ---
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBorder(BorderFactory.createTitledBorder("Course Description"));
        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        JPanel lessonPanel = new JPanel(new BorderLayout());
        lessonPanel.setBorder(BorderFactory.createTitledBorder("Lessons in Selected Course"));
        lessonListModel = new DefaultListModel<>();
        lessonJList = new JList<>(lessonListModel);
        lessonPanel.add(new JScrollPane(lessonJList), BorderLayout.CENTER);

        centerPanel.add(descPanel);
        centerPanel.add(lessonPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- RIGHT: Action Buttons ---
        // Changed to 10 rows to accommodate Edit buttons
        JPanel rightPanel = new JPanel(new GridLayout(10, 1, 5, 5));
        rightPanel.setPreferredSize(new Dimension(180, 0));

        JButton btnCreate = new JButton("Create Course");
        JButton btnEditCourse = new JButton("Edit Course");
        JButton btnDeleteCourse = new JButton("Delete Course");
        JButton btnAddLesson = new JButton("Add Lesson");
        JButton btnEditLesson = new JButton("Edit Lesson");
        JButton btnDeleteLesson = new JButton("Delete Lesson");
        JButton btnViewStudents = new JButton("View Students");
        JButton btnLogout = new JButton("Logout");

        // Action Listeners
        btnCreate.addActionListener(evt -> onCreateCourse());
        btnEditCourse.addActionListener(evt -> onEditCourse());
        btnDeleteCourse.addActionListener(evt -> onDeleteCourse());
        btnAddLesson.addActionListener(evt -> onAddLesson());
        btnEditLesson.addActionListener(evt -> onEditLesson());
        btnDeleteLesson.addActionListener(evt -> onDeleteLesson());
        btnViewStudents.addActionListener(evt -> onViewStudents());
        btnLogout.addActionListener(evt -> {
            new WelcomePage().setVisible(true);
            this.dispose();
        });

        rightPanel.add(btnCreate);
        rightPanel.add(btnEditCourse);
        rightPanel.add(btnDeleteCourse);
        rightPanel.add(new JSeparator());
        rightPanel.add(btnAddLesson);
        rightPanel.add(btnEditLesson);
        rightPanel.add(btnDeleteLesson);
        rightPanel.add(new JSeparator());
        rightPanel.add(btnViewStudents);
        rightPanel.add(btnLogout);

        mainPanel.add(rightPanel, BorderLayout.EAST);
        add(mainPanel);
    }

    // --- NEW EDIT LOGIC ---

    private void onEditCourse() {
        String courseId = getSelectedCourseId();
        if (courseId == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to edit.");
            return;
        }

        Course course = db.getCourseById(courseId);
        JTextField titleField = new JTextField(course.getTitle());
        JTextArea descField = new JTextArea(course.getDescription(), 5, 20);

        Object[] message = {
                "Course ID (Cannot Change): " + courseId,
                "New Title:", titleField,
                "New Description:", new JScrollPane(descField)
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Edit Course", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            ArrayList<Course> courses = db.loadCourses();
            for (Course c : courses) {
                if (c.getCourseId().equalsIgnoreCase(courseId)) {
                    c.setTitle(titleField.getText());
                    c.setDescription(descField.getText());
                    break;
                }
            }
            db.saveCourses(courses);
            refreshCourseList();
            updateDescription();
            JOptionPane.showMessageDialog(this, "Course updated!");
        }
    }

    private void onEditLesson() {
        String courseId = getSelectedCourseId();
        String lessonId = getSelectedLessonId();

        if (courseId == null || lessonId == null) {
            JOptionPane.showMessageDialog(this, "Please select both a course and a lesson to edit.");
            return;
        }

        Course course = db.getCourseById(courseId);
        Lesson targetLesson = null;
        for (Lesson l : course.getLessons()) {
            if (l.getLessonId().equalsIgnoreCase(lessonId)) {
                targetLesson = l;
                break;
            }
        }

        if (targetLesson == null) return;

        JTextField titleField = new JTextField(targetLesson.getTitle());
        JTextArea contentField = new JTextArea(targetLesson.getContent(), 8, 20);

        Object[] message = {
                "Lesson ID: " + lessonId,
                "New Title:", titleField,
                "New Content:", new JScrollPane(contentField)
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Edit Lesson", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            ArrayList<Course> allCourses = db.loadCourses();
            for (Course c : allCourses) {
                if (c.getCourseId().equalsIgnoreCase(courseId)) {
                    for (Lesson l : c.getLessons()) {
                        if (l.getLessonId().equalsIgnoreCase(lessonId)) {
                            l.setTitle(titleField.getText());
                            l.setContent(contentField.getText());
                            break;
                        }
                    }
                }
            }
            db.saveCourses(allCourses);
            refreshLessonList();
            JOptionPane.showMessageDialog(this, "Lesson updated!");
        }
    }

    // --- REFRESH AND HELPER METHODS ---

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