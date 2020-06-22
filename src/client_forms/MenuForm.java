package client_forms;

import database_instruments.DbTableForm;
import database_instruments.PosgtresDB;

import javax.swing.*;
import java.awt.*;

public class MenuForm extends JFrame {
    PosgtresDB db;

    public MenuForm(PosgtresDB db) {
        this.db = new PosgtresDB();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.orange);
        setLayout(null);
        setSize(300, 450);
        setTitle("Magistr");
        setVisible(true);

        JButton btnConnect = new JButton("Подключиться к БД");
        btnConnect.setBounds(50, 10, 200, 20);
        add(btnConnect);

        JButton btnStuds = new JButton("Запрос таблицы магистрантов");
        btnStuds.setBounds(50, 40, 200, 20);
        add(btnStuds);

        JButton btnDocs = new JButton("Учетные карточки и ЗК");
        btnDocs.setBounds(50, 110, 200, 20);
        add(btnDocs);

        JButton btnRaspr = new JButton("Формирование группы");
        btnRaspr.setBounds(50, 70, 200, 20);
        add(btnRaspr);

        JButton btnPrepod = new JButton("Закрепление преподавателя");
        btnPrepod.setBounds(50, 140, 200, 20);
        add(btnPrepod);


        JButton btnReport = new JButton("Формирование расписания");
        btnReport.setBounds(50, 170, 200, 20);
        add(btnReport);

        btnConnect.addActionListener((event) -> connect());
        btnStuds.addActionListener((event) -> showStudents());
        btnRaspr.addActionListener((event) -> this.showRaspr());
        btnDocs.addActionListener((event) -> this.showCards());
        btnPrepod.addActionListener((event) -> setTeacher());
        btnReport.addActionListener((event) -> this.showRasp());

    }

    void connect() {
        try {
            this.db.connect();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    void showStudents() {
        new DbTableForm(db, "abiturient");
    }

    void showRaspr() {
        new RaspredForm(db);
    }

    void showCards() {
        new DocumentsForm(db);
    }

    void setTeacher() {
        new TeachersForm(db);
    }

    void showRasp() {
        new RaspisForm(db);
    }

}
