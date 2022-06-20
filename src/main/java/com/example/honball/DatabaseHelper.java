package com.example.honball;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "HONBALL.db";    // database name
    public static final String TABLE_NAME = "board_table";      // table name

    // table 항목
    public static final String COL_1 = "Number";
    public static final String COL_2 = "Title";
    public static final String COL_3 = "Content";
    public static final String COL_4 = "Attach";

    public DatabaseHelper(@Nullable Context context){
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(Title Text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,  int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // insert
    public boolean insertData(String title, String content, String attach){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, title);
        contentValues.put(COL_3, content);
        contentValues.put(COL_4, attach);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }
}


// 참고 https://jw0652.tistory.com/590
