package com.example.commerce.product.entity;

import lombok.Getter;

@Getter
public enum ProductCategory {
    VEGETABLE_FRUIT("농산물"),
    MEAT_EGG("축산물"),
    SEAFOOD("수산물"),
    MEAL_KIT("밀키트/간편식"),
    SIDE_DISH("밑반찬/김치"),
    DAIRY_DELI("유제품/델리"),
    BEVERAGE("생수/음료"),
    PANTRY("면/양념/오일"),
    SNACK_BAKERY("간식/베이커리"),
    HEALTH_FOOD("건강식품");

    private final String categoryTitle;

    ProductCategory(String categoryTitle){
        this.categoryTitle = categoryTitle;
    }
}
