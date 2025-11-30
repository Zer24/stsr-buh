package Main;

import Main.repositories.MedicineRepository;
import Main.repositories.SaleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, JsonProcessingException {
        MedicineRepository repMed = new MedicineRepository();
        SaleRepository repSal = new SaleRepository();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        System.out.println("buhs");
    }
}