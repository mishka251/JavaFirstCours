package database_instruments;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PosgtresDB {
    Connection connection;

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }
        System.out.println("SQLite JDBC Driver successfully connected");
        connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:test123.db");
            // .getConnection(dbUrl, login, password);

        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("You successfully connected to database now");
        } else {
            System.out.println("Failed to make connection to database");
        }
    }

    public void createTable(String tableName, TableColumn[] columns) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE '");
        sql.append(tableName);
        sql.append("' (");


        for (int i = 0; i < columns.length; i++) {
            TableColumn column = columns[i];
            sql.append(column.name);
            sql.append(" ");
            sql.append(column.type);
            sql.append(" ");
            sql.append(column.extra);
            if (i != columns.length - 1) {
                sql.append(",");
            }
        }
        sql.append(")");

        Statement statement = connection.createStatement();

        statement.execute(sql.toString());
        // statement.close();
    }

    public long insert(String tableName, String[] columnNames, Object[] values) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO '");
        sql.append(tableName);
        sql.append("' (");


        for (int i = 0; i < columnNames.length; i++) {
            sql.append(columnNames[i]);
            if (i != columnNames.length - 1) {
                sql.append(", ");
            }
        }

        sql.append(") VALUES (");

        for (int i = 0; i < columnNames.length; i++) {
            sql.append("?");
            if (i != columnNames.length - 1) {
                sql.append(", ");
            }
        }

        sql.append(")");
        PreparedStatement statement = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < columnNames.length; i++) {
            if (values[i] instanceof java.util.Date) {
                statement.setObject(i + 1, values[i], Types.DATE);
            } else {
                statement.setObject(i + 1, values[i]);
            }
        }
        int affectedRows = statement.executeUpdate();

        if (affectedRows == 0) {
            //  statement.close();
            throw new SQLException("Creating user failed, no rows affected.");
        }

        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                //statement.close();
                return generatedKeys.getLong(1);
            } else {
                //  statement.close();
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }

    }

    void delete(String tableName, String columnName, Object value) throws SQLException {
        String sql = "DELETE FROM " +
                tableName +
                " WHERE " +
                columnName +
                "=?";
        PreparedStatement statement = connection.prepareStatement(sql);

        if (value instanceof java.util.Date) {
            statement.setObject(1, value, Types.DATE);
        } else {
            statement.setObject(1, value);
        }
        statement.execute();
        //  statement.close();
    }

    public void dropTable(String table) throws SQLException {

        String sql = "DROP TABLE " +
                table;
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.execute();
        // statement.close();
    }

    public Map<String, ArrayList<Object>> select(String table) throws SQLException {
        String sql = "SELECT * FROM '" + table + "'";
        PreparedStatement statement = connection.prepareStatement(sql);
//statement.setString(1, table);
        ResultSet resultSet = statement.executeQuery();
        Map<String, ArrayList<Object>> results = new HashMap<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

// The column count starts from 1
        for (int i = 1; i <= columnCount; i++) {
            String name = metaData.getColumnName(i);
            results.put(name, new ArrayList<>());
        }

        while (resultSet.next()) {
            for (String columnName : results.keySet()) {
                Object value = resultSet.getObject(columnName);
                results.get(columnName).add(value);
            }
        }

        //statement.close();
        return results;
    }

    public Map<String, ArrayList<Object>> selectWhere(String table, String condition) throws SQLException {
        String sql = "SELECT * FROM '" +
                table + "' WHERE " + condition;

        PreparedStatement statement = connection.prepareStatement(sql);

        ResultSet resultSet = statement.executeQuery();
        Map<String, ArrayList<Object>> results = new HashMap<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

// The column count starts from 1
        for (int i = 1; i <= columnCount; i++) {
            String name = metaData.getColumnName(i);
            results.put(name, new ArrayList<>());
        }

        while (resultSet.next()) {
            for (String columnName : results.keySet()) {
                Object value = resultSet.getObject(columnName);
                results.get(columnName).add(value);
            }
        }

        // statement.close();
        return results;
    }

    public Map<String, ArrayList<Object>> select(String table, String sortColumn, String sortType) throws SQLException {
        String sql = "SELECT * FROM '" +
                table +
                "' ORDER BY " +
                sortColumn +
                " " +
                sortType;
        PreparedStatement statement = connection.prepareStatement(sql);

        ResultSet resultSet = statement.executeQuery();
        Map<String, ArrayList<Object>> results = new HashMap<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

// The column count starts from 1
        for (int i = 1; i <= columnCount; i++) {
            String name = metaData.getColumnName(i);
            results.put(name, new ArrayList<>());
        }

        while (resultSet.next()) {
            for (String columnName : results.keySet()) {
                Object value = resultSet.getObject(columnName);
                results.get(columnName).add(value);
            }
        }

        // statement.close();
        return results;
    }

    public Map<String, ArrayList<Object>> selectWhere(String table, String sortColumn, String sortType, String condition) throws SQLException {
        String sql = "SELECT * FROM '" +
                table + "' WHERE " + condition +
                " ORDER BY " +
                sortColumn +
                " " +
                sortType;
        PreparedStatement statement = connection.prepareStatement(sql);

        ResultSet resultSet = statement.executeQuery();
        Map<String, ArrayList<Object>> results = new HashMap<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

// The column count starts from 1
        for (int i = 1; i <= columnCount; i++) {
            String name = metaData.getColumnName(i);
            results.put(name, new ArrayList<>());
        }

        while (resultSet.next()) {
            for (String columnName : results.keySet()) {
                Object value = resultSet.getObject(columnName);
                results.get(columnName).add(value);
            }
        }

        // statement.close();
        return results;
    }

    public void update(String tableName, String condition, String[] updatedColumns, Object[] newValues) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE '");
        sql.append(tableName);
        sql.append("' SET ");
        for (int i = 0; i < updatedColumns.length; i++) {
            sql.append(updatedColumns[i]);
            sql.append("=?");
            if (i != updatedColumns.length - 1) {
                sql.append(",");
            }
        }
        sql.append(" WHERE ");
        sql.append(condition);

        PreparedStatement statement = connection.prepareStatement(sql.toString());
        for (int i =0; i<newValues.length; i++) {
            Object value = newValues[i];
            if (value instanceof java.util.Date) {
                statement.setObject(i+1, value, Types.DATE);
            } else {
                statement.setObject(i+1, value);
            }
        }

        statement.execute();
    }

    ArrayList<String> getTableNames() throws SQLException {
        if (this.connection == null) {
            throw new SQLException("Нет подключения к БД");
        }

        PreparedStatement statement =
                connection.prepareStatement("SELECT \n" +
                        "    name\n" +
                        "FROM \n" +
                        "    sqlite_master \n" +
                        "WHERE \n" +
                        "    type ='table' AND \n" +
                        "    name NOT LIKE 'sqlite_%';");
        ResultSet resultSet = statement.executeQuery();
        ArrayList<String> tables = new ArrayList<>();
        while (resultSet.next()) {
            tables.add(resultSet.getString("name"));
        }
        return tables;
    }

    ArrayList<TableColumn> getTableColumnNames(String tableName) throws SQLException {
        if (this.connection == null) {
            throw new SQLException("Нет подключения к БД");
        }

        PreparedStatement statement =
                connection.prepareStatement("SELECT name, type FROM PRAGMA_TABLE_INFO(?)");
        statement.setString(1, tableName);
        ResultSet resultSet = statement.executeQuery();
        ArrayList<TableColumn> tables = new ArrayList<>();
        while (resultSet.next()) {
            String name = resultSet.getString("name");
            String type = resultSet.getString("type");
            tables.add(new TableColumn(name, type));
        }
        return tables;
    }
}
