package Main.repositories;

import Main.DatabaseConnection;
import Main.domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    public boolean insert(Connection connection, User user) throws SQLException {
        String sql = "INSERT INTO users (email, password, name, role) VALUES (?, ?, ?, ?)";
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(user.getEmail());
        objects.add(user.getPassword());
        objects.add(user.getName());
        objects.add(user.getRole());
        try {
            int result = DatabaseConnection.operationUpdate(sql, connection, objects);
            System.out.println("Пользователь добавлен: " + user.getEmail() + " (ID: " + user.getId() + ")");
            return result==1;
        }catch (Exception e){
            throw new RuntimeException("Не удалось добавить пользователя", e);
        }
    }
    public Optional<User> selectById(Connection connection, Integer id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(id);
        try (ResultSet resultSet = DatabaseConnection.operationQuery(sql, connection, objects)) {
            if (resultSet.next()) {
                return Optional.of(mapResultSetToUser(resultSet));
            }
        }
        return Optional.empty();
    }
    public List<User> selectAll(Connection connection) throws SQLException {
        String sql = "SELECT * FROM users ORDER BY name";
        List<User> users = new ArrayList<>();
        try (ResultSet resultSet = DatabaseConnection.operationQuery(sql, connection, null)) {
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        }
        return users;
    }
    public List<User> selectByRole(String role) throws SQLException {
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY name";
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, role.toUpperCase());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(mapResultSetToUser(resultSet));
                }
            }
        }
        return users;
    }
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET email = ?, password = ?, name = ?, role = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setUserParameters(statement, user);
            statement.setInt(5, user.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Пользователь обновлен: " + user.getEmail());
                return true;
            }
            return false;
        }
    }
    public boolean delete(Connection connection, Integer id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(id);
        int result = DatabaseConnection.operationUpdate(sql, connection, objects);
        if (result > 0) {
            System.out.println("Пользователь удален: ID " + id);
            return true;
        }
        return false;
    }
    public boolean existsById(Connection connection, Integer id) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE id = ?";
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(id);
        try (ResultSet resultSet = DatabaseConnection.operationQuery(sql, connection, objects)) {
            return resultSet.next();
        }
    }
    public boolean existsByEmail(Connection connection, String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(email);
        try (ResultSet resultSet = DatabaseConnection.operationQuery(sql, connection, objects)) {
            return resultSet.next();
        }
    }
    public Optional<User> authenticate(String email, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToUser(resultSet));
                }
            }
        }
        return Optional.empty();
    }
    public int getTotalUserCount() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM users";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
            return 0;
        }
    }
    private void setUserParameters(PreparedStatement statement, User user) throws SQLException {
        statement.setString(1, user.getEmail());
        statement.setString(2, user.getPassword());
        statement.setString(3, user.getName());
        statement.setString(4, user.getRole().toUpperCase());
    }
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getString("name"),
                resultSet.getString("role")
        );
    }
}