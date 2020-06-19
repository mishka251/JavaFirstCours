package client_forms;

import database_instruments.DbTableForm;
import database_instruments.PosgtresDB;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Map;

public class LoginForm extends JFrame {
    final JTextField loginField;
    final JTextField passwordField;

    final JButton btnLogin;

    final PosgtresDB db;

    public LoginForm(PosgtresDB db) {
        this.db = db;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setTitle("Вход в систему");

        setSize(320, 210);

        JLabel lblLogin = new JLabel("Login");
        lblLogin.setBounds(110, 10, 100, 20);
        add(lblLogin);

        loginField = new JTextField();
        loginField.setBounds(110, 40, 100, 20);
        add(loginField);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setBounds(110, 70, 100, 20);
        add(lblPassword);

        passwordField = new JTextField();
        passwordField.setBounds(110, 100, 100, 20);
        add(passwordField);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(120, 130, 80, 20);
        add(btnLogin);

        btnLogin.addActionListener(this::login);
        setVisible(true);
    }

    void login(ActionEvent event) {
        try {
            Map<String, ArrayList<Object>> result = db.selectWhere("USER", "login='" + loginField.getText() + "'");
            ArrayList<Object> password = result.get("password");
            if (password.size() == 0) {
                JOptionPane.showMessageDialog(this, "Нет такого пользователя", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!passwordField.getText().equals(password.get(0))) {
                JOptionPane.showMessageDialog(this, "Неверный пароль", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = (int) result.get("id").get(0);
            // JOptionPane.showMessageDialog(this, "Ok");
            if ("decanat".equals(result.get("position").get(0))) {
                //new DecanatForm(db, id);
                new MenuForm(db, id);
                dispose();
            } else if ("admin".equals(result.get("position").get(0))) {
                new DbTableForm(db, "user"); //new DecanatForm(db, id);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Неопределенный тип сотрудика", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
