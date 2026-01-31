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
        setMinimumSize(new Dimension(1000, 700));

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
        // Increased to 12 rows to accommodate quiz buttons
        JPanel rightPanel = new JPanel(new GridLayout(12, 1, 5, 5));
        rightPanel.setPreferredSize(new Dimension(180, 0));

        JButton btnCreate = new JButton("Create Course");
        JButton btnEditCourse = new JButton("Edit Course");
        JButton btnDeleteCourse = new JButton("Delete Course");
        JButton btnAddLesson = new JButton("Add Lesson");
        JButton btnEditLesson = new JButton("Edit Lesson");
        JButton btnDeleteLesson = new JButton("Delete Lesson");
        JButton btnAddQuiz = new JButton("Add Quiz");
        JButton btnManageQuiz = new JButton("Manage Quiz");
        JButton btnViewStudents = new JButton("View Students");
        JButton btnLogout = new JButton("Logout");

        // Action Listeners
        btnCreate.addActionListener(evt -> onCreateCourse());
        btnEditCourse.addActionListener(evt -> onEditCourse());
        btnDeleteCourse.addActionListener(evt -> onDeleteCourse());
        btnAddLesson.addActionListener(evt -> onAddLesson());
        btnEditLesson.addActionListener(evt -> onEditLesson());
        btnDeleteLesson.addActionListener(evt -> onDeleteLesson());
        btnAddQuiz.addActionListener(evt -> onAddQuiz());
        btnManageQuiz.addActionListener(evt -> onManageQuiz());
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
        rightPanel.add(btnAddQuiz);
        rightPanel.add(btnManageQuiz);
        rightPanel.add(btnViewStudents);
        rightPanel.add(btnLogout);

        mainPanel.add(rightPanel, BorderLayout.EAST);
        add(mainPanel);
    }

    // --- QUIZ MANAGEMENT METHODS ---

    private void onAddQuiz() {
        String courseId = getSelectedCourseId();
        String lessonId = getSelectedLessonId();

        if (courseId == null || lessonId == null) {
            JOptionPane.showMessageDialog(this, "Please select both a course and a lesson to add a quiz.");
            return;
        }

        JTextField quizIdField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField passingScoreField = new JTextField();

        Object[] message = {
                "Quiz ID:", quizIdField,
                "Quiz Title:", titleField,
                "Passing Score:", passingScoreField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add Quiz to Lesson", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            if (currentInstructor.addQuiz(courseId, lessonId, quizIdField.getText(),
                    passingScoreField.getText(), titleField.getText())) {
                JOptionPane.showMessageDialog(this, "Quiz added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add quiz.");
            }
        }
    }

    private void onManageQuiz() {
        String courseId = getSelectedCourseId();
        String lessonId = getSelectedLessonId();

        if (courseId == null || lessonId == null) {
            JOptionPane.showMessageDialog(this, "Please select both a course and a lesson.");
            return;
        }

        Lesson lesson = db.getLessonById(lessonId);
        if (lesson == null || lesson.getQuizzes().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No quizzes found in this lesson.");
            return;
        }

        // Show quiz selection dialog
        ArrayList<Quiz> quizzes = lesson.getQuizzes();
        String[] quizOptions = new String[quizzes.size()];
        for (int i = 0; i < quizzes.size(); i++) {
            quizOptions[i] = quizzes.get(i).getQuizId() + " - " + quizzes.get(i).getTitle();
        }

        String selectedQuiz = (String) JOptionPane.showInputDialog(
                this,
                "Select a quiz to manage:",
                "Manage Quiz",
                JOptionPane.QUESTION_MESSAGE,
                null,
                quizOptions,
                quizOptions[0]
        );

        if (selectedQuiz != null) {
            String quizId = selectedQuiz.split(" - ")[0];
            openQuizManagementDialog(courseId, lessonId, quizId);
        }
    }

    private void openQuizManagementDialog(String courseId, String lessonId, String quizId) {
        JDialog dialog = new JDialog(this, "Manage Quiz: " + quizId, true);
        dialog.setSize(700, 550);
        dialog.setLayout(new BorderLayout(10, 10));

        Lesson lesson = db.getLessonById(lessonId);
        Quiz selectedQuiz = null;
        for (Quiz q : lesson.getQuizzes()) {
            if (q.getQuizId().equalsIgnoreCase(quizId)) {
                selectedQuiz = q;
                break;
            }
        }

        if (selectedQuiz == null) return;

        final Quiz quiz = selectedQuiz; // For lambda usage

        // Top panel with quiz info
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Quiz Information"));
        infoPanel.add(new JLabel("Quiz ID:"));
        infoPanel.add(new JLabel(quiz.getQuizId()));
        infoPanel.add(new JLabel("Title:"));
        infoPanel.add(new JLabel(quiz.getTitle()));
        infoPanel.add(new JLabel("Passing Score:"));
        infoPanel.add(new JLabel(quiz.getPassingScore()));

        // Center panel with questions list
        DefaultListModel<String> questionModel = new DefaultListModel<>();
        JList<String> questionList = new JList<>(questionModel);

        ArrayList<Question> questions = quiz.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            questionModel.addElement(q.getQuestionId() + " - " + q.getQuestionTitle());
        }

        JScrollPane questionScroll = new JScrollPane(questionList);
        questionScroll.setBorder(BorderFactory.createTitledBorder("Questions (" + questions.size() + ")"));

        // Bottom panel with buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAddQuestion = new JButton("Add Question");
        JButton btnEditQuestion = new JButton("Edit Question");
        JButton btnDeleteQuestion = new JButton("Delete Question");
        JButton btnDeleteQuiz = new JButton("Delete Quiz");
        JButton btnClose = new JButton("Close");

        btnAddQuestion.addActionListener(e -> {
            addQuestionDialog(courseId, lessonId, quizId);
            dialog.dispose();
            openQuizManagementDialog(courseId, lessonId, quizId); // Refresh
        });

        btnEditQuestion.addActionListener(e -> {
            String selectedQuestion = questionList.getSelectedValue();
            if (selectedQuestion == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a question to edit.");
                return;
            }
            String questionId = selectedQuestion.split(" - ")[0];
            editQuestionDialog(courseId, lessonId, quizId, questionId);
            dialog.dispose();
            openQuizManagementDialog(courseId, lessonId, quizId); // Refresh
        });

        btnDeleteQuestion.addActionListener(e -> {
            String selectedQuestion = questionList.getSelectedValue();
            if (selectedQuestion == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a question to delete.");
                return;
            }

            String questionId = selectedQuestion.split(" - ")[0];
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to delete question: " + questionId + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (currentInstructor.deleteQuestion(courseId, lessonId, quizId, questionId)) {
                    JOptionPane.showMessageDialog(dialog, "Question deleted successfully!");
                    dialog.dispose();
                    openQuizManagementDialog(courseId, lessonId, quizId); // Refresh
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to delete question.");
                }
            }
        });

        btnDeleteQuiz.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to delete this entire quiz?",
                    "Confirm Delete Quiz",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (currentInstructor.deleteQuiz(courseId, lessonId, quizId)) {
                    JOptionPane.showMessageDialog(dialog, "Quiz deleted successfully!");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to delete quiz.");
                }
            }
        });

        btnClose.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnAddQuestion);
        buttonPanel.add(btnEditQuestion);
        buttonPanel.add(btnDeleteQuestion);
        buttonPanel.add(btnDeleteQuiz);
        buttonPanel.add(btnClose);

        dialog.add(infoPanel, BorderLayout.NORTH);
        dialog.add(questionScroll, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addQuestionDialog(String courseId, String lessonId, String quizId) {
        JDialog dialog = new JDialog(this, "Add Question", true);
        dialog.setSize(500, 450);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Question ID
        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idPanel.add(new JLabel("Question ID:"));
        JTextField questionIdField = new JTextField(20);
        idPanel.add(questionIdField);

        // Question Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(new JLabel("Question:"));
        JTextField questionTitleField = new JTextField(30);
        titlePanel.add(questionTitleField);

        // Choices
        JPanel choicesPanel = new JPanel();
        choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));
        choicesPanel.setBorder(BorderFactory.createTitledBorder("Answer Choices"));

        JTextField[] choiceFields = new JTextField[4];
        for (int i = 0; i < 4; i++) {
            JPanel choicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            choicePanel.add(new JLabel("Choice " + (i + 1) + ":"));
            choiceFields[i] = new JTextField(25);
            choicePanel.add(choiceFields[i]);
            choicesPanel.add(choicePanel);
        }

        // Correct answer
        JPanel correctPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        correctPanel.add(new JLabel("Correct Answer (1-4):"));
        JSpinner correctSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
        correctPanel.add(correctSpinner);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnSave = new JButton("Save Question");
        JButton btnCancel = new JButton("Cancel");

        btnSave.addActionListener(e -> {
            ArrayList<String> choices = new ArrayList<>();
            for (JTextField field : choiceFields) {
                choices.add(field.getText());
            }

            int correctChoice = (Integer) correctSpinner.getValue();

            if (currentInstructor.addQuestion(courseId, lessonId, quizId,
                    questionIdField.getText(),
                    questionTitleField.getText(),
                    choices, correctChoice)) {
                JOptionPane.showMessageDialog(dialog, "Question added successfully!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to add question.");
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        mainPanel.add(idPanel);
        mainPanel.add(titlePanel);
        mainPanel.add(choicesPanel);
        mainPanel.add(correctPanel);

        dialog.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editQuestionDialog(String courseId, String lessonId, String quizId, String questionId) {
        // Find the question
        Lesson lesson = db.getLessonById(lessonId);
        Question targetQuestion = null;

        for (Quiz quiz : lesson.getQuizzes()) {
            if (quiz.getQuizId().equalsIgnoreCase(quizId)) {
                for (Question q : quiz.getQuestions()) {
                    if (q.getQuestionId().equalsIgnoreCase(questionId)) {
                        targetQuestion = q;
                        break;
                    }
                }
                break;
            }
        }

        if (targetQuestion == null) {
            JOptionPane.showMessageDialog(this, "Question not found.");
            return;
        }

        JDialog dialog = new JDialog(this, "Edit Question: " + questionId, true);
        dialog.setSize(500, 450);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Question ID (read-only)
        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idPanel.add(new JLabel("Question ID (Cannot Change):"));
        idPanel.add(new JLabel(questionId));

        // Question Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(new JLabel("Question:"));
        JTextField questionTitleField = new JTextField(30);
        questionTitleField.setText(targetQuestion.getQuestionTitle());
        titlePanel.add(questionTitleField);

        // Choices
        JPanel choicesPanel = new JPanel();
        choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));
        choicesPanel.setBorder(BorderFactory.createTitledBorder("Answer Choices"));

        JTextField[] choiceFields = new JTextField[4];
        ArrayList<String> existingChoices = targetQuestion.getChoices();

        for (int i = 0; i < 4; i++) {
            JPanel choicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            choicePanel.add(new JLabel("Choice " + (i + 1) + ":"));
            choiceFields[i] = new JTextField(25);
            if (i < existingChoices.size()) {
                choiceFields[i].setText(existingChoices.get(i));
            }
            choicePanel.add(choiceFields[i]);
            choicesPanel.add(choicePanel);
        }

        // Correct answer
        JPanel correctPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        correctPanel.add(new JLabel("Correct Answer (1-4):"));
        JSpinner correctSpinner = new JSpinner(new SpinnerNumberModel(
                targetQuestion.getCorrectChoice(), 1, 4, 1));
        correctPanel.add(correctSpinner);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnSave = new JButton("Save Changes");
        JButton btnCancel = new JButton("Cancel");

        btnSave.addActionListener(e -> {
            // Update the question in the database
            ArrayList<Lesson> lessons = db.loadLessons();

            for (Lesson l : lessons) {
                if (l.getLessonId().equalsIgnoreCase(lessonId) &&
                        l.getCourseId().equalsIgnoreCase(courseId)) {

                    for (Quiz quiz : l.getQuizzes()) {
                        if (quiz.getQuizId().equalsIgnoreCase(quizId)) {

                            for (Question q : quiz.getQuestions()) {
                                if (q.getQuestionId().equalsIgnoreCase(questionId)) {
                                    // Update question details
                                    q.setQuestionTitle(questionTitleField.getText());

                                    ArrayList<String> newChoices = new ArrayList<>();
                                    for (JTextField field : choiceFields) {
                                        newChoices.add(field.getText());
                                    }
                                    q.setChoices(newChoices);
                                    q.setCorrectChoice((Integer) correctSpinner.getValue());

                                    db.saveLessons(lessons);
                                    JOptionPane.showMessageDialog(dialog, "Question updated successfully!");
                                    dialog.dispose();
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            JOptionPane.showMessageDialog(dialog, "Failed to update question.");
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        mainPanel.add(idPanel);
        mainPanel.add(titlePanel);
        mainPanel.add(choicesPanel);
        mainPanel.add(correctPanel);

        dialog.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // --- EXISTING EDIT LOGIC ---

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

        Lesson targetLesson = db.getLessonById(lessonId);

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
            ArrayList<Lesson> allLessons = db.loadLessons();
            for (Lesson l : allLessons) {
                if (l.getLessonId().equalsIgnoreCase(lessonId)) {
                    l.setTitle(titleField.getText());
                    l.setContent(contentField.getText());
                    break;
                }
            }
            db.saveLessons(allLessons);
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
                ArrayList<Lesson> lessons = db.getLessonsForCourse(courseId);
                for (Lesson l : lessons) {
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

    // --- OTHER ACTIONS ---

    private void onDeleteLesson() {
        String lessonId = getSelectedLessonId();
        if (lessonId == null) {
            JOptionPane.showMessageDialog(this, "Please select a lesson to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete lesson " + lessonId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (currentInstructor.deleteLesson(lessonId)) {
                JOptionPane.showMessageDialog(this, "Lesson Deleted.");
                refreshLessonList();
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