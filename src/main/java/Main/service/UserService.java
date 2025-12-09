package Main.service;

import Main.DatabaseConnection;
import Main.NotFoundException;
import Main.domain.User;
import Main.repositories.IUserRepository;
import Main.repositories.RepositoryFactory;
import Main.repositories.UserRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserService implements IUserService{
    private IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
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
            if(userRepository.selectByEmail(connection, user.getEmail()).isPresent()){
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

    public User getUserByEmail(String email) {
        Connection connection = null;
        try{
            connection = DatabaseConnection.startTransaction();
            Optional<User> user = userRepository.selectByEmail(connection, email);
            return user.orElse(null);
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось получить пользователя: "+e.getMessage(), e);
        }
    }
    public List<User> getClientsByName(String name) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.startTransaction();
            List<User> allUsers = userRepository.selectAll(connection);
            DatabaseConnection.closeConnection(connection, true);

            // Фильтруем клиентов по имени/фамилии
            return allUsers.stream()
                    .filter(user -> {
                        String fullName = user.getName().toLowerCase();
                        String searchName = name.toLowerCase();
                        return fullName.contains(searchName);
                    })
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            DatabaseConnection.closeConnection(connection, false);
            throw new RuntimeException("Ошибка при поиске клиентов: " + e.getMessage(), e);
        }
    }
}