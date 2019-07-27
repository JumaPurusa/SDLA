package com.example.jay.sdla.Models;

public class DocumentFile extends AppFile {

    private int icon;

    public DocumentFile(String url, String name, String size, int icon) {
        super(url, name, size);
        this.icon = icon;
    }

    public int getIcon(){
        return icon;
    }

}