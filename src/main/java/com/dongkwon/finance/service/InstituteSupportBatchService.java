package com.dongkwon.finance.service;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.dongkwon.finance.domain.Institute;
import com.dongkwon.finance.domain.Support;
import com.dongkwon.finance.domain.SupportSummary;
import com.dongkwon.finance.exception.CsvParsingException;
import com.dongkwon.finance.repository.InstituteRepository;
import com.dongkwon.finance.repository.SupportRepository;
import com.dongkwon.finance.repository.SupportSummaryRepository;
import com.fasterxml.jackson.databind.MappingIterator;
import com.google.common.annotations.VisibleForTesting;

@Service
public class InstituteSupportBatchService {
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);

    private final EntityManager entityManager;
    private final InstituteRepository instituteRepository;
    private final SupportRepository supportRepository;
    private final SupportSummaryRepository supportSummaryRepository;

    public InstituteSupportBatchService(EntityManager entityManager,
                                        InstituteRepository instituteRepository,
                                        SupportRepository supportRepository,
                                        SupportSummaryRepository supportSummaryRepository) {
        this.entityManager = entityManager;
        this.instituteRepository = instituteRepository;
        this.supportRepository = supportRepository;
        this.supportSummaryRepository = supportSummaryRepository;
    }

    static String normalizeInstituteName(String name) {
        return name.replace("(억원)", "")
                   //.replace("1)", "") // "주택도시기금1)"에서 "1)"은 제외할 필요가 없다고 메일로 확인 받음
                   .trim();
    }

    @Transactional
    public void deleteCurrentDataAndSaveNewData(MappingIterator<String[]> csvIterator) {
        deleteCurrentData();
        setInstituteSupports(csvIterator);
        calculateInstituteSupportSummaries();
    }

    private void deleteCurrentData() {
        supportRepository.deleteAllInBatch();
        supportSummaryRepository.deleteAllInBatch();
        instituteRepository.deleteAllInBatch();
    }

    @Transactional
    public void setInstituteSupports(MappingIterator<String[]> csvIterator) {
        try {
            final List<String> header = Arrays.asList(csvIterator.next());
            final List<Institute> headerInstitutes = saveInstitutes(header);

            while (csvIterator.hasNext()) {
                final List<String> row = Arrays.asList(csvIterator.next());

                final Integer year = Integer.valueOf(row.get(0));
                final Integer month = Integer.valueOf(row.get(1));
                if (month < 1 || month > 12) {
                    throw new CsvParsingException("월 컬럼에 유효하지 않은 수치가 있습니다.");
                }

                // 각각의 기관의 amount를 DB에 기록한다
                final List<Support> supports = new ArrayList<>();
                for (int idx = 2; idx < row.size(); idx++) {
                    // 헤더가 공백인 컬럼의 경우 스킵
                    if (headerInstitutes.get(idx) == null) {
                        continue;
                    }

                    final Institute institute = headerInstitutes.get(idx);
                    final Double amount = NUMBER_FORMAT.parse(row.get(idx)).doubleValue();

                    supports.add(Support.of(year, month, amount, institute));
                }
                supportRepository.saveAll(supports);
                entityManager.clear();
            }
        } catch (IllegalArgumentException | ParseException ex) {
            throw new CsvParsingException("CSV 파싱에 실패했습니다.");
        }
    }

    @VisibleForTesting
    List<Institute> saveInstitutes(List<String> csvHeader) {
        final List<Institute> instituteEntities = new ArrayList<>();
        instituteEntities.add(null); // header[0]은 "연도"
        instituteEntities.add(null); // header[1]은 "월"

        for (int idx = 2; idx < csvHeader.size(); idx++) {
            final String normalizedName = normalizeInstituteName(csvHeader.get(idx));
            if (!StringUtils.hasText(normalizedName)) {
                instituteEntities.add(null);
                continue;
            }

            final Optional<Institute> instituteOptional =
                    instituteRepository.findOneByName(normalizedName);
            if (instituteOptional.isPresent()) {
                instituteEntities.add(instituteOptional.get());
            } else {
                final Institute newInstitute = new Institute();
                newInstitute.setName(normalizedName);
                instituteRepository.save(newInstitute);
                instituteEntities.add(newInstitute);
            }
        }

        return instituteEntities;
    }

    @Transactional
    public void calculateInstituteSupportSummaries() {
        final List<Institute> institutes = instituteRepository.findAll();
        for (Institute institute: institutes) {
            final List<Support> supports = institute.getSupports();

            // Group by year and calculate them
            final Map<Integer, List<Support>> groupByYear =
                    supports.stream()
                            .collect(Collectors.groupingBy(Support::getYear));

            for (Map.Entry<Integer, List<Support>> entry : groupByYear.entrySet()) {
                final Integer year = entry.getKey();
                final List<Support> supportsOfYear = entry.getValue();

                final DoubleSummaryStatistics summaryStatistics =
                        supportsOfYear.stream()
                                      .map(Support::getAmount)
                                      .mapToDouble(Double::doubleValue)
                                      .summaryStatistics();

                supportSummaryRepository.save(
                        SupportSummary.of(
                                year,
                                summaryStatistics.getSum(),
                                summaryStatistics.getAverage(),
                                institute)
                );
            }
        }
    }
}
