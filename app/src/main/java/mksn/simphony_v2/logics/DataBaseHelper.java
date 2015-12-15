package mksn.simphony_v2.logics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Mike on 01.12.2015.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 16;
    private static final String DATABASE_NAME = "moneyManager.db";
    private static final String TABLE_TRANSACTIONS = "Transactions";
    private static final String TABLE_WALLETS = "Wallets";
    private static final String TABLE_CATEGORIES = "Categories";
    private static final String KEY_ID = "ID";
    private static final String KEY_ACT_DATE_DAY = "DATE_DAY";
    private static final String KEY_ACT_DATE_MONTH = "DATE_MONTH";
    private static final String KEY_ACT_DATE_YEAR = "DATE_YEAR";
    private static final String KEY_ACT_TYPE = "ACT_TYPE";
    private static final String KEY_CURRENCY = "CURRENCY";
    private static final String KEY_SUM = "SUM";
    private static final String KEY_WALLET = "WALLET_ID";
    private static final String KEY_CATEGORY = "CATEGORY_ID";
    private static final String KEY_WALLET_TYPE = "WALLET_TYPE";
    private static final String KEY_WALLET_NAME = "WALLET_NAME";
    private static final String KEY_WALLET_REMAINDER = "SUM_REMAINDER";
    private static final String KEY_CATEGORY_NAME = "CATEGORY_NAME";
    private static final String KEY_CATEGORY_ACT_COUNT = "ACT_COUNT";
    private static final String KEY_CATEGORY_ACT_SUM = "ACT_SUM";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_WALLETS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_WALLET_NAME + " TEXT,"
                + KEY_CURRENCY + " TEXT,"
                + KEY_WALLET_REMAINDER + " INTEGER,"
                + KEY_WALLET_TYPE + " INTEGER"
                + ")";
        db.execSQL(CREATE_TABLE);
        CREATE_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CATEGORY_NAME + " TEXT,"
                + KEY_ACT_TYPE + " TEXT,"
                + KEY_CATEGORY_ACT_COUNT + " INTEGER,"
                + KEY_CATEGORY_ACT_SUM + " INTEGER"
                + ")";
        db.execSQL(CREATE_TABLE);

        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, "Без категории");
        values.put(KEY_ACT_TYPE, 101);
        values.put(KEY_CATEGORY_ACT_COUNT, 0);
        values.put(KEY_CATEGORY_ACT_SUM, 0);
        db.insert(TABLE_CATEGORIES, null, values);

        values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, "Без категории");
        values.put(KEY_ACT_TYPE, 102);
        values.put(KEY_CATEGORY_ACT_COUNT, 0);
        values.put(KEY_CATEGORY_ACT_SUM, 0);
        db.insert(TABLE_CATEGORIES, null, values);

        CREATE_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_ACT_DATE_DAY + " INTEGER,"
                + KEY_ACT_DATE_MONTH + " INTEGER,"
                + KEY_ACT_DATE_YEAR + " INTEGER,"
                + KEY_SUM + " INTEGER,"
                + KEY_WALLET + " INTEGER NOT NULL, "
                + KEY_CATEGORY + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + KEY_WALLET + ") REFERENCES " + TABLE_WALLETS + "(" + KEY_ID + "), "
                + "FOREIGN KEY(" + KEY_CATEGORY + ") REFERENCES " + TABLE_CATEGORIES + "(" + KEY_ID + ")"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLETS);

        onCreate(db);
    }

    public int addWallet(String name, String currency, int sum_remainder, int type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_WALLET_NAME, name);
        values.put(KEY_CURRENCY, currency);
        values.put(KEY_WALLET_REMAINDER, sum_remainder);
        values.put(KEY_WALLET_TYPE, type);

        int result = (int) db.insert(TABLE_WALLETS, null, values);
        db.close();
        return result;
    }

    public int updateWallet(int id, String name, String currency, int sum_remainder, int type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_WALLET_NAME, name);
        values.put(KEY_CURRENCY, currency);
        values.put(KEY_WALLET_REMAINDER, sum_remainder);
        values.put(KEY_WALLET_TYPE, type);

        return db.update(TABLE_WALLETS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public void deleteWallet(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WALLETS, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public int addCategory(String name, int type, int actCount, int actSum) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, name);
        values.put(KEY_ACT_TYPE, type);
        values.put(KEY_CATEGORY_ACT_COUNT, actCount);
        values.put(KEY_CATEGORY_ACT_SUM, actSum);

        int result = (int) db.insert(TABLE_CATEGORIES, null, values);
        db.close();
        return result;
    }

    public int updateCategory(int id, String name, int type, int actCount, int actSum) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, name);
        values.put(KEY_ACT_TYPE, type);
        values.put(KEY_CATEGORY_ACT_COUNT, actCount);
        values.put(KEY_CATEGORY_ACT_SUM, actSum);

        return db.update(TABLE_CATEGORIES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public void deleteCategory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORIES, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public ArrayList<Category> getAllCategories(int type) {
        ArrayList<Category> categories = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " + KEY_ACT_TYPE + "= ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,
                new String[]{String.valueOf(type)});

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)),
                        Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)));
                categories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    public ArrayList<Wallet> getAllWallets() {
        ArrayList<Wallet> wallets = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_WALLETS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Wallet wallet = new Wallet(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                        Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)));
                wallets.add(0, wallet);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return wallets;
    }

    public void deleteAllWallets() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WALLETS, null, null);
        db.close();
    }

    public void deleteAllTransactions() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, null, null);
        db.close();
    }

    public void deleteAllCategories() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WALLETS, null, null);
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, "Без категории");
        values.put(KEY_ACT_TYPE, 101);
        values.put(KEY_CATEGORY_ACT_COUNT, 0);
        values.put(KEY_CATEGORY_ACT_SUM, 0);
        db.insert(TABLE_CATEGORIES, null, values);

        values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, "Без категории");
        values.put(KEY_ACT_TYPE, 102);
        values.put(KEY_CATEGORY_ACT_COUNT, 0);
        values.put(KEY_CATEGORY_ACT_SUM, 0);
        db.insert(TABLE_CATEGORIES, null, values);

        db.close();
    }

    public int addTransaction(String date, int transact_type, int sum, Wallet wallet, Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        String parsedDate[] = date.split("\\.");
        int day = Integer.parseInt(parsedDate[0]);
        int month = Integer.parseInt(parsedDate[1]);
        int year = Integer.parseInt(parsedDate[2]);
        ContentValues values = new ContentValues();
        values.put(KEY_ACT_DATE_DAY, day);
        values.put(KEY_ACT_DATE_MONTH, month);
        values.put(KEY_ACT_DATE_YEAR, year);
        values.put(KEY_SUM, sum);
        values.put(KEY_WALLET, wallet.getId());
        values.put(KEY_CATEGORY, category.getId());
        int result = (int) db.insert(TABLE_TRANSACTIONS, null, values);
        db.close();
        updateCategory(category.getId(),
                category.getName(),
                category.getCategoryType(),
                category.getActCount() + 1,
                category.getActSum() + sum);
        updateWallet(wallet.getId(),
                wallet.getName(),
                wallet.getCurrency(),
                (transact_type == AllData.ACT_OUTGO) ? (wallet.getSumRemainder() - sum)
                        : (wallet.getSumRemainder() + sum),
                wallet.getIconType());
        return result;
    }

    public void deleteTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, KEY_ID + " = ?", new String[]{String.valueOf(transaction.getId())});
        db.close();
        updateCategory(transaction.getCategory().getId(),
                transaction.getCategory().getName(),
                transaction.getCategory().getCategoryType(),
                transaction.getCategory().getActCount() - 1,
                transaction.getCategory().getActSum() - transaction.getSum());
        updateWallet(transaction.getWallet().getId(),
                transaction.getWallet().getName(),
                transaction.getWallet().getCurrency(),
                (transaction.getType() == AllData.ACT_OUTGO) ? (transaction.getWallet().getSumRemainder() + transaction.getSum())
                        : (transaction.getWallet().getSumRemainder() - transaction.getSum()),
                transaction.getWallet().getIconType());
    }

    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TRANSACTIONS + " ORDER BY "
                + KEY_ACT_DATE_YEAR + " DESC , "
                + KEY_ACT_DATE_MONTH + " DESC , "
                + KEY_ACT_DATE_DAY + " DESC , "
                + KEY_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(Integer.parseInt(cursor.getString(0)),
                        Integer.parseInt(cursor.getString(1)),
                        Integer.parseInt(cursor.getString(2)),
                        Integer.parseInt(cursor.getString(3)),
                        Integer.parseInt(cursor.getString(4)),
                        Integer.parseInt(cursor.getString(5)),
                        Integer.parseInt(cursor.getString(6)));
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transactions;
    }

    public void updateTransaction(Transaction newTransaction, Transaction oldTransaction) {
        if (newTransaction != oldTransaction) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            if (!Objects.equals(newTransaction.getFormattedDate(), oldTransaction.getFormattedDate())) {
                values.put(KEY_ACT_DATE_DAY, newTransaction.getDay());
                values.put(KEY_ACT_DATE_MONTH, newTransaction.getMonth());
                values.put(KEY_ACT_DATE_YEAR, newTransaction.getYear());
            } else {
                values.put(KEY_ACT_DATE_DAY, oldTransaction.getDay());
                values.put(KEY_ACT_DATE_MONTH, oldTransaction.getMonth());
                values.put(KEY_ACT_DATE_YEAR, oldTransaction.getYear());
            }
            boolean newSum = false;
            if (newTransaction.getSum() != oldTransaction.getSum()) {
                newSum = true;
                values.put(KEY_SUM, newTransaction.getSum());
            } else {
                values.put(KEY_SUM, oldTransaction.getSum());
            }
            boolean newWallet = false;
            if (newTransaction.getWallet() != oldTransaction.getWallet()) {
                newWallet = true;
                values.put(KEY_WALLET, newTransaction.getWallet().getId());
            } else {
                values.put(KEY_WALLET, oldTransaction.getWallet().getId());
            }
            boolean newCategory = false;
            if (newTransaction.getCategory() != oldTransaction.getCategory()) {
                newCategory = true;
                values.put(KEY_CATEGORY, newTransaction.getCategory().getId());
            } else {
                values.put(KEY_CATEGORY, oldTransaction.getCategory().getId());
            }
            db.update(TABLE_TRANSACTIONS, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(newTransaction.getId())});
            db.close();
            if (newCategory) {
                updateCategory(newTransaction.getCategory().getId(),
                        newTransaction.getCategory().getName(),
                        newTransaction.getCategory().getCategoryType(),
                        newTransaction.getCategory().getActCount() + 1,
                        (newSum) ? (newTransaction.getCategory().getActSum() + newTransaction.getSum())
                                : (newTransaction.getCategory().getActSum() + oldTransaction.getSum()));
                updateCategory(oldTransaction.getCategory().getId(),
                        oldTransaction.getCategory().getName(),
                        oldTransaction.getCategory().getCategoryType(),
                        oldTransaction.getCategory().getActCount() - 1,
                        oldTransaction.getCategory().getActSum() - oldTransaction.getSum());
            } else {
                if (newSum) {
                    updateCategory(oldTransaction.getCategory().getId(),
                            oldTransaction.getCategory().getName(),
                            oldTransaction.getCategory().getCategoryType(),
                            oldTransaction.getCategory().getActCount(),
                            oldTransaction.getCategory().getActSum() - oldTransaction.getSum() + newTransaction.getSum());
                }
            }
            if (newWallet) {
                int sumNew, sumOld;
                if (newTransaction.getType() == AllData.ACT_OUTGO) {
                    sumNew = newTransaction.getWallet().getSumRemainder() - newTransaction.getSum();
                } else {
                    sumNew = newTransaction.getWallet().getSumRemainder() + newTransaction.getSum();
                }
                if (oldTransaction.getType() == AllData.ACT_OUTGO) {
                    sumOld = oldTransaction.getWallet().getSumRemainder() + oldTransaction.getSum();
                } else {
                    sumOld = oldTransaction.getWallet().getSumRemainder() - oldTransaction.getSum();
                }
                updateWallet(newTransaction.getWallet().getId(),
                        newTransaction.getWallet().getName(),
                        newTransaction.getWallet().getCurrency(),
                        sumNew,
                        newTransaction.getWallet().getIconType());
                updateWallet(oldTransaction.getWallet().getId(),
                        oldTransaction.getWallet().getName(),
                        oldTransaction.getWallet().getCurrency(),
                        sumOld,
                        oldTransaction.getWallet().getIconType());
            } else {
                if (newSum || (newTransaction.getType() != oldTransaction.getType())) {
                    int sum = oldTransaction.getWallet().getSumRemainder();
                    if (newTransaction.getType() != oldTransaction.getType()) {
                        if (newTransaction.getType() == AllData.ACT_OUTGO) {
                            sum = sum - newTransaction.getSum() - oldTransaction.getSum();
                        } else {
                            sum = sum + newTransaction.getSum() + oldTransaction.getSum();
                        }
                    } else {
                        if (newTransaction.getType() == AllData.ACT_OUTGO) {
                            sum = sum - newTransaction.getSum() + oldTransaction.getSum();
                        } else {
                            sum = sum + newTransaction.getSum() - oldTransaction.getSum();
                        }
                    }

                    updateWallet(oldTransaction.getWallet().getId(),
                            oldTransaction.getWallet().getName(),
                            oldTransaction.getWallet().getCurrency(),
                            sum,
                            oldTransaction.getWallet().getIconType());
                }
            }
        }
    }
}
