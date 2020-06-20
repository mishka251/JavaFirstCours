package client_forms;

import database_instruments.PosgtresDB;

import javax.swing.*;
import java.awt.*;

public class MenuForm extends JFrame {
    PosgtresDB db;

    public MenuForm(PosgtresDB db) {
        this.db = db;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
getContentPane().setBackground(Color.cyan);
        setLayout(null);
        setSize(300, 150);
        setTitle("Меню");
        setVisible(true);

        JButton btnRaspr = new JButton("Распределение");
        btnRaspr.setBounds(50, 10, 200, 20);
        add(btnRaspr);

        JButton btnDocs = new JButton("Учетные карточки");
        btnDocs.setBounds(50, 40, 200, 20);
        add(btnDocs);

        JButton btnReport = new JButton("Отчет");
        btnReport.setBounds(50, 70, 200, 20);
        add(btnReport);

        btnRaspr.addActionListener((event) -> this.showRaspr());
        btnDocs.addActionListener((event) -> this.showCards());
        btnReport.addActionListener((event) -> this.showReport());

    }

    void showRaspr() {
        new RaspredForm(db);
    }

    void showCards() {
        new DocumentsForm(db);
    }

    void showReport() {
        new ReportForm(db);
    }

}
