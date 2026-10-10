package inventory_service.controller;

import inventory_service.repository.MedicineRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryApiController {

    private final MedicineRepository medicineRepository;

    public InventoryApiController(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public record InventoryStatus(String code, Integer quantityAvailable, String facilityName) {}

    @GetMapping(value = {"/{code}", "/"})
    public ResponseEntity<InventoryStatus> checkAvailability(@PathVariable(required = false) String code) {
        if (code == null || code.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return medicineRepository.findByMedicineCode(code)
                .map(m -> {
                    // Handle null quantities safely
                    Integer qty = m.getQuantityAvailable() != null ? m.getQuantityAvailable() : 0;
                    return ResponseEntity.ok(new InventoryStatus(m.getMedicineCode(), qty, m.getFacilityName()));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
