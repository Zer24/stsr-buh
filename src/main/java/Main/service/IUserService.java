// Main.service.IUserService.java
package Main.service;

import Main.domain.User;
import java.util.List;

public interface IUserService {
    List<User> getAllUsers();
    User getUserById(Integer id);
    void addUser(User user);
    void deleteUser(int id);
    User getUserByEmail(String email);
    List<User> getClientsByName(String name);
}