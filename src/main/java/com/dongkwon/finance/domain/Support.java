package com.dongkwon.finance.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "support")
public class Support {
    public static Support of(Integer year, Integer month, Double amount, Institute institute) {
        if (year == null || month == null || amount == null || institute == null) {
            throw new IllegalArgumentException();
        }
        if (year < 1) {
            throw new IllegalArgumentException("Invalid 'year'");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid 'month'");
        }
        final Support support = new Support();
        support.setYear(year);
        support.setMonth(month);
        support.setAmount(amount);
        support.setInstitute(institute);

        return support;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "institute_code", nullable = false)
    private Institute institute;

    public void setInstitute(Institute institute) {
        this.institute = institute;
        if (!institute.getSupports().contains(this)) {
            institute.getSupports().add(this);
        }
    }
}
