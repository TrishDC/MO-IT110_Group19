package gui;

import model.Leave;
import repository.CsvLeaveRepository;
import service.LeaveService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LeaveManagementFrame extends JFrame {

    private final LeaveService service = new LeaveService(new CsvLeaveRepository());

    private JTable table;
    private DefaultTableModel model;

    public LeaveManagementFrame() {
        setTitle("Leave Management");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        loadLeaves();
    }

    private void initUI() {
        model = new DefaultTableModel(
                new Object[]{"Leave ID", "Employee ID", "Type", "Start Date", "End Date", "Status"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] widths = {80, 100, 120, 120, 120, 100};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JButton btnAdd = new JButton("Add Leave");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> {
            AddLeaveDialog d = new AddLeaveDialog(this, service);
            d.setVisible(true);
            loadLeaves();
        });

        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadLeaves());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(btnAdd);
        top.add(btnDelete);
        top.add(btnRefresh);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadLeaves() {
        model.setRowCount(0);
        List<Leave> leaves = service.getAll();
        for (Leave l : leaves) {
            model.addRow(new Object[]{
                    l.getLeaveId(), l.getEmployeeId(), l.getLeaveType(),
                    l.getStartDate(), l.getEndDate(), l.getStatus()
            });
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a leave record first.");
            return;
        }
        int leaveId = Integer.parseInt(model.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this, "Delete Leave ID " + leaveId + "?", "Confirm", JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            service.delete(leaveId);
            loadLeaves();
        }
    }
}
