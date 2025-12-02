package Main.repositories;

import Main.DatabaseConnection;
import Main.domain.Medicine;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MedicineRepository {
    public int insert(Connection connection, Medicine medicine) throws SQLException {
        String sql = "INSERT INTO medicines (name, description, price, dosage_form, quantity_in_stock, requires_prescription) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(medicine.getName());
        objects.add(medicine.getDescription());
        objects.add(medicine.getPrice());
        objects.add(medicine.getDosageForm());
        objects.add(medicine.getQuantityInStock());
        objects.add(medicine.getRequiresPrescription());
        System.out.println(medicine);
        return DatabaseConnection.operationUpdate(sql, connection, objects);
    }
    public Optional<Medicine> selectById(Connection connection, Integer id) throws SQLException {
        String sql = "SELECT * FROM medicines WHERE id = ?";
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(id);
        try (ResultSet resultSet = DatabaseConnection.operationQuery(sql, connection, objects)) {
            if (resultSet.next()) {
                return Optional.of(mapResultSetToMedicine(resultSet));
            }
        }
        return Optional.empty();
    }
    public Optional<Medicine> selectByName(String name) throws SQLException {
        String sql = "SELECT * FROM medicines WHERE name = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToMedicine(resultSet));
                }
            }
        }
        return Optional.empty();
    }
    public List<Medicine> selectAll(Connection connection) throws SQLException {
        String sql = "SELECT * FROM medicines";
        List<Medicine> medicines = new ArrayList<>();
        try (ResultSet resultSet = DatabaseConnection.operationQuery(sql, connection, null)) {
            while (resultSet.next()) {
                medicines.add(mapResultSetToMedicine(resultSet));
            }
        }
        return medicines;
    }
    public boolean update(Connection connection, Medicine medicine) throws SQLException {
        String sql = "UPDATE medicines SET name = ?, description = ?, price = ?, " +
                "dosage_form = ?, quantity_in_stock = ?, requires_prescription = ? " +
                "WHERE id = ?";
        ArrayList<Object> objects = new ArrayList<>(Arrays.asList(medicine.getName(),
                medicine.getDescription(),
                medicine.getPrice(),
                medicine.getDosageForm(),
                medicine.getQuantityInStock(),
                medicine.getRequiresPrescription(),
                medicine.getId()));
        return DatabaseConnection.operationUpdate(sql, connection, objects)>0;
    }
    public boolean updateStockQuantity(Connection connection, Integer medicineId, Integer itemsSold) throws SQLException {
        String sql = "UPDATE medicines SET quantity_in_stock = quantity_in_stock - ? WHERE id = ?";
        ArrayList<Object> objects = new ArrayList<>(Arrays.asList(itemsSold, medicineId));
        int result = DatabaseConnection.operationUpdate(sql, connection, objects);
        if (result > 0) {
            System.out.println("Количество препарата ID " + medicineId + " обновлено: ");
            return true;
        }
        return false;
    }
    public boolean delete(Connection connection, Integer id) throws SQLException {
        String sql = "DELETE FROM medicines WHERE id = ?";
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(id);
        int rowsAffected = DatabaseConnection.operationUpdate(sql, connection, objects);
        if (rowsAffected > 0) {
            System.out.println("Препарат удален: ID " + id);
            return true;
        }
        return false;
    }
    public boolean existsById(Integer id) throws SQLException {
        String sql = "SELECT 1 FROM medicines WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
    private void setMedicineParameters(PreparedStatement statement, Medicine medicine) throws SQLException {
        statement.setString(1, medicine.getName());
        statement.setString(2, medicine.getDescription());
        statement.setBigDecimal(3, medicine.getPrice());
        statement.setString(4, medicine.getDosageForm());
        statement.setInt(5, medicine.getQuantityInStock());
        statement.setBoolean(6, medicine.getRequiresPrescription());
    }
    private Medicine mapResultSetToMedicine(ResultSet resultSet) throws SQLException {
        return new Medicine(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getBigDecimal("price"),
                resultSet.getString("dosage_form"),
                resultSet.getInt("quantity_in_stock"),
                resultSet.getBoolean("requires_prescription")
        );
    }
}