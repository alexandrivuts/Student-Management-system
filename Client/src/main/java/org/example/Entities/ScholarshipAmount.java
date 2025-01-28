package org.example.Entities;

import jakarta.persistence.*;

import java.math.BigDecimal;


public class ScholarshipAmount {

    private int amount_id;

    private BigDecimal min_average; // изменено с Float на BigDecimal

    private BigDecimal max_average; // изменено с Float на BigDecimal

    private BigDecimal amount; // изменено с Float на BigDecimal

    public int getAmount_id() {
        return amount_id;
    }
    public void setAmount_id(int amount_id) {
        this.amount_id = amount_id;
    }

    public BigDecimal getMin_average() {
        return min_average;
    }
    public void setMin_average(BigDecimal min_average) {
        this.min_average = min_average;
    }

    public BigDecimal getMax_average() {
        return max_average;
    }
    public void setMax_average(BigDecimal max_average) {
        this.max_average = max_average;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
