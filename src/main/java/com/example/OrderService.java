package com.example;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Collections;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public void save(OrderDto orderDto) {
        Order order = new Order();
        if (orderDto.getCustomerId() != null) {
            customerRepository.findById(orderDto.getCustomerId()).ifPresent(order::setCustomer);
        }

        // Associate the current authenticated user with the order
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String userEmail = oauth2User.getAttribute("email");
            System.out.println("Creating order for user email: " + userEmail);
            
            Optional<User> user = userRepository.findByEmail(userEmail);
            if (user.isPresent()) {
                System.out.println("Found user: " + user.get().getName() + ", associating with order");
                order.setUser(user.get());
            } else {
                System.out.println("WARNING: No user found with email: " + userEmail + " - order will not be associated with any user");
            }
        }
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString());
        order.setCreatedAt(LocalDateTime.now());

        List<OrderLine> orderLines = new ArrayList<>();
        BigDecimal totalNet = BigDecimal.ZERO;
        BigDecimal totalVat = BigDecimal.ZERO;

        if (orderDto.getOrderLines() != null) {
            for (OrderLineDto lineDto : orderDto.getOrderLines()) {
                if (lineDto.getProductId() == null || lineDto.getQuantity() <= 0) {
                    continue;
                }
                OrderLine line = new OrderLine();
                Product product = productRepository.findById(lineDto.getProductId()).orElseThrow();
                line.setProduct(product);
                line.setQuantity(lineDto.getQuantity());
                line.setUnitPriceNet(product.getPriceNet());

                BigDecimal lineTotalNet = product.getPriceNet().multiply(BigDecimal.valueOf(lineDto.getQuantity()));
                BigDecimal vatRate = product.getVatRate().divide(BigDecimal.valueOf(100));
                BigDecimal lineVat = lineTotalNet.multiply(vatRate);
                BigDecimal lineTotalGross = lineTotalNet.add(lineVat);

                line.setLineVat(lineVat);
                line.setLineTotalGross(lineTotalGross);
                line.setOrder(order);
                orderLines.add(line);

                totalNet = totalNet.add(lineTotalNet);
                totalVat = totalVat.add(lineVat);
            }
        }

        order.setOrderLines(orderLines);
        order.setTotalNet(totalNet);
        order.setTotalVat(totalVat);
        order.setTotalGross(totalNet.add(totalVat));

        orderRepository.save(order);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public List<Order> findOrdersByCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String userEmail = oauth2User.getAttribute("email");
            System.out.println("Looking for orders for user email: " + userEmail);
            
            Optional<User> user = userRepository.findByEmail(userEmail);
            if (user.isPresent()) {
                System.out.println("Found user: " + user.get().getName() + " with " + user.get().getOrders().size() + " orders");
                return user.get().getOrders();
            } else {
                System.out.println("No user found with email: " + userEmail);
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    public Order findById(Long id) {
        System.out.println("Finding order by ID: " + id);
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            System.out.println("Found order: " + order.get().getOrderNumber());
            // Force load orderLines to avoid lazy loading issues
            order.get().getOrderLines().size();
            System.out.println("Order has " + order.get().getOrderLines().size() + " order lines");
            return order.get();
        } else {
            System.out.println("Order not found with ID: " + id);
            return null;
        }
    }

    public List<Order> findOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }
}