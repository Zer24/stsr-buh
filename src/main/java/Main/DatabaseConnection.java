package Main;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DatabaseConnection {
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance();

    public static Connection getConnection() {
        return connectionPool.getConnection();
    }

    public static Connection startTransaction() throws SQLException {
        return getConnection();
    }

    public static void closeConnection(Connection connection, boolean commit) {
        if (connection != null) {
            try {
                if (commit) {
                    connection.commit();
                } else {
                    connection.rollback();
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addToStatement(PreparedStatement statement, ArrayList<Object> objects) throws SQLException {
        if (objects == null) return;

        for (int i = 0; i < objects.size(); i++) {
            Object obj = objects.get(i);

            switch (obj) {
                case Integer integer -> statement.setInt(i + 1, integer);
                case String s -> statement.setString(i + 1, s);
                case LocalDateTime localDateTime -> statement.setTimestamp(i + 1, Timestamp.valueOf(localDateTime));
                case BigDecimal bigDecimal -> statement.setBigDecimal(i + 1, bigDecimal);
                case Boolean aBoolean -> statement.setBoolean(i + 1, aBoolean);
                case Date date -> statement.setDate(i + 1, date);
                case Time time -> statement.setTime(i + 1, time);
                case Timestamp timestamp -> statement.setTimestamp(i + 1, timestamp);
                case Double aDouble -> statement.setDouble(i + 1, aDouble);
                case Float aFloat -> statement.setFloat(i + 1, aFloat);
                case Long aLong -> statement.setLong(i + 1, aLong);
                case Short aShort -> statement.setShort(i + 1, aShort);
                case Byte aByte -> statement.setByte(i + 1, aByte);
                case null -> statement.setNull(i + 1, Types.NULL);
                default -> statement.setObject(i + 1, obj);
            }
        }
    }

    public static ResultSet operationQuery(String sql, Connection connection, ArrayList<Object> objects) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        addToStatement(statement, objects);
        return statement.executeQuery();
    }

    public static int operationUpdate(String sql, Connection connection, ArrayList<Object> objects) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        addToStatement(statement, objects);
        return statement.executeUpdate();
    }
}