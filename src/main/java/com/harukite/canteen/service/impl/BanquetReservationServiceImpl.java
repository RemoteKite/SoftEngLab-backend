package com.harukite.canteen.service.impl;

import com.harukite.canteen.dto.BanquetReservationRequest;
import com.harukite.canteen.dto.BanquetReservationResponse;
import com.harukite.canteen.dto.BanquetReservationDishItemDto; // 导入新增的 DTO
import com.harukite.canteen.dto.PackageDto;
import com.harukite.canteen.exception.InvalidInputException;
import com.harukite.canteen.exception.ResourceNotFoundException;
import com.harukite.canteen.model.*;
import com.harukite.canteen.model.Package;
import com.harukite.canteen.repository.BanquetReservationRepository;
import com.harukite.canteen.repository.CanteenRepository;
import com.harukite.canteen.repository.DishRepository;
import com.harukite.canteen.repository.PackageRepository;
import com.harukite.canteen.repository.RoomRepository;
import com.harukite.canteen.repository.UserRepository;
import com.harukite.canteen.service.BanquetReservationService;
import com.harukite.canteen.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 宴会预订服务接口的实现类。
 * 包含宴会预订的创建、查询、更新和取消等业务逻辑。
 */
@Service("banquetReservationService")
@RequiredArgsConstructor
public class BanquetReservationServiceImpl implements BanquetReservationService
{

    private final BanquetReservationRepository banquetReservationRepository;
    private final UserRepository userRepository;
    private final CanteenRepository canteenRepository;
    private final RoomRepository roomRepository;
    private final DishRepository dishRepository;
    private final PackageRepository packageRepository;
    private final PackageService packageService;

    // 假设宴会默认时长为 2 小时，可根据实际业务需求调整或设为可配置项
    private static final int DEFAULT_BANQUET_DURATION_HOURS = 2;

    /**
     * 创建新的宴会预订。
     *
     * @param request 包含预订信息的 DTO
     * @param userId 预订用户ID
     * @return 创建成功的宴会预订响应 DTO
     * @throws ResourceNotFoundException 如果用户、食堂、包厢、菜品或套餐不存在
     * @throws InvalidInputException 如果包厢不可用、人数无效或菜品数量无效
     */
    @Override
    @Transactional
    public BanquetReservationResponse createBanquetReservation(BanquetReservationRequest request, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        Canteen canteen = canteenRepository.findById(request.getCanteenId())
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + request.getCanteenId()));

        Room room = null;
        if (request.getRoomId() != null) {
            room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId()));

            // 检查包厢可用性（创建时不需要排除任何现有预订）
            if (!isRoomAvailable(room.getRoomId(), request.getEventDate(), request.getEventTime(), null)) {
                throw new InvalidInputException("Room " + room.getName() + " is not available at the requested date and time.");
            }
            // 检查人数是否符合包厢容量
            if (request.getNumberOfGuests() > room.getCapacity()) {
                throw new InvalidInputException("Number of guests (" + request.getNumberOfGuests() + ") exceeds room capacity (" + room.getCapacity() + ").");
            }
        }

        Set<BanquetReservationDishItem> selectedDishItems = new HashSet<>();
        // 处理定制菜品项
        if (request.getSelectedDishItems() != null && !request.getSelectedDishItems().isEmpty()) {
            for (BanquetReservationDishItemDto itemDto : request.getSelectedDishItems()) {
                if (itemDto.getQuantity() <= 0) {
                    throw new InvalidInputException("Dish quantity must be greater than zero for dish ID: " + itemDto.getDishId());
                }
                Dish dish = dishRepository.findById(itemDto.getDishId())
                        .orElseThrow(() -> new ResourceNotFoundException("Custom dish not found with ID: " + itemDto.getDishId()));

                BanquetReservationDishItem dishItem = new BanquetReservationDishItem();
                dishItem.setDish(dish);
                dishItem.setQuantity(itemDto.getQuantity());
                dishItem.setSubtotal(dish.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
                // banquetReservation 会在保存 reservation 后设置
                selectedDishItems.add(dishItem);
            }
        }

        Set<Package> selectedPackages = new HashSet<>();
        if (request.getSelectedPackageIds() != null && !request.getSelectedPackageIds().isEmpty()) {
            selectedPackages = request.getSelectedPackageIds().stream()
                    .map(packageId -> packageRepository.findById(packageId)
                            .orElseThrow(() -> new ResourceNotFoundException("Package not found with ID: " + packageId)))
                    .collect(Collectors.toSet());
        }

        // 计算总价
        BigDecimal totalPrice = calculateBanquetPrice(room, selectedDishItems, selectedPackages);

        BanquetReservation reservation = new BanquetReservation();
        reservation.setUser(user);
        reservation.setCanteen(canteen);
        reservation.setRoom(room); // 可选
        reservation.setEventDate(request.getEventDate());
        reservation.setEventTime(request.getEventTime());
        reservation.setNumberOfGuests(request.getNumberOfGuests());
        reservation.setContactName(request.getContactName());
        reservation.setContactPhoneNumber(request.getContactPhoneNumber());
        reservation.setPurpose(request.getPurpose());
        reservation.setCustomMenuRequest(request.getCustomMenuRequest());
        reservation.setHasBirthdayCake(request.getHasBirthdayCake() != null ? request.getHasBirthdayCake() : false);
        reservation.setSpecialRequests(request.getSpecialRequests());
        reservation.setTotalPrice(totalPrice); // 设置计算后的总价
        reservation.setStatus(BanquetStatus.PENDING); // 默认待确认状态
        reservation.setSelectedPackages(selectedPackages);

        // 设置定制菜品项的关联
        for (BanquetReservationDishItem item : selectedDishItems) {
            item.setBanquetReservation(reservation); // 设置反向关联
        }
        reservation.setSelectedDishItems(selectedDishItems);

        BanquetReservation savedReservation = banquetReservationRepository.save(reservation);
        // Cascading Save 将会自动保存 selectedDishItems
        return convertToDto(savedReservation);
    }

    /**
     * 根据预订ID获取宴会预订详情。
     *
     * @param banquetId 宴会预订ID
     * @return 宴会预订响应 DTO
     * @throws ResourceNotFoundException 如果预订不存在
     */
    @Override
    @Transactional(readOnly = true)
    public BanquetReservationResponse getBanquetReservationById(String banquetId) {
        BanquetReservation reservation = banquetReservationRepository.findById(banquetId)
                .orElseThrow(() -> new ResourceNotFoundException("Banquet reservation not found with ID: " + banquetId));
        return convertToDto(reservation);
    }

    /**
     * 获取所有宴会预订列表。
     *
     * @return 宴会预订响应 DTO 列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<BanquetReservationResponse> getAllBanquetReservations() {
        return banquetReservationRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户ID获取其所有宴会预订。
     *
     * @param userId 用户ID
     * @return 宴会预订响应 DTO 列表
     * @throws ResourceNotFoundException 如果用户不存在
     */
    @Override
    @Transactional(readOnly = true)
    public List<BanquetReservationResponse> getBanquetReservationsByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        List<BanquetReservation> reservations = banquetReservationRepository.findByUser(user);
        return reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 根据食堂ID获取宴会预订列表。
     *
     * @param canteenId 食堂ID
     * @return 宴会预订响应 DTO 列表
     * @throws ResourceNotFoundException 如果食堂不存在
     */
    @Override
    @Transactional(readOnly = true)
    public List<BanquetReservationResponse> getBanquetReservationsByCanteenId(String canteenId) {
        Canteen canteen = canteenRepository.findById(canteenId)
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + canteenId));
        List<BanquetReservation> reservations = banquetReservationRepository.findByCanteen(canteen);
        return reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 更新宴会预订状态。
     * 通常由管理员操作。
     *
     * @param banquetId 宴会预订ID
     * @param newStatus 新的预订状态
     * @return 更新后的宴会预订响应 DTO
     * @throws ResourceNotFoundException 如果预订不存在
     * @throws InvalidInputException 如果状态转换无效
     */
    @Override
    @Transactional
    public BanquetReservationResponse updateBanquetStatus(String banquetId, BanquetStatus newStatus) {
        BanquetReservation reservation = banquetReservationRepository.findById(banquetId)
                .orElseThrow(() -> new ResourceNotFoundException("Banquet reservation not found with ID: " + banquetId));

        // 简单的状态转换逻辑示例 (可以根据业务需求扩展更复杂的规则)
        // 例如：PENDING -> CONFIRMED/CANCELLED, CONFIRMED -> COMPLETED/CANCELLED
        // 不允许从 COMPLETED 或 CANCELLED 转换回 PENDING/CONFIRMED
        if (reservation.getStatus() == BanquetStatus.CANCELLED || reservation.getStatus() == BanquetStatus.COMPLETED) {
            if (newStatus != BanquetStatus.CANCELLED && newStatus != BanquetStatus.COMPLETED) {
                throw new InvalidInputException("Cannot change status from " + reservation.getStatus() + " to " + newStatus);
            }
        }

        reservation.setStatus(newStatus);
        if (newStatus == BanquetStatus.CONFIRMED && reservation.getConfirmationDate() == null) {
            reservation.setConfirmationDate(LocalDateTime.now());
        }
        BanquetReservation updatedReservation = banquetReservationRepository.save(reservation);
        return convertToDto(updatedReservation);
    }

    /**
     * 取消宴会预订。
     *
     * @param banquetId 宴会预订ID
     * @param userId 操作用户ID (用于权限检查，确保只有预订所有者或管理员可以取消)
     * @throws ResourceNotFoundException 如果宴会预订不存在
     * @throws InvalidInputException 如果预订状态不允许取消或用户没有权限
     */
    @Override
    @Transactional
    public void cancelBanquetReservation(String banquetId, String userId) {
        BanquetReservation reservation = banquetReservationRepository.findById(banquetId)
                .orElseThrow(() -> new ResourceNotFoundException("Banquet reservation not found with ID: " + banquetId));

        // 权限检查：确保只有预订所有者或管理员才能取消
        // 实际应用中，这里需要通过 Spring Security 获取当前用户的角色进行判断
        if (!reservation.getUser().getUserId().equals(userId) && (reservation.getUser().getRole() != UserRole.ADMIN && reservation.getUser().getRole() != UserRole.STAFF)) {
            throw new InvalidInputException("You are not authorized to cancel this reservation.");
        }

        // 检查预订状态是否允许取消
        if (reservation.getStatus() == BanquetStatus.COMPLETED || reservation.getStatus() == BanquetStatus.CANCELLED) {
            throw new InvalidInputException("Reservation cannot be cancelled as its current status is " + reservation.getStatus());
        }

        reservation.setStatus(BanquetStatus.CANCELLED);
        banquetReservationRepository.save(reservation);
    }

    /**
     * 检查某个包厢在指定日期和时间段是否可用。
     * 考虑时间段重叠。
     *
     * @param roomId 包厢ID
     * @param date 预订日期
     * @param requestedTime 预订开始时间
     * @param banquetIdToExclude 可选参数，在更新预订时排除当前预订ID
     * @return 如果包厢可用则为 true，否则为 false
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isRoomAvailable(String roomId, LocalDate date, LocalTime requestedTime, String banquetIdToExclude) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        // 获取该包厢在指定日期的所有非取消/完成状态的预订
        List<BanquetReservation> existingReservations = banquetReservationRepository
                .findByRoomAndEventDate(room, date);

        LocalTime requestedEndTime = requestedTime.plusHours(DEFAULT_BANQUET_DURATION_HOURS);

        for (BanquetReservation existingReservation : existingReservations) {
            // 如果是更新操作，跳过当前正在更新的预订
            if (existingReservation.getBanquetId().equals(banquetIdToExclude)) {
                continue;
            }

            // 忽略已取消或已完成的预订
            if (existingReservation.getStatus() == BanquetStatus.CANCELLED || existingReservation.getStatus() == BanquetStatus.COMPLETED) {
                continue;
            }

            LocalTime existingStartTime = existingReservation.getEventTime();
            LocalTime existingEndTime = existingStartTime.plusHours(DEFAULT_BANQUET_DURATION_HOURS); // 假设现有预订也按默认时长

            // 检查时间段是否重叠：(start1 < end2 AND end1 > start2)
            if (requestedTime.isBefore(existingEndTime) && requestedEndTime.isAfter(existingStartTime)) {
                return false; // 存在重叠，包厢不可用
            }
        }
        return true; // 没有重叠，包厢可用
    }

    /**
     * 辅助方法：计算宴会预订的总价。
     * 总价 = 包厢基础费用 + 所有定制菜品价格之和 + 所有套餐价格之和。
     *
     * @param room 预订的包厢实体
     * @param selectedDishItems 选择的单品菜品项集合（包含数量）
     * @param selectedPackages 选择的套餐集合
     * @return 计算出的总价
     */
    private BigDecimal calculateBanquetPrice(Room room, Set<BanquetReservationDishItem> selectedDishItems, Set<Package> selectedPackages) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        if (room != null) {
            totalPrice = totalPrice.add(room.getBaseFee());
        }

        // 遍历菜品项，根据数量累加价格
        if (selectedDishItems != null) {
            for (BanquetReservationDishItem item : selectedDishItems) {
                totalPrice = totalPrice.add(item.getSubtotal()); // 使用小计金额
            }
        }

        if (selectedPackages != null) {
            for (Package pkg : selectedPackages) {
                totalPrice = totalPrice.add(pkg.getPrice());
            }
        }
        return totalPrice;
    }

    /**
     * 辅助方法：将 BanquetReservation 实体转换为 BanquetReservationResponse DTO。
     *
     * @param reservation BanquetReservation 实体
     * @return BanquetReservationResponse DTO
     */
    private BanquetReservationResponse convertToDto(BanquetReservation reservation) {
        // 转换定制菜品项
        List<BanquetReservationDishItemDto> customDishItemDtos = reservation.getSelectedDishItems().stream()
                .map(item -> new BanquetReservationDishItemDto(
                        item.getBanquetReservationDishItemId(),
                        item.getBanquetReservation().getBanquetId(),
                        item.getDish().getDishId(),
                        item.getDish().getName(),
                        item.getDish().getPrice(),
                        item.getQuantity(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());

        // 转换套餐
        List<String> packageIds = reservation.getSelectedPackages().stream()
                .map(Package::getPackageId)
                .collect(Collectors.toList());
        List<PackageDto> packageDtos = reservation.getSelectedPackages().stream()
                .map(Package::getPackageId) // 先获取ID
                .map(packageService::getPackageById) // 再通过服务获取DTO
                .collect(Collectors.toList());

        return new BanquetReservationResponse(
                reservation.getBanquetId(),
                reservation.getUser().getUserId(),
                reservation.getUser().getUsername(),
                reservation.getCanteen().getCanteenId(),
                reservation.getCanteen().getName(),
                reservation.getRoom() != null ? reservation.getRoom().getRoomId() : null,
                reservation.getRoom() != null ? reservation.getRoom().getName() : null,
                reservation.getEventDate(),
                reservation.getEventTime(),
                reservation.getNumberOfGuests(),
                reservation.getContactName(),
                reservation.getContactPhoneNumber(),
                reservation.getPurpose(),
                reservation.getCustomMenuRequest(),
                reservation.getHasBirthdayCake(),
                reservation.getSpecialRequests(),
                reservation.getTotalPrice(),
                reservation.getStatus(),
                reservation.getConfirmationDate(),
                reservation.getCreatedAt(),
                customDishItemDtos, // 返回新的菜品项 DTO 列表
                packageIds,
                packageDtos
        );
    }
}
