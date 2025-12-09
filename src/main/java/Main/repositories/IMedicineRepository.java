// Main.repositories.IMedicineRepository.java
package Main.repositories;

import Main.domain.Medicine;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IMedicineRepository {
    int insert(Connection connection, Medicine medicine) throws SQLException;
    Optional<Medicine> selectById(Connection connection, Integer id) throws SQLException;
    Optional<Medicine> selectByName(String name) throws SQLException;
    List<Medicine> selectAll(Connection connection) throws SQLException;
    boolean update(Connection connection, Medicine medicine) throws SQLException;
    boolean updateStockQuantity(Connection connection, Integer medicineId, Integer itemsSold) throws SQLException;
    boolean delete(Connection connection, Integer id) throws SQLException;
    boolean existsById(Integer id) throws SQLException;
}