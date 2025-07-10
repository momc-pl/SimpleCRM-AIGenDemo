@description('Container instance with multiple ports')
param location string = resourceGroup().location
param environment string
param containerImageName string
param postgresAdminUsername string
@secure()
param postgresAdminPassword string
param googleClientId string
@secure()
param googleClientSecret string
param dnsZoneResourceGroup string
param domainName string
param subdomain string

// Variables
var containerGroupName = 'simplecrm-${environment}-app'
var postgresServerName = 'simplecrm-${environment}-postgres'

// Container Instance with multiple ports
resource containerInstance 'Microsoft.ContainerInstance/containerGroups@2023-05-01' = {
  name: containerGroupName
  location: location
  properties: {
    containers: [
      {
        name: 'simplecrm-app'
        properties: {
          image: containerImageName
          ports: [
            {
              port: 80
              protocol: 'TCP'
            }
          ]
          environmentVariables: [
            {
              name: 'SPRING_DATASOURCE_URL'
              value: 'jdbc:postgresql://${postgresServerName}.postgres.database.azure.com:5432/appdb?sslmode=require'
            }
            {
              name: 'SPRING_DATASOURCE_USERNAME'
              value: postgresAdminUsername
            }
            {
              name: 'SPRING_DATASOURCE_PASSWORD'
              secureValue: postgresAdminPassword
            }
            {
              name: 'SPRING_JPA_SHOW_SQL'
              value: 'true'
            }
            {
              name: 'LOGGING_LEVEL_ROOT'
              value: 'INFO'
            }
            {
              name: 'SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID'
              value: googleClientId
            }
            {
              name: 'SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET'
              secureValue: googleClientSecret
            }
            {
              name: 'SERVER_PORT'
              value: '80'
            }
          ]
          resources: {
            requests: {
              cpu: 1
              memoryInGB: 2
            }
          }
        }
      }
    ]
    osType: 'Linux'
    restartPolicy: 'Always'
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

// DNS Module to handle cross-resource-group DNS operations
module dnsModule 'dns.bicep' = {
  name: 'dns-setup'
  scope: resourceGroup(dnsZoneResourceGroup)
  params: {
    domainName: domainName
    subdomain: subdomain
    targetCname: '${containerInstance.properties.ipAddress.dnsNameLabel}.polandcentral.azurecontainer.io'
  }
}

// Outputs
output containerUrl string = 'http://${containerInstance.properties.ipAddress.dnsNameLabel}.polandcentral.azurecontainer.io'
output applicationUrl string = 'http://${subdomain}.${domainName}'
output googleRedirectUri string = 'https://${subdomain}.${domainName}/login/oauth2/code/google'
output dnsRecordName string = dnsModule.outputs.dnsRecordName
output dnsRecordValue string = dnsModule.outputs.dnsRecordValue