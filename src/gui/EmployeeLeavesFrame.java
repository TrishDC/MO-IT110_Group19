/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Rhynne Gracelle
 */

package gui;

import model.EmployeeLeaveRequest;
import service.EmployeeLeaveUiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import model.Employee;

public class EmployeeLeavesFrame extends JFrame {

    private static final Color BG = new Color(245, 245, 245);
    private static final Color SIDEBAR = Color.BLACK;
    private static final Color TEXT_LIGHT = Color.WHITE;
    private static final Color TEXT_DARK = new Color(25, 25, 90);
    private static final Color MUTED = new Color(130, 130, 130);
    private static final Color TABLE_HEADER = Color.BLACK;

    private final EmployeeLeaveUiService leaveService;
    private final String currentEmployeeId;
    private final String currentEmployeeName;
    private final String currentDepartment;
    private final String currentPosition;

    private final EmployeeLeaveTableModel tableModel = new EmployeeLeaveTableModel();
    private JTable table;

    public EmployeeLeavesFrame(EmployeeLeaveUiService leaveService,
                               String currentEmployeeId,
                               String currentEmployeeName,
                               String currentDepartment,
                               String currentPosition) {
        this.leaveService = leaveService;
        this.currentEmployeeId = currentEmployeeId;
        this.currentEmployeeName = currentEmployeeName;
        this.currentDepartment = currentDepartment;
        this.currentPosition = currentPosition;

        applyGlobalFont();

        setTitle("MotorPH - Leaves");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 650));
        setSize(1280, 720);
        setLocationRelativeTo(null);

        setContentPane(buildRoot());
        loadLeaveRequests();
    }

    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainContent(), BorderLayout.CENTER);

        return root;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(30, 28, 20, 20));
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("MotorPH");
        lblTitle.setForeground(TEXT_LIGHT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(lblTitle);
        top.add(Box.createVerticalStrut(40));

        top.add(createSidebarLabel("Dashboard", false));
        top.add(Box.createVerticalStrut(24));
        top.add(createSidebarLabel("Employees", false));
        top.add(Box.createVerticalStrut(24));
        top.add(createSidebarLabel("Payroll", false));
        top.add(Box.createVerticalStrut(24));
        top.add(createSidebarLabel("Leaves", true));
        top.add(Box.createVerticalStrut(24));
        top.add(createSidebarLabel("Attendance", false));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 28, 20));
        bottom.setOpaque(false);

        JLabel lblLogout = new JLabel("Log Out");
        lblLogout.setForeground(TEXT_LIGHT);
        lblLogout.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        bottom.add(lblLogout);

        sidebar.add(top, BorderLayout.NORTH);
        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel buildMainContent() {
        JPanel main = new JPanel(new BorderLayout(18, 18));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(28, 35, 35, 35));

        main.add(buildHeader(), BorderLayout.NORTH);
        main.add(buildCenterPanel(), BorderLayout.CENTER);

        return main;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);

        JPanel profile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        profile.setOpaque(false);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel lblName = new JLabel(currentEmployeeName);
        lblName.setForeground(TEXT_DARK);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel lblPosition = new JLabel(currentPosition);
        lblPosition.setForeground(MUTED);
        lblPosition.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        textPanel.add(lblName);
        textPanel.add(lblPosition);

        JPanel avatar = new JPanel();
        avatar.setPreferredSize(new Dimension(56, 56));
        avatar.setOpaque(false);
        avatar.add(new CircleAvatar());

        profile.add(textPanel);
        profile.add(avatar);

        header.add(spacer, BorderLayout.CENTER);
        header.add(profile, BorderLayout.EAST);

        return header;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton btnAdd = createActionButton("Add");
        JButton btnUpdate = createActionButton("Update");
        JButton btnDelete = createActionButton("Delete");
        JButton btnRefresh = createActionButton("Refresh");

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadLeaveRequests());

        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);
        actions.add(btnRefresh);

        panel.add(actions, BorderLayout.NORTH);
        panel.add(buildTablePane(), BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane buildTablePane() {
        table = new JTable(tableModel);
        table.setRowHeight(42);
        table.setShowGrid(true);
        table.setGridColor(new Color(210, 210, 210));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 44));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        return scrollPane;
    }

    private void loadLeaveRequests() {
        List<EmployeeLeaveRequest> requests = leaveService.getLeavesByEmployee(currentEmployeeId);
        tableModel.setRequests(requests);
    }

    private void onAdd() {
        EmployeeLeaveRequest request = new EmployeeLeaveRequest();
        request.setEmployeeId(currentEmployeeId);
        request.setEmployeeName(currentEmployeeName);
        request.setDepartment(currentDepartment);
        request.setStatus("Pending");

        LeaveRequestDialog dialog = new LeaveRequestDialog(this, request, false);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            leaveService.fileLeave(request);
            loadLeaveRequests();
            JOptionPane.showMessageDialog(this, "Leave request filed successfully.");
        }
    }

    private void onUpdate() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a leave request first.");
            return;
        }

        EmployeeLeaveRequest selected = tableModel.getRequestAt(row);
        if (selected == null) {
            return;
        }

        if (!"Pending".equalsIgnoreCase(selected.getStatus())) {
            JOptionPane.showMessageDialog(this, "Only pending leave requests can be updated.");
            return;
        }

        LeaveRequestDialog dialog = new LeaveRequestDialog(this, selected, true);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            leaveService.updateLeave(selected);
            loadLeaveRequests();
            JOptionPane.showMessageDialog(this, "Leave request updated successfully.");
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a leave request first.");
            return;
        }

        EmployeeLeaveRequest selected = tableModel.getRequestAt(row);
        if (selected == null) {
            return;
        }

        if (!"Pending".equalsIgnoreCase(selected.getStatus())) {
            JOptionPane.showMessageDialog(this, "Only pending leave requests can be deleted.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this leave request?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            leaveService.deleteLeave(selected.getLeaveId(), currentEmployeeId);
            loadLeaveRequests();
            JOptionPane.showMessageDialog(this, "Leave request deleted successfully.");
        }
    }

    private JLabel createSidebarLabel(String text, boolean active) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_LIGHT);
        label.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 16));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 44));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        return button;
    }

    private void applyGlobalFont() {
        Font uiFont = new Font("Segoe UI", Font.PLAIN, 14);
        UIManager.put("Label.font", uiFont);
        UIManager.put("Button.font", uiFont);
        UIManager.put("TextField.font", uiFont);
        UIManager.put("TextArea.font", uiFont);
        UIManager.put("ComboBox.font", uiFont);
        UIManager.put("Table.font", uiFont);
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("OptionPane.font", uiFont);
        UIManager.put("OptionPane.messageFont", uiFont);
        UIManager.put("OptionPane.buttonFont", uiFont);
    }

    private static class CircleAvatar extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(56, 56);
        }
    }
}
