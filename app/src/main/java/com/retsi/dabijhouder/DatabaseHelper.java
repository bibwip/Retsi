package com.retsi.dabijhouder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Path;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "opdracht.db";


    public static final String TABLE_NAME = "opdracht_tabel";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "TYPE_OPDRACHT";
    public static final String COL_3 = "VAK";
    public static final String COL_4 = "TITEL";
    public static final String COL_5 = "DATUM";
    public static final String COL_6 = "BESCHRIJVING";

    public static final String TABLE_NAME1 = "vakken_tabel";
    public static final String COL1_ID = "ID";
    public static final String COL2_VAKKEN = "VAK";
    public static final String COL3_KLEUR = "KLEUR";



    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ("+COL_1+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_2+" TEXT, "+COL_3+" TEXT, "+COL_4+" TEXT, "+COL_5+" TEXT, "+COL_6+" TEXT)");
        db.execSQL("create table " + TABLE_NAME1 + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, VAK TEXT, KLEUR TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME1);
        onCreate(db);
    }

    public void updateOpdracht(String id, String type, String vak, String titel, String datum, String besschrijving) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, type);
        contentValues.put(COL_3, vak);
        contentValues.put(COL_4, titel);
        contentValues.put(COL_5, datum);
        contentValues.put(COL_6, besschrijving);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});
    }

    public boolean insertData(String typeOpdracht, String vak, String titel, String Datum, String beschrijving) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,typeOpdracht);
        contentValues.put(COL_3,vak);
        contentValues.put(COL_4,titel);
        contentValues.put(COL_5,Datum);
        contentValues.put(COL_6,beschrijving);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else return true;

    }

    public Cursor getAllRawData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
        return res;
    }

    public ArrayList<OpdrachtItem> getAllAssignments(Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);

        ArrayList<OpdrachtItem> items = new ArrayList<>();

        while (res.moveToNext()) {
            String typeOpdracht = res.getString(1);
            String vak = res.getString(2);
            String titel = res.getString(3);
            String datum = res.getString(4);
            String bescrhijving = res.getString(5);
            String typeKey = res.getString(1);

            switch (typeOpdracht) {
                case "Toets_key":
                    typeOpdracht = context.getString(R.string.Toets);
                    break;
                case "eindopdracht_key":
                    typeOpdracht = context.getString(R.string.Eindopdracht);
                    break;
                case "Huiswerk_key":
                    typeOpdracht = context.getString(R.string.Huiswerk);
                    break;
                case "overig_key":
                    typeOpdracht = context.getString(R.string.overig);
                    break;
                default:
                    break;
            }

            String[] SList = datum.split("-");
            int DatumKey = Integer.parseInt(SList[2] + SList[1] + SList[0]);
            OpdrachtItem opdracht = new OpdrachtItem(typeOpdracht, vak, titel, datum, bescrhijving, DatumKey, typeKey);
            items.add(opdracht);
        }

        Collections.sort(items, new Comparator<OpdrachtItem>() {
            @Override
            public int compare(OpdrachtItem opdrachtItem, OpdrachtItem t1) {
                return opdrachtItem.getDatumTagSorter().compareTo(t1.getDatumTagSorter());
            }
        });
        return items;
    }

    public boolean insertData(String vak, String kleur) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2_VAKKEN,vak);
        contentValues.put(COL3_KLEUR, kleur);
        long result = db.insert(TABLE_NAME1, null, contentValues);
        if (result == -1) {
            return false;
        } else return true;
    }

    public ArrayList<VakItem> getAllData2() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME1, null);
        ArrayList<VakItem> arrayList = new ArrayList<VakItem>();

        if (res.getCount() == 0) {
            return arrayList;
        }

        while (res.moveToNext()) {
            VakItem item = new VakItem(res.getString(1), res.getString(2));
            arrayList.add(item);
        }
        return arrayList;
    }

    public boolean updateVakkenData(String naam, String kleur) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2_VAKKEN, naam);
        contentValues.put(COL3_KLEUR, kleur);
        db.update(TABLE_NAME1, contentValues, "VAK = ?",new String[] {naam});
        return true;
    }

    public ArrayList<String> getVakkenNamen() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> data = new ArrayList<>();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME1, null);

        while (res.moveToNext()) {
            data.add(res.getString(res.getColumnIndex(COL2_VAKKEN)));
        }

        return data;
    }

    public void deleteVak(String naam, String kleur) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME1,"VAK=? and KLEUR=?",new String[]{naam,kleur});
    }

    public String getVakKleur(String naam) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME1+" WHERE TRIM(VAK) = '"+naam.trim()+"'", null);

        if (c.moveToNext()) {
            return c.getString(c.getColumnIndex(COL3_KLEUR));
        } else return "#FAECE2";
    }

    public void deleteOpdracht(String vak, String titel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,"VAK=? and TITEL=?",new String[]{vak,titel});
    }

    public int getId(String titel, String vak, String datum) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select * from " +
                        TABLE_NAME + " where " + COL_4 + " = ? AND " + COL_3 +
                        " = ? AND  " + COL_5 + " = ?",
                new String[] { titel, vak, datum});
        c.moveToNext();
        return c.getInt(c.getColumnIndex(COL_1));
    }

    public OpdrachtItem getOpdracht(int ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE ID = '"+ID+"'", null);
        c.moveToNext();
        OpdrachtItem item = new OpdrachtItem(c.getString(c.getColumnIndex(COL_2)),
                c.getString(c.getColumnIndex(COL_3)), c.getString(c.getColumnIndex(COL_4)),
                c.getString(c.getColumnIndex(COL_5)), c.getString(c.getColumnIndex(COL_6)));
        return item;
    }


}
