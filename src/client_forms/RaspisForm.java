package client_forms;

import database_instruments.PosgtresDB;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class RaspisForm extends JFrame {
    PosgtresDB db;

    ArrayList<StudRaspisPanel> abiturs;

    JScrollPane scroll;
    JPanel abitursPanel;

    RaspisForm(PosgtresDB db) {
        this.db = db;
        abiturs = new ArrayList<>();
        setLayout(null);
        setVisible(true);
        setTitle("Расписание/ Приложение Magistr");
        setSize(780, 550);
        getContentPane().setBackground(Color.orange);

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
        for (StudRaspisPanel panel : abiturs) {
            abitursPanel.remove(panel);
        }
        try {
            Map<String, ArrayList<Object>> groupTable = db.select("group");
            Integer[] group_ids = Arrays.copyOf(groupTable.get("id").toArray(), groupTable.get("id").size(), Integer[].class);
            Integer[] spec_ids = Arrays.copyOf(groupTable.get("speciality_id").toArray(), groupTable.get("id").size(), Integer[].class);
            String[] gr_nums = Arrays.copyOf(groupTable.get("number_code").toArray(), groupTable.get("id").size(), String[].class);


            if (group_ids.length == 0) {
                JOptionPane.showMessageDialog(this, "Нет групп");
                return;
            }

            String[] groupNames = new String[group_ids.length];
            for (int i = 0; i < group_ids.length; i++) {
                Map<String, ArrayList<Object>> specTable = db.selectWhere("speciality", "id=" + spec_ids[i]);
                groupNames[i] = (String) specTable.get("name").get(0) + gr_nums[i];
            }

            for (int grInd = 0; grInd < group_ids.length; grInd++) {
                for (int weekDay = 1; weekDay <= 5; weekDay++) {
                    for (int paraNum = 1; paraNum <= 5; paraNum++) {

                        int subjId = -1;
                        int teachId = -1;
                        String subjName = "";
                        String teacherName = "";
                        int raspId = -1;

                        Map<String, ArrayList<Object>> raspTable = db.selectWhere("raspisanie",
                                "group_id=" + group_ids[grInd] + " AND week_day=" + weekDay + " AND para=" + paraNum);

                        if (raspTable.get("id").size() > 0) {
                            subjId = (Integer) raspTable.get("subj_id").get(0);
                            teachId = (Integer) raspTable.get("teacher_id").get(0);
                            raspId = (Integer) raspTable.get("id").get(0);
                            Map<String, ArrayList<Object>> teachTable =
                                    db.selectWhere("teacher", "id=" + teachId);

                            teacherName = (String) teachTable.get("name").get(0)
                                    + " " + (String) teachTable.get("patronymic").get(0)
                                    + " " + (String) teachTable.get("surname").get(0);

                            Map<String, ArrayList<Object>> sTable =
                                    db.selectWhere("subject", "id=" + subjId);

                            subjName = (String) sTable.get("name").get(0);

                        }


                        StudRaspisPanel panel =
                                new StudRaspisPanel(group_ids[grInd], groupNames[grInd], weekDay, paraNum,
                                        subjId, subjName, teachId, teacherName, raspId);

                        panel.setBounds(0, 40 * (grInd * 5 * 5 + (weekDay - 1) * 5 + (paraNum - 1)), 750, 30);
                        abitursPanel.add(panel);
                        abiturs.add(panel);

                    }
                }
            }

            abitursPanel.setPreferredSize(new Dimension(450, 40 + 30 * group_ids.length * 5 * 5));
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


            Map<String, ArrayList<Object>> subjTable = db.select("subject");
            Integer[] subj_ids = Arrays.copyOf(subjTable.get("id").toArray(), subjTable.get("id").size(), Integer[].class);
            String[] subj_nums = Arrays.copyOf(subjTable.get("name").toArray(), subjTable.get("id").size(), String[].class);


            for (int i = 0; i < abiturs.size(); i++) {
                StudRaspisPanel panel = abiturs.get(i);
                if (panel.raspId == -1) {

                    int subjInd = r.nextInt(subj_ids.length);
                    Map<String, ArrayList<Object>> teachTable = db.selectWhere("teacher", "subj_id=" + subj_ids[subjInd]);

                    int teacherInd = r.nextInt(teachTable.get("id").size());

                    int teacherId = (Integer) teachTable.get("id").get(teacherInd);
                    String teacherName = (String) teachTable.get("name").get(teacherInd)
                            + " " + (String) teachTable.get("patronymic").get(teacherInd)
                            + " " + (String) teachTable.get("surname").get(teacherInd);

                    panel.setTeacher(teacherName, teacherId, subj_ids[subjInd], subj_nums[subjInd]);

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
            for (StudRaspisPanel panel : abiturs) {
                if (panel.raspId == -1) {
                    db.insert("raspisanie", new String[]{
                                    "group_id",
                                    "teacher_id",
                                    "para",
                                    "week_day",
                            },
                            new Object[]{
                                    panel.groupId,
                                    panel.teacherId,
                                    panel.paraNum,
                                    panel.weekDay,
                            });
                } else {
                    db.update("raspisanie", "id=" + panel.raspId,
                            new String[]{
                                    "group_id",
                                    "teacher_id",
                                    "para",
                                    "week_day",
                            },
                            new Object[]{
                                    panel.groupId,
                                    panel.teacherId,
                                    panel.paraNum,
                                    panel.weekDay,
                            });
                }

            }
            JOptionPane.showMessageDialog(this, "OK");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}

class StudRaspisPanel extends JPanel {
    String groupName;
    String subjName;
    int groupId;
    JLabel lbl;

    int paraNum;
    int weekDay;

    String teacherName;
    int teacherId;

    int subjId;
    int raspId;

    StudRaspisPanel(int groupId, String groupName,
                    int weekDay, int paraNumber,
                    int subjId, String subjName,
                    int teacherId, String teacherName,
                    int raspId) {
        super();
        this.groupId = groupId;
        this.groupName = groupName;
        // this.subjName = subjName;

        this.weekDay = weekDay;

//        this.teacherName = teacherName;
//        this.teacherId = teacherId;
        this.paraNum = paraNumber;

        this.raspId = raspId;

        setLayout(null);
//        SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
//        String zk_string = no_zk.equals("") ? "" : " ЗК#" + no_zk + " выдана " + sd.format(date);
//        String teachStr = teacherId == -1 ? "" : " препоодаватель =" + teacherName;
        lbl = new JLabel();
        lbl.setBounds(0, 0, 750, 20);
        add(lbl);
        setVisible(true);

        setTeacher(teacherName, teacherId, subjId, subjName);
    }

    void setTeacher(String t, int id, int subjId, String subjName) {
        this.teacherName = t;
        this.teacherId = id;

        this.subjName = subjName;
        this.subjId = subjId;

        String tmp = teacherId == -1 ? "" : " препоодаватель =" + teacherName;
        lbl.setText(getWeekDay(weekDay) + " " + getParaTime(paraNum) + " " + groupName + " - " + subjName + tmp);
    }

    String getWeekDay(int weekDay) {
        switch (weekDay) {
            case 1:
                return "Понедельник";
            case 2:
                return "Вторник";
            case 3:
                return "Среда";
            case 4:
                return "Четверг";
            case 5:
                return "Пятница";
        }
        return "";
    }

    String getParaTime(int paraNumber) {
        switch (paraNumber) {
            case 1:
                return "08:00-09:35";
            case 2:
                return "09:45-11:20";
            case 3:
                return "12:10-13:45";
            case 4:
                return "13:55-15:30";
            case 5:
                return "16:10-17:45";
            case 6:
                return "17:55-19:30";
        }
        return "";
    }
}
