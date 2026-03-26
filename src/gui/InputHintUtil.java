package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public final class InputHintUtil {

    private static final Color PLACEHOLDER_COLOR = new Color(150, 150, 150);
    private static final Color NORMAL_COLOR = new Color(25, 25, 25);

    private InputHintUtil() {
    }

    public static void applyPlaceholder(JTextField field, String placeholder) {
        if (field == null || placeholder == null || placeholder.isBlank()) {
            return;
        }

        field.setText(placeholder);
        field.setForeground(PLACEHOLDER_COLOR);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(NORMAL_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });
    }

    public static String getActualText(JTextField field, String placeholder) {
        if (field == null) {
            return "";
        }

        String value = field.getText();
        if (value == null || value.equals(placeholder)) {
            return "";
        }

        return value.trim();
    }

    public static void setTooltip(JComponent component, String tooltip) {
        if (component != null && tooltip != null && !tooltip.isBlank()) {
            component.setToolTipText(tooltip);
        }
    }
}