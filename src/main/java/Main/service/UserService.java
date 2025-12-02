package Main.service;

import Main.DatabaseConnection;
import Main.NotFoundException;
import Main.domain.User;
import Main.repositories.UserRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService {
    private UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public List<User> getAllUsers() {
        Connection connection = null;
        try{
            connection = DatabaseConnection.startTransaction();
            List<User> users = userRepository.selectAll(connection);
            DatabaseConnection.closeConnection(connection, true);
            return users;
        }catch (SQLException e) {
            DatabaseConnection.closeConnection(connection, false);
            throw new RuntimeException("Ошибка при получении списка пользователей "+e.getMessage(), e);
        }
    }

    public User getUserById(Integer id) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.startTransaction();
            Optional<User> user = userRepository.selectById(connection, id);
            if(user.isEmpty()){
                DatabaseConnection.closeConnection(connection, false);
                throw new NotFoundException("Не найдено пользователя с таким ID");
            }
            DatabaseConnection.closeConnection(connection,true);
            return user.get();
        } catch (SQLException e) {
            DatabaseConnection.closeConnection(connection,false);
            throw new RuntimeException("Ошибка при поиске пользователя", e);
        }
    }
    public void addUser(User user){
        Connection connection = null;
        try{
            connection = DatabaseConnection.startTransaction();
            if(userRepository.existsByEmail(connection, user.getEmail())){
                throw new RuntimeException("Пользователь с такой почтой уже существует");
            }
            if(userRepository.insert(connection, user)){
                DatabaseConnection.closeConnection(connection, true);
            }
        } catch (SQLException e) {
            DatabaseConnection.closeConnection(connection, false);
            throw new RuntimeException(e);
        }
    }
    public void deleteUser(int id){
        Connection connection = null;
        try{
            connection = DatabaseConnection.startTransaction();
            if(!userRepository.existsById(connection, id)){
                throw new RuntimeException("Пользователя с таким id не существует");
            }
            if(userRepository.delete(connection, id)){
                DatabaseConnection.closeConnection(connection, true);
            }
        } catch (SQLException e) {
            DatabaseConnection.closeConnection(connection, false);
            throw new RuntimeException(e);
        }
    }

}