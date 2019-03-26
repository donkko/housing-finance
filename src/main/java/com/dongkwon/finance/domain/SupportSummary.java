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
@Table(name = "support_summary")
public class SupportSummary {
    public static SupportSummary of(Integer year, Double sumAmount, Double averageAmount, Institute institute) {
        if (year == null || sumAmount == null || averageAmount == null || institute == null) {
            throw new IllegalArgumentException();
        }
        final SupportSummary supportSummary = new SupportSummary();
        supportSummary.setYear(year);
        supportSummary.setSumAmount(sumAmount);
        supportSummary.setAverageAmount(averageAmount);
        supportSummary.setInstitute(institute);

        return supportSummary;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "sum_amount", nullable = false)
    private Double sumAmount;

    public String getSumAmountIntStr() {
        return String.valueOf(sumAmount.intValue());
    }

    @Column(name = "average_amount", nullable = false)
    private Double averageAmount;

    public String getAverageAmountIntStr() {
        return String.valueOf(averageAmount.intValue());
    }

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "institute_code", nullable = false)
    private Institute institute;

    public void setInstitute(Institute institute) {
        this.institute = institute;
        if (!institute.getSupportSummaries().contains(this)) {
            institute.getSupportSummaries().add(this);
        }
    }
}
