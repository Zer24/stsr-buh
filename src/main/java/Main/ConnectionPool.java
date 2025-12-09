package Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {
    private static ConnectionPool instance;
    private final List<Connection> availableConnections = new ArrayList<>();
    private final List<Connection> usedConnections = new ArrayList<>();
    private static final int MAX_CONNECTIONS = 5;

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "";

    private ConnectionPool() {
        try {
            Class.forName("org.postgresql.Driver");
            initializePool();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Не удалось открыть пул соединений", e);
        }
    }

    public static synchronized ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    private void initializePool() throws SQLException {
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection.setAutoCommit(false);
            availableConnections.add(connection);
        }
    }

    public synchronized Connection getConnection() {
        if (availableConnections.isEmpty()) {
            throw new RuntimeException("В пуле подключений не осталось подключений");
        }

        Connection connection = availableConnections.remove(availableConnections.size() - 1);
        usedConnections.add(connection);

        return new PooledConnection(connection);
    }

    synchronized void releaseConnection(Connection realConnection) {
        if (usedConnections.remove(realConnection)) {
            try {
                if (!realConnection.getAutoCommit()) {
                    realConnection.rollback();
                }
                realConnection.setAutoCommit(false);
            } catch (SQLException e) {
                try {
                    realConnection = DriverManager.getConnection(URL, USER, PASSWORD);
                    realConnection.setAutoCommit(false);
                } catch (SQLException ex) {
                    return;
                }
            }
            availableConnections.add(realConnection);
        }
    }
}