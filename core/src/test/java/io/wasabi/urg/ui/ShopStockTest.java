package io.wasabi.urg.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShopStockTest {

    @Test
    void hasNormalStockByDefault() {
        ShopStock stock = new ShopStock(4, 3);

        assertEquals(4, stock.getNextCardCount());
        assertEquals(3, stock.getNextCharmCount());
    }

    @Test
    void overstockBonusIncreasesNextShop() {
        ShopStock stock = new ShopStock(4, 3);

        stock.addNextShopStock(1, 2);

        assertEquals(5, stock.getNextCardCount());
        assertEquals(5, stock.getNextCharmCount());
    }

    @Test
    void overstockBonusOnlyAppliesOnce() {
        ShopStock stock = new ShopStock(4, 3);

        // Overstock Charm used
        stock.addNextShopStock(1, 2);

        // Next shop
        assertEquals(5, stock.getNextCardCount());
        assertEquals(5, stock.getNextCharmCount());

        // Shop consumes the bonus
        stock.consumeBonus();

        // Following shop is back to normal
        assertEquals(4, stock.getNextCardCount());
        assertEquals(3, stock.getNextCharmCount());
    }
}
