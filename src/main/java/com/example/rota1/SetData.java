package com.example.rota1;

public class SetData {
    String title,description;
    int image;
    SetData(String title,String description,int image){
        this.title = title;
        this.description = description;
        this.image = image;
    }
    public String getTitle(){
        return this.title;
    }
    public String getDescription(){
        return this.description;
        }
    public int getImage(){
        return image;
    }
}
