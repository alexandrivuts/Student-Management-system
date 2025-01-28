package org.example.Server.Service;

import org.example.Server.DAO.ScholarshipAmountDAO;
import org.example.Server.Entities.ScholarshipAmount;
import org.example.Server.Utility.HibernateUtil;
import org.example.Server.Interfaces.Service;

import java.math.BigDecimal;
import java.util.List;

public class ScholarshipAmountService implements Service<ScholarshipAmount> {

    private final ScholarshipAmountDAO scholarshipAmountDAO;

    public ScholarshipAmountService() {
        this.scholarshipAmountDAO = new ScholarshipAmountDAO(HibernateUtil.getSessionFactory());
    }

    @Override
    public void insert(ScholarshipAmount scholarshipAmount) {
        if (isRangeValid(scholarshipAmount.getMin_average(), scholarshipAmount.getMax_average())
                && isRangeUnique(scholarshipAmount)) {
            scholarshipAmountDAO.insert(scholarshipAmount);
        } else {
            throw new IllegalArgumentException("Invalid or overlapping scholarship range.");
        }
    }

    @Override
    public void update(ScholarshipAmount scholarshipAmount) {
        if (isRangeValid(scholarshipAmount.getMin_average(), scholarshipAmount.getMax_average())
                && isRangeUniqueForUpdate(scholarshipAmount)) {
            scholarshipAmountDAO.update(scholarshipAmount);
        } else {
            throw new IllegalArgumentException("Invalid or overlapping scholarship range.");
        }
    }

    @Override
    public void delete(int id) {
        ScholarshipAmount existing = scholarshipAmountDAO.find(id);
        if (existing == null) {
            throw new IllegalArgumentException("Scholarship amount not found for deletion.");
        }
        scholarshipAmountDAO.delete(id);
    }

    @Override
    public ScholarshipAmount findById(int id) {
        ScholarshipAmount scholarshipAmount = scholarshipAmountDAO.find(id);
        if (scholarshipAmount == null) {
            throw new IllegalArgumentException("Scholarship amount not found.");
        }
        return scholarshipAmount;
    }

    @Override
    public List<ScholarshipAmount> findAll() {
        List<ScholarshipAmount> scholarshipAmounts = scholarshipAmountDAO.findAll();
        if (scholarshipAmounts.isEmpty()) {
            throw new IllegalStateException("No scholarship amounts found.");
        }
        return scholarshipAmounts;
    }

    private boolean isRangeValid(BigDecimal min, BigDecimal max) {
        return min != null && max != null && min.compareTo(max) < 0;
    }

    private boolean isRangeUnique(ScholarshipAmount scholarshipAmount) {
        List<ScholarshipAmount> existingAmounts = scholarshipAmountDAO.findAll();
        return existingAmounts.stream().noneMatch(existing ->
                (scholarshipAmount.getMin_average().compareTo(existing.getMax_average()) < 0
                        && scholarshipAmount.getMax_average().compareTo(existing.getMin_average()) > 0)
        );
    }

    private boolean isRangeUniqueForUpdate(ScholarshipAmount scholarshipAmount) {
        List<ScholarshipAmount> existingAmounts = scholarshipAmountDAO.findAll();
        return existingAmounts.stream()
                .filter(existing -> existing.getAmount_id() != scholarshipAmount.getAmount_id())
                .noneMatch(existing ->
                        (scholarshipAmount.getMin_average().compareTo(existing.getMax_average()) < 0
                                && scholarshipAmount.getMax_average().compareTo(existing.getMin_average()) > 0)
                );
    }
}
