@description('SimpleCRM application container with optimized configuration')
param location string = resourceGroup().location
param environment string
param postgresAdminUsername string
@secure()
param postgresAdminPassword string
param googleClientId string
@secure()
param googleClientSecret string

// Variables
var containerGroupName = 'simplecrm-${environment}-app'
var postgresServerName = 'simplecrm-${environment}-postgres'

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
              port: 80
              protocol: 'TCP'
            }
          ]
          environmentVariables: [
            {
              name: 'DB_URL'
              value: 'jdbc:postgresql://${postgresServerName}.postgres.database.azure.com:5432/appdb?sslmode=require'
            }
            {
              name: 'DB_USERNAME'
              value: postgresAdminUsername
            }
            {
              name: 'DB_PASSWORD'
              secureValue: postgresAdminPassword
            }
            {
              name: 'GOOGLE_CLIENT_ID'
              value: googleClientId
            }
            {
              name: 'GOOGLE_CLIENT_SECRET'
              secureValue: googleClientSecret
            }
            {
              name: 'PORT'
              value: '80'
            }
            {
              name: 'JAVA_OPTS'
              value: '-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport'
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
    restartPolicy: 'OnFailure'
    ipAddress: {
      type: 'Public'
      dnsNameLabel: containerGroupName
      ports: [
        {
          port: 80
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
output containerUrl string = 'http://${containerInstance.properties.ipAddress.dnsNameLabel}.polandcentral.azurecontainer.io'
output containerFQDN string = '${containerInstance.properties.ipAddress.dnsNameLabel}.polandcentral.azurecontainer.io'