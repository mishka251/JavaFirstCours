package client_forms;

import database_instruments.PosgtresDB;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DocumentsForm extends JFrame {
    PosgtresDB db;

    ArrayList<StudZkPanel> abiturs;

    JScrollPane scroll;
    JPanel abitursPanel;

    DocumentsForm(PosgtresDB db) {
        this.db = db;
        abiturs = new ArrayList<>();
        setLayout(null);
        setVisible(true);
        setTitle("Формирование учетных карточек и ЗК / «Приложение Magistr»");
        setSize(730, 450);
        getContentPane().setBackground(Color.orange);

        JLabel lblLoad1 = new JLabel("Нажмите, чтобы загрузить данные из БД");
        lblLoad1.setBounds(420, 30, 270, 20);
        add(lblLoad1);
        JLabel lblLoad2 = new JLabel("и сформировать учетные карточки");
        lblLoad2.setBounds(420, 40, 270, 20);
        add(lblLoad2);

        JButton btnLoad = new JButton("Сформировать");
        btnLoad.setBounds(420, 60, 150, 20);
        add(btnLoad);

        JLabel lblSet = new JLabel("Нажмите, чтобы сформировать и выдать ЗК");
        lblSet.setBounds(420, 90, 270, 20);
        add(lblSet);

        JButton btnSet = new JButton("Выдать ЗК");
        btnSet.setBounds(420, 110, 150, 20);
        add(btnSet);

        JLabel lblSave = new JLabel("Нажмите, чтобы сохранить данные в БД");
        lblSave.setBounds(420, 150, 270, 20);
        add(lblSave);
        JButton btnSave = new JButton("Сохранить");
        btnSave.setBounds(420, 170, 150, 20);
        add(btnSave);

        abitursPanel = new JPanel();
        abitursPanel.setLayout(null);

        scroll = new JScrollPane(abitursPanel);
        add(scroll);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBounds(0, 50, 400, 250);

        btnLoad.addActionListener((event) -> loadAbitur());
        btnSet.addActionListener((event) -> setZk());
        btnSave.addActionListener((event) -> saveGroups());
    }

    void loadAbitur() {
        for (StudZkPanel panel : abiturs) {
            abitursPanel.remove(panel);
        }
        try {
            Map<String, ArrayList<Object>> specTable = db.select("student");
            Integer[] stud_ids = Arrays.copyOf(specTable.get("id").toArray(), specTable.get("id").size(), Integer[].class);
            Integer[] ab_ids = Arrays.copyOf(specTable.get("abitur_id").toArray(), specTable.get("id").size(), Integer[].class);

            if (stud_ids.length == 0) {
                JOptionPane.showMessageDialog(this, "Абитуриенты не распределены");
                return;
            }

            Integer[] types_ids = new Integer[ab_ids.length];
            String[] abiturNames = new String[ab_ids.length];
            for (int i = 0; i < ab_ids.length; i++) {
                Map<String, ArrayList<Object>> abTable = db.selectWhere("abiturient", "id=" + ab_ids[i]);

                abiturNames[i] = abTable.get("surname").get(0) + " " + abTable.get("name").get(0);
                types_ids[i] = (Integer) abTable.get("type_id").get(0);
            }


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
            String[] spec = new String[abiturNames.length];
            int[] summBall = new int[abiturNames.length];

            for (int i = 0; i < category.length; i++) {
                Map<String, ArrayList<Object>> type = db.selectWhere("abiturient_type", "id=" + types_ids[i]);
                category[i] = (String) type.get("name").get(0);

                Map<String, ArrayList<Object>> ab = db.selectWhere("abiturient", "id=" + ab_ids[i]);
                summBall[i] = (Integer) ab.get("ball").get(0);

                int spec_id = (Integer) ab.get("spec_id").get(0);
                Map<String, ArrayList<Object>> specTable2 = db.selectWhere("speciality", "id=" + spec_id);
                spec[i] = (String) specTable2.get("name").get(0);
            }

            String[] no_zk = new String[abiturNames.length];
            Integer[] card_ids = new Integer[abiturNames.length];
            Date[] dates = new Date[abiturNames.length];

            for (int i = 0; i < category.length; i++) {
                Map<String, ArrayList<Object>> card = db.selectWhere("student_card", "student_id=" + stud_ids[i]);
                if (card.get("id").size() == 0) {
                    no_zk[i] = "";
                    card_ids[i] = -1;
                    dates[i] = new Date();
                } else {
                    no_zk[i] = (String) card.get("no_zk").get(0);
                    card_ids[i] = (Integer) card.get("id").get(0);
                    dates[i] = new Date((Long) card.get("date").get(0));
                }
            }

            for (int i = 0; i < category.length; i++) {
                StudZkPanel panel = new StudZkPanel(stud_ids[i], abiturNames[i], category[i], no_zk[i], card_ids[i], summBall[i], spec[i], dates[i]);
                panel.setBounds(0, 40 * i, 450, 30);
                abitursPanel.add(panel);
                abiturs.add(panel);
            }
            abitursPanel.setPreferredSize(new Dimension(450, 40 + 30 * category.length));
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

        if (abiturs.size() == 0) {
            JOptionPane.showMessageDialog(this, "Данные не загружены");
            return;
        }

        Date date = new Date();
        try {

            for (int i = 0; i < abiturs.size(); i++) {
                StudZkPanel panel = abiturs.get(i);
                if (panel.no_zk.equals("")) {
                    String zkNumber = (date.getYear() % 100) + "13" + formatInt(i);
                    //int ind = r.nextInt(groups.length);
                    panel.setZk(zkNumber, date);
                }
            }
            repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    void saveGroups() {
        if (abiturs.size() == 0) {
            JOptionPane.showMessageDialog(this, "Данные не загружены");
            return;
        }
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
    int sumBall;
    String spec;

    StudZkPanel(int id, String name, String type, String no_zk, int card_id, int sumBall, String spec, Date date) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.card_id = card_id;
        this.no_zk = no_zk;
        this.sumBall = sumBall;
        this.spec = spec;

        setLayout(null);
        SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
        String zk_string = no_zk.equals("") ? "" : " ЗК#" + no_zk + " выдана " + sd.format(date);
        lbl = new JLabel(name + " - " + type + " " + spec + " балл= " + sumBall + zk_string);
        lbl.setBounds(0, 0, 450, 20);
        add(lbl);
        setVisible(true);
    }

    void setZk(String zk, Date date) {
        this.no_zk = zk;
        SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
        lbl.setText(name + " - " + type + " " + spec + " балл= " + sumBall + " ЗК# " + no_zk + " выдана " + sd.format(date));
    }
}
