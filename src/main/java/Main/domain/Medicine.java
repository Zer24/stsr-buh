package Main.domain;

import java.math.BigDecimal;

public class Medicine {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private String dosageForm;
    private Integer quantityInStock;
    private Boolean requiresPrescription;

    // конструкторы, геттеры, сеттеры
    public Medicine() {}

    public Medicine(Integer id, String name, String description, BigDecimal price,
                    String dosageForm, Integer quantityInStock, Boolean requiresPrescription) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.dosageForm = dosageForm;
        this.quantityInStock = quantityInStock;
        this.requiresPrescription = requiresPrescription;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public Integer getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(Integer quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public Boolean getRequiresPrescription() {
        return requiresPrescription;
    }

    public void setRequiresPrescription(Boolean requiresPrescription) {
        this.requiresPrescription = requiresPrescription;
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", dosageForm='" + dosageForm + '\'' +
                ", quantityInStock=" + quantityInStock +
                ", requiresPrescription=" + requiresPrescription +
                '}';
    }
}