package com.example.myprojectnew;

public class ItemList {

    private String itemName;
    private int itemImage;

    public ItemList(String itemName,int itemImage){
        this.itemImage=itemImage;
        this.itemName=itemName;
    }
    public String getItemName(){
        return itemName;
    }

    public int getItemImage() {
        return itemImage;
    }
}
