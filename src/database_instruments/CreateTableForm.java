package database_instruments;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class CreateTableForm extends JFrame {
    final ArrayList<DBFieldPanel> panels;
    final JButton btnAddField;
    final JButton btnCreate;
    final JPanel mainPanel;
    final JTextField inputTableName;

    final PosgtresDB db;

    public CreateTableForm(PosgtresDB db) {
        this.db = db;

        mainPanel = new JPanel();
        panels = new ArrayList<>();

        setSize(600, 400);
        mainPanel.setBounds(0, 0, 600, 400);
        add(mainPanel);

        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.blue);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        btnAddField = new JButton("Add field");
        btnAddField.setBounds(460, 40, 120, 20);
        mainPanel.add(btnAddField);

        btnCreate = new JButton("Create table");
        btnCreate.setBounds(460, 65, 120, 20);
        mainPanel.add(btnCreate);

        inputTableName = new JTextField();
        inputTableName.setBounds(130, 320, 150, 20);
        mainPanel.add(inputTableName);
        inputTableName.setBackground(Color.cyan);

        JLabel lblTableName = new JLabel("Название таблицы");
        lblTableName.setBounds(10, 320, 110, 20);
        mainPanel.add(lblTableName);

        btnCreate.addActionListener(this::createTable);
        btnAddField.addActionListener(this::addField);

        setVisible(true);
    }

    void addField(ActionEvent event) {
        DBFieldPanel panel = new DBFieldPanel();
        panel.setBounds(5, 40 * panels.size(), 450, 30);

        mainPanel.add(panel);
        int number = panels.size();
        panel.btnDelete.addActionListener((ActionEvent event2) -> deletePanel(number));
        panels.add(panel);
        mainPanel.repaint();
    }

    void deletePanel(int index) {
        mainPanel.remove(panels.get(index));
        panels.remove(index);
        for (int i = index; i < panels.size(); i++) {
            panels.get(i).setBounds(5, 40 * i, 450, 30);
            panels.get(i).btnDelete.removeActionListener(panels.get(i).btnDelete.getActionListeners()[0]);
            int number = i;
            panels.get(i).btnDelete.addActionListener((ActionEvent event2) -> deletePanel(number));
        }
        mainPanel.repaint();
    }

    void createTable(ActionEvent event) {

        if (panels.size() == 0) {
            JOptionPane.showMessageDialog(this, "Нет полей", "Error", JOptionPane.ERROR_MESSAGE);
        }

        TableColumn[] columns = new TableColumn[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            columns[i] = panels.get(i).getColumnInfo();
        }
        boolean isValid = true;
        for (TableColumn column : columns) {
            if (column == null) {
                isValid = false;
                break;
            }
        }
        String tableName = inputTableName.getText();
        if (tableName == null || tableName.equals("")) {
            isValid = false;
        }
        if (!isValid) {
            JOptionPane.showMessageDialog(this, "Не все поля заполнены", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            db.createTable(tableName, columns);
            JOptionPane.showMessageDialog(this, "OK", "Сохранено", JOptionPane.INFORMATION_MESSAGE);
            setVisible(false);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class DBFieldPanel extends JPanel {
    final JTextField fieldName;
    final JComboBox<String> fieldType;
    final JButton btnDelete;

    //TODO more types when support in DB class

    final static String[] possibleTypes = {
            "varchar(100)",
            "integer",
            "date",
            "text",
    };

    public DBFieldPanel() {
        setLayout(null);
        JLabel lblFieldName = new JLabel("Название поля");
        lblFieldName.setBounds(0, 5, 90, 20);
        add(lblFieldName);

        fieldName = new JTextField();
        fieldName.setBounds(100, 5, 100, 20);
        add(fieldName);

        JLabel lblType = new JLabel("Тип поля");
        lblType.setBounds(210, 5, 60, 20);
        add(lblType);

        fieldType = new JComboBox<>(possibleTypes);
        fieldType.setBounds(270, 5, 100, 20);
        add(fieldType);

        btnDelete = new JButton("x");
        btnDelete.setBounds(380, 5, 50, 20);
        add(btnDelete);
        setBackground(Color.red);
        setVisible(true);
    }

    public TableColumn getColumnInfo() {
        String name = fieldName.getText();
        String type = (String) fieldType.getSelectedItem();

        if (name == null || type == null) {
            return null;
        }
        return new TableColumn(name, type);
    }
}