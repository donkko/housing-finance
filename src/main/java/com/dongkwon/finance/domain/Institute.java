package com.dongkwon.finance.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "institute")
public class Institute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code")
    private Long code;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ToString.Exclude
    @OneToMany(mappedBy = "institute", fetch = FetchType.LAZY)
    private List<Support> supports = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "institute", fetch = FetchType.LAZY)
    private List<SupportSummary> supportSummaries = new ArrayList<>();
}
