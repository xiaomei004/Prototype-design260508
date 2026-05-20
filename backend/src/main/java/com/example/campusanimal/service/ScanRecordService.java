package com.example.campusanimal.service;

import com.example.campusanimal.entity.Animal;
import com.example.campusanimal.entity.ScanRecord;
import com.example.campusanimal.repository.AnimalRepository;
import com.example.campusanimal.repository.ScanRecordRepository;
import com.example.campusanimal.vo.ScanRecordVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScanRecordService {

    private final ScanRecordRepository scanRecordRepository;
    private final AnimalRepository animalRepository;

    public ScanRecordService(ScanRecordRepository scanRecordRepository, AnimalRepository animalRepository) {
        this.scanRecordRepository = scanRecordRepository;
        this.animalRepository = animalRepository;
    }

    public List<ScanRecordVO> findByUserId(Long userId) {
        return scanRecordRepository.findByUserIdOrderByCreateTimeDesc(userId).stream()
                .map(this::toVO)
                .toList();
    }

    private ScanRecordVO toVO(ScanRecord record) {
        Animal matchedAnimal = record.getMatchedAnimalId() == null
                ? null
                : animalRepository.findById(record.getMatchedAnimalId()).orElse(null);

        return ScanRecordVO.builder()
                .id(record.getId())
                .imageUrl(record.getImageUrl())
                .resultType(record.getResultType())
                .matchedAnimalId(record.getMatchedAnimalId())
                .matchedAnimalName(matchedAnimal == null ? null : matchedAnimal.getName())
                .similarity(record.getSimilarity())
                .createTime(record.getCreateTime())
                .build();
    }
}
