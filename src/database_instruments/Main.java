package database_instruments;

import client_forms.MenuForm;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Random;

public class Main {

    static void createDB(PosgtresDB db) throws SQLException {
        db.createTable("faculty",
                new TableColumn[]{
                        new TableColumn("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
                        new TableColumn("name", "VARCHAR (100)"),
                });

        db.createTable("department",
                new TableColumn[]{
                        new TableColumn("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
                        new TableColumn("name", "VARCHAR (100)"),
                        new TableColumn("faculty_id", "INTEGER", "REFERENCES faculty (id)")
                });

        db.createTable("speciality",
                new TableColumn[]{
                        new TableColumn("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
                        new TableColumn("name", "VARCHAR (100)"),
                        new TableColumn("department_id", "INTEGER", "REFERENCES department (id)")
                });

        db.createTable("group",
                new TableColumn[]{
                        new TableColumn("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
                        new TableColumn("number_code", "VARCHAR (100)"),
                        new TableColumn("department_id", "INTEGER", "REFERENCES department (id)"),
                        new TableColumn("speciality_id", "INTEGER", "REFERENCES speciality (id)")
                });

        db.createTable("abiturient_type",
                new TableColumn[]{
                        new TableColumn("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
                        new TableColumn("name", "VARCHAR (100)")
                });

        db.createTable("abiturient",
                new TableColumn[]{
                        new TableColumn("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
                        new TableColumn("name", "VARCHAR (100)"),
                        new TableColumn("surname", "VARCHAR (100)"),
                        new TableColumn("patronymic", "VARCHAR (100)"),
                        new TableColumn("city", "VARCHAR (100)"),
                        new TableColumn("math_ege", "INTEGER"),
                        new TableColumn("rus_ege", "INTEGER"),
                        new TableColumn("inf_ege", "INTEGER"),
                        new TableColumn("type_id", "INTEGER", "REFERENCES abiturient_type (id)"),
                        new TableColumn("spec_id", "INTEGER", "REFERENCES speciality (id)")
                });

        db.createTable("student",
                new TableColumn[]{
                        new TableColumn("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
                        new TableColumn("group_id", "INTEGER", "REFERENCES [group] (id)"),
                        new TableColumn("abitur_id", "INTEGER", "REFERENCES abiturient (id)")
                });

        db.createTable("student_card",
                new TableColumn[]{
                        new TableColumn("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
                        new TableColumn("no_zk", "VARCHAR (100)"),
                        new TableColumn("no_sb", "VARCHAR (100)"),
                        new TableColumn("date", "DATE"),
                        new TableColumn("student_id", "INTEGER", "REFERENCES student (id)"),
                });

        db.createTable("spec_report",
                new TableColumn[]{
                        new TableColumn("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
                        new TableColumn("groups_cnt", "INTEGER"),
                        new TableColumn("students_cnt", "INTEGER"),
                        new TableColumn("mid_ball_ege", "DOUBLE"),
                        new TableColumn("budjet_cnt", "INTEGER"),
                        new TableColumn("contr_cnt", "INTEGER"),
                        new TableColumn("last_year_plan_perc", "DOUBLE"),
                        new TableColumn("now_plan_perc", "DOUBLE"),
                        new TableColumn("group_id", "INTEGER", "REFERENCES [group] (id)"),
                        new TableColumn("speciality_id", "INTEGER", "REFERENCES speciality (id)"),
                });

    }

    static String getRandomName() {
        String[] names = new String[]{
                "Иван", "Петр", "Сидор", "Вася", "Саша", "Азат"
        };
        Random r = new Random();
        return names[r.nextInt(names.length)];
    }

    static String getRandomSurname() {
        String[] names = new String[]{
                "Иванов", "Петров", "Сидоров", "Васильев", "Чернов", "Юсупов"
        };
        Random r = new Random();
        return names[r.nextInt(names.length)];
    }

    static void createStudents(PosgtresDB db) throws SQLException {
        long fac_id = db.insert("faculty", new String[]{"name"}, new Object[]{"ФИРТ"});
        long caf_id = db.insert("department", new String[]{
                        "name",
                        "faculty_id"
                },
                new Object[]{
                        "ВИиК",
                        fac_id
                });


        long spec1_id = db.insert("speciality", new String[]{
                        "name",
                        "department_id"
                },
                new Object[]{
                        "ЭАС",
                        caf_id
                });

        long spec2_id = db.insert("speciality", new String[]{
                        "name",
                        "department_id"
                },
                new Object[]{
                        "ПИ",
                        caf_id
                });


        long[] groups = new long[3];
        groups[0] = db.insert("group", new String[]{
                        "number_code",
                        "speciality_id"
                },
                new Object[]{
                        "1",
                        spec1_id
                });
        groups[1] = db.insert("group", new String[]{
                        "number_code",
                        "speciality_id"
                },
                new Object[]{
                        "1",
                        spec2_id
                });
        groups[2] = db.insert("group", new String[]{
                        "number_code",
                        "speciality_id"
                },
                new Object[]{
                        "2",
                        spec2_id
                });

        String[] abitur_types = new String[]{
                "бюджет",
                "контракт",
                "льготник"
        };
        long[] abit_types_id = new long[abitur_types.length];
        for (int i = 0; i < abitur_types.length; i++) {
            abit_types_id[i] = db.insert("abiturient_type", new String[]{
                    "name"
            }, new Object[]{abitur_types[i]});
        }

        Random r = new Random();


        for (int i = 0; i < 20; i++) {
            db.insert("abiturient", new String[]{
                            "name",
                            "surname",
                            "math_ege",
                            "rus_ege",
                            "inf_ege",
                            "spec_id",
                            "type_id"
                    },
                    new Object[]{
                            getRandomName(),
                            getRandomSurname(),
                            30 + r.nextInt(70),
                            30 + r.nextInt(70),
                            30 + r.nextInt(70),
                            spec1_id,
                            abit_types_id[r.nextInt(abit_types_id.length)]
                    });
        }

        for (int i = 0; i < 40; i++) {
            db.insert("abiturient", new String[]{
                            "name",
                            "surname",
                            "math_ege",
                            "rus_ege",
                            "inf_ege",
                            "spec_id",
                            "type_id"
                    },
                    new Object[]{
                            getRandomName(),
                            getRandomSurname(),
                            30 + r.nextInt(70),
                            30 + r.nextInt(70),
                            30 + r.nextInt(70),
                            spec2_id,
                            abit_types_id[r.nextInt(abit_types_id.length)]
                    });
        }


    }


    public static void main(String[] args) {
        PosgtresDB db = new PosgtresDB();
        db.connect();
        try {
            db.select("student");
        } catch (Exception _ex) {
            try {
                Main.createDB(db);
                Main.createStudents(db);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        new MenuForm(db);
    }
}
