package com.noljo.nolzo.seat.entity;

public enum SectionPrice {
    SECTION_1("1구역", 150000),
    SECTION_2("2구역", 120000),
    SECTION_3("3구역", 120000),
    SECTION_4("4구역", 80000),
    SECTION_5("5구역", 80000);

    private final String name;
    private final int price;

    SectionPrice(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public static int getPriceBySection(int section) {
        return switch (section) {
            case 1 -> SECTION_1.getPrice();
            case 2, 3 -> SECTION_2.getPrice();
            case 4, 5 -> SECTION_4.getPrice();
            default -> throw new IllegalArgumentException("Invalid section number");
        };
    }
}
