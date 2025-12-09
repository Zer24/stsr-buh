package Main.service;

import Main.DatabaseConnection;
import Main.NotFoundException;
import Main.domain.Medicine;
import Main.repositories.IMedicineRepository;
import Main.repositories.MedicineRepository;
import Main.repositories.RepositoryFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MedicineService implements IMedicineService{
    private IMedicineRepository medicineRepository;

    public MedicineService(IMedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public void addMedicine(Medicine medicine) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.startTransaction();
            if(medicine.getName().length()==0){
                throw new RuntimeException("Препарат должен иметь название!");
            }
            if (medicineRepository.selectByName(medicine.getName()).isPresent()) {
                throw new RuntimeException("Лекарство с названием '" + medicine.getName() + "' уже существует");
            }
            int added = medicineRepository.insert(connection, medicine);
            if(added==0){
                throw new RuntimeException("Изменений не произошло по неизвестной причине");
            }
            System.out.println("Добавлено новое лекарство: " + medicine.getName());

            DatabaseConnection.closeConnection(connection, true);
        } catch (SQLException e) {
            DatabaseConnection.closeConnection(connection, false);
            throw new RuntimeException("Ошибка при добавлении лекарства: " + e.getMessage(), e);
        }
    }
    public void updateMedicine(Medicine medicine) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.startTransaction();
            if(medicine.getName().length()==0){
                throw new RuntimeException("Препарат должен иметь название!");
            }
            medicineRepository.update(connection, medicine);

            System.out.println("Добавлено новое лекарство: " + medicine.getName());

            DatabaseConnection.closeConnection(connection, true);
        } catch (SQLException e) {
            DatabaseConnection.closeConnection(connection, false);
            throw new RuntimeException("Ошибка при добавлении лекарства: " + e.getMessage(), e);
        }
    }
    public void deleteMedicine(int id){
        Connection connection = null;
        try{
            connection = DatabaseConnection.startTransaction();
            if(id<0){
                throw new RuntimeException("id меньше нуля");
            }
            if(!medicineRepository.existsById(id)){
                throw new RuntimeException("Препарата с таким id не существует");
            }
            medicineRepository.delete(connection, id);
            DatabaseConnection.closeConnection(connection, true);
        } catch (SQLException e) {
            DatabaseConnection.closeConnection(connection, false);
            throw new RuntimeException(e);
        }
    }
    public boolean updateMedicineStock(Integer medicineId, Integer newQuantity) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.startTransaction();
            Optional<Medicine> medicineOpt = medicineRepository.selectById(connection, medicineId);
            if (medicineOpt.isEmpty()) {throw new RuntimeException("Лекарство с ID " + medicineId + " не найдено");}
            if (newQuantity < 0) {throw new RuntimeException("Количество не может быть отрицательным");}
            boolean updated = medicineRepository.updateStockQuantity(connection, medicineId, newQuantity);
            connection.commit();
            DatabaseConnection.closeConnection(connection, true);
            return updated;
        } catch (SQLException e) {
            DatabaseConnection.closeConnection(connection, false);
            throw new RuntimeException("Ошибка при обновлении запаса: " + e.getMessage(), e);
        }
    }
    public List<Medicine> getAllMedicines() {
        Connection connection = null;
        try{
            connection = DatabaseConnection.startTransaction();
            List<Medicine> medicines = medicineRepository.selectAll(connection);
            DatabaseConnection.closeConnection(connection, true);
            return medicines;
        }catch (SQLException e) {
            DatabaseConnection.closeConnection(connection, false);
            throw new RuntimeException("Ошибка при получении списка лекарств "+e.getMessage(), e);
        }
    }

    public Medicine getMedicineById(Integer id) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.startTransaction();
            Optional<Medicine> medicine = medicineRepository.selectById(connection, id);
            if(medicine.isEmpty()){
                DatabaseConnection.closeConnection(connection, false);
                throw new NotFoundException("Не найдено препарата с таким ID");
            }
            DatabaseConnection.closeConnection(connection,true);
            return medicine.get();
        } catch (SQLException e) {
            DatabaseConnection.closeConnection(connection,false);
            throw new RuntimeException("Ошибка при поиске лекарства: "+e.getMessage() , e);
        }
    }
    public List<Medicine> getMedicinesByName(String name) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.startTransaction();
            List<Medicine> allMedicines = medicineRepository.selectAll(connection);
            DatabaseConnection.closeConnection(connection, true);

            return allMedicines.stream()
                    .filter(medicine -> {
                        String medicineName = medicine.getName().toLowerCase();
                        String searchName = name.toLowerCase();
                        return medicineName.contains(searchName);
                    })
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            DatabaseConnection.closeConnection(connection, false);
            throw new RuntimeException("Ошибка при поиске препаратов: " + e.getMessage(), e);
        }
    }
}