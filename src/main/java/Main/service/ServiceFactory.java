// Main.factories.ServiceFactory.java
package Main.service;

import Main.repositories.IUserRepository;
import Main.repositories.RepositoryFactory;
import Main.repositories.UserRepository;
import Main.service.IUserService;
import Main.service.UserService;
// Другие импорты по мере необходимости

public class ServiceFactory implements IServiceFactory {
    private final IUserRepository userRepository;
    public ServiceFactory() {
        this.userRepository = new UserRepository();
    }

    @Override
    public IUserService createUserService() {
        return new UserService(new RepositoryFactory().createUserRepository());
    }
    @Override
    public IMedicineService createMedicineService() {
        return new MedicineService(new RepositoryFactory().createMedicineRepository());
    }
    @Override
    public ISaleService createSaleService() {
        RepositoryFactory factory = new RepositoryFactory();
        return new SaleService(factory.createSaleRepository(), factory.createMedicineRepository(), factory.createUserRepository());
    }
}