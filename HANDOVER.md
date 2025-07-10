# SimpleCRM Azure Deployment - Handover Documentation

## Overview
Successfully deployed SimpleCRM application to Azure with complete OAuth2 Google authentication, SSL certificate, and proper DNS configuration.

## Final Working Configuration

### Application URLs
- **Production URL**: https://simplecrm-tst.momc.pl/login
- **Container Direct Access**: http://simplecrm-tst-debug.polandcentral.azurecontainer.io:8080/login
- **Database**: Azure PostgreSQL Flexible Server

### Authentication
- **OAuth2 Provider**: Google
- **Client ID**: [Your Google OAuth2 Client ID]
- **Redirect URI**: https://simplecrm-tst.momc.pl/login/oauth2/code/google

## Issues Resolved

### 1. Environment Variable Configuration
**Problem**: Spring Boot application couldn't load environment variables from .env file in Azure
**Solution**: 
- Updated `application.properties` to use simple environment variable names
- Added `spring-dotenv` dependency to `pom.xml`
- Configured Azure Container Instance with proper environment variables

### 2. Docker Platform Compatibility
**Problem**: ARM64 Docker images built on Mac wouldn't run on Azure AMD64 infrastructure
**Solution**:
- Created `build-azure.sh` script with explicit `--platform linux/amd64` flag
- Updated Docker build process: `docker buildx build --platform linux/amd64 -t simplecrmtstacroxi4dxbglpndc.azurecr.io/simplecrm-app:latest --push .`

### 3. Port Permission Issues
**Problem**: Application failed to start with "Permission denied" when binding to port 80 as non-root user
**Solution**:
- Changed application port from 80 to 8080 (non-privileged port)
- Updated `Dockerfile` to expose port 8080
- Updated Azure Container Instance and Front Door configuration to use port 8080

### 4. OAuth2 Redirect URI Mismatch
**Problem**: Application redirected OAuth2 callbacks to internal container URL instead of external Front Door URL
**Solution**:
- Added `OAuth2Config.java` with explicit redirect URI configuration
- Added environment variables: `APP_BASE_URL` and `OAUTH2_REDIRECT_URI`
- Configured Spring Boot to handle reverse proxy headers: `server.forward-headers-strategy=framework`

### 5. Database Connection
**Problem**: Initial database connection configuration issues
**Solution**:
- Verified Azure PostgreSQL Flexible Server configuration
- Confirmed database `appdb` exists and is accessible
- Updated connection string with proper SSL mode: `jdbc:postgresql://simplecrm-tst-postgres.postgres.database.azure.com:5432/appdb?sslmode=require`

## Key Files Modified

### Application Configuration
- **`src/main/resources/application.properties`**: Added OAuth2 redirect URI and server configuration
- **`src/main/java/com/example/OAuth2Config.java`**: NEW - Custom OAuth2 configuration with external URL handling
- **`Dockerfile`**: Changed port from 80 to 8080, maintained non-root user security

### Build and Deployment
- **`build-azure.sh`**: NEW - Script for proper AMD64 Docker builds for Azure
- **`azure/bicep/debug-container.bicep`**: Container deployment with all environment variables
- **`azure/bicep/front-door.bicep`**: Front Door configuration updated for port 8080

### Environment Variables Required
```bash
DB_URL=jdbc:postgresql://your-postgres-server.postgres.database.azure.com:5432/appdb?sslmode=require
DB_USERNAME=your_db_username
DB_PASSWORD=your_secure_password
GOOGLE_CLIENT_ID=your_google_client_id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your_google_client_secret
OAUTH2_REDIRECT_URI=https://your-domain.com/login/oauth2/code/google
APP_BASE_URL=https://your-domain.com
PORT=8080
```

## Azure Resources Deployed

### Container Instance
- **Name**: simplecrm-tst-debug
- **Image**: simplecrmtstacroxi4dxbglpndc.azurecr.io/simplecrm-app:latest
- **Port**: 8080
- **Resources**: 2 CPU, 4GB RAM
- **Platform**: AMD64

### PostgreSQL Database
- **Name**: simplecrm-tst-postgres
- **Server**: simplecrm-tst-postgres.postgres.database.azure.com
- **Database**: appdb
- **Status**: Ready and accessible

### Front Door Configuration
- **Profile**: fd-simplecrm-tst
- **Custom Domain**: simplecrm-tst.momc.pl
- **SSL Certificate**: Auto-generated and active
- **Backend**: simplecrm-tst-debug.polandcentral.azurecontainer.io:8080

### DNS Configuration
- **Domain**: momc.pl
- **Subdomain**: simplecrm-tst
- **Type**: CNAME pointing to Front Door

## Deployment Commands

### Build and Push Application
```bash
./build-azure.sh
```

### Deploy Container
```bash
az deployment group create \
  --resource-group rg-simplecrm-tst-202507091622 \
  --template-file azure/bicep/debug-container.bicep \
  --parameters environment=tst
```

### Deploy Front Door
```bash
az deployment group create \
  --resource-group rg-simplecrm-tst-202507091622 \
  --template-file azure/bicep/front-door.bicep \
  --parameters environment=tst \
               subdomain=simplecrm-tst \
               domainName=momc.pl \
               containerFQDN=simplecrm-tst-debug.polandcentral.azurecontainer.io \
               containerPort=8080
```

## Testing and Verification

### Application Health
- ✅ Application starts successfully
- ✅ Database connection working
- ✅ HTTPS/SSL certificate active
- ✅ OAuth2 flow functional

### OAuth2 Flow Test
1. Visit: https://simplecrm-tst.momc.pl/login
2. Click "Sign in with Google"
3. Redirects to: https://accounts.google.com/o/oauth2/v2/auth?...
4. After authentication, returns to: https://simplecrm-tst.momc.pl/login/oauth2/code/google
5. User logged in successfully

## Security Considerations

### Implemented
- ✅ Non-root container user (appuser:1001)
- ✅ HTTPS/SSL termination at Front Door
- ✅ Secure environment variable handling
- ✅ OAuth2 state parameter validation
- ✅ Database SSL connections required

### Secrets Management
- Google OAuth2 credentials stored in Azure Container Instance environment variables
- Database password stored in Azure Container Instance secure environment variables
- Consider migrating to Azure Key Vault for production

## Troubleshooting

### Common Issues
1. **504 Gateway Timeout**: Wait 1-2 minutes for Front Door cache to refresh after container deployment
2. **Container Crashes**: Check logs with `az container logs --resource-group rg-simplecrm-tst-202507091622 --name simplecrm-tst-debug`
3. **OAuth2 Errors**: Verify Google OAuth2 client configuration includes the correct redirect URI

### Monitoring Commands
```bash
# Check container status
az container show --resource-group rg-simplecrm-tst-202507091622 --name simplecrm-tst-debug --query "instanceView.currentState"

# View container logs
az container logs --resource-group rg-simplecrm-tst-202507091622 --name simplecrm-tst-debug

# Test application directly
curl -I "http://simplecrm-tst-debug.polandcentral.azurecontainer.io:8080/login"

# Test through Front Door
curl -I "https://simplecrm-tst.momc.pl/login"
```

## Next Steps / Recommendations

### Production Readiness
1. **Secrets Management**: Migrate to Azure Key Vault
2. **Monitoring**: Implement Azure Application Insights
3. **Scaling**: Consider Azure Container Apps for auto-scaling
4. **Backup**: Implement automated database backups
5. **CI/CD**: Set up Azure DevOps or GitHub Actions pipeline

### Performance Optimization
1. **Container Resources**: Monitor and adjust CPU/memory based on usage
2. **Database**: Implement connection pooling optimization
3. **Front Door**: Configure caching policies for static resources
4. **Container Images**: Optimize Docker image size

## Contact Information
- **Azure Resource Group**: rg-simplecrm-tst-202507091622
- **Azure Subscription**: d82da1a0-86c9-4aa3-9a74-f8f8ec83388d
- **Deployment Date**: July 10, 2025
- **Environment**: Test/Development

---

**Status**: ✅ DEPLOYED AND OPERATIONAL
**Last Updated**: July 10, 2025
**OAuth2 Authentication**: ✅ WORKING
**SSL Certificate**: ✅ ACTIVE