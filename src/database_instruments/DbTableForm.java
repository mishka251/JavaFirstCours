package database_instruments;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DbTableForm extends JFrame {

    PosgtresDB db;
    String tableName;
    JTable table;
    DefaultTableModel tableModel;
    JButton btnDelete;
    JButton btnDrop;
    JTextField inputDelete;

    JComboBox<String> cmbTable;
    JComboBox<String> cmbDeleteField;
    JButton btnInsert;
    ArrayList<String> extraReadOnlyColumns = new ArrayList<String>();

    public DbTableForm(PosgtresDB db, String tableName) {
        this.db = db;
        this.tableName = tableName;
        extraReadOnlyColumns.add("id");

        setLayout(null);
        setSize(700, 400);
        setVisible(true);

        JPanel panel1 = new JPanel();
        panel1.setBounds(20, 50, 660, 230);
        panel1.setVisible(true);
       // add(panel1);

        JLabel lblDeletedField = new JLabel("Поле для удаления");
        lblDeletedField.setBounds(10, 10, 130, 20);
        add(lblDeletedField);

        cmbDeleteField = new JComboBox<>();
        cmbDeleteField.setBounds(150, 10, 120, 20);
        add(cmbDeleteField);

        JLabel lblInput = new JLabel("Значение удаляемого");
        lblInput.setBounds(300, 10, 130, 20);
        add(lblInput);

        inputDelete = new JTextField();
        inputDelete.setBounds(440, 10, 100, 20);
        add(inputDelete);

        btnDelete = new JButton("Удалить строки");
        btnDelete.setBounds(550, 10, 130, 20);
        add(btnDelete);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                String columnName = this.getColumnName(column);
                for(String colName:extraReadOnlyColumns){
                    if(colName.equals(columnName)){
                        return false;
                    }
                }
                return true;// !.equals("id");//super.isCellEditable(row, column);
            }
        };

        table = new JTable(tableModel);
        JScrollPane scroll =  new JScrollPane(table);
        scroll.setBounds(20, 50, 660, 230);
        add(scroll);
       // panel1.add();

        if (tableName == null) {
            setTitle("Редактор таблиц");
            JLabel lblTable = new JLabel("Table");
            lblTable.setBounds(0, 5, 50, 20);
            add(lblTable);

            cmbTable = new JComboBox<>();
            cmbTable.setBounds(60, 10, 60, 20);
            cmbTable.addActionListener((event) -> {
                fillTable();
                this.tableName = (String) cmbTable.getSelectedItem();
            });
            add(cmbTable);
            loadTables();

            btnDrop = new JButton("Drop table");
            btnDrop.setBounds(550, 330, 90, 20);
            add(btnDrop);
            btnDrop.addActionListener(this::dropTable);

        } else {
            setTitle("Редактор таблицы " + tableName);
            fillTable();
        }

        //this.fillTable();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        btnInsert = new JButton("Добавить строку");
        btnInsert.setBounds(530, 300, 140, 20);
        add(btnInsert);

        JButton btnUpdate = new JButton("Обновить БД");
        btnUpdate.setBounds(400, 300, 120, 20);
        add(btnUpdate);

        btnUpdate.addActionListener((event) -> this.update());
        btnDelete.addActionListener(this::delete);
        btnInsert.addActionListener((event) -> new CreateInstanceForm(db, tableName, new HashMap<>()));
    }

    public DbTableForm(PosgtresDB db, String tableName, String[] extraReadOnlyColumns){
        this(db, tableName);
        this.extraReadOnlyColumns.addAll(Arrays.asList(extraReadOnlyColumns));
    }

    void loadTables() {
        try {
            ArrayList<String> tables = db.getTableNames();
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cmbTable.getModel();
            model.removeAllElements();
            for (String table : tables) {
                model.addElement(table);
            }

            cmbTable.setModel(model);
            this.fillTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void dropTable(ActionEvent event) {
        //(String) cmbTable.getSelectedItem();
        int dialogResult = JOptionPane.showConfirmDialog(this, "Точно удалить таблицу?");
        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                db.dropTable(tableName);
                JOptionPane.showMessageDialog(this, "OK", "Dropped", JOptionPane.INFORMATION_MESSAGE);
                if (cmbTable != null) {
                    loadTables();
                } else {
                    dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    void delete(ActionEvent event) {
        String deleteCondition = inputDelete.getText();
        // String tableName = (String) cmbTable.getSelectedItem();
        String deleteField = (String) cmbDeleteField.getSelectedItem();
        if (deleteCondition == null || deleteCondition.equals("")
                || deleteField == null || deleteField.equals("")) {
            JOptionPane.showMessageDialog(this, "Введите значение для удаления");
            return;
        }
        try {
            db.delete(tableName, deleteField, deleteCondition);
            JOptionPane.showMessageDialog(this, "OK", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            fillTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void fillTable() {
        try {
            while (tableModel.getRowCount() > 0) {
                tableModel.removeRow(0);
            }
            // String tableName = (String) cmbTable.getSelectedItem();
            Map<String, ArrayList<Object>> data = db.select(tableName);
            String[] columnNames = Arrays.copyOf(data.keySet().toArray(), data.keySet().size(), String[].class);
            int rows = data.get(columnNames[0]).size();
            int columns = columnNames.length;
            Object[][] values = new Object[rows][];
            for (int i = 0; i < rows; i++) {
                values[i] = new Object[columns];
                for (int j = 0; j < columns; j++) {
                    values[i][j] = data.get(columnNames[j]).get(i);
                }
            }
            tableModel.setColumnIdentifiers(columnNames);
            // Наполнение модели данными
            for (Object[] value : values) {
                tableModel.addRow(value);
            }

            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cmbDeleteField.getModel();
            model.removeAllElements();
            for (Object columnName : columnNames) {
                model.addElement((String) columnName);
            }
            cmbDeleteField.setModel(model);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void update() {
        int idColumnIndex = table.getColumn("id").getModelIndex();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            long id = (int) tableModel.getValueAt(i, idColumnIndex);
            ArrayList<String> colNames = new ArrayList<>();
            ArrayList<Object> colValues = new ArrayList<>();

            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                String name = tableModel.getColumnName(j);
                Object val = tableModel.getValueAt(i, j);
                if (!name.equals("id")) {
                    colNames.add(name);
                    colValues.add(val);
                }
            }
            try {
                db.update((String) tableName, "id=" + id,
                        Arrays.copyOf(colNames.toArray(), colNames.size(), String[].class), colValues.toArray());
                //JOptionPane.showMessageDialog(this, "OK");
            } catch (Exception ex) {
                // JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        this.fillTable();
    }
}
