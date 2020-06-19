package client_forms;

import database_instruments.PosgtresDB;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class RaspredForm extends JFrame {
    PosgtresDB db;

    JComboBox<String> spec;
    Integer[] specIds;

    ArrayList<AbiturPanel> abiturs;

    JScrollPane scroll;
    JPanel abitursPanel;

    RaspredForm(PosgtresDB db) {
        this.db = db;
        abiturs = new ArrayList<>();
        setLayout(null);
        setVisible(true);
        setSize(500, 450);
        try {
            Map<String, ArrayList<Object>> specTable = db.select("speciality");
            String[] specNames = Arrays.copyOf(specTable.get("name").toArray(), specTable.get("name").size(), String[].class);
            specIds = Arrays.copyOf(specTable.get("id").toArray(), specTable.get("id").size(), Integer[].class);

            JLabel lblSpec = new JLabel("специальность");
            lblSpec.setBounds(10, 10, 100, 20);
            add(lblSpec);

            spec = new JComboBox<>(specNames);
            spec.setBounds(110, 10, 100, 20);
            add(spec);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }

        JButton btnLoad = new JButton("Load");
        btnLoad.setBounds(400, 50, 90, 20);
        add(btnLoad);

        JButton btnSet = new JButton("Set groups");
        btnSet.setBounds(400, 80, 90, 20);
        add(btnSet);

        JButton btnSave = new JButton("Save");
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
        btnSet.addActionListener((event) -> setGroups());
        btnSave.addActionListener((event) -> saveGroups());
    }

    void loadAbitur() {
        Integer specId = specIds[this.spec.getSelectedIndex()];
        for (AbiturPanel panel : abiturs) {
            abitursPanel.remove(panel);
        }
        try {
            Map<String, ArrayList<Object>> specTable = db.selectWhere("abiturient", "spec_id=" + specId);
            String[] names = Arrays.copyOf(specTable.get("name").toArray(), specTable.get("name").size(), String[].class);
            String[] surnames = Arrays.copyOf(specTable.get("surname").toArray(), specTable.get("surname").size(), String[].class);
            Integer[] math_ege = Arrays.copyOf(specTable.get("math_ege").toArray(), specTable.get("math_ege").size(), Integer[].class);
            Integer[] rus_ege = Arrays.copyOf(specTable.get("rus_ege").toArray(), specTable.get("rus_ege").size(), Integer[].class);
            Integer[] ing_ege = Arrays.copyOf(specTable.get("inf_ege").toArray(), specTable.get("inf_ege").size(), Integer[].class);

            Integer[] types_ids = Arrays.copyOf(specTable.get("type_id").toArray(), specTable.get("type_id").size(), Integer[].class);
            Integer[] ab_ids = Arrays.copyOf(specTable.get("id").toArray(), specTable.get("id").size(), Integer[].class);


            Integer[] balls = new Integer[rus_ege.length];
            for (int i = 0; i < balls.length; i++) {
                balls[i] = math_ege[i] + rus_ege[i] + ing_ege[i];
            }

            String[] abiturNames = new String[names.length];
            for (int i = 0; i < names.length; i++) {
                abiturNames[i] = surnames[i] + " " + names[i];
            }

            for (int i = 0; i < abiturNames.length; i++) {
                for (int j = i + 1; j < abiturNames.length; j++) {
                    if (balls[i] < balls[j]) {
                        int tmp = balls[i];
                        balls[i] = balls[j];
                        balls[j] = tmp;

                        Integer tmp2 = types_ids[i];
                        types_ids[i] = types_ids[j];
                        types_ids[j] = tmp2;

                        String tmp3 = abiturNames[i];
                        abiturNames[i] = abiturNames[j];
                        abiturNames[j] = tmp3;

                        tmp = ab_ids[i];
                        ab_ids[i]=ab_ids[j];
                        ab_ids[j]=tmp;
                    }
                }
            }

            for (int i = 0; i < abiturNames.length; i++) {
                for (int j = i + 1; j < abiturNames.length; j++) {
                    if (types_ids[i] < types_ids[j]) {
                        int tmp = balls[i];
                        balls[i] = balls[j];
                        balls[j] = tmp;

                        Integer tmp3 = types_ids[i];
                        types_ids[i] = types_ids[j];
                        types_ids[j] = tmp3;

                        String tmp2 = abiturNames[i];
                        abiturNames[i] = abiturNames[j];
                        abiturNames[j] = tmp2;

                        tmp = ab_ids[i];
                        ab_ids[i]=ab_ids[j];
                        ab_ids[j]=tmp;
                    }
                }
            }

            String[] category = new String[abiturNames.length];
            for (int i = 0; i < category.length; i++) {
                Map<String, ArrayList<Object>> type = db.selectWhere("abiturient_type", "id=" + types_ids[i]);
                category[i] = (String) type.get("name").get(0);
            }

            String[] group = new String[abiturNames.length];
            Integer[] groupId = new Integer[abiturNames.length];
            for (int i = 0; i < category.length; i++) {
                Map<String, ArrayList<Object>> stud = db.selectWhere("student", "abitur_id=" + ab_ids[i]);
                if (stud.get("id").size() == 0) {
                    group[i] = "";
                    groupId[i] = -1;
                } else {
                    Map<String, ArrayList<Object>> gr = db.selectWhere("group", "id=" + stud.get("group_id").get(0));
                    Integer groupSpecsIds = (Integer) gr.get("speciality_id").get(0);

                    Map<String, ArrayList<Object>> spec = db.selectWhere("speciality", "id=" + groupSpecsIds);
                    String name = (String) spec.get("name").get(0);

                    group[i] = name + " " + gr.get("number_code").get(0);
                    groupId[i] = (Integer) gr.get("id").get(0);
                }
            }

            for (int i = 0; i < category.length; i++) {
                AbiturPanel panel = new AbiturPanel(ab_ids[i], names[i], category[i], balls[i], group[i], groupId[i]);
                panel.setBounds(10, 40 * i, 200, 30);
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

    void setGroups() {
        Integer specId = specIds[this.spec.getSelectedIndex()];
        Random r = new Random();
        try {

            Map<String, ArrayList<Object>> gr = db.selectWhere("group", "speciality_id=" + specId);
            String[] groupNumbers = Arrays.copyOf(gr.get("number_code").toArray(), gr.get("number_code").size(), String[].class);
            Integer[] groupSpecsIds = Arrays.copyOf(gr.get("speciality_id").toArray(), gr.get("speciality_id").size(), Integer[].class);
            Integer[] groupIds = Arrays.copyOf(gr.get("id").toArray(), gr.get("id").size(), Integer[].class);


            String[] groups = new String[groupNumbers.length];

            for (int i = 0; i < groups.length; i++) {
                Map<String, ArrayList<Object>> spec = db.selectWhere("speciality", "id=" + groupSpecsIds[i]);
                String name = (String) spec.get("name").get(0);
                groups[i] = name + " " + groupNumbers[i];
            }


            for (AbiturPanel panel : abiturs) {
                if (panel.group.equals("")) {
                    int ind = r.nextInt(groups.length);
                    panel.setGroup(groups[ind], groupIds[ind]);
                }
            }
            repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    void saveGroups() {
        try {
            for (AbiturPanel panel : abiturs) {
                db.insert("student", new String[]{
                                "group_id",
                                "abitur_id",
                        },
                        new Object[]{
                                panel.groupId,
                                panel.id
                        });
            }
            JOptionPane.showMessageDialog(this, "OK");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}

class AbiturPanel extends JPanel {
    String name;
    String type;
    int sumBall;
    String group;
    int id;
    JLabel lbl;

    int groupId;

    AbiturPanel(int id, String name, String type, int summBall, String group, int groupId) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.sumBall = summBall;
        this.group = group;
        this.groupId = groupId;

        setLayout(null);
        lbl = new JLabel(name + " - " + type + " - " + summBall + " " + group);
        lbl.setBounds(0, 0, 200, 20);
        add(lbl);
        setVisible(true);
    }

    void setGroup(String group, int groupId) {
        this.group = group;
        this.groupId = groupId;
        lbl.setText(name + " - " + type + " - " + sumBall + " " + group);
    }
}
