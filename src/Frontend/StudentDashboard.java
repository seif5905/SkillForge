package Frontend;

import Backend.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class StudentDashboard extends javax.swing.JFrame {

    private Student currentStudent;
    private DatabaseManager db = new DatabaseManager();

    private DefaultListModel<String> enrolledListModel;
    private DefaultListModel<String> catalogListModel;
    private DefaultListModel<String> lessonListModel;

    private JList<String> enrolledJList;
    private JList<String> catalogJList;
    private JList<String> lessonJList;
    private JTextArea lessonContentArea;

    public StudentDashboard(Student student) {
        this.currentStudent = student;
        initComponents();
        refreshData();
        setTitle("Student Dashboard - " + currentStudent.getUsername());
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- TAB 1: MY COURSES (Study View) ---
        JPanel myCoursesPanel = new JPanel(new BorderLayout(10, 10));
        myCoursesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left: List of Enrolled Courses
        enrolledListModel = new DefaultListModel<>();
        enrolledJList = new JList<>(enrolledListModel);
        enrolledJList.setBorder(BorderFactory.createTitledBorder("My Enrolled Courses"));
        enrolledJList.addListSelectionListener(e -> refreshLessonList());

        // Center: Lesson List and Content
        JPanel studyPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        lessonListModel = new DefaultListModel<>();
        lessonJList = new JList<>(lessonListModel);
        lessonJList.setBorder(BorderFactory.createTitledBorder("Lessons"));
        lessonJList.addListSelectionListener(e -> updateLessonContent());

        lessonContentArea = new JTextArea();
        lessonContentArea.setEditable(false);
        lessonContentArea.setLineWrap(true);
        lessonContentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(lessonContentArea);
        contentScroll.setBorder(BorderFactory.createTitledBorder("Lesson Content"));

        studyPanel.add(new JScrollPane(lessonJList));
        studyPanel.add(contentScroll);

        myCoursesPanel.add(new JScrollPane(enrolledJList), BorderLayout.WEST);
        enrolledJList.setPreferredSize(new Dimension(200, 0));
        myCoursesPanel.add(studyPanel, BorderLayout.CENTER);

        // --- TAB 2: COURSE CATALOG (Enroll View) ---
        JPanel catalogPanel = new JPanel(new BorderLayout(10, 10));
        catalogPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        catalogListModel = new DefaultListModel<>();
        catalogJList = new JList<>(catalogListModel);
        JButton btnEnroll = new JButton("Enroll in Selected Course");
        btnEnroll.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEnroll.addActionListener(e -> onEnroll());

        catalogPanel.add(new JScrollPane(catalogJList), BorderLayout.CENTER);
        catalogPanel.add(btnEnroll, BorderLayout.SOUTH);

        // --- Add Tabs and Logout ---
        tabbedPane.addTab("My Learning", myCoursesPanel);
        tabbedPane.addTab("Find Courses", catalogPanel);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            new WelcomePage().setVisible(true);
            this.dispose();
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel(" Welcome, " + currentStudent.getUsername()), BorderLayout.WEST);
        topPanel.add(btnLogout, BorderLayout.EAST);

        this.setLayout(new BorderLayout());
        this.add(topPanel, BorderLayout.NORTH);
        this.add(tabbedPane, BorderLayout.CENTER);
    }

    private void refreshData() {
        // Refresh Catalog
        catalogListModel.clear();
        ArrayList<Course> allCourses = db.loadCourses();
        for (Course c : allCourses) {
            catalogListModel.addElement(c.getCourseId() + " - " + c.getTitle());
        }

        // Refresh My Courses
        enrolledListModel.clear();
        for (String courseId : currentStudent.getEnrolledCourses()) {
            Course c = db.getCourseById(courseId);
            if (c != null) {
                enrolledListModel.addElement(c.getCourseId() + " - " + c.getTitle());
            }
        }
    }

    private void refreshLessonList() {
        lessonListModel.clear();
        lessonContentArea.setText("");
        String selected = enrolledJList.getSelectedValue();
        if (selected != null) {
            String courseId = selected.split(" - ")[0];
            Course c = db.getCourseById(courseId);
            for (Lesson l : c.getLessons()) {
                lessonListModel.addElement(l.getLessonId() + " - " + l.getTitle());
            }
        }
    }

    private void updateLessonContent() {
        String selectedCourse = enrolledJList.getSelectedValue();
        String selectedLesson = lessonJList.getSelectedValue();

        if (selectedCourse != null && selectedLesson != null) {
            String courseId = selectedCourse.split(" - ")[0];
            String lessonId = selectedLesson.split(" - ")[0];
            Course c = db.getCourseById(courseId);
            for (Lesson l : c.getLessons()) {
                if (l.getLessonId().equals(lessonId)) {
                    lessonContentArea.setText(l.getContent());
                    break;
                }
            }
        }
    }

    private void onEnroll() {
        String selected = catalogJList.getSelectedValue();
        if (selected == null) return;

        String courseId = selected.split(" - ")[0];

        if (currentStudent.getEnrolledCourses().contains(courseId)) {
            JOptionPane.showMessageDialog(this, "You are already enrolled!");
            return;
        }

        if (currentStudent.enrollInCourse(courseId)) {
            JOptionPane.showMessageDialog(this, "Enrolled Successfully!");
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Enrollment failed.");
        }
    }
}