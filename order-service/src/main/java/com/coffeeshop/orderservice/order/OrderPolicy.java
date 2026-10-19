package com.coffeeshop.orderservice.order;

import com.coffeeshop.orderservice.domain.actor.Actor;
import com.coffeeshop.orderservice.domain.actor.Role;

public final class OrderPolicy {
    private OrderPolicy(){

    }
    public static boolean canPlaceForSelf(Actor actor){
        return actor.has(Role.CUSTOMER);
    }
    public static boolean canPlaceWalkIn(Actor actor){
        return actor.has(Role.WAITER);
    }
    public static boolean canView(Actor actor, Order order) {
        if (actor.hasAny(Role.ADMIN, Role.WAITER)) {
            return true;
        }
        return actor.has(Role.CUSTOMER) && order.isOwnedBy(actor.userId());
    }

    public static boolean canCancel(Actor actor, Order order) {
        if (actor.hasAny(Role.ADMIN, Role.WAITER)) {
            return true;
        }
        return actor.has(Role.CUSTOMER) && order.isOwnedBy(actor.userId());
    }

    public static boolean canComplete(Actor actor) {
        return actor.has(Role.WAITER);
    }
}
