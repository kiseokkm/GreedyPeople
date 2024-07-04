package com.sparta.greeypeople.menu.dto.response;

import com.sparta.greeypeople.menu.entity.Menu;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuResponseDto {

    private Long storeId;
    private String menuName;
    private int price;
    private Long likes;

    public MenuResponseDto(Long storeId, String menuName, Integer price) {
        this.storeId = storeId;
        this.menuName = menuName;
        this.price = price;
    }

    public MenuResponseDto(Menu menu) {
        this.storeId = menu.getStore().getId();
        this.menuName = menu.getMenuName();
        this.price = menu.getPrice();
        this.likes = menu.getMenuLikes();
    }
}
