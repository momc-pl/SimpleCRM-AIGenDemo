@description('Debug SimpleCRM application container')
param location string = resourceGroup().location
param environment string

// Variables
var containerGroupName = 'simplecrm-${environment}-debug'

// Container Instance
resource containerInstance 'Microsoft.ContainerInstance/containerGroups@2023-05-01' = {
  name: containerGroupName
  location: location
  properties: {
    containers: [
      {
        name: 'simplecrm-app'
        properties: {
          image: 'simplecrmtstacroxi4dxbglpndc.azurecr.io/simplecrm-app:latest'
          ports: [
            {
              port: 8080
              protocol: 'TCP'
            }
          ]
          environmentVariables: [
            {
              name: 'DB_URL'
              value: 'jdbc:postgresql://simplecrm-tst-postgres.postgres.database.azure.com:5432/appdb?sslmode=require'
            }
            {
              name: 'DB_USERNAME'
              value: 'simplecrm_admin'
            }
            {
              name: 'DB_PASSWORD'
              value: 'SecurePassword123!'
            }
            {
              name: 'GOOGLE_CLIENT_ID'
              value: 'your_google_client_id.apps.googleusercontent.com'
            }
            {
              name: 'GOOGLE_CLIENT_SECRET'
              value: 'your_google_client_secret'
            }
            {
              name: 'OAUTH2_REDIRECT_URI'
              value: 'https://simplecrm-tst.momc.pl/login/oauth2/code/google'
            }
            {
              name: 'APP_BASE_URL'
              value: 'https://simplecrm-tst.momc.pl'
            }
            {
              name: 'PORT'
              value: '8080'
            }
            {
              name: 'JAVA_OPTS'
              value: '-Xmx1g -Xms512m'
            }
          ]
          resources: {
            requests: {
              cpu: 2
              memoryInGB: 4
            }
          }
        }
      }
    ]
    osType: 'Linux'
    restartPolicy: 'Never'
    ipAddress: {
      type: 'Public'
      dnsNameLabel: containerGroupName
      ports: [
        {
          port: 8080
          protocol: 'TCP'
        }
      ]
    }
    imageRegistryCredentials: [
      {
        server: 'simplecrmtstacroxi4dxbglpndc.azurecr.io'
        username: 'simplecrmtstacroxi4dxbglpndc'
        password: 'your_azure_registry_password'
      }
    ]
  }
}

// Outputs
output containerUrl string = 'http://${containerInstance.properties.ipAddress.dnsNameLabel}.polandcentral.azurecontainer.io:8080'
output containerFQDN string = '${containerInstance.properties.ipAddress.dnsNameLabel}.polandcentral.azurecontainer.io'