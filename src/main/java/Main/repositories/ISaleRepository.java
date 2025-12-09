// Main.repositories.ISaleRepository.java
package Main.repositories;

import Main.domain.Sale;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ISaleRepository {
    boolean insert(Connection connection, Sale sale) throws SQLException;
    Optional<Sale> selectById(Connection connection, Integer id) throws SQLException;
    List<Sale> selectAll(Connection connection) throws SQLException;
    List<Sale> SelectAllWithDetails() throws SQLException;
    List<Sale> selectByClientId(Integer clientId) throws SQLException;
    List<Sale> selectByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException;
    List<Sale> selectTodaySales() throws SQLException;
    boolean update(Sale sale) throws SQLException;
    boolean delete(Connection connection, Integer id) throws SQLException;
    boolean existsById(Connection connection, Integer id) throws SQLException;
    int getTotalSalesCount() throws SQLException;
    BigDecimal getTotalRevenue() throws SQLException;
    BigDecimal getRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException;
    List<Object[]> getMedicineSalesStatistics() throws SQLException;
}