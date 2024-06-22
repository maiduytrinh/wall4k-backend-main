package com.tp.wallios.entity.reference;

public enum CategoryType {
    FOR_YOU("foryou", "For You"),
    LIVE("live", "Live Wallpapers"),
    DAILY_PICK("daily", "Daily Picks"),
    DEPTH_EFFECT("depth_effect", "Depth Effect"),
    CATEGORY("category", "");

    public final String type;
    public final String name;


    private CategoryType(String type, String name) {
        this.type = type;
        this.name = name;
    }
}
