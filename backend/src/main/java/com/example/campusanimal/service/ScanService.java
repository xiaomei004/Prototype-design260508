package com.example.campusanimal.service;

import com.example.campusanimal.dto.ScanRequest;
import com.example.campusanimal.entity.Animal;
import com.example.campusanimal.entity.ScanRecord;
import com.example.campusanimal.entity.User;
import com.example.campusanimal.repository.AnimalRepository;
import com.example.campusanimal.repository.ScanRecordRepository;
import com.example.campusanimal.vo.AnimalVO;
import com.example.campusanimal.vo.ScanResultVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;

@Service
public class ScanService {

    private final ScanRecordRepository scanRecordRepository;
    private final AnimalRepository animalRepository;
    private final AnimalService animalService;

    public ScanService(ScanRecordRepository scanRecordRepository,
                       AnimalRepository animalRepository,
                       AnimalService animalService) {
        this.scanRecordRepository = scanRecordRepository;
        this.animalRepository = animalRepository;
        this.animalService = animalService;
    }

    public ScanResultVO scan(ScanRequest request, User currentUser) {
        String imageUrl = request.getImageUrl().toLowerCase();
        Animal matched = animalRepository.findFirstByName("大橘")
                .orElseGet(() -> animalService.findAllEntities().stream()
                        .min(Comparator.comparing(Animal::getId))
                        .orElse(null));

        boolean success = imageUrl.contains("cat")
                || imageUrl.contains("dog")
                || imageUrl.contains("png")
                || Math.random() > 0.5;

        ScanRecord record = scanRecordRepository.save(ScanRecord.builder()
                .userId(currentUser.getId())
                .imageUrl(request.getImageUrl())
                .resultType(success ? "matched" : "new")
                .matchedAnimalId(success && matched != null ? matched.getId() : null)
                .similarity(success ? 92.0 : 38.5)
                .createTime(LocalDateTime.now())
                .build());

        AnimalVO animalVO = success && matched != null ? animalService.toVO(matched) : null;
        return ScanResultVO.builder()
                .resultType(record.getResultType())
                .similarity(record.getSimilarity())
                .animal(animalVO)
                .build();
    }
}
