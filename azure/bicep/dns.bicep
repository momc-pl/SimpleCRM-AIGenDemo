@description('DNS configuration for custom domain')
param domainName string
param subdomain string
param targetCname string

// Reference to existing DNS Zone
resource dnsZone 'Microsoft.Network/dnsZones@2023-07-01-preview' existing = {
  name: domainName
}

// CNAME record for the application
resource cnameRecord 'Microsoft.Network/dnsZones/CNAME@2023-07-01-preview' = {
  name: subdomain
  parent: dnsZone
  properties: {
    TTL: 300
    CNAMERecord: {
      cname: targetCname
    }
  }
}

// Outputs
output dnsRecordName string = subdomain
output dnsRecordValue string = targetCname