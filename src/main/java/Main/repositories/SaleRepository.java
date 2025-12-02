package Main.repositories;

import Main.DatabaseConnection;
import Main.domain.Sale;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SaleRepository {

    // CREATE - добавление новой продажи
    public boolean insert(Connection connection, Sale sale) throws SQLException {
        String sql = "INSERT INTO sales (client_id, pharmacist_id, sale_datetime, medicine_id, quantity, total_amount) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        ArrayList<Object> objects = new ArrayList<>(Arrays.asList(sale.getClientId(), sale.getPharmacistId(), sale.getSaleDateTime(), sale.getMedicineId(), sale.getQuantity(), sale.getTotalAmount()));
        int result = DatabaseConnection.operationUpdate(sql, connection, objects);
        if (result>0) {
            System.out.println("Продажа добавлена: " + sale);
            return true;
        }
        return false;
    }
    public Optional<Sale> selectById(Connection connection, Integer id) throws SQLException {
        String sql = "SELECT s.*, " +
                "c.name as client_name, p.name as pharmacist_name, m.name as medicine_name " +
                "FROM sales s " +
                "LEFT JOIN users c ON s.client_id = c.id " +
                "LEFT JOIN users p ON s.pharmacist_id = p.id " +
                "LEFT JOIN medicines m ON s.medicine_id = m.id "+
                "WHERE s.id = ?";
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(id);
        try (ResultSet resultSet = DatabaseConnection.operationQuery(sql, connection, objects);) {
            if (resultSet.next()) {
                return Optional.of(mapResultSetToSaleWithDetails(resultSet));
            }
        }
        return Optional.empty();
    }
    public List<Sale> selectAll(Connection connection) throws SQLException {
        String sql = "SELECT s.*, " +
                "c.name as client_name, p.name as pharmacist_name, m.name as medicine_name " +
                "FROM sales s " +
                "LEFT JOIN users c ON s.client_id = c.id " +
                "LEFT JOIN users p ON s.pharmacist_id = p.id " +
                "LEFT JOIN medicines m ON s.medicine_id = m.id";
        List<Sale> sales = new ArrayList<>();
        try (ResultSet set = DatabaseConnection.operationQuery(sql, connection,  null)){
            while (set.next()) {
                sales.add(mapResultSetToSaleWithDetails(set));
            }
            return sales;
        }
    }
    public List<Sale> SelectAllWithDetails() throws SQLException {
        String sql = "SELECT s.*, " +
                "c.name as client_name, p.name as pharmacist_name, m.name as medicine_name " +
                "FROM sales s " +
                "LEFT JOIN users c ON s.client_id = c.id " +
                "LEFT JOIN users p ON s.pharmacist_id = p.id " +
                "LEFT JOIN medicines m ON s.medicine_id = m.id " +
                "ORDER BY s.sale_datetime DESC";

        List<Sale> sales = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                sales.add(mapResultSetToSaleWithDetails(resultSet));
            }
        }
        return sales;
    }
    public List<Sale> selectByClientId(Integer clientId) throws SQLException {
        String sql = "SELECT * FROM sales WHERE client_id = ? ORDER BY sale_datetime DESC";
        List<Sale> sales = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, clientId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    sales.add(mapResultSetToSale(resultSet));
                }
            }
        }
        return sales;
    }
    public List<Sale> selectByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        String sql = "SELECT * FROM sales WHERE sale_datetime BETWEEN ? AND ? ORDER BY sale_datetime DESC";
        List<Sale> sales = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setTimestamp(1, Timestamp.valueOf(startDate));
            statement.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    sales.add(mapResultSetToSale(resultSet));
                }
            }
        }
        return sales;
    }
    public List<Sale> selectTodaySales() throws SQLException {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return selectByDateRange(startOfDay, endOfDay);
    }
    public boolean update(Sale sale) throws SQLException {
        String sql = "UPDATE sales SET client_id = ?, pharmacist_id = ?, sale_datetime = ?, " +
                "medicine_id = ?, quantity = ?, total_amount = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setSaleParameters(statement, sale);
            statement.setInt(7, sale.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Продажа обновлена: ID " + sale.getId());
                return true;
            }
            return false;
        }
    }
    public boolean delete(Connection connection, Integer id) throws SQLException {
        String sql = "DELETE FROM sales WHERE id = ?";
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(id);
        int result = DatabaseConnection.operationUpdate(sql, connection, objects);
        if (result > 0) {
            System.out.println("Продажа удалена: ID " + id);
            return true;
        }
        return false;
    }
    public boolean existsById(Connection connection, Integer id) throws SQLException {
        String sql = "SELECT 1 FROM sales WHERE id = ?";
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(id);
        try (ResultSet resultSet = DatabaseConnection.operationQuery(sql, connection, objects)) {
            return resultSet.next();
        }
    }
    public int getTotalSalesCount() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM sales";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
            return 0;
        }
    }
    public BigDecimal getTotalRevenue() throws SQLException {
        String sql = "SELECT SUM(total_amount) as total FROM sales";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                BigDecimal total = resultSet.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        }
    }
    public BigDecimal getRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        String sql = "SELECT SUM(total_amount) as total FROM sales WHERE sale_datetime BETWEEN ? AND ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setTimestamp(1, Timestamp.valueOf(startDate));
            statement.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    BigDecimal total = resultSet.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }
    public List<Object[]> getMedicineSalesStatistics() throws SQLException {
        String sql = "SELECT m.name, SUM(s.quantity) as total_quantity, SUM(s.total_amount) as total_revenue " +
                "FROM sales s " +
                "JOIN medicines m ON s.medicine_id = m.id " +
                "GROUP BY m.id, m.name " +
                "ORDER BY total_revenue DESC";

        List<Object[]> statistics = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Object[] stat = new Object[3];
                stat[0] = resultSet.getString("name");
                stat[1] = resultSet.getInt("total_quantity");
                stat[2] = resultSet.getBigDecimal("total_revenue");
                statistics.add(stat);
            }
        }
        return statistics;
    }
    private void setSaleParameters(PreparedStatement statement, Sale sale) throws SQLException {
        statement.setInt(1, sale.getClientId());
        statement.setInt(2, sale.getPharmacistId());
        statement.setTimestamp(3, Timestamp.valueOf(
                sale.getSaleDateTime() != null ? sale.getSaleDateTime() : LocalDateTime.now()
        ));
        statement.setInt(4, sale.getMedicineId());
        statement.setInt(5, sale.getQuantity());
        statement.setBigDecimal(6, sale.getTotalAmount());
    }
    private Sale mapResultSetToSale(ResultSet resultSet) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp("sale_datetime");
        LocalDateTime saleDateTime = timestamp != null ? timestamp.toLocalDateTime() : null;

        return new Sale(
                resultSet.getInt("id"),
                resultSet.getInt("client_id"),
                resultSet.getInt("pharmacist_id"),
                saleDateTime,
                resultSet.getInt("medicine_id"),
                resultSet.getInt("quantity"),
                resultSet.getBigDecimal("total_amount")
        );
    }
    private Sale mapResultSetToSaleWithDetails(ResultSet resultSet) throws SQLException {
        Sale sale = mapResultSetToSale(resultSet);

        // Устанавливаем дополнительные поля из JOIN
        sale.setClientName(resultSet.getString("client_name"));
        sale.setPharmacistName(resultSet.getString("pharmacist_name"));
        sale.setMedicineName(resultSet.getString("medicine_name"));

        return sale;
    }
}