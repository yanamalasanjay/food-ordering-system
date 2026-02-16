-- Sample Data for Food Ordering System
-- Author: Yanamala Sanjay

-- Insert sample customers
INSERT INTO customers (name, email, phone, address, password, is_premium) VALUES
('John Doe', 'john@example.com', '1234567890', '123 Main St', '$2a$10$abcdefghijklmnopqrstuv', FALSE),
('Jane Smith', 'jane@example.com', '9876543210', '456 Oak Ave', '$2a$10$abcdefghijklmnopqrstuv', TRUE),
('Bob Johnson', 'bob@example.com', '5555555555', '789 Pine Rd', '$2a$10$abcdefghijklmnopqrstuv', FALSE);

-- Insert sample menu items
INSERT INTO menu_items (name, description, price, category, available, preparation_time) VALUES
('Margherita Pizza', 'Classic pizza with tomato sauce and mozzarella', 12.99, 'Pizza', TRUE, 15),
('Pepperoni Pizza', 'Pizza with pepperoni and cheese', 14.99, 'Pizza', TRUE, 15),
('Cheese Burger', 'Burger with cheese, lettuce, and tomato', 8.99, 'Burger', TRUE, 10),
('Veggie Burger', 'Vegetarian burger with grilled vegetables', 9.99, 'Burger', TRUE, 10),
('Chicken Wings', 'Spicy chicken wings (8 pieces)', 11.99, 'Appetizer', TRUE, 12),
('French Fries', 'Crispy french fries', 4.99, 'Sides', TRUE, 8),
('Coca Cola', 'Cold beverage', 2.99, 'Beverages', TRUE, 2),
('Pasta Alfredo', 'Creamy pasta with alfredo sauce', 13.99, 'Pasta', TRUE, 20);

-- Insert ingredients for menu items
INSERT INTO menu_item_ingredients (menu_item_id, ingredient_name) VALUES
(1, 'Mozzarella Cheese'),
(1, 'Tomato Sauce'),
(1, 'Pizza Dough'),
(2, 'Mozzarella Cheese'),
(2, 'Tomato Sauce'),
(2, 'Pizza Dough'),
(2, 'Pepperoni'),
(3, 'Burger Bun'),
(3, 'Beef Patty'),
(3, 'Cheese Slice'),
(4, 'Burger Bun'),
(4, 'Veggie Patty'),
(5, 'Chicken Wings'),
(6, 'Potatoes'),
(8, 'Pasta'),
(8, 'Alfredo Sauce');

-- Insert sample inventory
INSERT INTO inventory (ingredient_name, quantity, unit, minimum_threshold) VALUES
('Mozzarella Cheese', 50, 'kg', 10),
('Tomato Sauce', 30, 'liters', 5),
('Pizza Dough', 100, 'pieces', 20),
('Pepperoni', 25, 'kg', 5),
('Burger Bun', 150, 'pieces', 30),
('Beef Patty', 80, 'pieces', 20),
('Cheese Slice', 200, 'pieces', 40),
('Veggie Patty', 60, 'pieces', 15),
('Chicken Wings', 120, 'kg', 25),
('Potatoes', 100, 'kg', 20),
('Pasta', 40, 'kg', 10),
('Alfredo Sauce', 20, 'liters', 5);
