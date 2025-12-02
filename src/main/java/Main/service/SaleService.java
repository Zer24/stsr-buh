package Main.service;

import Main.DatabaseConnection;
import Main.NotFoundException;
import Main.domain.Medicine;
import Main.domain.Sale;
import Main.repositories.MedicineRepository;
import Main.repositories.SaleRepository;
import Main.repositories.UserRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SaleService {
    private SaleRepository saleRepository;
    private MedicineRepository medicineRepository;
    private UserRepository userRepository;

    public SaleService() {
        this.saleRepository = new SaleRepository();
        this.medicineRepository = new MedicineRepository();
        this.userRepository = new UserRepository();
    }

    public void addSale(Sale sale) {
        Connection connection = null;
        try {
            // Получаем соединение (начало транзакции)
            connection = DatabaseConnection.startTransaction();

            if(sale.getClientId()==null || sale.getMedicineId()==null || sale.getPharmacistId()==null){throw new RuntimeException("Вместо значений полей получено null");}
            if(userRepository.selectById(connection,sale.getPharmacistId()).get().isClient()){
                throw new RuntimeException("Пользователь не может продавать препараты");
            }
            if(sale.getQuantity()<=0){throw new RuntimeException("Нельзя продать неположительное число препарата");}
            if(sale.getTotalAmount().compareTo(BigDecimal.valueOf(0))==-1){throw new RuntimeException("Продажа с отрицательной выручкой");}

            if(medicineRepository.selectById(connection, sale.getMedicineId()).get().getQuantityInStock()<sale.getQuantity()){throw new RuntimeException("Нельзя продать больше препаратов чем есть в наличии");}

            if(!medicineRepository.updateStockQuantity(connection, sale.getMedicineId(), sale.getQuantity())){throw new RuntimeException("Произошла ошибка при изменении числа препаратов в наличии");}

            if(!saleRepository.insert(connection, sale)){throw new RuntimeException("Ошибка при сохранении ");}

            DatabaseConnection.closeConnection(connection, true);
        } catch (SQLException e) {
            DatabaseConnection.closeConnection(connection, false);
            throw new RuntimeException("Ошибка при добавлении лекарства: " + e.getMessage(), e);
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

    public void deleteSale(int id){
        Connection connection = null;
        try{
            connection = DatabaseConnection.startTransaction();
            if(!saleRepository.existsById(connection, id)){
                throw new RuntimeException("Пользователя с таким id не существует");
            }
            if(saleRepository.delete(connection, id)){
                DatabaseConnection.closeConnection(connection, true);
            }
        } catch (SQLException e) {
            DatabaseConnection.closeConnection(connection, false);
            throw new RuntimeException(e);
        }
    }
}