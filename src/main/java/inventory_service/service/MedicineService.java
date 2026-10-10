package inventory_service.service;

import inventory_service.model.Medicine;
import inventory_service.repository.MedicineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    public Optional<Medicine> getMedicineById(Long id) {
        return medicineRepository.findById(id);
    }

    public Medicine saveMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    public void deleteMedicine(Long id) {
        if (!medicineRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete: Medicine with ID " + id + " does not exist.");
        }
        medicineRepository.deleteById(id);
    }

    // New search method utilizing the custom repository query
    public List<Medicine> searchMedicines(String keyword) {
        return medicineRepository.findByMedicineNameContainingIgnoreCaseOrMedicineCodeContainingIgnoreCase(keyword, keyword);
    }
}
