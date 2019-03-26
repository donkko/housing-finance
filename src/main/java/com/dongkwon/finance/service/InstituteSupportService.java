package com.dongkwon.finance.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.dongkwon.finance.domain.Institute;
import com.dongkwon.finance.domain.Support;
import com.dongkwon.finance.domain.SupportSummary;
import com.dongkwon.finance.exception.DataNotFoundException;
import com.dongkwon.finance.model.AmountByYear;
import com.dongkwon.finance.model.Prediction;
import com.dongkwon.finance.repository.InstituteRepository;
import com.dongkwon.finance.repository.SupportRepository;
import com.dongkwon.finance.repository.SupportSummaryRepository;

@Service
public class InstituteSupportService {
    private final InstituteRepository instituteRepository;
    private final SupportRepository supportRepository;
    private final SupportSummaryRepository supportSummaryRepository;

    public InstituteSupportService(InstituteRepository instituteRepository,
                                   SupportRepository supportRepository,
                                   SupportSummaryRepository supportSummaryRepository) {
        this.instituteRepository = instituteRepository;
        this.supportRepository = supportRepository;
        this.supportSummaryRepository = supportSummaryRepository;
    }

    public List<String> getAllInstituteNames() {
        return instituteRepository.findAll()
                                  .stream()
                                  .map(Institute::getName)
                                  .collect(Collectors.toList());
    }

    public List<AmountByYear> getAmountsByYear() {
        final List<SupportSummary> supportSummaries = supportSummaryRepository.findAll();

        // XXX: 2017년은 11월, 12월 데이터가 없는데 이 경우에도 합계를 내려줘야 하는지 확인 필요

        // Group by year
        final Map<Integer, List<SupportSummary>> yearMap =
                supportSummaries.stream().collect(Collectors.groupingBy(SupportSummary::getYear));

        // Sort by year
        final List<Integer> sortedYearList = yearMap.keySet()
                                              .stream()
                                              .sorted(Comparator.comparingInt(Integer::intValue))
                                              .collect(Collectors.toList());

        // Calculate and transform
        return sortedYearList.stream().map(year -> {
            Double totalAmount = yearMap.get(year).stream().mapToDouble(SupportSummary::getSumAmount).sum();

            Map<String, String> detailAmount = yearMap.get(year).stream().collect(Collectors.toMap(
                    entity -> entity.getInstitute().getName(),
                    SupportSummary::getSumAmountIntStr)
            );

            return AmountByYear.of(year, totalAmount, detailAmount);
        }).collect(Collectors.toList());
    }

    public SupportSummary getTopInstitute() {
        return supportSummaryRepository.findFirstByOrderBySumAmountDesc()
                                       .orElseThrow(DataNotFoundException::new);
    }

    public Pair<SupportSummary, SupportSummary> getMinMaxAmount(String instituteName) {
        final Institute oehwanInstitute =
                instituteRepository.findOneByName(instituteName)
                                   .orElseThrow(() -> new DataNotFoundException(
                                           String.format("[%s]은 등록되지 않은 기관입니다.", instituteName)));
        final SupportSummary minSupportSummary =
                supportSummaryRepository.findFirstByInstituteOrderByAverageAmountAsc(oehwanInstitute)
                                        .orElseThrow(DataNotFoundException::new);
        final SupportSummary maxSupportSummary =
                supportSummaryRepository.findFirstByInstituteOrderByAverageAmountDesc(oehwanInstitute)
                                        .orElseThrow(DataNotFoundException::new);

        return Pair.of(minSupportSummary, maxSupportSummary);
    }

    public Prediction predict2018(String instituteName, Integer month) {
        final Institute institute =
                instituteRepository.findOneByName(instituteName)
                                   .orElseThrow(() -> new DataNotFoundException(
                                           String.format("[%s]은 등록되지 않은 기관입니다.", instituteName)));

        final Support support;
        if (1 <= month && month <= 10) {
            // month가 1 ~ 10 일 경우, 2017년 같은 달의 데이터를 가져온다
            support = supportRepository.findOneByInstituteAndYearAndMonth(institute, 2017, month)
                                      .orElseThrow(DataNotFoundException::new);
        } else {
            // month가 11 ~ 12 일 경우, 2016년 같은 달의 데이터를 가져온다
            support = supportRepository.findOneByInstituteAndYearAndMonth(institute, 2016, month)
                                      .orElseThrow(DataNotFoundException::new);
        }

        return Prediction.builder()
                         .bank(String.valueOf(institute.getCode()))
                         .year(2018)
                         .month(month)
                         .amount(support.getAmount().intValue())
                         .build();
    }
}
