# SimpleCRM Project Information Preservation

## Project Overview
SimpleCRM is a Spring Boot-based Customer Relationship Management system with the following key features:
- OAuth2 Google authentication
- Customer management with address support
- Product catalog management
- Order management system
- PostgreSQL database backend
- Thymeleaf templating for frontend
- Containerized deployment with Docker

## Technologies Used
- **Backend Framework**: Spring Boot 3.1.0
- **Java Version**: 17
- **Database**: PostgreSQL
- **Security**: Spring Security with OAuth2 (Google)
- **Frontend**: Thymeleaf, Bootstrap
- **Build Tool**: Maven
- **Containerization**: Docker
- **Cloud Deployment**: Azure (Container Instances, PostgreSQL Flexible Server, Container Registry)

## Database Schema

### Core Entities
1. **Users** (`users` table)
   - Primary key: `id` (Long)
   - Fields: `google_id` (unique), `name`, `email` (unique)
   - Relationships: One-to-Many with Orders

2. **Customers** (`customers` table)
   - Primary key: `id` (Long)
   - Fields: `name`, `email` (unique), `phone`
   - Relationships: One-to-Many with Addresses and Orders

3. **Addresses** (`addresses` table)
   - Primary key: `id` (Long)
   - Fields: `street`, `city`, `zip`, `country`
   - Relationships: Many-to-One with Customer

4. **Products** (`products` table)
   - Primary key: `id` (Long)
   - Fields: `name`, `price_net`, `vat_rate`

5. **Orders** (`orders` table)
   - Primary key: `id` (Long)
   - Fields: `order_number` (unique), `created_at`, `total_net`, `total_vat`, `total_gross`
   - Relationships: Many-to-One with User and Customer, One-to-Many with OrderLines

6. **Order Lines** (`order_lines` table)
   - Primary key: `id` (Long)
   - Fields: `quantity`, `unit_price_net`, `line_vat`, `line_total_gross`
   - Relationships: Many-to-One with Order and Product

## Application Configuration

### Environment Variables
- `SPRING_DATASOURCE_URL`: PostgreSQL connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID`: Google OAuth client ID
- `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET`: Google OAuth client secret
- `PORT`: Application port (default: 8080)
- `LOGGING_LEVEL_ROOT`: Logging level (default: INFO)

### Google OAuth2 Configuration
- **Client ID**: 633578308117-3h0ogak44a64ok3lb81jfng78up9u9i7.apps.googleusercontent.com
- **Scopes**: profile, email
- **Redirect URI Pattern**: https://{domain}/login/oauth2/code/google

## Azure Deployment Information

### Resource Names (Test Environment)
- **Resource Group**: rg-simplecrm-tst-202507091622
- **Container Registry**: simplecrmtstacroxi4dxbglpndc.azurecr.io
- **PostgreSQL Server**: simplecrm-tst-postgres.postgres.database.azure.com
- **Container Instance**: simplecrm-tst-app
- **Custom Domain**: simplecrm-tst.momc.pl

### Database Configuration
- **Host**: simplecrm-tst-postgres.postgres.database.azure.com
- **Database Name**: appdb
- **Admin Username**: simplecrm_admin
- **SSL Mode**: Required
- **Port**: 5432

### Container Configuration
- **Image**: simplecrmtstacroxi4dxbglpndc.azurecr.io/simplecrm:latest
- **Architecture**: linux/amd64 (important for Azure Container Instances)
- **Port**: 8080
- **Health Check**: /actuator/health (if Spring Boot Actuator is enabled)

## Build and Deployment Process

### Local Development
1. Ensure Java 17 and Maven are installed
2. Set up PostgreSQL database
3. Configure environment variables in application.properties
4. Run: `./mvnw spring-boot:run`

### Docker Build
```bash
# Build for AMD64 architecture (required for Azure)
docker build --platform linux/amd64 -t simplecrmtstacroxi4dxbglpndc.azurecr.io/simplecrm:latest .

# Push to Azure Container Registry
az acr login --name simplecrmtstacroxi4dxbglpndc
docker push simplecrmtstacroxi4dxbglpndc.azurecr.io/simplecrm:latest
```

### Azure Deployment
- Infrastructure deployed via Bicep templates in `azure/bicep/` directory
- Container deployed to Azure Container Instances
- PostgreSQL Flexible Server for database
- DNS integration with custom domain (momc.pl)

## Security Features
- OAuth2 authentication with Google
- CSRF protection enabled
- SQL injection protection via JPA/Hibernate
- Password-free authentication (OAuth2 only)
- Secure database connections with SSL

## Key Business Logic

### Order Processing
- Orders are automatically associated with the authenticated user
- Order numbers are generated using UUID format: "ORD-{UUID}"
- VAT calculation is performed automatically based on product VAT rate
- Order totals calculated as: net + VAT = gross

### User Management
- Users are automatically created on first OAuth2 login
- User data synced from Google (name, email)
- Users can only see their own orders

### Customer Management
- Customers can have multiple addresses
- Customer email must be unique
- Address management with CRUD operations

## File Structure
```
SimpleCRM-Radek/
├── pom.xml                          # Maven configuration
├── Dockerfile                       # Container configuration
├── mvnw, mvnw.cmd                   # Maven wrapper
├── .mvn/                           # Maven wrapper configuration
├── src/main/
│   ├── java/com/example/
│   │   ├── SimpleCrmApplication.java        # Main application class
│   │   ├── entities/                        # JPA entities
│   │   │   ├── User.java
│   │   │   ├── Customer.java
│   │   │   ├── Address.java
│   │   │   ├── Product.java
│   │   │   ├── Order.java
│   │   │   └── OrderLine.java
│   │   ├── repositories/                    # Spring Data repositories
│   │   │   ├── UserRepository.java
│   │   │   ├── CustomerRepository.java
│   │   │   ├── AddressRepository.java
│   │   │   ├── ProductRepository.java
│   │   │   └── OrderRepository.java
│   │   ├── services/                        # Business logic
│   │   │   ├── UserService.java
│   │   │   ├── OrderService.java
│   │   │   └── CustomOAuth2UserService.java
│   │   ├── controllers/                     # Web controllers
│   │   │   ├── DashboardController.java
│   │   │   ├── LoginController.java
│   │   │   ├── CustomerController.java
│   │   │   └── CustomErrorController.java
│   │   ├── config/                          # Configuration classes
│   │   │   └── SecurityConfig.java
│   │   └── dto/                             # Data Transfer Objects
│   │       ├── OrderDto.java
│   │       └── OrderLineDto.java
│   └── resources/
│       ├── application.properties           # Application configuration
│       ├── static/                          # Static web resources
│       └── templates/                       # Thymeleaf templates
└── azure/                                  # Azure deployment scripts
    └── bicep/                              # Infrastructure as Code
```

## Known Issues and Fixes

### Architecture Mismatch
- **Issue**: Docker images built on ARM64 (Apple Silicon) don't run on Azure Container Instances (x86_64)
- **Fix**: Always build with `--platform linux/amd64` flag

### Database Connection
- **Issue**: PostgreSQL requires SSL and proper authentication
- **Fix**: Use SSL mode 'require' and ensure firewall rules allow connection

### OAuth2 Redirect URI
- **Issue**: Google OAuth2 requires exact redirect URI match
- **Fix**: Add https://{domain}/login/oauth2/code/google to Google OAuth2 configuration

## Testing Strategy
- Unit tests for service layer components
- Integration tests for repository layer
- End-to-end tests for user flows (login, order creation, customer management)
- Security tests for authentication and authorization

## Performance Considerations
- Database connection pooling configured
- JPA lazy loading used for associations
- Container resource limits set appropriately
- Health checks configured for monitoring

## Monitoring and Logging
- Application logs to stdout (captured by container runtime)
- Security events logged at DEBUG level
- Database query logging configurable
- Health endpoint available for monitoring

## Next Steps for Full Functionality
1. Create Thymeleaf templates for all views
2. Add product management controllers
3. Implement order management UI
4. Add customer creation functionality
5. Implement comprehensive error handling
6. Add data validation
7. Create user management interface
8. Add reporting and analytics features