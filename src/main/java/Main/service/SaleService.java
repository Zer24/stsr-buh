package Main.service;

import Main.DatabaseConnection;
import Main.NotFoundException;
import Main.domain.Medicine;
import Main.domain.Sale;
import Main.repositories.MedicineRepository;
import Main.repositories.SaleRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SaleService {
    private SaleRepository saleRepository;

    public SaleService() {
        this.saleRepository = new SaleRepository();
    }

    public Sale addSaleWithTransaction(Sale sale) {
        Connection connection = null;
        try {
            // Получаем соединение (начало транзакции)
            connection = DatabaseConnection.startTransaction();

            if(sale.getClientId()==null || sale.getMedicineId()==null || sale.getPharmacistId()==null){throw new RuntimeException("Вместо значений полей получено null");}
            if(sale.getQuantity()<=0){throw new RuntimeException("Нельзя продать неположительное число препарата");}
            if(sale.getTotalAmount().compareTo(BigDecimal.valueOf(0))==-1){throw new RuntimeException("Продажа с отрицательной выручкой");}

            Optional<Sale> savedSale = saleRepository.insert(sale);
            if(savedSale.isEmpty()){throw new RuntimeException("Ошибка при сохранении ");}
            System.out.println("Добавлена новая запись: " + savedSale.toString());

            connection.commit();
            return savedSale.get();

        } catch (SQLException e) {
            DatabaseConnection.closeConnection(connection, false);
            throw new RuntimeException("Ошибка при добавлении лекарства: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(connection, true);
        }
    }
    // Простые методы без сложной логики могут не требовать явного управления транзакциями
    public List<Sale> getAllSales() {
        Connection connection = null;
        try{
            connection = DatabaseConnection.startTransaction();
            List<Sale> sales = saleRepository.selectAll(connection);
            DatabaseConnection.closeConnection(connection, true);
            return sales;
        }catch (SQLException e) {
            DatabaseConnection.closeConnection(connection, false);
            throw new RuntimeException("Ошибка при получении списка лекарств "+e.getMessage(), e);
        }
    }

    public Sale getSaleById(Integer id) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.startTransaction();
            Optional<Sale> sale = saleRepository.selectById(connection, id);
            if(sale.isEmpty()){
                DatabaseConnection.closeConnection(connection, false);
                throw new NotFoundException("Не найдено продажи с таким ID");
            }
            return sale.get();
        } catch (SQLException e) {
            DatabaseConnection.closeConnection(connection, false);
            throw new RuntimeException("Ошибка при поиске лекарства", e);
        }
    }
}