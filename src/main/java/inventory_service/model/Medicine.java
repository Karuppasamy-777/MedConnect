package inventory_service.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * The @Entity annotation tells Spring Data JPA that this class represents a table 
 * in our H2 database. An instance of this class represents a single row in that table.
 */
@Entity
public class Medicine {

    /**
     * @Id marks this field as the Primary Key for the table.
     * @GeneratedValue(strategy = GenerationType.IDENTITY) tells the database to 
     * automatically generate a unique, auto-incrementing ID for every new medicine.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column(nullable = false) ensures that the database will reject any 
     * attempt to save a medicine without a name. This fulfills the requirement 
     * that this field is required.
     */
    @Column(nullable = false)
    private String medicineName;

    @Column(nullable = false)
    private String medicineCode;

    @Column(nullable = false)
    private String facilityName;

    @Column(nullable = false)
    private Integer quantityAvailable;

    @Column(nullable = false)
    private Double dailyConsumption;

    @Column(nullable = false)
    private LocalDate expiryDate;

    /**
     * No-argument constructor.
     * JPA (Hibernate) requires a default, empty constructor to function properly. 
     * It uses this to create empty objects before filling them with data from the database.
     */
    public Medicine() {
    }

    // --- Getters and Setters ---
    // Getters allow us to read the data, while setters allow us to modify it.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicineCode() {
        return medicineCode;
    }

    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }

    /**
     * Setter for quantityAvailable.
     * Includes validation to ensure the quantity cannot be set to a negative number.
     */
    public void setQuantityAvailable(Integer quantityAvailable) {
        if (quantityAvailable != null && quantityAvailable < 0) {
            throw new IllegalArgumentException("Quantity available cannot be negative");
        }
        this.quantityAvailable = quantityAvailable;
    }

    public Double getDailyConsumption() {
        return dailyConsumption;
    }

    /**
     * Setter for dailyConsumption.
     * Includes validation to ensure the consumption cannot be set to a negative number.
     * Zero is allowed, meaning the medicine is currently not being consumed.
     */
    public void setDailyConsumption(Double dailyConsumption) {
        if (dailyConsumption != null && dailyConsumption < 0) {
            throw new IllegalArgumentException("Daily consumption cannot be negative");
        }
        this.dailyConsumption = dailyConsumption;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}
