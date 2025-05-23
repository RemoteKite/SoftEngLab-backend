package com.harukite.canteen.service.impl;

import com.harukite.canteen.dto.DishDto;
import com.harukite.canteen.dto.PackageDto;
import com.harukite.canteen.exception.DuplicateEntryException;
import com.harukite.canteen.exception.ResourceNotFoundException;
import com.harukite.canteen.model.Dish;
import com.harukite.canteen.model.Package;
import com.harukite.canteen.repository.DishRepository;
import com.harukite.canteen.repository.PackageRepository;
import com.harukite.canteen.service.DishService;
import com.harukite.canteen.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 宴会套餐服务接口的实现类。
 * 包含套餐的创建、查询、更新和删除业务逻辑。
 */
@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService
{

    private final PackageRepository packageRepository;
    private final DishRepository dishRepository;
    private final DishService dishService; // 注入 DishService 以获取 DishDto

    /**
     * 创建新套餐。
     *
     * @param packageDto 包含套餐信息的 DTO
     * @return 创建成功的套餐 DTO
     * @throws DuplicateEntryException   如果套餐名称已存在
     * @throws ResourceNotFoundException 如果套餐中包含的菜品不存在
     */
    @Override
    @Transactional
    public PackageDto createPackage(PackageDto packageDto)
    {
        if (packageRepository.findByName(packageDto.getName()).isPresent())
        {
            throw new DuplicateEntryException("Package with name '" + packageDto.getName() + "' already exists.");
        }

        Set<Dish> dishes = new HashSet<>();
        BigDecimal calculatedPrice = BigDecimal.ZERO;

        if (packageDto.getDishIds() != null && !packageDto.getDishIds().isEmpty())
        {
            for (String dishId : packageDto.getDishIds())
            {
                Dish dish = dishRepository.findById(dishId)
                        .orElseThrow(() -> new ResourceNotFoundException("Dish not found with ID: " + dishId));
                dishes.add(dish);
                calculatedPrice = calculatedPrice.add(dish.getPrice());
            }
        }

        Package newPackage = new Package();
        newPackage.setName(packageDto.getName());
        newPackage.setDescription(packageDto.getDescription());
        // 如果DTO提供了价格，使用DTO的价格；否则使用计算的价格
        newPackage.setPrice(packageDto.getPrice() != null ? packageDto.getPrice() : calculatedPrice);
        newPackage.setDishes(dishes);

        Package savedPackage = packageRepository.save(newPackage);
        return convertToDto(savedPackage);
    }

    /**
     * 根据套餐ID获取套餐详情。
     *
     * @param packageId 套餐ID
     * @return 套餐 DTO
     * @throws ResourceNotFoundException 如果套餐不存在
     */
    @Override
    @Transactional(readOnly = true)
    public PackageDto getPackageById(String packageId)
    {
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found with ID: " + packageId));
        return convertToDto(pkg);
    }

    /**
     * 获取所有套餐列表。
     *
     * @return 套餐 DTO 列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<PackageDto> getAllPackages()
    {
        return packageRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 更新套餐信息。
     *
     * @param packageId         要更新的套餐ID
     * @param updatedPackageDto 包含更新信息的套餐 DTO
     * @return 更新后的套餐 DTO
     * @throws ResourceNotFoundException 如果套餐不存在或其中包含的菜品不存在
     * @throws DuplicateEntryException   如果更新后的套餐名称已存在且不属于当前套餐
     */
    @Override
    @Transactional
    public PackageDto updatePackage(String packageId, PackageDto updatedPackageDto)
    {
        Package existingPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found with ID: " + packageId));

        // 检查更新后的名称是否重复
        if (updatedPackageDto.getName() != null && !updatedPackageDto.getName().equals(existingPackage.getName()))
        {
            packageRepository.findByName(updatedPackageDto.getName()).ifPresent(pkg -> {
                if (!pkg.getPackageId().equals(packageId))
                {
                    throw new DuplicateEntryException("Package with name '" + updatedPackageDto.getName() + "' already exists.");
                }
            });
        }

        // 更新基本信息
        if (updatedPackageDto.getName() != null)
        {
            existingPackage.setName(updatedPackageDto.getName());
        }
        if (updatedPackageDto.getDescription() != null)
        {
            existingPackage.setDescription(updatedPackageDto.getDescription());
        }
        if (updatedPackageDto.getPrice() != null)
        {
            existingPackage.setPrice(updatedPackageDto.getPrice());
        }

        // 更新包含的菜品
        if (updatedPackageDto.getDishIds() != null)
        {
            Set<Dish> newDishes = new HashSet<>();
            BigDecimal recalculatedPrice = BigDecimal.ZERO;
            if (!updatedPackageDto.getDishIds().isEmpty())
            {
                for (String dishId : updatedPackageDto.getDishIds())
                {
                    Dish dish = dishRepository.findById(dishId)
                            .orElseThrow(() -> new ResourceNotFoundException("Dish not found with ID: " + dishId));
                    newDishes.add(dish);
                    recalculatedPrice = recalculatedPrice.add(dish.getPrice());
                }
            }
            existingPackage.setDishes(newDishes);
            // 如果DTO没有提供新的价格，则使用重新计算的价格
            if (updatedPackageDto.getPrice() == null)
            {
                existingPackage.setPrice(recalculatedPrice);
            }
        }

        Package savedPackage = packageRepository.save(existingPackage);
        return convertToDto(savedPackage);
    }

    /**
     * 删除套餐。
     *
     * @param packageId 要删除的套餐ID
     * @throws ResourceNotFoundException 如果套餐不存在
     */
    @Override
    @Transactional
    public void deletePackage(String packageId)
    {
        if (!packageRepository.existsById(packageId))
        {
            throw new ResourceNotFoundException("Package not found with ID: " + packageId);
        }
        packageRepository.deleteById(packageId);
    }

    /**
     * 辅助方法：将 Package 实体转换为 PackageDto。
     *
     * @param pkg Package 实体
     * @return PackageDto
     */
    private PackageDto convertToDto(Package pkg)
    {
        List<DishDto> dishDtos = pkg.getDishes().stream()
                .map(Dish::getDishId)
                .map(dishService::getDishById) // 使用 DishService 获取包含平均评分的 DishDto
                .collect(Collectors.toList());

        return new PackageDto(
                pkg.getPackageId(),
                pkg.getName(),
                pkg.getDescription(),
                pkg.getPrice(),
                dishDtos.stream().map(DishDto::getDishId).collect(Collectors.toList()), // 返回ID列表给DTO
                dishDtos // 返回完整的DishDto列表给DTO
        );
    }
}
