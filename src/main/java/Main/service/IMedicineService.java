// Main.service.IMedicineService.java
package Main.service;

import Main.domain.Medicine;
import java.util.List;

public interface IMedicineService {
    void addMedicine(Medicine medicine);
    void updateMedicine(Medicine medicine);
    void deleteMedicine(int id);
    boolean updateMedicineStock(Integer medicineId, Integer newQuantity);
    List<Medicine> getAllMedicines();
    Medicine getMedicineById(Integer id);
    List<Medicine> getMedicinesByName(String name);
}