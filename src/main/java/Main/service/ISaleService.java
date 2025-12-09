// Main.service.ISaleService.java
package Main.service;

import Main.domain.Sale;
import java.util.List;

public interface ISaleService {
    void addSale(Sale sale);
    List<Sale> getAllSales();
    Sale getSaleById(Integer id);
    void deleteSale(int id);
}