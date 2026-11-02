package com.coffeeshop.orderservice.domain.order;

import java.math.BigDecimal;
import java.util.UUID;

import com.coffeeshop.orderservice.domain.order.exception.InvalidOrderException;

public record OrderItem(
        UUID menuId,
        String menuName,
        int quantity,
        BigDecimal unitPrice,
        Station station
) {
    private static final int MAX_QUANTITY = 50;

    public OrderItem {
        if (menuId == null){
            throw new InvalidOrderException("MenuID wajib diisi");
        }
        if (menuName == null || menuName.isBlank()){
            throw new InvalidOrderException("MenuName wajib diisi");
        }
        if (quantity < 1 || quantity > MAX_QUANTITY){
            throw new InvalidOrderException("Quantity harus antara 1 hingga " + MAX_QUANTITY);
        }
        if (unitPrice == null || unitPrice.signum() < 0){
            throw new InvalidOrderException("UnitPrice tidak boleh kosong atau negatif");
        }
        if (station == null){
            throw new InvalidOrderException("Station wajib diisi");
        }
    }

    public BigDecimal subTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
