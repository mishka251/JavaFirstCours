package database_instruments;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateInstanceForm extends JFrame {
    final PosgtresDB db;
    final String tableName;

    ArrayList<TableColumn> columns;
    final Map<TableColumn, FieldPanel> inputs;

    final JButton btnInsert;

    public CreateInstanceForm(PosgtresDB db, String tableName, Map<String, Object> filledFields) {
        this.db = db;
        this.tableName = tableName;
        inputs = new HashMap<>();

        try {
            columns = db.getTableColumnNames(tableName);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }

        setSize(500, 400);
        setLayout(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(0, 0, 500, 400);
        add(mainPanel);
        mainPanel.setVisible(true);
        mainPanel.setBackground(Color.red);

        for (int i = 0; i < columns.size(); i++) {
            TableColumn column = columns.get(i);
            FieldPanel panel = new FieldPanel(column, filledFields);
            inputs.put(column, panel);
            panel.setBounds(5, 35 * i, 240, 30);
            mainPanel.add(panel);
            mainPanel.repaint();
        }

        btnInsert = new JButton("Insert");
        btnInsert.setBounds(420, 40, 80, 30);
        mainPanel.add(btnInsert);

        btnInsert.addActionListener(this::Insert);
        setVisible(true);
    }

    void Insert(ActionEvent event) {
        Object[] values = new Object[columns.size()];
        String[] columnNames = new String[columns.size()];
        SimpleDateFormat parser = new SimpleDateFormat("dd.MM.yyyy");
        try {
            for (int i = 0; i < columns.size(); i++) {
                TableColumn column = columns.get(i);
                columnNames[i] = column.name;
                String inputValue = inputs.get(column).getValue();
                switch (column.type) {
                    case "date":
                    case "DATE":
                        values[i] = parser.parse(inputValue);
                        break;
                    case "integer":
                    case "INTEGER":
                    case "int4":
                        values[i] = Integer.parseInt(inputValue);
                        break;
                    case "varchar":
                    case "VARCHAR":
                    case "VARCHAR (100)":
                    case "text":
                        values[i] = inputValue;
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Unknown type " + column.type, "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            db.insert(tableName, columnNames, values);
            JOptionPane.showMessageDialog(this, "OK", "Сохранено", JOptionPane.INFORMATION_MESSAGE);
            setVisible(false);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}


class FieldPanel extends JPanel {
    final JTextField inputValue;

    FieldPanel(TableColumn column, Map<String, Object> filledFields) {
        setLayout(null);

        JLabel label = new JLabel(column.name);
        label.setBounds(5, 5, 80, 20);
        add(label);

        inputValue = new JTextField();
        inputValue.setBounds(100, 5, 100, 20);
        add(inputValue);

        setBackground(Color.cyan);

        try {
            Object value = filledFields.get(column.name);
            inputValue.setText(value.toString());
        } catch (NullPointerException ex) {
            //
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        setVisible(true);
    }

    String getValue() {
        return inputValue.getText();
    }
}