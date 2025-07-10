package com.example;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_lines")
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    @Column(name = "unit_price_net")
    private BigDecimal unitPriceNet;

    @Column(name = "line_vat")
    private BigDecimal lineVat;

    @Column(name = "line_total_gross")
    private BigDecimal lineTotalGross;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPriceNet() {
        return unitPriceNet;
    }

    public void setUnitPriceNet(BigDecimal unitPriceNet) {
        this.unitPriceNet = unitPriceNet;
    }

    public BigDecimal getLineVat() {
        return lineVat;
    }

    public void setLineVat(BigDecimal lineVat) {
        this.lineVat = lineVat;
    }

    public BigDecimal getLineTotalGross() {
        return lineTotalGross;
    }

    public void setLineTotalGross(BigDecimal lineTotalGross) {
        this.lineTotalGross = lineTotalGross;
    }
}