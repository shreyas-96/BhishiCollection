package com.example.routewisecollection.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.routewisecollection.models.Route;

import java.util.List;

@Dao
public interface RouteDao {
    
    @Insert
    long insert(Route route);
    
    @Update
    void update(Route route);
    
    @Delete
    void delete(Route route);
    
    @Query("SELECT * FROM routes WHERE isActive = 1 ORDER BY routeName ASC")
    LiveData<List<Route>> getAllActiveRoutes();
    
    @Query("SELECT * FROM routes WHERE id = :routeId")
    LiveData<Route> getRouteById(int routeId);
    
    @Query("SELECT * FROM routes WHERE routeCode = :routeCode AND isActive = 1")
    Route getRouteByCode(String routeCode);
    
    @Query("UPDATE routes SET customerCount = :count WHERE id = :routeId")
    void updateCustomerCount(int routeId, int count);
    
    @Query("UPDATE routes SET isActive = 0 WHERE id = :routeId")
    void deactivateRoute(int routeId);
    
    @Query("SELECT COUNT(*) FROM routes WHERE isActive = 1")
    int getRouteCount();
    
    @Query("SELECT * FROM routes ORDER BY routeName ASC")
    LiveData<List<Route>> getAllRoutes();
    
    @Query("SELECT * FROM routes WHERE routeName LIKE :query OR routeCode LIKE :query ORDER BY routeName ASC")
    LiveData<List<Route>> searchRoutes(String query);
}
