package com.example.vasu.aismap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.Double2;

/**
 * Created by Mitch on 2016-05-13.
 */
public class MachineDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "machinelist.db";
    public static final String TABLE_NAME = "machinelist_data";
    public static final String COL1 = "ID";
    public static final String COL2 = "LATITUDE";
    public static final String COL3 = "LONGITUDE";


    public MachineDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table machinelist_data " +
                "(id integer primary key AUTOINCREMENT,LONGITUDE text,LATITUDE text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(Double item0, Double item1) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, item0);
        contentValues.put(COL3, item1);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public Cursor getListContents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return data;
    }

    public void deleteData () {
        SQLiteDatabase db = this.getWritableDatabase();
        //  return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
        db.execSQL("delete from "+ TABLE_NAME);
    }

    public Integer deleteDataFromId (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "NAME = ?",new String[] {id});
    }

    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }

}