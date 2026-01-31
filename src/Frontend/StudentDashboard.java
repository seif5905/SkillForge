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
    private DefaultListModel<String> certModel; // Promoted to field for auto-refresh

    private JList<String> enrolledJList;
    private JList<String> catalogJList;
    private JList<String> lessonJList;
    private JTextArea lessonContentArea;

    public StudentDashboard(Student student) {
        this.currentStudent = student;
        initComponents();
        refreshData();
        refreshCertificates(); // Initial load of certificates
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
        JPanel studyPanel = new JPanel(new BorderLayout(10, 10));

        // Lesson list on the left side
        lessonListModel = new DefaultListModel<>();
        lessonJList = new JList<>(lessonListModel);
        lessonJList.setBorder(BorderFactory.createTitledBorder("Lessons"));
        lessonJList.addListSelectionListener(e -> updateLessonContent());

        // Content and buttons on the right side
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));

        lessonContentArea = new JTextArea();
        lessonContentArea.setEditable(false);
        lessonContentArea.setLineWrap(true);
        lessonContentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(lessonContentArea);
        contentScroll.setBorder(BorderFactory.createTitledBorder("Lesson Content"));

        // Button panel - NOW INCLUDES GENERATE CERTIFICATE
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5)); // Changed to 3 rows

        JButton btnTakeQuiz = new JButton("Take Quiz");
        btnTakeQuiz.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTakeQuiz.addActionListener(e -> onTakeQuiz());

        JButton btnMarkComplete = new JButton("Mark Lesson as Completed");
        btnMarkComplete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnMarkComplete.addActionListener(e -> onMarkLessonComplete());

        // MOVED HERE: Generate Certificate Button
        JButton btnGenerateCert = new JButton("Generate Course Certificate");
        btnGenerateCert.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGenerateCert.setForeground(new Color(0, 102, 51)); // Dark green for emphasis
        btnGenerateCert.addActionListener(e -> onGenerateCertificate());

        buttonPanel.add(btnTakeQuiz);
        buttonPanel.add(btnMarkComplete);
        buttonPanel.add(btnGenerateCert); // Added button here

        contentPanel.add(contentScroll, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        studyPanel.add(new JScrollPane(lessonJList), BorderLayout.WEST);
        studyPanel.add(contentPanel, BorderLayout.CENTER);

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

        // --- TAB 3: CERTIFICATES ---
        JPanel certificatesPanel = new JPanel(new BorderLayout(10, 10));
        certificatesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        certModel = new DefaultListModel<>();
        JList<String> certList = new JList<>(certModel);
        certList.setBorder(BorderFactory.createTitledBorder("My Certificates"));

        JPanel certButtonPanel = new JPanel(new FlowLayout());

        // Removed "Generate Certificate" button from here

        JButton btnRefreshCert = new JButton("Refresh Certificates");
        btnRefreshCert.addActionListener(e -> refreshCertificates());

        certButtonPanel.add(btnRefreshCert);

        certificatesPanel.add(new JScrollPane(certList), BorderLayout.CENTER);
        certificatesPanel.add(certButtonPanel, BorderLayout.SOUTH);

        // --- Add Tabs ---
        tabbedPane.addTab("My Learning", myCoursesPanel);
        tabbedPane.addTab("Find Courses", catalogPanel);
        tabbedPane.addTab("My Certificates", certificatesPanel);

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

    // --- NEW METHOD: REFRESH CERTIFICATES ---
    private void refreshCertificates() {
        certModel.clear();
        for (Certificate cert : currentStudent.getCertificates()) {
            certModel.addElement("Certificate #" + cert.getCertificateId() + " - " +
                    cert.getCourseTitle() + " (Course: " + cert.getCourseId() + ")");
        }
    }

    // --- MARK LESSON AS COMPLETED ---
    private void onMarkLessonComplete() {
        String selectedLesson = lessonJList.getSelectedValue();

        if (selectedLesson == null) {
            JOptionPane.showMessageDialog(this, "Please select a lesson first.");
            return;
        }

        String lessonId = selectedLesson.split(" - ")[0];

        // Check if already completed
        if (currentStudent.getCompletedLessons().contains(lessonId)) {
            JOptionPane.showMessageDialog(this,
                    "This lesson is already marked as completed!",
                    "Already Completed",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Try to mark as complete
        if (currentStudent.markLessonAsCompleted(lessonId)) {
            JOptionPane.showMessageDialog(this,
                    "Lesson marked as completed!\n\nGreat job! 🎉",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshLessonList(); // Refresh to show completion status
        } else {
            JOptionPane.showMessageDialog(this,
                    "Cannot mark lesson as completed.\n\n" +
                            "You must pass all quizzes in this lesson first!",
                    "Incomplete Quizzes",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // --- GENERATE CERTIFICATE ---
    private void onGenerateCertificate() {
        // Now checks the enrolled list since button is in My Learning tab
        String selectedCourse = enrolledJList.getSelectedValue();

        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course from the 'My Enrolled Courses' list on the left.",
                    "No Course Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseId = selectedCourse.split(" - ")[0];

        // Check if already has certificate
        for (Certificate cert : currentStudent.getCertificates()) {
            if (cert.getCourseId().equalsIgnoreCase(courseId)) {
                JOptionPane.showMessageDialog(this,
                        "You already have a certificate for this course!\n\n" +
                                "Check the 'My Certificates' tab.",
                        "Certificate Exists",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        // Try to generate certificate
        if (currentStudent.generateCertificate(courseId)) {
            Course course = db.getCourseById(courseId);
            refreshCertificates(); // FIX: Auto-update the certificate tab

            JOptionPane.showMessageDialog(this,
                    "🎓 Congratulations! 🎓\n\n" +
                            "Certificate generated successfully for: " + course.getTitle() + "\n" +
                            "You can view it in the 'My Certificates' tab.",
                    "Certificate Generated",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Get course details for better error message
            ArrayList<Lesson> allLessons = db.getLessonsForCourse(courseId);
            int totalLessons = allLessons.size();
            int completedCount = 0;

            for (Lesson lesson : allLessons) {
                if (currentStudent.getCompletedLessons().contains(lesson.getLessonId())) {
                    completedCount++;
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Cannot generate certificate yet.\n\n" +
                            "You must complete ALL lessons in this course first!\n\n" +
                            "Progress: " + completedCount + " / " + totalLessons + " lessons completed",
                    "Course Incomplete",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // --- QUIZ TAKING FUNCTIONALITY ---

    private void onTakeQuiz() {
        String selectedLesson = lessonJList.getSelectedValue();

        if (selectedLesson == null) {
            JOptionPane.showMessageDialog(this, "Please select a lesson first.");
            return;
        }

        String lessonId = selectedLesson.split(" - ")[0];
        Lesson lesson = db.getLessonById(lessonId);

        if (lesson == null || lesson.getQuizzes().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No quizzes available for this lesson.");
            return;
        }

        // If multiple quizzes, let student choose
        ArrayList<Quiz> quizzes = lesson.getQuizzes();
        Quiz selectedQuiz;

        if (quizzes.size() == 1) {
            selectedQuiz = quizzes.get(0);
        } else {
            String[] quizOptions = new String[quizzes.size()];
            for (int i = 0; i < quizzes.size(); i++) {
                quizOptions[i] = quizzes.get(i).getQuizId() + " - " + quizzes.get(i).getTitle();
            }

            String choice = (String) JOptionPane.showInputDialog(
                    this,
                    "Select a quiz:",
                    "Available Quizzes",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    quizOptions,
                    quizOptions[0]
            );

            if (choice == null) return;

            String quizId = choice.split(" - ")[0];
            selectedQuiz = null;
            for (Quiz q : quizzes) {
                if (q.getQuizId().equalsIgnoreCase(quizId)) {
                    selectedQuiz = q;
                    break;
                }
            }
        }

        if (selectedQuiz != null) {
            openQuizDialog(selectedQuiz);
        }
    }

    private void openQuizDialog(Quiz quiz) {
        JDialog quizDialog = new JDialog(this, "Quiz: " + quiz.getTitle(), true);
        quizDialog.setSize(700, 600);
        quizDialog.setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(3, 1));
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        headerPanel.add(new JLabel("Quiz: " + quiz.getTitle(), SwingConstants.CENTER));
        headerPanel.add(new JLabel("Passing Score: " + quiz.getPassingScore() + "%", SwingConstants.CENTER));
        headerPanel.add(new JLabel("Total Questions: " + quiz.getQuestions().size(), SwingConstants.CENTER));

        Font headerFont = new Font("Segoe UI", Font.BOLD, 14);
        for (Component c : headerPanel.getComponents()) {
            c.setFont(headerFont);
        }

        // Questions Panel
        JPanel questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        questionsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        ArrayList<Question> questions = quiz.getQuestions();
        JRadioButton[][] answerButtons = new JRadioButton[questions.size()][4];

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);

            // Question panel
            JPanel questionPanel = new JPanel();
            questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
            questionPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY),
                    new EmptyBorder(10, 10, 10, 10)
            ));

            JLabel questionLabel = new JLabel((i + 1) + ". " + q.getQuestionTitle());
            questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            questionPanel.add(questionLabel);
            questionPanel.add(Box.createVerticalStrut(10));

            // Answer choices
            ButtonGroup bg = new ButtonGroup();
            ArrayList<String> choices = q.getChoices();

            for (int j = 0; j < choices.size() && j < 4; j++) {
                answerButtons[i][j] = new JRadioButton(choices.get(j));
                bg.add(answerButtons[i][j]);
                questionPanel.add(answerButtons[i][j]);
            }

            questionsPanel.add(questionPanel);
            questionsPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Submit Button
        JButton btnSubmit = new JButton("Submit Quiz");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubmit.addActionListener(e -> {
            int score = 0;
            int totalQuestions = questions.size();

            for (int i = 0; i < questions.size(); i++) {
                for (int j = 0; j < 4; j++) {
                    if (answerButtons[i][j] != null && answerButtons[i][j].isSelected()) {
                        if (j + 1 == questions.get(i).getCorrectChoice()) {
                            score++;
                        }
                        break;
                    }
                }
            }

            double percentage = (score * 100.0) / totalQuestions;
            String passingScoreStr = quiz.getPassingScore();
            double passingScore = 0;

            try {
                passingScore = Double.parseDouble(passingScoreStr);
            } catch (NumberFormatException ex) {
                passingScore = 50;
            }

            boolean passed = percentage >= passingScore;

            // SAVE RESULT
            String selectedCourse = enrolledJList.getSelectedValue();
            String selectedLesson = lessonJList.getSelectedValue();

            if (selectedCourse != null && selectedLesson != null) {
                String courseId = selectedCourse.split(" - ")[0];
                String lessonId = selectedLesson.split(" - ")[0];
                currentStudent.saveQuizResult(courseId, lessonId, quiz.getQuizId(), score, passed);
            }

            String resultMessage = String.format(
                    "Your Score: %d/%d (%.1f%%)\n\n%s",
                    score, totalQuestions, percentage,
                    passed ? "Congratulations! You passed! ✅" : "Sorry, you did not pass. Try again! ❌"
            );

            JOptionPane.showMessageDialog(quizDialog, resultMessage,
                    passed ? "Quiz Passed!" : "Quiz Failed",
                    passed ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

            quizDialog.dispose();
            refreshLessonList(); // Refresh to update quiz status indicators
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSubmit);

        quizDialog.add(headerPanel, BorderLayout.NORTH);
        quizDialog.add(scrollPane, BorderLayout.CENTER);
        quizDialog.add(buttonPanel, BorderLayout.SOUTH);

        quizDialog.setLocationRelativeTo(this);
        quizDialog.setVisible(true);
    }

    // --- REFRESH DATA METHODS ---

    private void refreshData() {
        // Refresh Catalog
        catalogListModel.clear();
        ArrayList<Course> allCourses = currentStudent.getAvailableCourses();
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
            if (c != null) {
                ArrayList<Lesson> lessons = new ArrayList<>();
                for (String lessonId : c.getLessons()) {
                    Lesson l = db.getLessonById(lessonId);
                    if (l != null) {
                        lessons.add(l);
                    }
                }
                for (Lesson l : lessons) {
                    String quizIndicator = (l.getQuizzes() != null && !l.getQuizzes().isEmpty()) ? " [Has Quiz]" : "";
                    String completedIndicator = currentStudent.getCompletedLessons().contains(l.getLessonId()) ? " ✅" : "";
                    lessonListModel.addElement(l.getLessonId() + " - " + l.getTitle() + quizIndicator + completedIndicator);
                }
            }
        }
    }

    private void updateLessonContent() {
        String selectedLesson = lessonJList.getSelectedValue();

        if (selectedLesson != null) {
            String lessonId = selectedLesson.split(" - ")[0];
            Lesson l = db.getLessonById(lessonId);
            if (l != null) {
                lessonContentArea.setText(l.getContent());
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