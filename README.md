# Food Ordering & Kitchen Management System

**Author:** Yanamala Sanjay
**Institution:** Indian Institute of Technology Bhubaneswar
**Tech Stack:** Spring Boot, MySQL, WebSocket, Java 17

---

## 📋 Project Overview

A comprehensive cloud kitchen management platform that handles real-time order tracking and kitchen queue operations using priority-based algorithms. The system provides WebSocket-based kitchen display with automated inventory management and ingredient stock tracking.

### Key Features

- ✅ **Real-time Order Tracking** - WebSocket-based updates
- ✅ **Priority-Based Kitchen Queue** - Efficient order management using PriorityQueue
- ✅ **Automated Inventory Management** - Stock tracking and availability updates
- ✅ **Email Notifications** - Order confirmations and status updates
- ✅ **Dynamic Menu Availability** - Based on ingredient stock levels
- ✅ **RESTful API** - Clean API design with proper separation of concerns

---

## 🏗️ Architecture

```
┌─────────────┐
│   Client    │ (REST API / WebSocket)
└──────┬──────┘
       │
┌──────▼──────────────────────┐
│   Controller Layer          │ (REST APIs, WebSocket endpoints)
└──────┬──────────────────────┘
       │
┌──────▼──────────────────────┐
│   Service Layer             │ (Business Logic)
│ - OrderService              │
│ - KitchenQueueService       │
│ - InventoryService          │
│ - EmailService              │
└──────┬──────────────────────┘
       │
┌──────▼──────────────────────┐
│   Repository Layer          │ (Data Access)
└──────┬──────────────────────┘
       │
┌──────▼──────────────────────┐
│   MySQL Database            │
└─────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| Backend Framework | Spring Boot 3.2.0 |
| Language | Java 17 |
| Database | MySQL 8.0 |
| Real-time Communication | WebSocket (STOMP) |
| Email | Spring Mail (SMTP) |
| Build Tool | Maven |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security |

---

## 🚀 Getting Started

### Prerequisites

- JDK 17 or higher
- MySQL 8.0 or higher
- Maven 3.6+
- Git

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/yanamalasanjay/food-ordering-system.git
   cd food-ordering-system
   ```

2. **Configure MySQL Database**
   ```bash
   # Login to MySQL
   mysql -u root -p

   # Create database (or let application create it)
   CREATE DATABASE food_ordering_db;
   ```

3. **Update application.properties**
   ```properties
   # Edit src/main/resources/application.properties
   spring.datasource.username=YOUR_MYSQL_USERNAME
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```

4. **Configure Email (Optional for testing)**
   ```properties
   # For Gmail, enable "App Passwords"
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   ```

5. **Build the project**
   ```bash
   mvn clean install
   ```

6. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

7. **Access the application**
   - REST API: `http://localhost:8080/api`
   - WebSocket: `ws://localhost:8080/ws`

---

## 📡 API Endpoints

### Order Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Create new order |
| GET | `/api/orders/{id}` | Get order by ID |
| GET | `/api/orders/customer/{customerId}` | Get customer orders |
| PUT | `/api/orders/{id}/status` | Update order status |
| DELETE | `/api/orders/{id}` | Cancel order |

### Menu Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/menu` | Get available menu items |
| GET | `/api/menu/category/{category}` | Get items by category |
| GET | `/api/menu/search?q=pizza` | Search menu items |
| POST | `/api/menu` | Add menu item (Admin) |

### Kitchen Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/kitchen/orders` | Get active orders |
| GET | `/api/kitchen/next` | Get next order to prepare |
| POST | `/api/kitchen/{id}/start` | Start order preparation |
| POST | `/api/kitchen/{id}/ready` | Mark order ready |

### Inventory Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/inventory` | Get all inventory |
| PUT | `/api/inventory/{id}` | Update inventory quantity |
| POST | `/api/inventory` | Add inventory item |

---

## 🔄 WebSocket Integration

### Kitchen Display Connection

```javascript
// Connect to WebSocket
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    // Subscribe to kitchen updates
    stompClient.subscribe('/topic/kitchen', function(update) {
        const orderUpdate = JSON.parse(update.body);
        console.log('Kitchen Update:', orderUpdate);
        // Update kitchen display UI
    });
});
```

### Update Types
- `NEW_ORDER` - New order received
- `STATUS_UPDATE` - Order status changed
- `CANCELLED` - Order cancelled

---

## 🧪 Testing the Application

### Sample Order Request

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      {
        "menuItemId": 1,
        "quantity": 2
      }
    ],
    "priority": "MEDIUM",
    "deliveryAddress": "123 Main St"
  }'
```

### Get Available Menu

```bash
curl http://localhost:8080/api/menu
```

### Check Kitchen Queue

```bash
curl http://localhost:8080/api/kitchen/orders
```

---

## 🎯 Key Design Decisions

### 1. Priority Queue for Kitchen Management
**Why?** Orders need to be processed based on priority and wait time, not just FIFO.

**Implementation:**
- Used Java's `PriorityQueue<Order>`
- Custom `Comparable` implementation in Order entity
- O(log n) insertion and removal
- O(1) peek operation

### 2. WebSocket for Real-time Updates
**Why?** Kitchen staff need instant notifications of new orders.

**Alternative Considered:** HTTP polling
**Why WebSocket?**
- Bidirectional communication
- Lower latency
- Reduced server load
- Better user experience

### 3. Automated Inventory Management
**Why?** Prevent orders for out-of-stock items.

**Implementation:**
- Scheduled job checks inventory every 5 minutes
- Updates menu item availability automatically
- Sends alerts for low stock

### 4. Asynchronous Email Sending
**Why?** Don't block order processing waiting for email.

**Implementation:**
- `@Async` annotation
- Separate thread pool
- Non-blocking execution

---

## 📊 Database Schema

### Key Tables
- `customers` - User information
- `menu_items` - Available dishes
- `orders` - Order records
- `order_items` - Items in each order
- `inventory` - Ingredient stock levels

### Relationships
- One-to-Many: Customer → Orders
- One-to-Many: Order → OrderItems
- Many-to-One: OrderItem → MenuItem

---

## 🔐 Security Considerations

**Current Implementation:** Basic security for demo purposes

**Production Recommendations:**
1. JWT-based authentication
2. Role-based access control (CUSTOMER, KITCHEN_STAFF, ADMIN)
3. Rate limiting on API endpoints
4. Input validation and sanitization
5. HTTPS only
6. Secure password storage (BCrypt ✓)

---

## 📈 Performance Optimizations

1. **Database Indexing**
   - Indexed frequently queried columns
   - Composite indexes for multi-column queries

2. **Lazy Loading**
   - Used `@ManyToOne(fetch = FetchType.LAZY)` where appropriate

3. **Connection Pooling**
   - Spring Boot's default HikariCP

4. **Async Processing**
   - Email sending doesn't block main thread

---

## 🐛 Troubleshooting

### Application doesn't start
- Check if MySQL is running: `mysql -u root -p`
- Verify database credentials in `application.properties`
- Ensure port 8080 is not in use

### WebSocket connection fails
- Check CORS settings in WebSocketConfig
- Verify firewall allows WebSocket connections
- Try with SockJS fallback

### Emails not sending
- Verify SMTP credentials
- For Gmail, enable "App Passwords"
- Check firewall allows SMTP port 587

---

## 🚀 Future Enhancements

- [ ] Order tracking page for customers
- [ ] Analytics dashboard for admin
- [ ] Mobile app integration
- [ ] Payment gateway integration
- [ ] Delivery partner assignment
- [ ] Rating and review system
- [ ] Advanced reporting features

---

## 👨‍💻 Author

**Yanamala Sanjay**
- Email: sanjayyanamala07@gmail.com
- LinkedIn: [linkedin.com/in/sanjay-yanamala-1b45b9219](https://linkedin.com/in/sanjay-yanamala-1b45b9219/)
- GitHub: [github.com/yanamalasanjay](https://github.com/yanamalasanjay)

---

## 📝 License

This project is created for educational and interview purposes.

---

**Built with ❤️ at IIT Bhubaneswar**
