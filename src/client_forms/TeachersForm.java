package client_forms;

import database_instruments.PosgtresDB;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class TeachersForm extends JFrame {
    PosgtresDB db;

    ArrayList<StudTeacherPanel> abiturs;

    JScrollPane scroll;
    JPanel abitursPanel;

    TeachersForm(PosgtresDB db) {
        this.db = db;
        abiturs = new ArrayList<>();
        setLayout(null);
        setVisible(true);
        setTitle("Распределение преподователей / «Приложение Magistr»");
        setSize(780, 550);
        getContentPane().setBackground(Color.cyan);

        JLabel lblLoad1 = new JLabel("Нажмите, чтобы загрузить данные из БД");
        lblLoad1.setBounds(10, 320, 270, 20);
        add(lblLoad1);
        JLabel lblLoad2 = new JLabel("и распределить преподовтаелей");
        lblLoad2.setBounds(10, 340, 270, 20);
        add(lblLoad2);

        JButton btnLoad = new JButton("Сформировать");
        btnLoad.setBounds(10, 360, 150, 20);
        add(btnLoad);

        JLabel lblSet = new JLabel("Нажмите, чтобы распределить");
        lblSet.setBounds(10, 390, 270, 20);
        add(lblSet);

        JButton btnSet = new JButton("Распределить");
        btnSet.setBounds(10, 410, 150, 20);
        add(btnSet);

        JLabel lblSave = new JLabel("Нажмите, чтобы сохранить данные в БД");
        lblSave.setBounds(10, 450, 270, 20);
        add(lblSave);
        JButton btnSave = new JButton("Сохранить");
        btnSave.setBounds(10, 470, 150, 20);
        add(btnSave);

        abitursPanel = new JPanel();
        abitursPanel.setLayout(null);

        scroll = new JScrollPane(abitursPanel);
        add(scroll);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBounds(0, 10, 750, 250);

        btnLoad.addActionListener((event) -> loadAbitur());
        btnSet.addActionListener((event) -> setPrep());
        btnSave.addActionListener((event) -> saveGroups());
    }

    void loadAbitur() {
        for (StudTeacherPanel panel : abiturs) {
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
            Integer[] teachersId = new Integer[abiturNames.length];
            String[] teachers = new String[abiturNames.length];

            for (int i = 0; i < category.length; i++) {
                Map<String, ArrayList<Object>> card = db.selectWhere("student_card", "student_id=" + stud_ids[i]);
                if (card.get("id").size() == 0) {
                    no_zk[i] = "";
                    card_ids[i] = -1;
                    dates[i] = new Date();
                    teachers[i] = "";
                    teachersId[i] = -1;
                } else {
                    no_zk[i] = (String) card.get("no_zk").get(0);
                    card_ids[i] = (Integer) card.get("id").get(0);
                    dates[i] = new Date((Long) card.get("date").get(0));
                    teachersId[i] = (Integer) card.get("teacher_id").get(0);
                    if (teachersId[i] == null) {
                        teachersId[i] = -1;
                        teachers[i] = "";
                    } else {
                        Map<String, ArrayList<Object>> teacherTable = db.selectWhere("teacher", "id=" + teachersId[i]);
                        teachers[i] =
                                (String) teacherTable.get("name").get(0)
                                        + " " + teacherTable.get("patronymic").get(0)
                                        + " " + teacherTable.get("surname").get(0);
                    }


                }
            }

            for (int i = 0; i < category.length; i++) {
                StudTeacherPanel panel = new StudTeacherPanel(stud_ids[i], abiturNames[i], category[i], no_zk[i],
                        card_ids[i], summBall[i], spec[i], dates[i], teachers[i], teachersId[i]);

                panel.setBounds(0, 40 * i, 750, 30);
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

    void setPrep() {

        if (abiturs.size() == 0) {
            JOptionPane.showMessageDialog(this, "Данные не загружены");
            return;
        }

        Random r = new Random();
        try {

            Map<String, ArrayList<Object>> teachers = db.select("teacher");
            Map<Integer, Integer> studsForId = new HashMap<>();
            Map<Integer, String> NamesId = new HashMap<>();
            Integer[] teachIds = Arrays.copyOf(teachers.get("id").toArray(), teachers.get("id").size(), Integer[].class);

            String[] teachNames = Arrays.copyOf(teachers.get("name").toArray(), teachers.get("name").size(), String[].class);
            String[] teachSurnames = Arrays.copyOf(teachers.get("surname").toArray(), teachers.get("surname").size(), String[].class);
            String[] teachPatrs = Arrays.copyOf(teachers.get("patronymic").toArray(), teachers.get("patronymic").size(), String[].class);


            for (int i = 0; i < teachIds.length; i++) {
                int id = teachIds[i];
                Map<String, ArrayList<Object>> studCards = db.selectWhere("student_card", "teacher_id=" + id);
                studsForId.put(id, studCards.get("id").size());
                NamesId.put(id, teachNames[i] + " " + teachPatrs[i] + " " + teachSurnames[i]);
            }


            for (int i = 0; i < abiturs.size(); i++) {
                StudTeacherPanel panel = abiturs.get(i);
                if (panel.teacherId == -1) {
                    int teacherId = teachIds[0];
                    while (studsForId.get(teacherId) >= 2) {
                        teacherId = teachIds[r.nextInt(teachIds.length)];
                    }
                    panel.setTeacher(NamesId.get(teacherId), teacherId);
                    studsForId.put(teacherId, studsForId.get(teacherId) + 1);
//                    String zkNumber = (date.getYear() % 100) + "13" + formatInt(i);
//                    //int ind = r.nextInt(groups.length);
//                    panel.setZk(zkNumber, date);
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
            for (StudTeacherPanel panel : abiturs) {
                if (panel.card_id == -1) {
                    db.insert("student_card", new String[]{
                                    "no_zk",
                                    "no_sb",
                                    "date",
                                    "student_id",
                                    "teacher_id"
                            },
                            new Object[]{
                                    panel.no_zk,
                                    panel.no_zk,
                                    new Date(),
                                    panel.id,
                                    panel.teacherId,
                            });
                } else {
                    db.update("student_card", "id=" + panel.card_id,
                            new String[]{
                                    "teacher_id"
                            },
                            new Object[]{
                                    panel.teacherId
                            });
                }

            }
            JOptionPane.showMessageDialog(this, "OK");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}

class StudTeacherPanel extends JPanel {
    String name;
    String type;
    int id;
    JLabel lbl;

    String no_zk;

    int card_id;
    int sumBall;
    String spec;

    String teacher;
    int teacherId;
    Date date;

    StudTeacherPanel(int id, String name, String type, String no_zk, int card_id, int sumBall, String spec, Date date,
                     String teacher, int teacherId) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.card_id = card_id;
        this.no_zk = no_zk;
        this.sumBall = sumBall;
        this.spec = spec;

        this.teacher = teacher;
        this.teacherId = teacherId;

        this.date = date;

        setLayout(null);
        SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
        String zk_string = no_zk.equals("") ? "" : " ЗК#" + no_zk + " выдана " + sd.format(date);
        String teachStr = teacherId == -1 ? "" : " препоодаватель =" + teacher;
        lbl = new JLabel(name + " - " + type + " " + spec + " балл= " + sumBall + zk_string + teachStr);
        lbl.setBounds(0, 0, 750, 20);
        add(lbl);
        setVisible(true);
    }

    void setTeacher(String t, int id) {
        this.teacher = t;
        this.teacherId = id;
        SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
        lbl.setText(name + " - " + type + " " + spec + " балл= " + sumBall + " ЗК# " + no_zk + " выдана " + sd.format(date)
                + " препоодаватель =" + teacher);
    }
}
