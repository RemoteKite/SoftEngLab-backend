-- 创建数据库（如果需要，请取消注释并根据您的数据库系统调整）
-- CREATE DATABASE canteen_management_system;
-- \c canteen_management_system; -- 连接到新创建的数据库

-- 创建 ENUM 类型
CREATE TYPE user_role AS ENUM ('DINER', 'ADMIN', 'STAFF');
CREATE TYPE order_status AS ENUM ('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED');
CREATE TYPE banquet_status AS ENUM ('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED');


-- 1. 用户表 (Users)
-- 存储所有用户的信息，包括学生、教师、食堂管理员和工作人员
CREATE TABLE Users
(
    user_id       VARCHAR(255) PRIMARY KEY,
    username      VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email         VARCHAR(100) UNIQUE,
    phone_number  VARCHAR(20) UNIQUE,
    role          user_role    NOT NULL, -- 使用自定义 ENUM 类型
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE Users IS '存储所有用户的信息，包括用餐者、食堂管理员和工作人员';
COMMENT ON COLUMN Users.user_id IS '用户唯一ID';
COMMENT ON COLUMN Users.username IS '用户名';
COMMENT ON COLUMN Users.password_hash IS '用户密码的哈希值';
COMMENT ON COLUMN Users.email IS '用户邮箱';
COMMENT ON COLUMN Users.phone_number IS '用户电话号码';
COMMENT ON COLUMN Users.role IS '用户角色：用餐者、管理员、工作人员';
COMMENT ON COLUMN Users.created_at IS '用户创建时间';


-- 2. 食堂表 (Canteens)
-- 存储各个食堂的基本信息
CREATE TABLE Canteens
(
    canteen_id    VARCHAR(255) PRIMARY KEY,
    name          VARCHAR(100) NOT NULL UNIQUE,
    description   TEXT,
    location      VARCHAR(255),
    opening_hours VARCHAR(255),
    contact_phone VARCHAR(20),
    image_url     VARCHAR(255)
);
COMMENT ON TABLE Canteens IS '存储各个食堂的基本信息';
COMMENT ON COLUMN Canteens.canteen_id IS '食堂唯一ID';
COMMENT ON COLUMN Canteens.name IS '食堂名称';
COMMENT ON COLUMN Canteens.description IS '食堂介绍';
COMMENT ON COLUMN Canteens.location IS '食堂位置';
COMMENT ON COLUMN Canteens.opening_hours IS '食堂开放时间';
COMMENT ON COLUMN Canteens.contact_phone IS '食堂联系电话';
COMMENT ON COLUMN Canteens.image_url IS '食堂图片URL';


-- 3. 菜品表 (Dishes)
-- 存储食堂提供的所有菜品详细信息
-- 移除了 allergens 和 dietary_tags 字段，这些信息将通过独立的关联表存储
CREATE TABLE Dishes
(
    dish_id      VARCHAR(255) PRIMARY KEY,
    canteen_id   VARCHAR(255)   NOT NULL,
    name         VARCHAR(100)   NOT NULL,
    description  TEXT,
    price        DECIMAL(10, 2) NOT NULL,
    image_url    VARCHAR(255),
    is_available BOOLEAN   DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (canteen_id) REFERENCES Canteens (canteen_id)
);
COMMENT ON TABLE Dishes IS '存储食堂提供的所有菜品详细信息';
COMMENT ON COLUMN Dishes.dish_id IS '菜品唯一ID';
COMMENT ON COLUMN Dishes.canteen_id IS '所属食堂ID';
COMMENT ON COLUMN Dishes.name IS '菜品名称';
COMMENT ON COLUMN Dishes.description IS '菜品描述';
COMMENT ON COLUMN Dishes.price IS '菜品价格';
COMMENT ON COLUMN Dishes.image_url IS '菜品图片URL';
COMMENT ON COLUMN Dishes.is_available IS '菜品是否可用';
COMMENT ON COLUMN Dishes.created_at IS '菜品创建时间';


-- 4. 过敏原表 (Allergens)
-- 存储预定义的过敏原信息
CREATE TABLE Allergens
(
    allergen_id   VARCHAR(255) PRIMARY KEY,
    allergen_name VARCHAR(100) NOT NULL UNIQUE
);
COMMENT ON TABLE Allergens IS '存储预定义的过敏原信息';
COMMENT ON COLUMN Allergens.allergen_id IS '过敏原唯一ID';
COMMENT ON COLUMN Allergens.allergen_name IS '过敏原名称（如：花生、牛奶、麸质）';


-- 5. 菜品-过敏原关联表 (Dish_Allergens)
-- 关联菜品和其包含的过敏原
CREATE TABLE Dish_Allergens
(
    dish_id     VARCHAR(255) NOT NULL,
    allergen_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (dish_id, allergen_id),
    FOREIGN KEY (dish_id) REFERENCES Dishes (dish_id),
    FOREIGN KEY (allergen_id) REFERENCES Allergens (allergen_id)
);
COMMENT ON TABLE Dish_Allergens IS '关联菜品和其包含的过敏原';
COMMENT ON COLUMN Dish_Allergens.dish_id IS '菜品ID';
COMMENT ON COLUMN Dish_Allergens.allergen_id IS '过敏原ID';


-- 6. 饮食标签表 (Dietary_Tags)
-- 存储预定义的饮食标签，如素食、清真等
CREATE TABLE Dietary_Tags
(
    tag_id   VARCHAR(255) PRIMARY KEY,
    tag_name VARCHAR(100) NOT NULL UNIQUE
);
COMMENT ON TABLE Dietary_Tags IS '存储预定义的饮食标签，如素食、清真等';
COMMENT ON COLUMN Dietary_Tags.tag_id IS '饮食标签唯一ID';
COMMENT ON COLUMN Dietary_Tags.tag_name IS '饮食标签名称（如：素食、清真、无麸质）';


-- 7. 菜品-饮食标签关联表 (Dish_Dietary_Tags)
-- 关联菜品和其对应的饮食标签
CREATE TABLE Dish_Dietary_Tags
(
    dish_id VARCHAR(255) NOT NULL,
    tag_id  VARCHAR(255) NOT NULL,
    PRIMARY KEY (dish_id, tag_id),
    FOREIGN KEY (dish_id) REFERENCES Dishes (dish_id),
    FOREIGN KEY (tag_id) REFERENCES Dietary_Tags (tag_id)
);
COMMENT ON TABLE Dish_Dietary_Tags IS '关联菜品和其对应的饮食标签';
COMMENT ON COLUMN Dish_Dietary_Tags.dish_id IS '菜品ID';
COMMENT ON COLUMN Dish_Dietary_Tags.tag_id IS '饮食标签ID';

-- 10. 每日菜谱表 (Daily_Menus)
-- 存储每日各时间段的食谱信息
CREATE TABLE Daily_Menus
(
    menu_id              VARCHAR(255) PRIMARY KEY,
    canteen_id           VARCHAR(255) NOT NULL,
    menu_date            DATE         NOT NULL,
    start_time           TIME         NOT NULL,
    end_time             TIME         NOT NULL,
    published_by_user_id VARCHAR(255),
    published_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (canteen_id) REFERENCES Canteens (canteen_id),
    FOREIGN KEY (published_by_user_id) REFERENCES Users (user_id),
    UNIQUE (canteen_id, menu_date, start_time, end_time)
);
COMMENT ON TABLE Daily_Menus IS '存储每日各时间段的食谱信息';
COMMENT ON COLUMN Daily_Menus.menu_id IS '菜谱唯一ID';
COMMENT ON COLUMN Daily_Menus.canteen_id IS '所属食堂ID';
COMMENT ON COLUMN Daily_Menus.menu_date IS '菜谱日期';
COMMENT ON COLUMN Daily_Menus.start_time IS '菜谱开始时间';
COMMENT ON COLUMN Daily_Menus.end_time IS '菜谱结束时间';
COMMENT ON COLUMN Daily_Menus.published_by_user_id IS '发布菜谱的管理员ID';
COMMENT ON COLUMN Daily_Menus.published_at IS '菜谱发布时间';


-- 11. 菜谱-菜品关联表 (Menu_Dishes)
-- 关联每日菜谱和具体的菜品
CREATE TABLE Menu_Dishes
(
    menu_id VARCHAR(255) NOT NULL,
    dish_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (menu_id, dish_id),
    FOREIGN KEY (menu_id) REFERENCES Daily_Menus (menu_id),
    FOREIGN KEY (dish_id) REFERENCES Dishes (dish_id)
);
COMMENT ON TABLE Menu_Dishes IS '关联每日菜谱和具体的菜品';
COMMENT ON COLUMN Menu_Dishes.menu_id IS '菜谱ID';
COMMENT ON COLUMN Menu_Dishes.dish_id IS '菜品ID';


-- 12. 餐品预订表 (Orders)
-- 存储用户的餐品预订信息
CREATE TABLE Orders
(
    order_id     VARCHAR(255) PRIMARY KEY,
    user_id      VARCHAR(255)   NOT NULL,
    canteen_id   VARCHAR(255)   NOT NULL,
    order_date   DATE           NOT NULL,
    pickup_time  TIME           NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status       order_status DEFAULT 'PENDING', -- 使用自定义 ENUM 类型
    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users (user_id),
    FOREIGN KEY (canteen_id) REFERENCES Canteens (canteen_id)
);
COMMENT ON TABLE Orders IS '存储用户的餐品预订信息';
COMMENT ON COLUMN Orders.order_id IS '订单唯一ID';
COMMENT ON COLUMN Orders.user_id IS '预订用户ID';
COMMENT ON COLUMN Orders.canteen_id IS '预订食堂ID';
COMMENT ON COLUMN Orders.canteen_id IS '预订食堂ID';
COMMENT ON COLUMN Orders.order_date IS '预订日期';
COMMENT ON COLUMN Orders.pickup_time IS '取餐时间';
COMMENT ON COLUMN Orders.total_amount IS '订单总金额';
COMMENT ON COLUMN Orders.status IS '订单状态';
COMMENT ON COLUMN Orders.created_at IS '订单创建时间';


-- 13. 订单详情表 (Order_Items)
-- 存储每个订单包含的菜品及其数量
CREATE TABLE Order_Items
(
    order_item_id VARCHAR(255) PRIMARY KEY,
    order_id      VARCHAR(255)   NOT NULL,
    dish_id       VARCHAR(255)   NOT NULL,
    quantity      INT            NOT NULL,
    subtotal      DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders (order_id),
    FOREIGN KEY (dish_id) REFERENCES Dishes (dish_id)
);
COMMENT ON TABLE Order_Items IS '存储每个订单包含的菜品及其数量';
COMMENT ON COLUMN Order_Items.order_item_id IS '订单项唯一ID';
COMMENT ON COLUMN Order_Items.order_id IS '所属订单ID';
COMMENT ON COLUMN Order_Items.dish_id IS '菜品ID';
COMMENT ON COLUMN Order_Items.quantity IS '菜品数量';
COMMENT ON COLUMN Order_Items.subtotal IS '该菜品小计金额';


-- 14. 评价与反馈表 (Ratings_Reviews)
-- 存储用户对菜品的评分和点评
CREATE TABLE Ratings_Reviews
(
    review_id   VARCHAR(255) PRIMARY KEY,
    user_id     VARCHAR(255) NOT NULL,
    dish_id     VARCHAR(255) NOT NULL,
    rating      INT CHECK (rating >= 1 AND rating <= 5),
    comment     TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users (user_id),
    FOREIGN KEY (dish_id) REFERENCES Dishes (dish_id),
    UNIQUE (user_id, dish_id) -- 确保每个用户对每个菜品只有一份评价
);
COMMENT ON TABLE Ratings_Reviews IS '存储用户对菜品的评分和点评';
COMMENT ON COLUMN Ratings_Reviews.review_id IS '评价唯一ID';
COMMENT ON COLUMN Ratings_Reviews.user_id IS '评价用户ID';
COMMENT ON COLUMN Ratings_Reviews.dish_id IS '被评价菜品ID';
COMMENT ON COLUMN Ratings_Reviews.rating IS '评分（1-5星）';
COMMENT ON COLUMN Ratings_Reviews.comment IS '点评内容';
COMMENT ON COLUMN Ratings_Reviews.review_date IS '评价时间';


-- 15. 宴会包厢表 (Rooms)
CREATE TABLE Rooms
(
    room_id     VARCHAR(255) PRIMARY KEY,
    canteen_id  VARCHAR(255)   NOT NULL,
    name        VARCHAR(100)   NOT NULL,
    capacity    INT            NOT NULL,
    description TEXT,
    image_url   VARCHAR(255),
    base_fee    DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (canteen_id) REFERENCES Canteens (canteen_id)
);
COMMENT ON TABLE Rooms IS '宴会包厢表';
COMMENT ON COLUMN Rooms.room_id IS '包厢唯一ID';
COMMENT ON COLUMN Rooms.canteen_id IS '所属食堂ID';
COMMENT ON COLUMN Rooms.name IS '包厢名称（如"学士楼"、"学子楼"）';
COMMENT ON COLUMN Rooms.capacity IS '包厢容纳人数';
COMMENT ON COLUMN Rooms.description IS '包厢描述';
COMMENT ON COLUMN Rooms.image_url IS '包厢图片URL';
COMMENT ON COLUMN Rooms.base_fee IS '包厢基础费用';


-- 16. 宴会套餐表 (Packages)
CREATE TABLE Packages (
    package_id  VARCHAR(255) PRIMARY KEY,
    canteen_id  VARCHAR(255) NOT NULL, -- 新增：所属食堂ID
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    price       DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (canteen_id) REFERENCES Canteens(canteen_id), -- 新增：外键约束
    UNIQUE (name, canteen_id) -- 新增：复合唯一性约束
);
COMMENT ON TABLE Packages IS '宴会套餐表';
COMMENT ON COLUMN Packages.package_id IS '套餐唯一ID';
COMMENT ON COLUMN Packages.canteen_id IS '所属食堂ID'; -- 新增：注释
COMMENT ON COLUMN Packages.name IS '套餐名称（在所属食堂内唯一）'; -- 修改：注释
COMMENT ON COLUMN Packages.description IS '套餐描述';
COMMENT ON COLUMN Packages.price IS '套餐价格';


-- 17. 套餐-菜品关联表 (Package_Dishes)
CREATE TABLE Package_Dishes
(
    package_id VARCHAR(255) NOT NULL,
    dish_id    VARCHAR(255) NOT NULL,
    PRIMARY KEY (package_id, dish_id),
    FOREIGN KEY (package_id) REFERENCES Packages (package_id),
    FOREIGN KEY (dish_id) REFERENCES Dishes (dish_id)
);
COMMENT ON TABLE Package_Dishes IS '套餐-菜品关联表';
COMMENT ON COLUMN Package_Dishes.package_id IS '套餐ID';
COMMENT ON COLUMN Package_Dishes.dish_id IS '菜品ID';


-- 18. 宴会预订表 (Banquet_Reservations)
CREATE TABLE Banquet_Reservations
(
    banquet_id           VARCHAR(255) PRIMARY KEY,
    user_id              VARCHAR(255)   NOT NULL,
    canteen_id           VARCHAR(255)   NOT NULL,
    room_id              VARCHAR(255),                     -- 新增：包厢ID
    event_date           DATE           NOT NULL,
    event_time           TIME           NOT NULL,
    number_of_guests     INT            NOT NULL,
    contact_name         VARCHAR(100)   NOT NULL,          -- 新增：联系人姓名
    contact_phone_number VARCHAR(20)    NOT NULL,          -- 新增：联系人手机号
    purpose              VARCHAR(255),
    custom_menu_request  TEXT,
    has_birthday_cake    BOOLEAN        DEFAULT FALSE,     -- 新增：是否包含生日蛋糕
    special_requests     TEXT,                             -- 新增：特殊需求
    total_price          DECIMAL(10, 2) NOT NULL,
    status               banquet_status DEFAULT 'PENDING', -- 使用自定义 ENUM 类型
    confirmation_date    TIMESTAMP,
    created_at           TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users (user_id),
    FOREIGN KEY (canteen_id) REFERENCES Canteens (canteen_id),
    FOREIGN KEY (room_id) REFERENCES Rooms (room_id)
);
COMMENT ON TABLE Banquet_Reservations IS '存储食堂宴会或包厢预订信息';
COMMENT ON COLUMN Banquet_Reservations.banquet_id IS '宴会预订唯一ID';
COMMENT ON COLUMN Banquet_Reservations.user_id IS '预订用户ID';
COMMENT ON COLUMN Banquet_Reservations.canteen_id IS '预订食堂ID';
COMMENT ON COLUMN Banquet_Reservations.room_id IS '预订包厢ID';
COMMENT ON COLUMN Banquet_Reservations.event_date IS '宴会日期';
COMMENT ON COLUMN Banquet_Reservations.event_time IS '宴会时间';
COMMENT ON COLUMN Banquet_Reservations.number_of_guests IS '宾客人数';
COMMENT ON COLUMN Banquet_Reservations.contact_name IS '联系人姓名';
COMMENT ON COLUMN Banquet_Reservations.contact_phone_number IS '联系人手机号';
COMMENT ON COLUMN Banquet_Reservations.purpose IS '宴会目的（如：生日聚会、会议）';
COMMENT ON COLUMN Banquet_Reservations.custom_menu_request IS '定制菜单请求';
COMMENT ON COLUMN Banquet_Reservations.has_birthday_cake IS '是否包含生日蛋糕';
COMMENT ON COLUMN Banquet_Reservations.special_requests IS '特殊需求';
COMMENT ON COLUMN Banquet_Reservations.total_price IS '宴会总价';
COMMENT ON COLUMN Banquet_Reservations.status IS '预订状态';
COMMENT ON COLUMN Banquet_Reservations.confirmation_date IS '确认时间';
COMMENT ON COLUMN Banquet_Reservations.created_at IS '预订创建时间';


-- 19. 宴会预订-菜品关联表 (Banquet_Reservation_Dishes)
-- 存储宴会预订中定制的单品菜品
CREATE TABLE Banquet_Reservation_Dishes
(
    banquet_id VARCHAR(255) NOT NULL,
    dish_id    VARCHAR(255) NOT NULL,
    PRIMARY KEY (banquet_id, dish_id),
    FOREIGN KEY (banquet_id) REFERENCES Banquet_Reservations (banquet_id),
    FOREIGN KEY (dish_id) REFERENCES Dishes (dish_id)
);
COMMENT ON TABLE Banquet_Reservation_Dishes IS '宴会预订中定制的单品菜品关联表';
COMMENT ON COLUMN Banquet_Reservation_Dishes.banquet_id IS '宴会预订ID';
COMMENT ON COLUMN Banquet_Reservation_Dishes.dish_id IS '菜品ID';


-- 20. 宴会预订-套餐关联表 (Banquet_Reservation_Packages)
-- 存储宴会预订中选择的套餐
CREATE TABLE Banquet_Reservation_Packages
(
    banquet_id VARCHAR(255) NOT NULL,
    package_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (banquet_id, package_id),
    FOREIGN KEY (banquet_id) REFERENCES Banquet_Reservations (banquet_id),
    FOREIGN KEY (package_id) REFERENCES Packages (package_id)
);
COMMENT ON TABLE Banquet_Reservation_Packages IS '宴会预订中选择的套餐关联表';
COMMENT ON COLUMN Banquet_Reservation_Packages.banquet_id IS '宴会预订ID';
COMMENT ON COLUMN Banquet_Reservation_Packages.package_id IS '套餐ID';

CREATE TABLE Canteen_Images
(
    image_id    VARCHAR(255) PRIMARY KEY,
    canteen_id  VARCHAR(255) NOT NULL,
    image_url   VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (canteen_id) REFERENCES Canteens(canteen_id) ON DELETE CASCADE
);

-- 索引（可选，但推荐用于提高查询性能）
CREATE INDEX idx_users_role ON Users (role);
CREATE INDEX idx_dishes_canteen_id ON Dishes (canteen_id);
CREATE INDEX idx_daily_menus_date_time ON Daily_Menus (menu_date, start_time, end_time);
CREATE INDEX idx_orders_user_id ON Orders (user_id);
CREATE INDEX idx_orders_canteen_id ON Orders (canteen_id);
CREATE INDEX idx_reviews_dish_id ON Ratings_Reviews (dish_id);

-- 新增索引
CREATE INDEX idx_rooms_canteen_id ON Rooms (canteen_id);
CREATE INDEX idx_package_dishes_package_id ON Package_Dishes (package_id);
CREATE INDEX idx_package_dishes_dish_id ON Package_Dishes (dish_id);
CREATE INDEX idx_banquet_reservations_user_id ON Banquet_Reservations (user_id);
CREATE INDEX idx_banquet_reservations_canteen_id ON Banquet_Reservations (canteen_id);
CREATE INDEX idx_banquet_reservations_room_id ON Banquet_Reservations (room_id);
CREATE INDEX idx_banquet_reservation_dishes_banquet_id ON Banquet_Reservation_Dishes (banquet_id);
CREATE INDEX idx_banquet_reservation_dishes_dish_id ON Banquet_Reservation_Dishes (dish_id);
CREATE INDEX idx_banquet_reservation_packages_banquet_id ON Banquet_Reservation_Packages (banquet_id);
CREATE INDEX idx_banquet_reservation_packages_package_id ON Banquet_Reservation_Packages (package_id);

CREATE INDEX idx_dish_allergens_dish_id ON Dish_Allergens (dish_id);
CREATE INDEX idx_dish_allergens_allergen_id ON Dish_Allergens (allergen_id);
CREATE INDEX idx_dish_dietary_tags_dish_id ON Dish_Dietary_Tags (dish_id);
CREATE INDEX idx_dish_dietary_tags_tag_id ON Dish_Dietary_Tags (tag_id);