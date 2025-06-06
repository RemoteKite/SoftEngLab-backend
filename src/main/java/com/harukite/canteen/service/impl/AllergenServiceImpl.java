package com.harukite.canteen.service.impl;

import com.harukite.canteen.dto.AllergenDto;
import com.harukite.canteen.exception.DuplicateEntryException;
import com.harukite.canteen.exception.ResourceNotFoundException;
import com.harukite.canteen.model.Allergen;
import com.harukite.canteen.repository.AllergenRepository;
import com.harukite.canteen.repository.UserRepository;
import com.harukite.canteen.service.AllergenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 过敏原服务接口的实现类。
 * 包含过敏原的创建、查询、更新和删除等业务逻辑。
 */
@Service
@RequiredArgsConstructor
public class AllergenServiceImpl implements AllergenService
{

    private final AllergenRepository allergenRepository;
    private final UserRepository userRepository; // 注入 UserRepository

    /**
     * 创建一个新的过敏原。
     *
     * @param allergenDto 包含过敏原名称的 DTO
     * @return 创建成功的过敏原 DTO
     * @throws DuplicateEntryException 如果过敏原名称已存在
     */
    @Override
    @Transactional
    public AllergenDto createAllergen(AllergenDto allergenDto)
    {
        // 检查过敏原名称是否已存在
        Optional<Allergen> existingAllergen = allergenRepository.findByAllergenName(allergenDto.getAllergenName());
        if (existingAllergen.isPresent())
        {
            throw new DuplicateEntryException("Allergen with name '" + allergenDto.getAllergenName() + "' already exists.");
        }

        Allergen allergen = new Allergen();
        allergen.setAllergenId(UUID.randomUUID().toString()); // 生成新的ID
        allergen.setAllergenName(allergenDto.getAllergenName());
        Allergen savedAllergen = allergenRepository.save(allergen);
        return convertToDto(savedAllergen);
    }

    /**
     * 根据 ID 获取过敏原详情。
     *
     * @param id 过敏原 ID
     * @return 对应的过敏原 DTO
     * @throws ResourceNotFoundException 如果过敏原不存在
     */
    @Override
    @Transactional(readOnly = true)
    public AllergenDto getAllergenById(String id)
    {
        Allergen allergen = allergenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allergen not found with ID: " + id));
        return convertToDto(allergen);
    }

    /**
     * 获取所有过敏原列表。
     *
     * @return 过敏原 DTO 列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<AllergenDto> getAllAllergens()
    {
        return allergenRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 更新一个现有过敏原。
     *
     * @param id                 要更新的过敏原 ID
     * @param updatedAllergenDto 包含更新信息的 DTO
     * @return 更新后的过敏原 DTO
     * @throws ResourceNotFoundException 如果过敏原不存在
     * @throws DuplicateEntryException   如果更新后的名称已存在于其他过敏原
     */
    @Override
    @Transactional
    public AllergenDto updateAllergen(String id, AllergenDto updatedAllergenDto)
    {
        Allergen existingAllergen = allergenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allergen not found with ID: " + id));

        // 如果名称有变化，检查新名称是否已被其他过敏原占用
        if (!existingAllergen.getAllergenName().equals(updatedAllergenDto.getAllergenName()))
        {
            Optional<Allergen> allergenWithNewName = allergenRepository.findByAllergenName(updatedAllergenDto.getAllergenName());
            if (allergenWithNewName.isPresent() && !allergenWithNewName.get().getAllergenId().equals(id))
            {
                throw new DuplicateEntryException("Allergen with name '" + updatedAllergenDto.getAllergenName() + "' already exists.");
            }
        }

        existingAllergen.setAllergenName(updatedAllergenDto.getAllergenName());
        Allergen savedAllergen = allergenRepository.save(existingAllergen);
        return convertToDto(savedAllergen);
    }

    /**
     * 根据 ID 删除一个过敏原。
     *
     * @param id 要删除的过敏原 ID
     * @throws ResourceNotFoundException 如果过敏原不存在
     * @throws IllegalStateException     如果有过敏原被菜品或用户引用，不能直接删除
     */
    @Override
    @Transactional
    public void deleteAllergen(String id)
    {
        Allergen allergen = allergenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allergen not found with ID: " + id));

        // 检查此过敏原是否被任何 Dish 引用
        // 由于 Allergen 是 Dish 的 mappedBy 端，可以直接通过 getDishes() 检查
        // 请确保在调用此方法前，getDishes() 能够加载关联数据（例如，通过 EAGER fetch 或在同一事务中加载）
        if (!allergen.getDishes().isEmpty())
        {
            throw new IllegalStateException("Allergen is currently associated with dishes and cannot be deleted.");
        }


        allergenRepository.delete(allergen);
    }

    /**
     * 辅助方法：将 Allergen 实体转换为 AllergenDto。
     *
     * @param allergen Allergen 实体
     * @return AllergenDto
     */
    private AllergenDto convertToDto(Allergen allergen)
    {
        return new AllergenDto(allergen.getAllergenId(), allergen.getAllergenName());
    }
}
