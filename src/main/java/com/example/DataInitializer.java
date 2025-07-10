package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    public DataInitializer(CustomerRepository customerRepository, 
                          ProductRepository productRepository,
                          AddressRepository addressRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public void run(String... args) {
        // Initialize sample data only if tables are empty
        if (customerRepository.count() == 0) {
            initializeCustomers();
        }
        
        if (productRepository.count() == 0) {
            initializeProducts();
        }
    }

    private void initializeCustomers() {
        // Create sample customers
        Customer customer1 = new Customer();
        customer1.setName("John Doe");
        customer1.setEmail("john.doe@example.com");
        customer1.setPhone("+1-555-123-4567");
        customerRepository.save(customer1);

        Customer customer2 = new Customer();
        customer2.setName("Jane Smith");
        customer2.setEmail("jane.smith@example.com");
        customer2.setPhone("+1-555-987-6543");
        customerRepository.save(customer2);

        Customer customer3 = new Customer();
        customer3.setName("ACME Corporation");
        customer3.setEmail("contact@acme.corp");
        customer3.setPhone("+1-555-246-8135");
        customerRepository.save(customer3);

        // Add sample addresses
        Address address1 = new Address();
        address1.setStreet("123 Main Street");
        address1.setCity("New York");
        address1.setZip("10001");
        address1.setCountry("USA");
        address1.setCustomer(customer1);
        addressRepository.save(address1);

        Address address2 = new Address();
        address2.setStreet("456 Oak Avenue");
        address2.setCity("Los Angeles");
        address2.setZip("90210");
        address2.setCountry("USA");
        address2.setCustomer(customer2);
        addressRepository.save(address2);

        Address address3 = new Address();
        address3.setStreet("789 Corporate Blvd");
        address3.setCity("Chicago");
        address3.setZip("60601");
        address3.setCountry("USA");
        address3.setCustomer(customer3);
        addressRepository.save(address3);

        System.out.println("Initialized sample customers and addresses");
    }

    private void initializeProducts() {
        // Create sample products
        Product product1 = new Product();
        product1.setName("Professional Services");
        product1.setPriceNet(new BigDecimal("100.00"));
        product1.setVatRate(new BigDecimal("20.00"));
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Software License");
        product2.setPriceNet(new BigDecimal("299.99"));
        product2.setVatRate(new BigDecimal("20.00"));
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setName("Training Course");
        product3.setPriceNet(new BigDecimal("499.00"));
        product3.setVatRate(new BigDecimal("20.00"));
        productRepository.save(product3);

        Product product4 = new Product();
        product4.setName("Support Package");
        product4.setPriceNet(new BigDecimal("150.00"));
        product4.setVatRate(new BigDecimal("20.00"));
        productRepository.save(product4);

        Product product5 = new Product();
        product5.setName("Hardware Device");
        product5.setPriceNet(new BigDecimal("750.50"));
        product5.setVatRate(new BigDecimal("20.00"));
        productRepository.save(product5);

        System.out.println("Initialized sample products");
    }
}