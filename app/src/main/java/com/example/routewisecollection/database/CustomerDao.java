package com.example.routewisecollection.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.routewisecollection.models.Customer;

import java.util.List;

@Dao
public interface CustomerDao {
    
    @Insert
    long insert(Customer customer);
    
    @Update
    void update(Customer customer);
    
    @Delete
    void delete(Customer customer);
    
    @Query("SELECT * FROM customers WHERE id = :customerId")
    LiveData<Customer> getCustomerById(int customerId);
    
    @Query("SELECT * FROM customers WHERE route_id = :routeId AND is_active = 1 ORDER BY name ASC")
    LiveData<List<Customer>> getCustomersByRoute(int routeId);
    
    @Query("SELECT * FROM customers WHERE is_active = 1 ORDER BY name ASC")
    LiveData<List<Customer>> getAllActiveCustomers();
    
    @Query("SELECT * FROM customers ORDER BY name ASC")
    LiveData<List<Customer>> getAllCustomers();
    
    @Query("SELECT * FROM customers WHERE name LIKE '%' || :searchQuery || '%' OR account_number LIKE '%' || :searchQuery || '%'")
    LiveData<List<Customer>> searchCustomers(String searchQuery);
    
    @Query("SELECT COUNT(*) FROM customers WHERE route_id = :routeId AND is_active = 1")
    LiveData<Integer> getCustomerCountByRoute(int routeId);
    
    @Query("UPDATE customers SET is_active = 0 WHERE id = :customerId")
    void deactivateCustomer(int customerId);
}
