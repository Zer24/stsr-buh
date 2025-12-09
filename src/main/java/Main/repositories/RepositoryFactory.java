package Main.repositories;

public class RepositoryFactory implements IRepositoryFactory{

    @Override
    public IUserRepository createUserRepository() {return new UserRepository();}

    @Override
    public ISaleRepository createSaleRepository() {
        return new SaleRepository();
    }

    @Override
    public IMedicineRepository createMedicineRepository() {
        return new MedicineRepository();
    }
}
