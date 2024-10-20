package com.example.myapplication;

import java.util.Date;

public class Food {
    int Id;
    String Name;
    Double Price;
    Date CreateAt;

    public Food(int id, String name, double price, Date createAt) {
        Id = id;
        Name = name;
        Price = price;
        CreateAt = createAt;

    }


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public Date getCreateAt() {
        return CreateAt;
    }

    public void setCreateAt(Date createAt) {
        CreateAt = createAt;
    }
}
