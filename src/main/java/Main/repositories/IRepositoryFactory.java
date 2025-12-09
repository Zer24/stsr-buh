package Main.repositories;

import Main.service.IMedicineService;
import Main.service.ISaleService;
import Main.service.IUserService;

public interface IRepositoryFactory {
    IUserRepository createUserRepository();
    ISaleRepository createSaleRepository();
    IMedicineRepository createMedicineRepository();
}
