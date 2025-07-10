@description('DNS configuration for Front Door custom domain')
param domainName string
param subdomain string
param frontDoorEndpoint string
param validationToken string

// Reference to existing DNS Zone
resource dnsZone 'Microsoft.Network/dnsZones@2023-07-01-preview' existing = {
  name: domainName
}

// CNAME record pointing to Front Door endpoint
resource cnameRecord 'Microsoft.Network/dnsZones/CNAME@2023-07-01-preview' = {
  name: subdomain
  parent: dnsZone
  properties: {
    TTL: 300
    CNAMERecord: {
      cname: frontDoorEndpoint
    }
  }
}

// TXT record for domain validation
resource txtRecord 'Microsoft.Network/dnsZones/TXT@2023-07-01-preview' = {
  name: '_dnsauth.${subdomain}'
  parent: dnsZone
  properties: {
    TTL: 300
    TXTRecords: [
      {
        value: [validationToken]
      }
    ]
  }
}

// Outputs
output cnameRecordName string = subdomain
output cnameRecordValue string = frontDoorEndpoint
output txtRecordName string = '_dnsauth.${subdomain}'
output txtRecordValue string = validationToken