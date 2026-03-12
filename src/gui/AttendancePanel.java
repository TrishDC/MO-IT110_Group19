package gui;

import model.AttendanceRecord;
import model.Employee;
import repository.CsvAttendanceRepository;
import service.AttendanceService;
import service.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class AttendancePanel extends JPanel {

    private static final String LIST_CARD = "LIST";
    private static final String FORM_CARD = "FORM";

    private static final Color PAGE_BG = new Color(242, 242, 242);
    private static final Color TEXT_DARK = new Color(25, 25, 25);
    private static final Color MUTED_TEXT = new Color(130, 130, 130);
    private static final Color TABLE_BORDER = new Color(220, 220, 220);
    private static final Color TABLE_SELECT_BG = new Color(232, 239, 252);

    private final AttendanceService attendanceService;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    private JTable table;
    private DefaultTableModel model;
    private JScrollPane tableScrollPane;
    private JLabel emptyStateLabel;

    private JButton btnUpdate;
    private JButton btnTimeIn;
    private JButton btnTimeOut;
    private JButton btnRefresh;

    private AttendanceFormPanel formPanel;
    private AttendanceRecord selectedRecordForUpdate;
    
    private final Employee currentUser;
    private JTextField txtEmployeeFilter;
    private JButton btnViewAll;
    private JButton btnViewMine;

    public AttendancePanel() {
        this(new AttendanceService(new repository.CsvAttendanceRepository()));
    }

    public AttendancePanel(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
        this.currentUser = SessionManager.getCurrentUser();

        setLayout(new BorderLayout());
        setBackground(PAGE_BG);
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        contentPanel.setOpaque(false);
        contentPanel.add(buildListPage(), LIST_CARD);
        contentPanel.add(buildFormPage(), FORM_CARD);

        add(contentPanel, BorderLayout.CENTER);

        loadAttendanceHistory();
        showListPage();
}
    private JPanel buildListPage() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);
        wrapper.add(buildTopArea(), BorderLayout.NORTH);
        wrapper.add(buildTableArea(), BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildTopArea() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 6, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        boolean hrCanManageAll = attendanceService.canManageAllAttendance(currentUser);

        if (hrCanManageAll) {
            txtEmployeeFilter = new JTextField(12);
            txtEmployeeFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            txtEmployeeFilter.setPreferredSize(new Dimension(140, 40));

            btnViewMine = createActionButton("My Records");
            btnViewAll = createActionButton("View All");

            btnViewMine.addActionListener(e -> loadMyAttendanceHistory());
            btnViewAll.addActionListener(e -> loadAttendanceHistory());

            left.add(new JLabel("Employee ID:"));
            left.add(txtEmployeeFilter);
            left.add(btnViewMine);
            left.add(btnViewAll);
        }

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        btnUpdate = createActionButton("Update");
        btnTimeIn = createActionButton("Time In");
        btnTimeOut = createActionButton("Time Out");
        btnRefresh = createActionButton("Refresh");

        right.add(btnUpdate);
        right.add(btnTimeIn);
        right.add(btnTimeOut);
        right.add(btnRefresh);

        btnUpdate.addActionListener(e -> openUpdateForm());
        btnTimeIn.addActionListener(e -> handleTimeIn());
        btnTimeOut.addActionListener(e -> handleTimeOut());
        btnRefresh.addActionListener(e -> refreshBasedOnRole());

        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        return top;
    }

    private JPanel buildTableArea() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        model = new DefaultTableModel(
                new Object[]{"Employee ID", "Date", "Time In", "Time Out"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(44);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setGridColor(new Color(225, 225, 225));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setBackground(Color.WHITE);
        table.setForeground(TEXT_DARK);
        table.setSelectionBackground(TABLE_SELECT_BG);
        table.setSelectionForeground(TEXT_DARK);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.BLACK);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(header.getWidth(), 48));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBorder(new EmptyBorder(0, 10, 0, 10));
        renderer.setVerticalAlignment(SwingConstants.CENTER);
        renderer.setForeground(TEXT_DARK);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(140);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);

        tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(TABLE_BORDER, 1));
        tableScrollPane.getViewport().setBackground(Color.WHITE);

        emptyStateLabel = new JLabel("No attendance history found.", SwingConstants.CENTER);
        emptyStateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        emptyStateLabel.setForeground(MUTED_TEXT);
        emptyStateLabel.setOpaque(true);
        emptyStateLabel.setBackground(Color.WHITE);
        emptyStateLabel.setBorder(new EmptyBorder(30, 20, 30, 20));

        panel.add(tableScrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFormPage() {
        formPanel = new AttendanceFormPanel();
        formPanel.addBackListener(e -> showListPage());
        formPanel.addSubmitListener(e -> submitUpdateForm());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(formPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private void handleTimeIn() {
        Employee currentUser = SessionManager.getCurrentUser();

        try {
            attendanceService.timeIn(currentUser);
            loadAttendanceHistory();
            JOptionPane.showMessageDialog(this, "Time In recorded successfully.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Time In Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleTimeOut() {
        Employee currentUser = SessionManager.getCurrentUser();

        try {
            attendanceService.timeOut(currentUser);
            loadAttendanceHistory();
            JOptionPane.showMessageDialog(this, "Time Out recorded successfully.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Time Out Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openUpdateForm() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an attendance record first.");
            return;
        }

        String employeeId = String.valueOf(model.getValueAt(row, 0));
        String date = String.valueOf(model.getValueAt(row, 1));
        String timeIn = String.valueOf(model.getValueAt(row, 2));
        String timeOut = String.valueOf(model.getValueAt(row, 3));

        selectedRecordForUpdate = new AttendanceRecord();
        selectedRecordForUpdate.setEmployeeId(employeeId);
        selectedRecordForUpdate.setDate(date);
        selectedRecordForUpdate.setLogIn(timeIn);
        selectedRecordForUpdate.setLogOut(timeOut);

        formPanel.setAttendanceData(employeeId, date, timeIn, timeOut);
        formPanel.setEditableFields(false, false, true, true);

        cardLayout.show(contentPanel, FORM_CARD);
    }

    private void submitUpdateForm() {
        if (selectedRecordForUpdate == null) {
            showListPage();
            return;
        }

        try {
            selectedRecordForUpdate.setLogIn(formPanel.getTimeIn());
            selectedRecordForUpdate.setLogOut(formPanel.getTimeOut());

            attendanceService.updateAttendance(currentUser, selectedRecordForUpdate);

            selectedRecordForUpdate = null;
            loadAttendanceHistory();
            JOptionPane.showMessageDialog(this, "Attendance updated successfully.");
            showListPage();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAttendanceHistory() {
        model.setRowCount(0);

        if (currentUser == null) {
            refreshEmptyState();
            return;
        }

        List<AttendanceRecord> records = attendanceService.getVisibleAttendance(currentUser);

        for (AttendanceRecord record : records) {
            model.addRow(new Object[]{
                    record.getEmployeeId(),
                    record.getDate(),
                    record.getLogIn(),
                    record.getLogOut()
            });
        }

        refreshEmptyState();
    }

    private void showListPage() {
        selectedRecordForUpdate = null;
        cardLayout.show(contentPanel, LIST_CARD);
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(130, 46));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void refreshEmptyState() {
        if (model.getRowCount() == 0) {
            tableScrollPane.setViewportView(emptyStateLabel);
        } else {
            tableScrollPane.setViewportView(table);
        }

        tableScrollPane.revalidate();
        tableScrollPane.repaint();
    }
    
    private void loadMyAttendanceHistory() {
        model.setRowCount(0);

        if (currentUser == null) {
            refreshEmptyState();
            return;
        }

        List<AttendanceRecord> records =
                attendanceService.getAttendanceByEmployee(currentUser.getId());

        for (AttendanceRecord record : records) {
            model.addRow(new Object[]{
                    record.getEmployeeId(),
                    record.getDate(),
                    record.getLogIn(),
                    record.getLogOut()
            });
        }

        refreshEmptyState();
    }

    private void refreshBasedOnRole() {
        if (currentUser == null) {
            refreshEmptyState();
            return;
        }

        if (attendanceService.canManageAllAttendance(currentUser)
                && txtEmployeeFilter != null
                && txtEmployeeFilter.getText() != null
                && !txtEmployeeFilter.getText().trim().isEmpty()) {

            model.setRowCount(0);

            List<AttendanceRecord> records =
                    attendanceService.getVisibleAttendanceByEmployee(
                            currentUser,
                            txtEmployeeFilter.getText().trim()
                    );

            for (AttendanceRecord record : records) {
                model.addRow(new Object[]{
                        record.getEmployeeId(),
                        record.getDate(),
                        record.getLogIn(),
                        record.getLogOut()
                });
            }

            refreshEmptyState();
            return;
        }

        loadAttendanceHistory();
    }
}