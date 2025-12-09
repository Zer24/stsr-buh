// Main.repositories.IUserRepository.java
package Main.repositories;

import Main.domain.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IUserRepository {
    boolean insert(Connection connection, User user) throws SQLException;
    Optional<User> selectById(Connection connection, Integer id) throws SQLException;
    List<User> selectAll(Connection connection) throws SQLException;
    List<User> selectByRole(String role) throws SQLException;
    boolean update(User user) throws SQLException;
    boolean delete(Connection connection, Integer id) throws SQLException;
    boolean existsById(Connection connection, Integer id) throws SQLException;
    Optional<User> selectByEmail(Connection connection, String email) throws SQLException;
    Optional<User> authenticate(String email, String password) throws SQLException;
    int getTotalUserCount() throws SQLException;
}