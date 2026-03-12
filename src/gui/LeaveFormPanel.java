package gui;

import model.Leave;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class LeaveFormPanel extends JPanel {

    private static final String DATE_PLACEHOLDER = "yyyy-MM-dd";

    private final JLabel lblBack = new JLabel("<html><u>Back</u></html>");
    private final JButton btnSubmit = new JButton("Submit");

    private final JTextField txtStartDate = new JTextField();
    private final JTextField txtEndDate = new JTextField();
    private final JComboBox<String> cmbLeaveType = new JComboBox<>(
            new String[]{"Vacation Leave", "Sick Leave", "Emergency Leave", "Personal Leave", "Other"}
    );
    private final JTextArea txtNotes = new JTextArea(7, 20);
    private final JTextField txtStatus = new JTextField();

    private java.awt.event.ActionListener backListener;

    public LeaveFormPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(22, 32, 24, 32));

        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildFormArea(), BorderLayout.CENTER);

        add(root, BorderLayout.CENTER);

        styleComponents();
        wireEvents();
        installDatePlaceholder(txtStartDate);
        installDatePlaceholder(txtEndDate);

        txtStatus.setEditable(false);
        txtStatus.setText("Pending");
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 22, 0));

        lblBack.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblBack.setForeground(new Color(95, 95, 95));
        lblBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        top.add(lblBack);
        return top;
    }

    private JPanel buildFormArea() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        JPanel leftColumn = buildLeftColumn();
        JPanel rightColumn = buildRightColumn();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 34);
        form.add(leftColumn, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        form.add(rightColumn, gbc);

        GridBagConstraints wrapGbc = new GridBagConstraints();
        wrapGbc.gridx = 0;
        wrapGbc.gridy = 0;
        wrapGbc.weightx = 1;
        wrapGbc.weighty = 1;
        wrapGbc.fill = GridBagConstraints.BOTH;
        wrapGbc.anchor = GridBagConstraints.NORTHWEST;

        wrapper.add(form, wrapGbc);
        return wrapper;
    }

    private JPanel buildLeftColumn() {
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        left.add(createFieldBlock("Start Date", txtStartDate));
        left.add(Box.createVerticalStrut(22));
        left.add(createFieldBlock("End Date", txtEndDate));

        return left;
    }

    private JPanel buildRightColumn() {
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        right.add(createFieldBlock("Leave Type", cmbLeaveType));
        right.add(Box.createVerticalStrut(22));
        right.add(createNotesBlock());
        right.add(Box.createVerticalStrut(22));
        right.add(createFieldBlock("Status", txtStatus));
        right.add(Box.createVerticalStrut(22));
        right.add(createSubmitRow());

        return right;
    }

    private JPanel createFieldBlock(String labelText, JComponent field) {
        JPanel block = new JPanel(new BorderLayout(0, 8));
        block.setOpaque(false);
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(new Color(20, 20, 20));

        block.add(label, BorderLayout.NORTH);
        block.add(field, BorderLayout.CENTER);

        return block;
    }

    private JPanel createNotesBlock() {
        JPanel block = new JPanel(new BorderLayout(0, 8));
        block.setOpaque(false);
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));

        JLabel label = new JLabel("Notes");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(new Color(20, 20, 20));

        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        txtNotes.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtNotes.setBackground(Color.WHITE);
        txtNotes.setForeground(new Color(25, 25, 25));
        txtNotes.setBorder(new EmptyBorder(12, 14, 12, 14));

        JScrollPane scroll = new JScrollPane(txtNotes);
        scroll.setPreferredSize(new Dimension(100, 168));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 168));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 120), 1));
        scroll.getViewport().setBackground(Color.WHITE);

        block.add(label, BorderLayout.NORTH);
        block.add(scroll, BorderLayout.CENTER);

        return block;
    }

    private JPanel createSubmitRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        row.setOpaque(false);
        row.add(btnSubmit);
        return row;
    }

    private void styleComponents() {
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);
        Color borderColor = new Color(115, 115, 115);
        Dimension fieldSize = new Dimension(100, 54);

        styleTextField(txtStartDate, fieldFont, borderColor, fieldSize);
        styleTextField(txtEndDate, fieldFont, borderColor, fieldSize);
        styleTextField(txtStatus, fieldFont, borderColor, fieldSize);

        cmbLeaveType.setFont(fieldFont);
        cmbLeaveType.setPreferredSize(fieldSize);
        cmbLeaveType.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        cmbLeaveType.setBackground(Color.WHITE);
        cmbLeaveType.setForeground(new Color(25, 25, 25));
        cmbLeaveType.setOpaque(true);
        cmbLeaveType.setBorder(BorderFactory.createLineBorder(borderColor, 1));

        txtStatus.setBackground(Color.WHITE);
        txtStatus.setDisabledTextColor(new Color(25, 25, 25));
        txtStatus.setCaretColor(new Color(25, 25, 25));

        btnSubmit.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setBackground(Color.BLACK);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setBorder(BorderFactory.createEmptyBorder());
        btnSubmit.setPreferredSize(new Dimension(126, 46));
        btnSubmit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleTextField(JTextField field, Font font, Color borderColor, Dimension size) {
        field.setFont(font);
        field.setPreferredSize(size);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, size.height));
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(25, 25, 25));
        field.setCaretColor(new Color(25, 25, 25));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                new EmptyBorder(0, 14, 0, 14)
        ));
    }

    private void installDatePlaceholder(JTextField field) {
        field.setText(DATE_PLACEHOLDER);
        field.setForeground(new Color(185, 185, 185));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (DATE_PLACEHOLDER.equals(field.getText())) {
                    field.setText("");
                    field.setForeground(new Color(25, 25, 25));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText() == null || field.getText().trim().isEmpty()) {
                    field.setText(DATE_PLACEHOLDER);
                    field.setForeground(new Color(185, 185, 185));
                }
            }
        });
    }

    private void wireEvents() {
        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (backListener != null) {
                    backListener.actionPerformed(
                            new java.awt.event.ActionEvent(
                                    LeaveFormPanel.this,
                                    java.awt.event.ActionEvent.ACTION_PERFORMED,
                                    "back"
                            )
                    );
                }
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                lblBack.setForeground(new Color(55, 55, 55));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                lblBack.setForeground(new Color(95, 95, 95));
            }
        });
    }

    public void setFormMode(boolean editMode) {
        btnSubmit.setText(editMode ? "Update" : "Submit");
    }

    public void setLeaveData(Leave leave) {
        setDateText(txtStartDate, leave.getStartDate());
        setDateText(txtEndDate, leave.getEndDate());

        cmbLeaveType.setSelectedItem(
                leave.getLeaveType() == null || leave.getLeaveType().trim().isEmpty()
                        ? "Vacation Leave"
                        : leave.getLeaveType()
        );

        txtNotes.setText(leave.getNotes() == null ? "" : leave.getNotes());

        txtStatus.setText(
                leave.getStatus() == null || leave.getStatus().trim().isEmpty()
                        ? "Pending"
                        : leave.getStatus()
        );
    }

    private void setDateText(JTextField field, String value) {
        if (value == null || value.trim().isEmpty()) {
            field.setText(DATE_PLACEHOLDER);
            field.setForeground(new Color(185, 185, 185));
        } else {
            field.setText(value);
            field.setForeground(new Color(25, 25, 25));
        }
    }

    public void clearForm() {
        setDateText(txtStartDate, "");
        setDateText(txtEndDate, "");
        cmbLeaveType.setSelectedIndex(0);
        txtNotes.setText("");
        txtStatus.setText("Pending");
    }

    public void fillLeave(Leave leave) {
        String startDate = DATE_PLACEHOLDER.equals(txtStartDate.getText().trim()) ? "" : txtStartDate.getText().trim();
        String endDate = DATE_PLACEHOLDER.equals(txtEndDate.getText().trim()) ? "" : txtEndDate.getText().trim();

        leave.setStartDate(startDate);
        leave.setEndDate(endDate);
        leave.setLeaveType((String) cmbLeaveType.getSelectedItem());
        leave.setNotes(txtNotes.getText().trim());
        leave.setStatus(txtStatus.getText().trim());
        leave.setReason("");
    }

    public void addBackListener(java.awt.event.ActionListener listener) {
        this.backListener = listener;
    }

    public void addSubmitListener(java.awt.event.ActionListener listener) {
        btnSubmit.addActionListener(listener);
    }
}