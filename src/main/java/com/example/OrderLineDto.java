package com.example;

import java.math.BigDecimal;

public class OrderLineDto {

    private Long id;
    private Long productId;
    private int quantity;
    private BigDecimal unitPriceNet;
    private BigDecimal lineVat;
    private BigDecimal lineTotalGross;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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