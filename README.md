# SimpleCRM - Spring Boot CRM System

A modern Customer Relationship Management system built with Spring Boot, featuring Google OAuth2 authentication, PostgreSQL database, and Docker containerization.

## Features

- **Authentication**: Google OAuth2 integration
- **Customer Management**: CRUD operations for customers and addresses
- **Product Catalog**: Manage products with pricing and VAT
- **Order Management**: Create and track orders
- **Responsive UI**: Bootstrap-based Thymeleaf templates
- **Containerized**: Docker support for easy deployment
- **Cloud Ready**: Azure deployment support

## Technology Stack

- **Backend**: Spring Boot 3.1.0, Java 17
- **Database**: PostgreSQL
- **Security**: Spring Security with OAuth2
- **Frontend**: Thymeleaf, Bootstrap 5
- **Build**: Maven
- **Containerization**: Docker
- **Cloud**: Azure Container Instances, PostgreSQL Flexible Server

## Quick Start

### Prerequisites

- Java 17 or later
- Maven 3.6+
- PostgreSQL 12+ (or Docker for local development)
- Google OAuth2 credentials

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd SimpleCRM-Radek
   ```

2. **Set up PostgreSQL database**
   ```bash
   # Using Docker
   docker run --name postgres-crm \
     -e POSTGRES_DB=appdb \
     -e POSTGRES_USER=appuser \
     -e POSTGRES_PASSWORD=appsecret \
     -p 5432:5432 -d postgres:15
   ```

3. **Configure Google OAuth2**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or use existing
   - Enable Google+ API
   - Create OAuth2 credentials
   - Add redirect URI: `http://localhost:8080/login/oauth2/code/google`

4. **Set environment variables**
   ```bash
   export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/appdb
   export SPRING_DATASOURCE_USERNAME=appuser
   export SPRING_DATASOURCE_PASSWORD=appsecret
   export SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET=your_google_client_secret
   ```

5. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

6. **Access the application**
   - Open browser: http://localhost:8080
   - Sign in with Google account

## Docker Deployment

### Build Docker Image

```bash
# Build for x86_64 architecture (required for Azure/most cloud platforms)
docker build --platform linux/amd64 -t simplecrm:latest .

# Or for local development (current architecture)
docker build -t simplecrm:latest .
```

### Run with Docker Compose

Create `docker-compose.yml`:

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: appdb
      POSTGRES_USER: appuser
      POSTGRES_PASSWORD: appsecret
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  app:
    image: simplecrm:latest
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/appdb
      SPRING_DATASOURCE_USERNAME: appuser
      SPRING_DATASOURCE_PASSWORD: appsecret
      SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET: ${GOOGLE_CLIENT_SECRET}
    depends_on:
      - postgres

volumes:
  postgres_data:
```

Run with:
```bash
export GOOGLE_CLIENT_SECRET=your_google_client_secret
docker-compose up -d
```

## Azure Deployment

### Prerequisites
- Azure CLI installed and logged in
- Azure subscription

### Deploy to Azure

1. **Create resource group**
   ```bash
   az group create --name rg-simplecrm --location "East US"
   ```

2. **Deploy infrastructure using Bicep**
   ```bash
   cd azure
   az deployment group create \
     --resource-group rg-simplecrm \
     --template-file bicep/infrastructure.bicep \
     --parameters @bicep/parameters.json
   ```

3. **Build and push Docker image**
   ```bash
   # Login to Azure Container Registry
   az acr login --name your-acr-name
   
   # Build and push
   docker build --platform linux/amd64 -t your-acr-name.azurecr.io/simplecrm:latest .
   docker push your-acr-name.azurecr.io/simplecrm:latest
   ```

4. **Deploy container**
   ```bash
   az deployment group create \
     --resource-group rg-simplecrm \
     --template-file bicep/container.bicep \
     --parameters @bicep/parameters.json
   ```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/appdb` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `appuser` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | Required |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret | Required |
| `PORT` | Application port | `8080` |
| `LOGGING_LEVEL_ROOT` | Logging level | `INFO` |

### Google OAuth2 Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create or select a project
3. Enable Google+ API
4. Go to "Credentials" → "Create Credentials" → "OAuth client ID"
5. Choose "Web application"
6. Add authorized redirect URIs:
   - Local: `http://localhost:8080/login/oauth2/code/google`
   - Production: `https://your-domain.com/login/oauth2/code/google`

## Database Schema

The application uses JPA/Hibernate to automatically create the database schema. Key entities:

- **Users**: Google OAuth2 authenticated users
- **Customers**: Business customers with contact information
- **Addresses**: Customer addresses (one-to-many)
- **Products**: Product catalog with pricing and VAT
- **Orders**: Customer orders
- **Order Lines**: Order line items with products and quantities

## API Endpoints

### Web Routes
- `GET /` - Dashboard
- `GET /login` - Login page
- `GET /customers/{id}` - Customer details
- `GET /customers/{id}/edit` - Edit customer
- `POST /customers/{id}` - Update customer
- `POST /customers/{id}/addresses` - Add address

### Health Check
- `GET /actuator/health` - Application health (if Actuator is enabled)

## Development

### Running Tests
```bash
./mvnw test
```

### Code Coverage
```bash
./mvnw clean test jacoco:report
```

### Building for Production
```bash
./mvnw clean package -DskipTests
```

## Troubleshooting

### Common Issues

1. **OAuth2 Login Fails**
   - Check Google OAuth2 credentials
   - Verify redirect URI matches exactly
   - Ensure Google+ API is enabled

2. **Database Connection Fails**
   - Verify PostgreSQL is running
   - Check connection URL and credentials
   - For Azure: ensure firewall rules allow connection

3. **Container Won't Start on Azure**
   - Ensure Docker image is built for `linux/amd64` architecture
   - Check container logs: `az container logs --resource-group rg-name --name container-name`
   - Verify environment variables are set correctly

4. **Template Not Found Errors**
   - Ensure Thymeleaf templates are in `src/main/resources/templates/`
   - Check template file names match controller return values

### Useful Commands

```bash
# View application logs (local)
./mvnw spring-boot:run --debug

# View container logs (Azure)
az container logs --resource-group rg-simplecrm --name simplecrm-app

# Connect to PostgreSQL (local)
psql -h localhost -U appuser -d appdb

# Check Azure resources
az resource list --resource-group rg-simplecrm --output table
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- Check the troubleshooting section
- Review application logs
- Create an issue in the repository