package com.yjy.banker.bank.Persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Profile;

import java.util.UUID;

public class BankDatabase extends SQLiteOpenHelper implements IBankDatabase {
    private static final String DB_NAME = "bank";
    private static final int DB_VERSION = 1;

    public static final String TABLE_ACCOUNTS = "accounts";

    public static final String COLUMN_ACCOUNTS_ID = "_id";
    public static final String COLUMN_ACCOUNTS_BALANCE = "balance";
    private static final String COLUMN_ACCOUNTS_NAME = "name";
    private static final String COLUMN_ACCOUNTS_DESCRIPTION = "description";
    private static final String COLUMN_ACCOUNTS_PHOTO = "photo";

    private static final String SQL_CREATE_TABLE =
            "create table " + TABLE_ACCOUNTS + " (" +
                    COLUMN_ACCOUNTS_ID + " integer primary key autoincrement," +
                    COLUMN_ACCOUNTS_BALANCE + " integer," +
                    COLUMN_ACCOUNTS_NAME + " text," +
                    COLUMN_ACCOUNTS_DESCRIPTION + " text," +
                    COLUMN_ACCOUNTS_PHOTO + " text" +
                    ");";

    public BankDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void reset() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("drop table " + TABLE_ACCOUNTS);
            db.execSQL(SQL_CREATE_TABLE);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public boolean load(@NonNull IOnLoadListener onLoadListener) {
        String[] columns = new String[]{
                COLUMN_ACCOUNTS_ID,
                COLUMN_ACCOUNTS_BALANCE,
                COLUMN_ACCOUNTS_NAME,
                COLUMN_ACCOUNTS_DESCRIPTION,
                COLUMN_ACCOUNTS_PHOTO
        };
        Cursor cursor = getReadableDatabase().query(
                TABLE_ACCOUNTS,
                columns,
                null,
                null, null, null, null
        );

        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }

        do {
            if (!onLoadListener.onLoad(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ACCOUNTS_ID)),
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ACCOUNTS_BALANCE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_ACCOUNTS_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_ACCOUNTS_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_ACCOUNTS_PHOTO))
            )) {
                cursor.close();
                return false;
            }
        } while (cursor.moveToNext());

        cursor.close();
        return true;
    }

    @Override
    public AccountID insertAccount() {
        ContentValues initAccountContentValues = new ContentValues();
        initAccountContentValues.put(COLUMN_ACCOUNTS_BALANCE, 0);

        SQLiteDatabase writableDatabase = getWritableDatabase();
        long id =
                writableDatabase.insert(TABLE_ACCOUNTS, null, initAccountContentValues);
        writableDatabase.close();

        return new AccountID(id);
    }

    @Override
    public void updateBalance(AccountID id, int balance) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ACCOUNTS_BALANCE, balance);
        getWritableDatabase().update(
                TABLE_ACCOUNTS,
                contentValues,
                COLUMN_ACCOUNTS_ID + "=" + id.getID(),
                null);
    }

    @Override
    public long getBalance(AccountID id) {
        final String[] column = {
                COLUMN_ACCOUNTS_BALANCE
        };
        String selection = COLUMN_ACCOUNTS_ID + "=" + id.getID();
        Cursor cursor = getReadableDatabase().query(
                TABLE_ACCOUNTS,
                column,
                selection,
                null, null, null, null);

        long balance;
        if (cursor.moveToNext()) {
            balance = cursor.getInt(0);
        } else {
            balance = -1;
        }

        cursor.close();
        return balance;
    }

    @Override
    public void updateProfile(AccountID id, Profile profile) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ACCOUNTS_NAME, profile.getName());
        contentValues.put(COLUMN_ACCOUNTS_DESCRIPTION, profile.getDescription());

        UUID photoUUID = profile.getPhoto();
        if (photoUUID != null) {
            contentValues.put(COLUMN_ACCOUNTS_PHOTO, photoUUID.toString());
        }

        getWritableDatabase().update(TABLE_ACCOUNTS,
                contentValues,
                COLUMN_ACCOUNTS_ID + "=" + id.getID(),
                null);

    }

    @Override
    public Profile getProfile(AccountID id) {
        final String[] columns = {
                COLUMN_ACCOUNTS_NAME,
                COLUMN_ACCOUNTS_DESCRIPTION,
                COLUMN_ACCOUNTS_PHOTO,
        };

        Cursor cursor = getReadableDatabase().query(
                TABLE_ACCOUNTS,
                columns,
                COLUMN_ACCOUNTS_ID + "=" + id.getID(),
                null, null, null, null
        );

        Profile profile;
        if (cursor.moveToNext()) {
            profile = new Profile();
            profile.setName(cursor.getString(0));
            profile.setDescription(cursor.getString(1));

            try {
                profile.setPhoto(UUID.fromString(cursor.getString(2)));
            } catch (IllegalArgumentException e) {
                profile.setPhoto(null);
            }

        } else {
            profile = null;
        }

        cursor.close();
        return profile;
    }
}
