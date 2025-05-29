package com.harukite.canteen.service.impl;

import com.harukite.canteen.dto.DailyMenuRequest;
import com.harukite.canteen.dto.DailyMenuResponse;
import com.harukite.canteen.dto.DishDto;
import com.harukite.canteen.exception.DuplicateEntryException;
import com.harukite.canteen.exception.ResourceNotFoundException;
import com.harukite.canteen.model.Canteen;
import com.harukite.canteen.model.DailyMenu;
import com.harukite.canteen.model.Dish;
import com.harukite.canteen.model.User;
import com.harukite.canteen.repository.CanteenRepository;
import com.harukite.canteen.repository.DailyMenuRepository;
import com.harukite.canteen.repository.DishRepository;
import com.harukite.canteen.repository.UserRepository;
import com.harukite.canteen.service.DailyMenuService;
import com.harukite.canteen.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 每日菜谱服务接口的实现类。
 * 包含菜谱的发布、查询和管理业务逻辑。
 */
@Service
@RequiredArgsConstructor
public class DailyMenuServiceImpl implements DailyMenuService
{

    private final DailyMenuRepository dailyMenuRepository;
    private final CanteenRepository canteenRepository;
    private final UserRepository userRepository;
    private final DishRepository dishRepository;
    private final DishService dishService; // 注入 DishService 来获取 DishDto (包含平均评分)

    /**
     * 发布每日菜谱。
     *
     * @param request     菜谱发布请求 DTO
     * @param publisherName 发布者用户名
     * @return 发布成功的菜谱响应 DTO
     * @throws ResourceNotFoundException 如果食堂、发布者或菜品不存在
     * @throws DuplicateEntryException   如果指定食堂、日期和时间段的菜谱已存在
     */
    @Override
    @Transactional
    public DailyMenuResponse publishDailyMenu(DailyMenuRequest request, String publisherName)
    {
        Canteen canteen = canteenRepository.findById(request.getCanteenId())
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + request.getCanteenId()));
        User publisher = userRepository.findByUsername(publisherName)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher user not found with Name: " + publisherName));

        // 检查指定食堂、日期和时间段的菜谱是否已存在
        if (dailyMenuRepository.findByCanteenAndMenuDateAndStartTimeAndEndTime(
                canteen, request.getMenuDate(), request.getStartTime(), request.getEndTime()).isPresent())
        {
            throw new DuplicateEntryException("A menu already exists for canteen '" + canteen.getName() +
                    "' on " + request.getMenuDate() + " from " + request.getStartTime() + " to " + request.getEndTime());
        }

        // 查找所有菜品实体
        Set<Dish> dishes = request.getDishIds().stream()
                .map(dishId -> dishRepository.findById(dishId)
                        .orElseThrow(() -> new ResourceNotFoundException("Dish not found with ID: " + dishId)))
                .collect(Collectors.toSet());

        DailyMenu dailyMenu = new DailyMenu();
        dailyMenu.setCanteen(canteen);
        dailyMenu.setMenuDate(request.getMenuDate());
        dailyMenu.setStartTime(request.getStartTime());
        dailyMenu.setEndTime(request.getEndTime());
        dailyMenu.setPublishedBy(publisher);
        dailyMenu.setDishes(dishes); // 设置菜品集合

        DailyMenu savedMenu = dailyMenuRepository.save(dailyMenu);
        dailyMenuRepository.flush();
        return convertToDto(savedMenu);
    }

    /**
     * 根据食堂ID和日期获取每日菜谱列表。
     *
     * @param canteenId 食堂ID
     * @param menuDate  菜谱日期
     * @return 每日菜谱响应 DTO 列表
     * @throws ResourceNotFoundException 如果食堂不存在
     */
    @Override
    @Transactional(readOnly = true)
    public List<DailyMenuResponse> getDailyMenusByCanteenAndDate(String canteenId, LocalDate menuDate)
    {
        Canteen canteen = canteenRepository.findById(canteenId)
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + canteenId));

        List<DailyMenu> dailyMenus = dailyMenuRepository.findByCanteenAndMenuDate(canteen, menuDate);
        return dailyMenus.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取菜谱列表。
     * @return 每日菜谱响应 DTO 列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<DailyMenuResponse> getAllDailyMenus()
    {
        List<DailyMenu> dailyMenus = dailyMenuRepository.findAll();
        return dailyMenus.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 根据菜谱ID获取菜谱详情。
     *
     * @param menuId 菜谱ID
     * @return 菜谱响应 DTO
     * @throws ResourceNotFoundException 如果菜谱不存在
     */
    @Override
    @Transactional(readOnly = true)
    public DailyMenuResponse getDailyMenuById(String menuId)
    {
        DailyMenu dailyMenu = dailyMenuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Daily Menu not found with ID: " + menuId));
        return convertToDto(dailyMenu);
    }

    /**
     * 更新每日菜谱。
     *
     * @param menuId  要更新的菜谱ID
     * @param request 包含更新信息的菜谱请求 DTO
     * @return 更新后的菜谱响应 DTO
     * @throws ResourceNotFoundException 如果菜谱、食堂或菜品不存在
     * @throws DuplicateEntryException   如果更新后的食堂、日期和时间段的菜谱已存在
     */
    @Override
    @Transactional
    public DailyMenuResponse updateDailyMenu(String menuId, DailyMenuRequest request)
    {
        DailyMenu existingMenu = dailyMenuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Daily Menu not found with ID: " + menuId));

        Canteen canteen = canteenRepository.findById(request.getCanteenId())
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + request.getCanteenId()));

        // 检查更新后的唯一性约束
        dailyMenuRepository.findByCanteenAndMenuDateAndStartTimeAndEndTime(
                        canteen, request.getMenuDate(), request.getStartTime(), request.getEndTime())
                .ifPresent(menu -> {
                    if (!menu.getMenuId().equals(menuId))
                    { // 如果找到的菜单不是当前正在更新的菜单
                        throw new DuplicateEntryException("A menu already exists for canteen '" + canteen.getName() +
                                "' on " + request.getMenuDate() + " from " + request.getStartTime() + " to " + request.getEndTime());
                    }
                });

        // 更新基本信息
        existingMenu.setCanteen(canteen);
        existingMenu.setMenuDate(request.getMenuDate());
        existingMenu.setStartTime(request.getStartTime());
        existingMenu.setEndTime(request.getEndTime());

        // 更新菜品列表
        Set<Dish> updatedDishes = request.getDishIds().stream()
                .map(dishId -> dishRepository.findById(dishId)
                        .orElseThrow(() -> new ResourceNotFoundException("Dish not found with ID: " + dishId)))
                .collect(Collectors.toSet());
        existingMenu.setDishes(updatedDishes);

        DailyMenu savedMenu = dailyMenuRepository.save(existingMenu);
        return convertToDto(savedMenu);
    }

    /**
     * 删除每日菜谱。
     *
     * @param menuId 要删除的菜谱ID
     * @throws ResourceNotFoundException 如果菜谱不存在
     */
    @Override
    @Transactional
    public void deleteDailyMenu(String menuId)
    {
        if (!dailyMenuRepository.existsById(menuId))
        {
            throw new ResourceNotFoundException("Daily Menu not found with ID: " + menuId);
        }
        dailyMenuRepository.deleteById(menuId);
    }

    /**
     * 辅助方法：将 DailyMenu 实体转换为 DailyMenuResponse DTO。
     *
     * @param dailyMenu DailyMenu 实体
     * @return DailyMenuResponse DTO
     */
    private DailyMenuResponse convertToDto(DailyMenu dailyMenu)
    {
        List<DishDto> dishDtos = dailyMenu.getDishes().stream()
                .map(Dish::getDishId) // 获取每个 Dish 对象的 ID
                .map(dishService::getDishById) // 使用 DishService 获取包含平均评分的 DishDto
                .collect(Collectors.toList());

        return new DailyMenuResponse(
                dailyMenu.getMenuId(),
                dailyMenu.getCanteen().getCanteenId(),
                dailyMenu.getCanteen().getName(),
                dailyMenu.getMenuDate(),
                dailyMenu.getStartTime(),
                dailyMenu.getEndTime(),
                dailyMenu.getPublishedBy().getUsername(),
                dailyMenu.getPublishedAt(),
                dishDtos
        );
    }
}
