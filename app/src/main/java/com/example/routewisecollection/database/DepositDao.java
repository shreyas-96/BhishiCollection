package com.example.routewisecollection.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.routewisecollection.models.Deposit;

import java.util.List;

@Dao
public interface DepositDao {

    // ➤ Insert new deposit
    @Insert
    long insert(Deposit deposit);

    // ➤ Update existing deposit
    @Update
    void update(Deposit deposit);

    // ➤ Delete a deposit
    @Delete
    void delete(Deposit deposit);

    // ➤ Get deposit by its ID
    @Query("SELECT * FROM deposits WHERE id = :depositId")
    LiveData<Deposit> getDepositById(int depositId);

    // ➤ Get all deposits by customer (latest first)
    @Query("SELECT * FROM deposits WHERE customerId = :customerId ORDER BY depositDate DESC, depositTime DESC")
    LiveData<List<Deposit>> getDepositsByCustomer(int customerId);

    // ➤ Get all deposits for a specific date
    @Query("SELECT * FROM deposits WHERE depositDate = :date ORDER BY depositTime DESC")
    LiveData<List<Deposit>> getDepositsByDate(String date);

    // ➤ Get deposits within a date range
    @Query("SELECT * FROM deposits WHERE depositDate BETWEEN :startDate AND :endDate ORDER BY depositDate DESC, depositTime DESC")
    LiveData<List<Deposit>> getDepositsByDateRange(String startDate, String endDate);

    // ➤ Get all deposits (latest first)
    @Query("SELECT * FROM deposits ORDER BY depositDate DESC, depositTime DESC")
    LiveData<List<Deposit>> getAllDeposits();

    // ➤ Get total amount deposited by a specific customer
    @Query("SELECT SUM(totalAmount) FROM deposits WHERE customerId = :customerId")
    LiveData<Double> getTotalDepositsByCustomer(int customerId);

    // ➤ Get total collection for a specific date
    @Query("SELECT SUM(totalAmount) FROM deposits WHERE depositDate = :date")
    LiveData<Double> getTotalCollectionByDate(String date);

    // ➤ Get total collection within a date range
    @Query("SELECT SUM(totalAmount) FROM deposits WHERE depositDate BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalCollectionByDateRange(String startDate, String endDate);

    // ➤ Find deposit by receipt number
    @Query("SELECT * FROM deposits WHERE receiptNumber = :receiptNumber")
    LiveData<Deposit> getDepositByReceiptNumber(String receiptNumber);
}
