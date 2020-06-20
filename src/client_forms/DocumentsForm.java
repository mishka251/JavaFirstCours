package client_forms;

import database_instruments.PosgtresDB;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class DocumentsForm extends JFrame {
    PosgtresDB db;

    // JComboBox<String> spec;
    // Integer[] specIds;

    ArrayList<StudZkPanel> abiturs;

    JScrollPane scroll;
    JPanel abitursPanel;

    DocumentsForm(PosgtresDB db) {
        this.db = db;
        abiturs = new ArrayList<>();
        setLayout(null);
        setVisible(true);
        setSize(500, 450);
//        try {
//            Map<String, ArrayList<Object>> specTable = db.select("speciality");
//            String[] specNames = Arrays.copyOf(specTable.get("name").toArray(), specTable.get("name").size(), String[].class);
//            specIds = Arrays.copyOf(specTable.get("id").toArray(), specTable.get("id").size(), Integer[].class);
//
//            JLabel lblSpec = new JLabel("специальность");
//            lblSpec.setBounds(10, 10, 100, 20);
//            add(lblSpec);
//
//            spec = new JComboBox<>(specNames);
//            spec.setBounds(110, 10, 100, 20);
//            add(spec);
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, ex.getMessage());
//        }

        JButton btnLoad = new JButton("Загрузить");
        btnLoad.setBounds(400, 50, 90, 20);
        add(btnLoad);

        JButton btnSet = new JButton("Выдать ЗК");
        btnSet.setBounds(400, 80, 90, 20);
        add(btnSet);

        JButton btnSave = new JButton("Сохранить");
        btnSave.setBounds(400, 110, 90, 20);
        add(btnSave);

        abitursPanel = new JPanel();
        abitursPanel.setLayout(null);

        scroll = new JScrollPane(abitursPanel);
        add(scroll);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBounds(10, 50, 300, 250);

        btnLoad.addActionListener((event) -> loadAbitur());
        btnSet.addActionListener((event) -> setZk());
        btnSave.addActionListener((event) -> saveGroups());
    }

    void loadAbitur() {
        // Integer specId = specIds[this.spec.getSelectedIndex()];
        for (StudZkPanel panel : abiturs) {
            abitursPanel.remove(panel);
        }
        try {
            Map<String, ArrayList<Object>> specTable = db.select("student");
            Integer[] stud_ids = Arrays.copyOf(specTable.get("id").toArray(), specTable.get("id").size(), Integer[].class);
            Integer[] ab_ids = Arrays.copyOf(specTable.get("abitur_id").toArray(), specTable.get("id").size(), Integer[].class);

            // String[] names = new String[ab_ids.length]; //Arrays.copyOf(specTable.get("name").toArray(), specTable.get("name").size(), String[].class);
            // String[] surnames = new String[ab_ids.length]; //Arrays.copyOf(specTable.get("surname").toArray(), specTable.get("surname").size(), String[].class);
            Integer[] types_ids = new Integer[ab_ids.length]; //Arrays.copyOf(specTable.get("type_id").toArray(), specTable.get("type_id").size(), Integer[].class);
            String[] abiturNames = new String[ab_ids.length];
            for (int i = 0; i < ab_ids.length; i++) {
                Map<String, ArrayList<Object>> abTable = db.selectWhere("abiturient", "id=" + ab_ids[i]);

                abiturNames[i] = abTable.get("surname").get(0) + " " + abTable.get("name").get(0);
                types_ids[i] = (Integer) abTable.get("type_id").get(0);
            }


//            for (int i = 0; i < names.length; i++) {
//                abiturNames[i] = surnames[i] + " " + names[i];
//            }


            for (int i = 0; i < abiturNames.length; i++) {
                for (int j = i + 1; j < abiturNames.length; j++) {
                    if (types_ids[i] < types_ids[j]) {

                        Integer tmp3 = types_ids[i];
                        types_ids[i] = types_ids[j];
                        types_ids[j] = tmp3;

                        String tmp2 = abiturNames[i];
                        abiturNames[i] = abiturNames[j];
                        abiturNames[j] = tmp2;

                        int tmp = ab_ids[i];
                        ab_ids[i] = ab_ids[j];
                        ab_ids[j] = tmp;

                        tmp = stud_ids[i];
                        stud_ids[i] = stud_ids[j];
                        stud_ids[j] = tmp;
                    }
                }
            }

            String[] category = new String[abiturNames.length];
            for (int i = 0; i < category.length; i++) {
                Map<String, ArrayList<Object>> type = db.selectWhere("abiturient_type", "id=" + types_ids[i]);
                category[i] = (String) type.get("name").get(0);
            }

            String[] no_zk = new String[abiturNames.length];
            Integer[] card_ids = new Integer[abiturNames.length];
            for (int i = 0; i < category.length; i++) {
                Map<String, ArrayList<Object>> card = db.selectWhere("student_card", "student_id=" + stud_ids[i]);
                if (card.get("id").size() == 0) {
                    no_zk[i] = "";
                    card_ids[i] = -1;
                } else {
                    no_zk[i] = (String) card.get("no_zk").get(0);
                    card_ids[i] = (Integer) card.get("id").get(0);
                }
            }

            for (int i = 0; i < category.length; i++) {
                StudZkPanel panel = new StudZkPanel(stud_ids[i], abiturNames[i], category[i], no_zk[i], card_ids[i]);
                panel.setBounds(10, 40 * i, 250, 30);
                abitursPanel.add(panel);
                abiturs.add(panel);
            }
            abitursPanel.setPreferredSize(new Dimension(300, 40 + 30 * category.length));
            scroll.setViewportView(abitursPanel);
            repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    String formatInt(int val) {
        if (val < 10) {
            return "000" + val;
        }
        if (val < 100) {
            return "00" + val;
        }
        if (val < 1000) {
            return "0" + val;
        }
        return Integer.toString(val);
    }

    void setZk() {
        Date date = new Date();
        try {

            for (int i =0; i<abiturs.size(); i++) {
                StudZkPanel panel = abiturs.get(i);
                if (panel.no_zk.equals("")) {
                    String zkNumber = (date.getYear() % 100) + "13" + formatInt(i);
                    //int ind = r.nextInt(groups.length);
                    panel.setZk(zkNumber);
                }
            }
            repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    void saveGroups() {
        try {
            for (StudZkPanel panel : abiturs) {
                if (panel.card_id == -1) {
                    db.insert("student_card", new String[]{
                                    "no_zk",
                                    "no_sb",
                                    "date",
                                    "student_id"
                            },
                            new Object[]{
                                    panel.no_zk,
                                    panel.no_zk,
                                    new Date(),
                                    panel.id
                            });
                }

            }
            JOptionPane.showMessageDialog(this, "OK");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}

class StudZkPanel extends JPanel {
    String name;
    String type;
    int id;
    JLabel lbl;

    String no_zk;

    int card_id;


    StudZkPanel(int id, String name, String type, String no_zk, int card_id) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.card_id = card_id;
        this.no_zk = no_zk;

        setLayout(null);
        lbl = new JLabel(name + " - " + type + " " + no_zk);
        lbl.setBounds(0, 0, 250, 20);
        add(lbl);
        setVisible(true);
    }

    void setZk(String zk) {
        this.no_zk = zk;
        lbl.setText(name + " - " + type + " - " + " " + no_zk);
    }
}
