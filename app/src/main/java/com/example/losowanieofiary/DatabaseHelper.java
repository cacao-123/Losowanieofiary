package com.example.losowanieofiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "Losowanie.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table Klasy(nazwa TEXT primary key, liczba_uczniow INTEGER)");
    }

    // Aktualizacja bazy danych
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists Klasy");
    }

    // Dodawanie klasy
    public Boolean insertData(String nazwa, int liczba_uczniow) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nazwa", nazwa);
        contentValues.put("liczba_uczniow", liczba_uczniow);
        long result = db.insert("Klasy", null, contentValues);
        return result != -1;
    }

    // Pobieranie wszystkich klas
    public Cursor getAllClasses() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select * from Klasy", null);
    }

    public Cursor getSelectedClass (String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select liczba_uczniow from Klasy where nazwa =  ?", new String[] { name });
    }
}