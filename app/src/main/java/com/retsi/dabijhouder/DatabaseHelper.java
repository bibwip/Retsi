package com.retsi.dabijhouder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "opdracht.db";


    public static final String TABLE_NAME = "opdracht_tabel";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "TYPE_OPDRACHT";
    public static final String COL_3 = "VAK";
    public static final String COL_4 = "TITEL";
    public static final String COL_5 = "DATUM";
    public static final String COL_6 = "BESCHRIJVING";
    public static final String COL_7 = "BELANGRIJK";

    public static final String TABLE_NAME1 = "vakken_tabel";
    public static final String COL1_ID = "ID";
    public static final String COL2_VAKKEN = "VAK";
    public static final String COL3_KLEUR = "KLEUR";

    private ArrayList<ContentValues> table1data = new ArrayList<>();
    private ArrayList<ContentValues> table2data = new ArrayList<>();
    private boolean updated = false;


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ("+COL_1+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_2+" TEXT, "+COL_3+" TEXT, "+COL_4+" TEXT, "+COL_5+" TEXT, "+COL_6+" TEXT, "+COL_7+" BOOLEAN)");
        db.execSQL("create table " + TABLE_NAME1 + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, VAK TEXT, KLEUR TEXT)");

        if (updated){
            for (ContentValues data : table1data) {
                db.insert(TABLE_NAME, null, data);
            }
            for (ContentValues data : table2data) {
                db.insert(TABLE_NAME1, null, data);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion && oldVersion != 0){
            Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
            while (res.moveToNext()){
                ContentValues contentValues = new ContentValues();
                contentValues.put(COL_2, res.getString(res.getColumnIndex(COL_2)));
                contentValues.put(COL_3, res.getString(res.getColumnIndex(COL_3)));
                contentValues.put(COL_4, res.getString(res.getColumnIndex(COL_4)));
                contentValues.put(COL_5, res.getString(res.getColumnIndex(COL_5)));
                contentValues.put(COL_6, res.getString(res.getColumnIndex(COL_6)));
                contentValues.put(COL_7, 0);
                table1data.add(contentValues);
            }

            Cursor res2 = db.rawQuery("select * from "+TABLE_NAME1, null);
            while (res2.moveToNext()){
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put(COL2_VAKKEN, res2.getString(res2.getColumnIndex(COL2_VAKKEN)));
                contentValues2.put(COL3_KLEUR, res2.getString(res2.getColumnIndex(COL3_KLEUR)));
                table2data.add(contentValues2);
            }

            updated = true;
        }
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
        contentValues.put(COL_7, 0);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else return true;

    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
        return res;
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

    public void deleteOpdracht(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+TABLE_NAME+" where "+COL1_ID+"='"+id+"'");
    }

    public OpdrachtItem getOpdracht(int ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE ID = '"+ID+"'", null);
        c.moveToNext();
        OpdrachtItem item = new OpdrachtItem(c.getInt(c.getColumnIndex(COL_1)),
                c.getString(c.getColumnIndex(COL_2)),
                c.getString(c.getColumnIndex(COL_3)), c.getString(c.getColumnIndex(COL_4)),
                c.getString(c.getColumnIndex(COL_5)), c.getString(c.getColumnIndex(COL_6)),
                c.getInt(c.getColumnIndex(COL_7)),
                null, null);
        return item;
    }

    public void SetBelangerijk(int id, int waarde){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_7, waarde);
        db.update(TABLE_NAME, contentValues, "ID = '"+id+"'",new String[] {});
    }


}
