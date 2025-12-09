// Main.factories.IServiceFactory.java
package Main.service;

import Main.service.IUserService;
// Другие сервисы добавьте по мере необходимости

public interface IServiceFactory {
    IUserService createUserService();
    ISaleService createSaleService();
    IMedicineService createMedicineService();
}