package inventory_service.repository;

import inventory_service.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    
    // Automatically generates a SQL query to search by name OR code using LIKE %keyword%
    List<Medicine> findByMedicineNameContainingIgnoreCaseOrMedicineCodeContainingIgnoreCase(String name, String code);
}
