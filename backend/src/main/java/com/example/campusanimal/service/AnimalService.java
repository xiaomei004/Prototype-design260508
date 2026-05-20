package com.example.campusanimal.service;

import com.example.campusanimal.common.PageResult;
import com.example.campusanimal.dto.AnimalCreateRequest;
import com.example.campusanimal.dto.AnimalUpdateRequest;
import com.example.campusanimal.entity.Animal;
import com.example.campusanimal.entity.User;
import com.example.campusanimal.exception.BusinessException;
import com.example.campusanimal.repository.AnimalRepository;
import com.example.campusanimal.vo.AnimalVO;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    public PageResult<AnimalVO> list(String keyword, String species, String status, int pageNum, int pageSize) {
        List<AnimalVO> filtered = animalRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .filter(animal -> isBlank(keyword) || containsIgnoreCase(animal.getName(), keyword)
                        || containsIgnoreCase(animal.getLocation(), keyword)
                        || containsIgnoreCase(animal.getFeatures(), keyword))
                .filter(animal -> isBlank(species) || species.equals(animal.getSpecies()))
                .filter(animal -> isBlank(status) || status.equals(animal.getStatus()))
                .map(this::toVO)
                .toList();

        int fromIndex = Math.max((pageNum - 1) * pageSize, 0);
        int toIndex = Math.min(fromIndex + pageSize, filtered.size());
        List<AnimalVO> pageList = fromIndex >= filtered.size() ? List.of() : filtered.subList(fromIndex, toIndex);
        return new PageResult<>(pageList, filtered.size(), pageNum, pageSize);
    }

    public AnimalVO detail(Long id) {
        return toVO(getEntity(id));
    }

    public AnimalVO create(AnimalCreateRequest request, User currentUser) {
        Animal animal = animalRepository.save(Animal.builder()
                .name(request.getName())
                .species(request.getSpecies())
                .gender(defaultString(request.getGender(), "未知"))
                .color(defaultString(request.getColor(), request.getSpecies()))
                .location(request.getLocation())
                .status(defaultString(request.getStatus(), "待审核"))
                .features(defaultString(request.getFeatures(), "新建档案"))
                .imageUrl(defaultString(request.getImageUrl(),
                        "狗".equals(request.getSpecies()) ? "/uploads/dog2.png" : "/uploads/cat1.png"))
                .sterilized(request.getSterilized() == null ? 0 : request.getSterilized())
                .vaccineStatus(defaultString(request.getVaccineStatus(), "未知"))
                .firstFoundTime(request.getFirstFoundTime() == null ? LocalDateTime.now() : request.getFirstFoundTime())
                .auditStatus("PENDING")
                .createUserId(currentUser.getId())
                .createTime(LocalDateTime.now())
                .build());
        return toVO(animal);
    }

    public boolean update(Long id, AnimalUpdateRequest request, User currentUser) {
        Animal animal = getEntity(id);
        ensureCanManageAnimal(animal, currentUser);
        if (!isBlank(request.getLocation())) {
            animal.setLocation(request.getLocation());
        }
        if (!isBlank(request.getStatus())) {
            animal.setStatus(request.getStatus());
        }
        if (!isBlank(request.getFeatures())) {
            animal.setFeatures(request.getFeatures());
        }
        if (!isBlank(request.getAuditStatus())) {
            animal.setAuditStatus(request.getAuditStatus());
        }
        animalRepository.save(animal);
        return true;
    }

    public boolean delete(Long id, User currentUser) {
        Animal animal = getEntity(id);
        ensureCanManageAnimal(animal, currentUser);
        animalRepository.deleteById(id);
        return true;
    }

    public boolean audit(Long id, String auditStatus) {
        Animal animal = getEntity(id);
        animal.setAuditStatus(auditStatus);
        if ("APPROVED".equalsIgnoreCase(auditStatus) && "待审核".equals(animal.getStatus())) {
            animal.setStatus("健康");
        }
        animalRepository.save(animal);
        return true;
    }

    public List<AnimalVO> pendingList() {
        return animalRepository.findByAuditStatusOrderByIdDesc("PENDING").stream()
                .map(this::toVO)
                .toList();
    }

    public List<AnimalVO> findByUserId(Long userId) {
        return animalRepository.findByCreateUserIdOrderByIdDesc(userId).stream()
                .map(this::toVO)
                .toList();
    }

    public List<Animal> findAllEntities() {
        return animalRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public AnimalVO toVO(Animal animal) {
        return AnimalVO.builder()
                .id(animal.getId())
                .name(animal.getName())
                .species(animal.getSpecies())
                .gender(animal.getGender())
                .color(animal.getColor())
                .location(animal.getLocation())
                .status(animal.getStatus())
                .features(animal.getFeatures())
                .imageUrl(animal.getImageUrl())
                .sterilized(animal.getSterilized())
                .vaccineStatus(animal.getVaccineStatus())
                .firstFoundTime(animal.getFirstFoundTime())
                .auditStatus(animal.getAuditStatus())
                .createUserId(animal.getCreateUserId())
                .createTime(animal.getCreateTime())
                .build();
    }

    public Animal getEntity(Long id) {
        return animalRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "动物档案不存在"));
    }

    private boolean containsIgnoreCase(String text, String keyword) {
        return text != null && text.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    private boolean isBlank(String text) {
        return text == null || text.isBlank();
    }

    private String defaultString(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }

    private void ensureCanManageAnimal(Animal animal, User currentUser) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
        boolean isOwner = animal.getCreateUserId() != null && animal.getCreateUserId().equals(currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new BusinessException(403, "无权修改其他用户创建的动物档案");
        }
    }
}
