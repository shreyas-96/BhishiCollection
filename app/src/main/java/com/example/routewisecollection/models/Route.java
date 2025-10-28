package com.example.routewisecollection.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "routes")
public class Route {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String routeName;
    private String routeCode;
    private String description;
    private int customerCount;
    private boolean isActive;

    // No-arg constructor
    public Route() {
        this.isActive = true;
        this.customerCount = 0;
    }

    // Existing constructors
    public Route(String routeName, String routeCode, String description) {
        this.routeName = routeName;
        this.routeCode = routeCode;
        this.description = description;
        this.isActive = true;
        this.customerCount = 0;
    }

    public Route(String routeName, String routeCode, int customerCount) {
        this.routeName = routeName;
        this.routeCode = routeCode;
        this.customerCount = customerCount;
        this.isActive = true;
    }

    // ✅ New constructor for (id, routeName)
    public Route(int id, String routeName) {
        this.id = id;
        this.routeName = routeName;
        this.isActive = true;
        this.customerCount = 0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCustomerCount() { return customerCount; }
    public void setCustomerCount(int customerCount) { this.customerCount = customerCount; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
