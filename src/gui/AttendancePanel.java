package gui;

import model.AttendanceRecord;
import model.Employee;
import repository.CsvAttendanceRepository;
import service.AttendanceService;
import service.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

public class AttendancePanel extends JPanel {

    private static final String LIST_CARD = "LIST";
    private static final String FORM_CARD = "FORM";

    private static final Color PAGE_BG = new Color(242, 242, 242);
    private static final Color TEXT_DARK = new Color(25, 25, 25);
    private static final Color MUTED_TEXT = new Color(130, 130, 130);
    private static final Color TABLE_BORDER = new Color(220, 220, 220);
    private static final Color TABLE_SELECT_BG = new Color(232, 239, 252);
    private static final Color FIELD_BORDER = new Color(180, 180, 180);
    private static final String SEARCH_PLACEHOLDER = "Employee ID";

    private final AttendanceService attendanceService;
    private final Employee currentUser;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    private JTable table;
    private DefaultTableModel model;
    private JScrollPane tableScrollPane;
    private JLabel emptyStateLabel;

    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnTimeIn;
    private JButton btnTimeOut;
    private JButton btnRefresh;
    private JButton btnViewAll;
    private JButton btnViewMine;

    private JTextField txtEmployeeFilter;

    private AttendanceFormPanel formPanel;
    private AttendanceRecord selectedRecordForUpdate;

    public AttendancePanel() {
        this(new AttendanceService(new CsvAttendanceRepository()));
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

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        boolean canViewBroader = attendanceService.canViewBroaderAttendance(currentUser);
        boolean canUpdateAny = attendanceService.canUpdateAnyAttendance(currentUser);

        if (canViewBroader) {
            txtEmployeeFilter = createSearchField();

            JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            searchRow.setOpaque(false);
            searchRow.add(txtEmployeeFilter);

            JPanel viewButtonsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
            viewButtonsRow.setOpaque(false);

            btnViewMine = createActionButton("My Records");
            btnViewAll = createActionButton("View All");

            btnViewMine.addActionListener(e -> loadMyAttendanceHistory());
            btnViewAll.addActionListener(e -> loadAttendanceHistory());

            viewButtonsRow.add(btnViewMine);
            viewButtonsRow.add(btnViewAll);

            left.add(searchRow);
            left.add(Box.createVerticalStrut(8));
            left.add(viewButtonsRow);
        }

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        btnUpdate = createActionButton("Update");
        btnDelete = createActionButton("Delete");
        btnTimeIn = createActionButton("Time In");
        btnTimeOut = createActionButton("Time Out");
        btnRefresh = createActionButton("Refresh");

        if (canUpdateAny) {
            right.add(btnUpdate);
            right.add(btnDelete);
        }

        right.add(btnTimeIn);
        right.add(btnTimeOut);
        right.add(btnRefresh);

        btnUpdate.addActionListener(e -> openUpdateForm());
        btnDelete.addActionListener(e -> deleteSelectedAttendance());
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
        try {
            attendanceService.timeIn(currentUser);
            loadAttendanceHistory();
            JOptionPane.showMessageDialog(this, "Time In recorded successfully.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Time In Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleTimeOut() {
        try {
            attendanceService.timeOut(currentUser);
            loadAttendanceHistory();
            JOptionPane.showMessageDialog(this, "Time Out recorded successfully.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Time Out Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openUpdateForm() {
        if (!attendanceService.canUpdateAnyAttendance(currentUser)) {
            JOptionPane.showMessageDialog(this, "You do not have permission to update attendance records.");
            return;
        }

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

    private void deleteSelectedAttendance() {
        if (!attendanceService.canDeleteAnyAttendance(currentUser)) {
            JOptionPane.showMessageDialog(this, "You do not have permission to delete attendance records.");
            return;
        }

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an attendance record first.");
            return;
        }

        String employeeId = String.valueOf(model.getValueAt(row, 0));
        String date = String.valueOf(model.getValueAt(row, 1));

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this attendance record?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            attendanceService.deleteAttendance(currentUser, employeeId, date);
            loadAttendanceHistory();
            JOptionPane.showMessageDialog(this, "Attendance deleted successfully.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete Error", JOptionPane.ERROR_MESSAGE);
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

    private void loadMyAttendanceHistory() {
        model.setRowCount(0);

        if (currentUser == null) {
            refreshEmptyState();
            return;
        }

        List<AttendanceRecord> records = attendanceService.getAttendanceByEmployee(currentUser.getId());

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

        if (attendanceService.canViewBroaderAttendance(currentUser)) {
            // Search bar intentionally not functional yet, per your note.
            loadAttendanceHistory();
            return;
        }

        loadMyAttendanceHistory();
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

    private JTextField createSearchField() {
        JTextField field = new JTextField(14);
        field.setPreferredSize(new Dimension(180, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.GRAY);
        field.setText(SEARCH_PLACEHOLDER);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(FIELD_BORDER, 1, true),
                new EmptyBorder(0, 12, 0, 12)
        ));
        field.setBackground(Color.WHITE);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (SEARCH_PLACEHOLDER.equals(field.getText())) {
                    field.setText("");
                    field.setForeground(TEXT_DARK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(SEARCH_PLACEHOLDER);
                    field.setForeground(Color.GRAY);
                }
            }
        });

        return field;
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
}