package inventory_service.controller;

import inventory_service.model.Medicine;
import inventory_service.service.MedicineService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    // A Java Record to bundle the Medicine with its calculated UI statuses
    public record MedicineView(Medicine medicine, String stockStatus, String expiryStatus) {}

    @GetMapping("/medicines")
    public String listMedicines(@RequestParam(required = false) String search, Model model) {
        // 1. Fetch ALL medicines to calculate global metrics accurately (independent of search)
        List<Medicine> allMedicines = medicineService.getAllMedicines();
        
        long lowStockCount = 0;
        long expiredCount = 0;
        long nearExpiryCount = 0;
        LocalDate today = LocalDate.now();

        // Calculate global metrics
        for (Medicine m : allMedicines) {
            // Low stock: less than 7 days supply (only if daily consumption > 0)
            if (m.getDailyConsumption() != null && m.getDailyConsumption() > 0) {
                if (m.getQuantityAvailable() < (m.getDailyConsumption() * 7)) {
                    lowStockCount++;
                }
            }
            
            // Expiry tracking
            if (m.getExpiryDate() != null) {
                if (m.getExpiryDate().isBefore(today)) {
                    expiredCount++;
                } else if (m.getExpiryDate().isBefore(today.plusDays(30))) {
                    nearExpiryCount++;
                }
            }
        }

        // 2. Fetch the display list (either filtered by search, or all)
        List<Medicine> displayList = (search == null || search.trim().isEmpty()) 
            ? allMedicines 
            : medicineService.searchMedicines(search);
        
        // 3. Map the display list to our View wrapper to avoid repeating logic in Thymeleaf
        List<MedicineView> viewList = displayList.stream().map(m -> {
            String stockStatus = "OK";
            if (m.getDailyConsumption() != null && m.getDailyConsumption() > 0 && m.getQuantityAvailable() < (m.getDailyConsumption() * 7)) {
                stockStatus = "Low Stock";
            }
            
            String expiryStatus = "OK";
            if (m.getExpiryDate() != null) {
                if (m.getExpiryDate().isBefore(today)) {
                    expiryStatus = "Expired";
                } else if (m.getExpiryDate().isBefore(today.plusDays(30))) {
                    expiryStatus = "Expiring Soon";
                }
            }
            return new MedicineView(m, stockStatus, expiryStatus);
        }).toList();

        // 4. Pass everything to the Model
        model.addAttribute("medicines", viewList);
        model.addAttribute("search", search);
        model.addAttribute("totalCount", allMedicines.size());
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("expiredCount", expiredCount);
        model.addAttribute("nearExpiryCount", nearExpiryCount);
        
        return "medicines";
    }

    @GetMapping("/medicines/new")
    public String showAddMedicineForm(Model model) {
        model.addAttribute("medicine", new Medicine());
        return "add-medicine";
    }

    @PostMapping("/medicines")
    public String saveMedicine(@ModelAttribute Medicine medicine, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Please ensure numbers and dates are formatted correctly.");
            return "add-medicine";
        }
        
        try {
            if (medicine.getMedicineName() == null || medicine.getMedicineName().trim().isEmpty()) {
                throw new IllegalArgumentException("Medicine Name is required.");
            }
            if (medicine.getMedicineCode() == null || medicine.getMedicineCode().trim().isEmpty()) {
                throw new IllegalArgumentException("Medicine Code is required.");
            }
            if (medicine.getFacilityName() == null || medicine.getFacilityName().trim().isEmpty()) {
                throw new IllegalArgumentException("Facility Name is required.");
            }
            if (medicine.getQuantityAvailable() == null || medicine.getQuantityAvailable() < 0) {
                throw new IllegalArgumentException("Quantity Available must be 0 or greater.");
            }
            if (medicine.getDailyConsumption() == null || medicine.getDailyConsumption() < 0) {
                throw new IllegalArgumentException("Average Daily Consumption must be 0 or greater.");
            }
            if (medicine.getExpiryDate() == null) {
                throw new IllegalArgumentException("Expiry Date is required.");
            }

            medicineService.saveMedicine(medicine);
            return "redirect:/medicines";
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "add-medicine";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An unexpected error occurred while saving. Please try again.");
            return "add-medicine";
        }
    }
}
