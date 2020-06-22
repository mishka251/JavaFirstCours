package client_forms;

import database_instruments.PosgtresDB;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class RaspForm extends JFrame {
    PosgtresDB db;

    JTextField oldPlan;
    JTextField newPlan;

    JTextPane report;

    JScrollPane scroll;

    JComboBox<String> speciality;
    Integer[] spec_ids;

    final JFileChooser fileChooser;

    RaspForm(PosgtresDB db) {
        this.db = db;
        setLayout(null);
        setVisible(true);
        setSize(500, 520);
        getContentPane().setBackground(Color.cyan);
        fileChooser = new JFileChooser();

        setTitle("Формирование отчёта / «Contingent»");

        JLabel lblOldPlan = new JLabel("Введите план прошлого года (20)");
        lblOldPlan.setBounds(10, 10, 200, 20);
        add(lblOldPlan);

        oldPlan = new JTextField();
        oldPlan.setBounds(10, 30, 100, 20);
        add(oldPlan);

        JLabel lblNewPlan = new JLabel("Введите план текущего года (30)");
        lblNewPlan.setBounds(10, 60, 200, 20);
        add(lblNewPlan);

        newPlan = new JTextField();
        newPlan.setBounds(10, 80, 100, 20);
        add(newPlan);

        report = new JTextPane();
        report.setBounds(10, 120, 400, 300);

        scroll = new JScrollPane(report);
        add(scroll);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBounds(10, 120, 400, 300);

        JButton btnCreate = new JButton("Создать отчёт");
        btnCreate.setBounds(10, 430, 140, 20);
        add(btnCreate);

        JButton btnSave = new JButton("Save");
        btnSave.setBounds(210, 430, 140, 20);
        add(btnSave);

        btnCreate.addActionListener((event) -> loadReport());
        btnSave.addActionListener((event) -> saveReposrt());


        try {
            Map<String, ArrayList<Object>> tableSpec = db.select("speciality");
            spec_ids = Arrays.copyOf(tableSpec.get("id").toArray(), tableSpec.get("id").size(), Integer[].class);
            String[] spec_names = Arrays.copyOf(tableSpec.get("name").toArray(), tableSpec.get("name").size(), String[].class);

            JLabel lblSpec = new JLabel("Специальность");
            lblSpec.setBounds(230, 10, 100, 20);
            add(lblSpec);

            speciality = new JComboBox<>(spec_names);
            speciality.setBounds(340, 10, 100, 20);
            add(speciality);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    int groupsCnt;
    double midBall;
    Map<String, Integer> countByType;

    void loadReport() {
        midBall = 0;
        countByType = new HashMap<>();
        int nowPlanCnt;
        int oldPlanCnt;
        try {
            nowPlanCnt = Integer.parseInt(newPlan.getText());
            oldPlanCnt = Integer.parseInt(oldPlan.getText());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Не заполнен план или заполнен неверно");
            return;
        }

        try {
            StringBuilder sb = new StringBuilder();

            Map<String, ArrayList<Object>> tableFac = db.select("faculty");
            String facName = (String) tableFac.get("name").get(0);
            sb.append("Факультет ");
            sb.append(facName);
            sb.append("\n");

            Map<String, ArrayList<Object>> tableCaf = db.select("department");
            String cafName = (String) tableCaf.get("name").get(0);
            sb.append("Кафедра ");
            sb.append(cafName);
            sb.append("\n");

            Map<String, ArrayList<Object>> tableSpec = db.selectWhere("speciality", "id=" + spec_ids[speciality.getSelectedIndex()]);
            Integer[] spec_ids = Arrays.copyOf(tableSpec.get("id").toArray(), tableSpec.get("id").size(), Integer[].class);
            String[] spec_names = Arrays.copyOf(tableSpec.get("name").toArray(), tableSpec.get("name").size(), String[].class);

            for (int specInd = 0; specInd < spec_ids.length; specInd++) {
                sb.append("Специальность ");
                sb.append(spec_names[specInd]);
                sb.append("\n");

                Map<String, ArrayList<Object>> tableGroup = db.selectWhere("group", "speciality_id=" + spec_ids[specInd]);

                Integer[] groups_ids = Arrays.copyOf(tableGroup.get("id").toArray(), tableGroup.get("id").size(), Integer[].class);
                String[] groups_names = Arrays.copyOf(tableGroup.get("number_code").toArray(), tableGroup.get("number_code").size(), String[].class);

                groupsCnt = groups_ids.length;

                for (int grInd = 0; grInd < groups_ids.length; grInd++) {
                    sb.append("Группа ");
                    sb.append(spec_names[specInd]).append(groups_names[grInd]);
                    sb.append("\n");

                    Map<String, ArrayList<Object>> tableStud = db.selectWhere("student", "group_id=" + groups_ids[grInd]);
                    Integer[] stud_ids = Arrays.copyOf(tableStud.get("id").toArray(), tableStud.get("id").size(), Integer[].class);
                    Integer[] abitur_ids = Arrays.copyOf(tableStud.get("abitur_id").toArray(), tableStud.get("abitur_id").size(), Integer[].class);

                    for (int studInd = 0; studInd < stud_ids.length; studInd++) {
                        Map<String, ArrayList<Object>> tableAbitur = db.selectWhere("abiturient", "id=" + abitur_ids[studInd]);

                        String name = tableAbitur.get("surname").get(0) + " " + tableAbitur.get("name").get(0);
                        Integer _ball = (Integer) tableAbitur.get("math_ege").get(0)
                                + (Integer) tableAbitur.get("rus_ege").get(0)
                                + (Integer) tableAbitur.get("inf_ege").get(0);

                        midBall += _ball;

                        Integer type_id = (Integer) tableAbitur.get("type_id").get(0);
                        Map<String, ArrayList<Object>> tableAbiturType = db.selectWhere("abiturient_type", "id=" + type_id);
                        String type = (String) tableAbiturType.get("name").get(0);

                        countByType.put(type, 1 + countByType.getOrDefault(type, 0));

                        Map<String, ArrayList<Object>> tableCard = db.selectWhere("student_card", "student_id=" + stud_ids[studInd]);

                        String no_zk = (String) tableCard.get("no_zk").get(0);
                        Date date = new Date((Long) tableCard.get("date").get(0));

                        sb.append(name);
                        sb.append(" ");
                        sb.append(type);
                        sb.append(" балл ЕГЭ -");
                        sb.append(_ball);

                        sb.append(" ЗК# ");
                        sb.append(no_zk);
                        sb.append("\n");

                        SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
                        sb.append(" выдана ");
                        sb.append(sd.format(date));
                        sb.append("\n");
                    }
                }

                sb.append("Итого\n");

                double studs = 0;
                for (String type : countByType.keySet()) {
                    sb.append(type);
                    sb.append(" - ");
                    sb.append(countByType.get(type));
                    sb.append("\n");
                    studs += countByType.get(type);
                }

                sb.append("Всего ");
                sb.append(studs);
                sb.append("\n");

                midBall /= studs;

                sb.append("Средний балл ");
                sb.append(midBall);
                sb.append("\n");


                sb.append(studs / nowPlanCnt * 100);
                sb.append("% от плана ");
                sb.append("\n");

                sb.append(studs / oldPlanCnt * 100);
                sb.append("% от плана прошлого года");
                sb.append("\n");
                scroll.setViewportView(report);
            }
            report.setText(sb.toString());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    void saveReposrt() {

        int nowPlanCnt;
        int oldPlanCnt;
        try {
            nowPlanCnt = Integer.parseInt(newPlan.getText());
            oldPlanCnt = Integer.parseInt(oldPlan.getText());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Не заполнен план или заполнен неверно");
            return;
        }

        try {

            double studs = 0;
            for (String type : countByType.keySet()) {
                studs += countByType.get(type);
            }


            db.insert("spec_report",
                    new String[]{
                            "groups_cnt",
                            "students_cnt",
                            "mid_ball_ege",
                            "budjet_cnt",
                            "contr_cnt",
                            "last_year_plan_perc",
                            "now_plan_perc",
                            "speciality_id"
                    },
                    new Object[]{
                            groupsCnt,
                            studs,
                            midBall,
                            countByType.get("бюджет"),
                            countByType.get("контракт"),
                            studs / oldPlanCnt,
                            studs / nowPlanCnt,
                            spec_ids[speciality.getSelectedIndex()]

                    }
            );

            int dialogResult = fileChooser.showSaveDialog(this);
            if (dialogResult != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File selected = fileChooser.getSelectedFile();
            try {
                FileWriter fw = new FileWriter(selected);
                fw.write(report.getText());
                fw.close();
            } catch (java.io.IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }

        } catch (
                Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
