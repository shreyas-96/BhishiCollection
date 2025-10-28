package com.example.routewisecollection.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.routewisecollection.models.Customer;
import com.example.routewisecollection.models.Deposit;
import com.example.routewisecollection.models.Route;

@Database(
        entities = {Customer.class, Deposit.class, Route.class},
        version = 4, // <- updated version
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    // DAO getters
    public abstract CustomerDao customerDao();
    public abstract DepositDao depositDao();
    public abstract RouteDao routeDao();

    // -------------------------
    // Migration 3 → 4
    // -------------------------
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Rename old table
            database.execSQL("ALTER TABLE deposits RENAME TO deposits_old;");

            // Create new table with consistent snake_case column names
            database.execSQL(
                    "CREATE TABLE deposits (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "customer_id INTEGER NOT NULL," +
                            "deposit_date TEXT," +
                            "deposit_time TEXT," +
                            "amount REAL," +
                            "interest_amount REAL," +
                            "total_amount REAL," +
                            "payment_method TEXT," +
                            "receipt_number TEXT," +
                            "notes TEXT," +
                            "customer_name TEXT" +
                            ");"
            );

            // Copy data from old table to new table
            database.execSQL(
                    "INSERT INTO deposits (id, customer_id, deposit_date, deposit_time, amount, interest_amount, total_amount, payment_method, receipt_number, notes, customer_name) " +
                            "SELECT id, customerId, depositDate, depositTime, amount, interestAmount, totalAmount, paymentMethod, receiptNumber, notes, customerName " +
                            "FROM deposits_old;"
            );

            // Drop old table
            database.execSQL("DROP TABLE deposits_old;");
        }
    };

    // -------------------------
    // Singleton instance
    // -------------------------
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "routewise_collection_db"
                            )
                            .addMigrations(MIGRATION_3_4)
                            .fallbackToDestructiveMigration() // optional safety
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
