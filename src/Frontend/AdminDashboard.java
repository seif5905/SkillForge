package Frontend;

import Backend.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class AdminDashboard extends javax.swing.JFrame {
    private Admin admin;
    private DefaultTableModel pendingModel;
    private DefaultTableModel approvedModel;

    public AdminDashboard(Admin admin) {
        this.admin = admin;
        initComponents();
        refreshTables();
    }

    private void refreshTables() {
        loadCoursesByStatus("pending", pendingModel);
        loadCoursesByStatus("approved", approvedModel);
    }

    private void loadCoursesByStatus(String status, DefaultTableModel model) {
        DatabaseManager db = new DatabaseManager();
        ArrayList<Course> allCourses = db.loadCourses();
        model.setRowCount(0);
        for (Course c : allCourses) {
            if (c.getStatus().equalsIgnoreCase(status)) {
                model.addRow(new Object[]{c.getCourseId(), c.getTitle(), c.getInstructorId()});
            }
        }
    }

    private void initComponents() {
        setTitle("Admin Dashboard - " + admin.getUsername());
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- TOP PANEL: Header & Logout ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(45, 52, 54));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcomeLabel = new JLabel("Welcome, Admin " + admin.getUsername());
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- CENTER: Tabbed Pane for Courses ---
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Pending Courses
        JPanel pendingPanel = new JPanel(new BorderLayout());
        pendingModel = new DefaultTableModel(new String[]{"ID", "Title", "Instructor"}, 0);
        JTable pendingTable = new JTable(pendingModel);
        pendingPanel.add(new JScrollPane(pendingTable), BorderLayout.CENTER);

        JPanel pendingActions = new JPanel();
        JButton approveBtn = new JButton("Approve");
        JButton rejectBtn = new JButton("Reject");

        approveBtn.addActionListener(e -> handleStatus(pendingTable, "Approved"));
        rejectBtn.addActionListener(e -> handleStatus(pendingTable, "Rejected"));

        pendingActions.add(approveBtn);
        pendingActions.add(rejectBtn);
        pendingPanel.add(pendingActions, BorderLayout.SOUTH);

        // Tab 2: Approved Courses
        JPanel approvedPanel = new JPanel(new BorderLayout());
        approvedModel = new DefaultTableModel(new String[]{"ID", "Title", "Instructor"}, 0);
        JTable approvedTable = new JTable(approvedModel);
        approvedPanel.add(new JScrollPane(approvedTable), BorderLayout.CENTER);

        // Add a "Revoke" button to approved courses for extra control
        JButton revokeBtn = new JButton("Revoke Approval (Set to Pending)");
        revokeBtn.addActionListener(e -> handleStatus(approvedTable, "Pending"));
        JPanel approvedActions = new JPanel();
        approvedActions.add(revokeBtn);
        approvedPanel.add(approvedActions, BorderLayout.SOUTH);

        tabbedPane.addTab("Pending Courses", pendingPanel);
        tabbedPane.addTab("Approved Courses", approvedPanel);
        add(tabbedPane, BorderLayout.CENTER);

        // --- BOTTOM: Management Tools (Deletions) ---
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("System Cleanup"));

        // User Deletion
        JPanel userRow = new JPanel();
        userRow.add(new JLabel("Delete User (ID):"));
        JTextField uField = new JTextField(10);
        JButton uBtn = new JButton("Delete User");
        uBtn.addActionListener(e -> {
            if(admin.deleteUser(uField.getText())) {
                JOptionPane.showMessageDialog(this, "User deleted.");
                refreshTables();
            } else JOptionPane.showMessageDialog(this, "User not found.");
        });
        userRow.add(uField); userRow.add(uBtn);

        // Course Deletion
        JPanel courseRow = new JPanel();
        courseRow.add(new JLabel("Delete Course (ID):"));
        JTextField cField = new JTextField(10);
        JButton cBtn = new JButton("Delete Course");
        cBtn.addActionListener(e -> {
            if(admin.deleteCourse(cField.getText())) {
                JOptionPane.showMessageDialog(this, "Course deleted.");
                refreshTables();
            } else JOptionPane.showMessageDialog(this, "Course not found.");
        });
        courseRow.add(cField); courseRow.add(cBtn);

        bottomPanel.add(userRow);
        bottomPanel.add(courseRow);
        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private void handleStatus(JTable table, String status) {
        int row = table.getSelectedRow();
        if (row != -1) {
            String id = (String) table.getValueAt(row, 0);
            if (admin.setCourseStatus(id, status)) {
                refreshTables();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a course.");
        }
    }
}