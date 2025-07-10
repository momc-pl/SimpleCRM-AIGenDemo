@description('Azure Front Door with automatic SSL for SimpleCRM')
param location string = resourceGroup().location
param environment string
param subdomain string
param domainName string
param containerFQDN string
param containerPort int = 8080

// Variables
var frontDoorProfileName = 'fd-simplecrm-${environment}'
var endpointName = 'simplecrm-${environment}-endpoint'
var originGroupName = 'simplecrm-${environment}-origin-group'
var originName = 'simplecrm-${environment}-origin'
var routeName = 'simplecrm-${environment}-route'
var customDomainName = replace('${subdomain}-${domainName}', '.', '-')

// Front Door Profile
resource frontDoorProfile 'Microsoft.Cdn/profiles@2023-05-01' = {
  name: frontDoorProfileName
  location: 'Global'
  sku: {
    name: 'Standard_AzureFrontDoor'
  }
  properties: {}
}

// Front Door Endpoint
resource frontDoorEndpoint 'Microsoft.Cdn/profiles/afdEndpoints@2023-05-01' = {
  name: endpointName
  parent: frontDoorProfile
  location: 'Global'
  properties: {
    enabledState: 'Enabled'
  }
}

// Origin Group
resource originGroup 'Microsoft.Cdn/profiles/originGroups@2023-05-01' = {
  name: originGroupName
  parent: frontDoorProfile
  properties: {
    loadBalancingSettings: {
      sampleSize: 4
      successfulSamplesRequired: 3
      additionalLatencyInMilliseconds: 50
    }
    healthProbeSettings: {
      probePath: '/login'
      probeRequestType: 'HEAD'
      probeProtocol: 'Http'
      probeIntervalInSeconds: 100
    }
  }
}

// Origin
resource origin 'Microsoft.Cdn/profiles/originGroups/origins@2023-05-01' = {
  name: originName
  parent: originGroup
  properties: {
    hostName: containerFQDN
    httpPort: containerPort
    httpsPort: 443
    originHostHeader: containerFQDN
    priority: 1
    weight: 1000
    enabledState: 'Enabled'
  }
}

// Custom Domain
resource customDomain 'Microsoft.Cdn/profiles/customdomains@2023-05-01' = {
  name: customDomainName
  parent: frontDoorProfile
  properties: {
    hostName: '${subdomain}.${domainName}'
    tlsSettings: {
      certificateType: 'ManagedCertificate'
      minimumTlsVersion: 'TLS12'
    }
  }
}

// Route
resource route 'Microsoft.Cdn/profiles/afdEndpoints/routes@2023-05-01' = {
  name: routeName
  parent: frontDoorEndpoint
  dependsOn: [
    origin
    customDomain
  ]
  properties: {
    customDomains: [
      {
        id: customDomain.id
      }
    ]
    originGroup: {
      id: originGroup.id
    }
    supportedProtocols: [
      'Http'
      'Https'
    ]
    patternsToMatch: [
      '/*'
    ]
    forwardingProtocol: 'HttpOnly'
    linkToDefaultDomain: 'Enabled'
    httpsRedirect: 'Enabled'
  }
}

// Outputs
output frontDoorEndpointHostName string = frontDoorEndpoint.properties.hostName
output customDomainName string = '${subdomain}.${domainName}'
output frontDoorId string = frontDoorProfile.id
output customDomainValidationToken string = customDomain.properties.validationProperties.validationToken