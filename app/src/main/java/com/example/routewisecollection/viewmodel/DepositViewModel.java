package com.example.routewisecollection.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.routewisecollection.database.AppDatabase;
import com.example.routewisecollection.database.CustomerDao;
import com.example.routewisecollection.database.DepositDao;
import com.example.routewisecollection.database.RouteDao;
import com.example.routewisecollection.models.Customer;
import com.example.routewisecollection.models.Deposit;
import com.example.routewisecollection.models.Route;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DepositViewModel extends AndroidViewModel {

    private final CustomerDao customerDao;
    private final DepositDao depositDao;
    private final RouteDao routeDao;
    private final ExecutorService executorService;

    public DepositViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        customerDao = database.customerDao();
        depositDao = database.depositDao();
        routeDao = database.routeDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Customer operations
    public void insertCustomer(Customer customer, OnInsertListener listener) {
        executorService.execute(() -> {
            long id = customerDao.insert(customer);
            if (listener != null) {
                listener.onInsertComplete(id);
            }
        });
    }

    public void updateCustomer(Customer customer) {
        executorService.execute(() -> customerDao.update(customer));
    }

    public void deleteCustomer(Customer customer) {
        executorService.execute(() -> customerDao.delete(customer));
    }

    public LiveData<Customer> getCustomerById(int customerId) {
        return customerDao.getCustomerById(customerId);
    }

    public LiveData<List<Customer>> getCustomersByRoute(int routeId) {
        return customerDao.getCustomersByRoute(routeId);
    }

    public LiveData<List<Customer>> getAllActiveCustomers() {
        return customerDao.getAllActiveCustomers();
    }

    public LiveData<List<Customer>> getAllCustomers() {
        return customerDao.getAllCustomers();
    }

    public LiveData<List<Customer>> searchCustomers(String query) {
        return customerDao.searchCustomers("%" + query + "%");
    }

    // Deposit operations
    public void insertDeposit(Deposit deposit, OnInsertListener listener) {
        executorService.execute(() -> {
            long id = depositDao.insert(deposit);
            if (listener != null) {
                listener.onInsertComplete(id);
            }
        });
    }

    public void updateDeposit(Deposit deposit) {
        executorService.execute(() -> depositDao.update(deposit));
    }

    public void deleteDeposit(Deposit deposit) {
        executorService.execute(() -> depositDao.delete(deposit));
    }

    public LiveData<Deposit> getDepositById(int depositId) {
        return depositDao.getDepositById(depositId);
    }

    public LiveData<List<Deposit>> getDepositsByCustomer(int customerId) {
        return depositDao.getDepositsByCustomer(customerId);
    }

    public LiveData<List<Deposit>> getDepositsByDate(String date) {
        return depositDao.getDepositsByDate(date);
    }

    public LiveData<List<Deposit>> getDepositsByDateRange(String startDate, String endDate) {
        return depositDao.getDepositsByDateRange(startDate, endDate);
    }

    public LiveData<List<Deposit>> getAllDeposits() {
        return depositDao.getAllDeposits();
    }

    public LiveData<Double> getTotalDepositsByCustomer(int customerId) {
        return depositDao.getTotalDepositsByCustomer(customerId);
    }

    public LiveData<Double> getTotalCollectionByDate(String date) {
        return depositDao.getTotalCollectionByDate(date);
    }

    public LiveData<Double> getTotalCollectionByDateRange(String startDate, String endDate) {
        return depositDao.getTotalCollectionByDateRange(startDate, endDate);
    }

    // Route operations
    public void insertRoute(Route route, OnInsertListener listener) {
        executorService.execute(() -> {
            long id = routeDao.insert(route);
            if (listener != null) {
                listener.onInsertComplete(id);
            }
        });
    }

    public void updateRoute(Route route) {
        executorService.execute(() -> routeDao.update(route));
    }

    public void deleteRoute(Route route) {
        executorService.execute(() -> routeDao.delete(route));
    }

    public LiveData<Route> getRouteById(int routeId) {
        return routeDao.getRouteById(routeId);
    }

    public LiveData<List<Route>> getAllActiveRoutes() {
        return routeDao.getAllActiveRoutes();
    }

    public LiveData<List<Route>> getAllRoutes() {
        return routeDao.getAllRoutes();
    }

    public LiveData<List<Route>> searchRoutes(String query) {
        return routeDao.searchRoutes("%" + query + "%");
    }

    public void updateRouteCustomerCount(int routeId) {
        customerDao.getCustomerCountByRoute(routeId).observeForever(count -> {
            if (count != null) {
                routeDao.updateCustomerCount(routeId, count);
            }
        });
    }

    // Simple route operations without callback
    public void insertRoute(Route route) {
        executorService.execute(() -> routeDao.insert(route));
    }

    public int getRouteCount() {
        try {
            return routeDao.getRouteCount();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }

    // Callback interface
    public interface OnInsertListener {
        void onInsertComplete(long id);
    }
}
