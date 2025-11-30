package Main;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        connection.setAutoCommit(false);
        return connection;
    }
    public static Connection startTransaction() throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        connection.setAutoCommit(false); // Выключаем авто-коммит
        return connection;
    }
    public static void closeConnection(Connection connection, boolean finish){
        if (connection != null) {
            try {
                if(finish){
                    connection.setAutoCommit(true);
                }else{
                    connection.rollback();
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void addToStatement(PreparedStatement statement, ArrayList<Object> objects) throws SQLException {
        for (int i = 0; i < objects.size(); i++) {
            if(objects.get(i) instanceof Integer){
                statement.setInt(i+1, (Integer) objects.get(i));
            }else if (objects.get(i) instanceof String){
                statement.setString(i+1, (String) objects.get(i));
            }else if (objects.get(i) instanceof LocalDateTime){
                statement.setTimestamp(i+1, Timestamp.valueOf((LocalDateTime) objects.get(i)));
            }else if(objects.get(i) instanceof BigDecimal){
                statement.setBigDecimal(i+1, (BigDecimal) objects.get(i));
            } else if(objects.get(i) instanceof Boolean){
                statement.setBoolean(i+1, (boolean) objects.get(i));
            } else{
                System.out.println("АХТУНГ НЕИЗВЕСТНЫЙ ТИП "+objects.get(i));
            }
        }
    }
    public static ResultSet operationQuery(String sql, Connection connection, ArrayList<Object> objects) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        if(objects!=null) addToStatement(statement, objects);
        return statement.executeQuery();
    }
    public static int operationUpdate(String sql, Connection connection, ArrayList<Object> objects) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        if(objects!=null) addToStatement(statement, objects);
        return statement.executeUpdate();
    }
}