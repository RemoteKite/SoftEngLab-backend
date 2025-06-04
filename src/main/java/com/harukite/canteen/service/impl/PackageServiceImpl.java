package com.harukite.canteen.service.impl;

import com.harukite.canteen.dto.DishDto;
import com.harukite.canteen.dto.PackageDto;
import com.harukite.canteen.exception.DuplicateEntryException;
import com.harukite.canteen.exception.ResourceNotFoundException;
import com.harukite.canteen.model.Canteen;
import com.harukite.canteen.model.Dish;
import com.harukite.canteen.model.Package;
import com.harukite.canteen.repository.CanteenRepository;
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
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;
    private final DishRepository dishRepository;
    private final DishService dishService; // 注入 DishService 以获取 DishDto
    private final CanteenRepository canteenRepository; // 新增：注入 CanteenRepository

    /**
     * 创建新套餐。
     *
     * @param packageDto 包含套餐信息的 DTO
     * @return 创建成功的套餐 DTO
     * @throws DuplicateEntryException 如果套餐名称在所属食堂内已存在
     * @throws ResourceNotFoundException 如果所属食堂或套餐中包含的菜品不存在
     */
    @Override
    @Transactional
    public PackageDto createPackage(PackageDto packageDto) {
        Canteen canteen = canteenRepository.findById(packageDto.getCanteenId())
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + packageDto.getCanteenId()));

        // 检查套餐名称在所属食堂内是否已存在
        if (packageRepository.findByNameAndCanteen(packageDto.getName(), canteen).isPresent()) {
            throw new DuplicateEntryException("Package with name '" + packageDto.getName() + "' already exists in canteen '" + canteen.getName() + "'.");
        }

        Set<Dish> dishes = new HashSet<>();
        BigDecimal calculatedPrice = BigDecimal.ZERO;

        if (packageDto.getDishIds() != null && !packageDto.getDishIds().isEmpty()) {
            for (String dishId : packageDto.getDishIds()) {
                Dish dish = dishRepository.findById(dishId)
                        .orElseThrow(() -> new ResourceNotFoundException("Dish not found with ID: " + dishId));
                dishes.add(dish);
                calculatedPrice = calculatedPrice.add(dish.getPrice());
            }
        }

        Package newPackage = new Package();
        newPackage.setCanteen(canteen); // 设置所属食堂
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
    public PackageDto getPackageById(String packageId) {
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
    public List<PackageDto> getAllPackages() {
        return packageRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 根据食堂ID获取套餐列表。
     *
     * @param canteenId 食堂ID
     * @return 套餐 DTO 列表
     * @throws ResourceNotFoundException 如果食堂不存在
     */
    @Override
    @Transactional(readOnly = true)
    public List<PackageDto> getPackagesByCanteenId(String canteenId) {
        Canteen canteen = canteenRepository.findById(canteenId)
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + canteenId));
        List<Package> packages = packageRepository.findByCanteen(canteen);
        return packages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 更新套餐信息。
     *
     * @param packageId 要更新的套餐ID
     * @param updatedPackageDto 包含更新信息的套餐 DTO
     * @return 更新后的套餐 DTO
     * @throws ResourceNotFoundException 如果套餐不存在或其中包含的菜品不存在
     * @throws DuplicateEntryException 如果更新后的套餐名称在所属食堂内已存在且不属于当前套餐
     */
    @Override
    @Transactional
    public PackageDto updatePackage(String packageId, PackageDto updatedPackageDto) {
        Package existingPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found with ID: " + packageId));

        Canteen newCanteen; // 默认使用当前食堂
        if (updatedPackageDto.getCanteenId() != null && !updatedPackageDto.getCanteenId().equals(existingPackage.getCanteen().getCanteenId())) {
            newCanteen = canteenRepository.findById(updatedPackageDto.getCanteenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + updatedPackageDto.getCanteenId()));
            existingPackage.setCanteen(newCanteen); // 更新所属食堂
        }
        else
        {
            newCanteen = existingPackage.getCanteen();
        }

        // 检查更新后的名称是否在新的所属食堂内重复
        if (updatedPackageDto.getName() != null && !updatedPackageDto.getName().equals(existingPackage.getName())) {
            packageRepository.findByNameAndCanteen(updatedPackageDto.getName(), newCanteen).ifPresent(pkg -> {
                if (!pkg.getPackageId().equals(packageId)) {
                    throw new DuplicateEntryException("Package with name '" + updatedPackageDto.getName() + "' already exists in canteen '" + newCanteen.getName() + "'.");
                }
            });
        }

        // 更新基本信息
        if (updatedPackageDto.getName() != null) {
            existingPackage.setName(updatedPackageDto.getName());
        }
        if (updatedPackageDto.getDescription() != null) {
            existingPackage.setDescription(updatedPackageDto.getDescription());
        }
        if (updatedPackageDto.getPrice() != null) {
            existingPackage.setPrice(updatedPackageDto.getPrice());
        }

        // 更新包含的菜品
        if (updatedPackageDto.getDishIds() != null) {
            Set<Dish> newDishes = new HashSet<>();
            BigDecimal recalculatedPrice = BigDecimal.ZERO;
            if (!updatedPackageDto.getDishIds().isEmpty()) {
                for (String dishId : updatedPackageDto.getDishIds()) {
                    Dish dish = dishRepository.findById(dishId)
                            .orElseThrow(() -> new ResourceNotFoundException("Dish not found with ID: " + dishId));
                    newDishes.add(dish);
                    recalculatedPrice = recalculatedPrice.add(dish.getPrice());
                }
            }
            existingPackage.setDishes(newDishes);
            // 如果DTO没有提供新的价格，则使用重新计算的价格
            if (updatedPackageDto.getPrice() == null) {
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
    public void deletePackage(String packageId) {
        if (!packageRepository.existsById(packageId)) {
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
    private PackageDto convertToDto(Package pkg) {
        List<DishDto> dishDtos = pkg.getDishes().stream()
                .map(Dish::getDishId) // 先获取ID
                .map(dishService::getDishById) // 再通过服务获取DTO
                .collect(Collectors.toList());

        return new PackageDto(
                pkg.getPackageId(),
                pkg.getCanteen().getCanteenId(), // 新增：获取食堂ID
                pkg.getCanteen().getName(),     // 新增：获取食堂名称
                pkg.getName(),
                pkg.getDescription(),
                pkg.getPrice(),
                dishDtos.stream().map(DishDto::getDishId).collect(Collectors.toList()), // 返回ID列表给DTO
                dishDtos // 返回完整的DishDto列表给DTO
        );
    }
}
