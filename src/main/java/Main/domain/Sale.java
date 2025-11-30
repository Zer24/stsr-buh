package Main.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Sale {
    private Integer id;
    private Integer clientId;
    private Integer pharmacistId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime saleDateTime;
    private Integer medicineId;
    private Integer quantity;
    private BigDecimal totalAmount;

    // Дополнительные поля для JOIN запросов
    private String clientName;
    private String pharmacistName;
    private String medicineName;

    // Конструкторы
    public Sale() {}

    public Sale(Integer clientId, Integer pharmacistId, Integer medicineId,
                Integer quantity, BigDecimal totalAmount) {
        this.clientId = clientId;
        this.pharmacistId = pharmacistId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.saleDateTime = LocalDateTime.now();
    }

    public Sale(Integer id, Integer clientId, Integer pharmacistId, LocalDateTime saleDateTime,
                Integer medicineId, Integer quantity, BigDecimal totalAmount) {
        this.id = id;
        this.clientId = clientId;
        this.pharmacistId = pharmacistId;
        this.saleDateTime = saleDateTime;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    // Геттеры и сеттеры
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }

    public Integer getPharmacistId() { return pharmacistId; }
    public void setPharmacistId(Integer pharmacistId) { this.pharmacistId = pharmacistId; }

    public LocalDateTime getSaleDateTime() { return saleDateTime; }
    public void setSaleDateTime(LocalDateTime saleDateTime) { this.saleDateTime = saleDateTime; }

    public Integer getMedicineId() { return medicineId; }
    public void setMedicineId(Integer medicineId) { this.medicineId = medicineId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    // Дополнительные поля
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getPharmacistName() { return pharmacistName; }
    public void setPharmacistName(String pharmacistName) { this.pharmacistName = pharmacistName; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    @Override
    public String toString() {
        if (clientName != null && pharmacistName != null && medicineName != null) {
            return String.format("Sale{id=%d, client='%s', pharmacist='%s', medicine='%s', quantity=%d, amount=%s, date=%s}",
                    id, clientName, pharmacistName, medicineName, quantity, totalAmount, saleDateTime);
        } else {
            return String.format("Sale{id=%d, clientId=%d, pharmacistId=%d, medicineId=%d, quantity=%d, amount=%s, date=%s}",
                    id, clientId, pharmacistId, medicineId, quantity, totalAmount, saleDateTime);
        }
    }
}