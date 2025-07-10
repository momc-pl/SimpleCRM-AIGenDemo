@description('Simple test container for Azure deployment verification')
param location string = resourceGroup().location
param environment string

// Variables
var containerGroupName = 'simplecrm-${environment}-test'

// Container Instance
resource containerInstance 'Microsoft.ContainerInstance/containerGroups@2023-05-01' = {
  name: containerGroupName
  location: location
  properties: {
    containers: [
      {
        name: 'test-nginx'
        properties: {
          image: 'simplecrmtstacroxi4dxbglpndc.azurecr.io/simplecrm-test:latest'
          ports: [
            {
              port: 80
              protocol: 'TCP'
            }
          ]
          resources: {
            requests: {
              cpu: 1
              memoryInGB: 1
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

// Outputs
output containerUrl string = 'http://${containerInstance.properties.ipAddress.dnsNameLabel}.polandcentral.azurecontainer.io'
output containerFQDN string = '${containerInstance.properties.ipAddress.dnsNameLabel}.polandcentral.azurecontainer.io'