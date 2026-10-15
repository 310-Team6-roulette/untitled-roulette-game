package io.wasabi.urg.ui;

public class ShopStock {

    private final int baseCards;
    private final int baseCharms;

    private int nextShopBonusCards = 0;
    private int nextShopBonusCharms = 0;

    public ShopStock(int baseCards, int baseCharms) {
        this.baseCards = baseCards;
        this.baseCharms = baseCharms;
    }

    public void addNextShopStock(int additionalCards, int additionalCharms) {
        nextShopBonusCards += additionalCards;
        nextShopBonusCharms += additionalCharms;
    }

    public int getNextCardCount() {
        return baseCards + nextShopBonusCards;
    }
    public int getNextCharmCount() {
        return baseCharms + nextShopBonusCharms;
    }

    public void consumeBonus() {
        nextShopBonusCards = 0;
        nextShopBonusCharms = 0;
    }
}
