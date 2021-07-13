package com.retsi.dabijhouder

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*
import kotlin.collections.ArrayList

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, 2) {
    private val table1data = ArrayList<ContentValues>()
    private val table2data = ArrayList<ContentValues>()
    private var updated = false
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table $TABLE_NAME ($COL_1 INTEGER PRIMARY KEY AUTOINCREMENT, $COL_2 TEXT, $COL_3 TEXT, $COL_4 TEXT, $COL_5 TEXT, $COL_6 TEXT, $COL_7 BOOLEAN)")
        db.execSQL("create table $TABLE_NAME1(ID INTEGER PRIMARY KEY AUTOINCREMENT, VAK TEXT, KLEUR TEXT)")
        if (updated) {
            for (data in table1data) {
                db.insert(TABLE_NAME, null, data)
            }
            for (data in table2data) {
                db.insert(TABLE_NAME1, null, data)
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion && oldVersion != 0) {
            val res = db.rawQuery("select * from $TABLE_NAME", null)
            while (res.moveToNext()) {
                val contentValues = ContentValues()
                contentValues.put(COL_2, res.getString(res.getColumnIndex(COL_2)))
                contentValues.put(COL_3, res.getString(res.getColumnIndex(COL_3)))
                contentValues.put(COL_4, res.getString(res.getColumnIndex(COL_4)))
                contentValues.put(COL_5, res.getString(res.getColumnIndex(COL_5)))
                contentValues.put(COL_6, res.getString(res.getColumnIndex(COL_6)))
                contentValues.put(COL_7, 0)
                table1data.add(contentValues)
            }
            val res2 = db.rawQuery("select * from $TABLE_NAME1", null)
            while (res2.moveToNext()) {
                val contentValues2 = ContentValues()
                contentValues2.put(COL2_VAKKEN, res2.getString(res2.getColumnIndex(COL2_VAKKEN)))
                contentValues2.put(COL3_KLEUR, res2.getString(res2.getColumnIndex(COL3_KLEUR)))
                table2data.add(contentValues2)
            }
            updated = true
        }
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME1")
        onCreate(db)
    }

    fun updateOpdracht(
        id: String,
        type: String?,
        vak: String?,
        titel: String?,
        datum: String?,
        besschrijving: String?
    ) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_2, type)
        contentValues.put(COL_3, vak)
        contentValues.put(COL_4, titel)
        contentValues.put(COL_5, datum)
        contentValues.put(COL_6, besschrijving)
        val affectedRows = db.update(TABLE_NAME, contentValues, "ID = ?", arrayOf(id))
        if (affectedRows == 0) {
            db.insert(TABLE_NAME, null, contentValues)
        }
        db.close()
    }

    fun insertData(
        typeOpdracht: String?,
        vak: String?,
        titel: String?,
        Datum: String?,
        beschrijving: String?
    ): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_2, typeOpdracht)
        contentValues.put(COL_3, vak)
        contentValues.put(COL_4, titel)
        contentValues.put(COL_5, Datum)
        contentValues.put(COL_6, beschrijving)
        contentValues.put(COL_7, 0)
        val result = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return result != -1L
    }

    fun opdrachten(): ArrayList<OpdrachtItem>{
            val db = this.writableDatabase
            val res = db.rawQuery("select * from " + TABLE_NAME, null)

            val items = ArrayList<OpdrachtItem>()

            while (res.moveToNext()) {
                val typeOpdracht = res.getString(1)
                val vak = res.getString(2)
                val titel = res.getString(3)
                val datum = res.getString(4)
                val bescrhijving = res.getString(5)
                val typeKey = res.getString(1)
                val itemId = res.getInt(0)
                val belangerijk = res.getInt(6)
                val sList = datum.split("-").toTypedArray()
                val datumKey = (sList[2] + sList[1] + sList[0]).toInt()
                val opdracht = OpdrachtItem(
                    itemId, typeOpdracht, vak, titel, datum,
                    bescrhijving, belangerijk, datumKey, typeKey
                )
                items.add(opdracht)
            }

        items.sortWith {opdrachtItem, t1 -> t1.belangerijk.compareTo(opdrachtItem.belangerijk)}
        items.sortWith { opdrachtItem, t1 ->
            if(opdrachtItem.belangerijk == t1.belangerijk){
                opdrachtItem.datumTagSorter!!.compareTo(t1.datumTagSorter!!)
            } else {
                0
            }
        }
        db.close()
        return items

    }

    fun allData(): Cursor {
            val db = this.writableDatabase
            return db.rawQuery("select * from $TABLE_NAME", null)
        }

    fun insertData(vak: String?, kleur: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL2_VAKKEN, vak)
        contentValues.put(COL3_KLEUR, kleur)
        val result = db.insert(TABLE_NAME1, null, contentValues)
        return result != -1L
    }

    fun allData2(): ArrayList<VakItem>{
            val db = this.writableDatabase
            val res = db.rawQuery("select * from " + TABLE_NAME1, null)
            val arrayList = ArrayList<VakItem>()
            if (res.count == 0) {
                return arrayList
            }
            while (res.moveToNext()) {
                val item = VakItem(res.getString(1), res.getString(2))
                arrayList.add(item)
            }
            return arrayList
        }

    fun updateVakkenData(naam: String, kleur: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL2_VAKKEN, naam)
        contentValues.put(COL3_KLEUR, kleur)
        db.update(TABLE_NAME1, contentValues, "VAK = ?", arrayOf(naam))
        return true
    }

    fun vakkenNamen(): ArrayList<String>{
            val db = this.writableDatabase
            val data = ArrayList<String>()
            val res = db.rawQuery("select * from " + TABLE_NAME1, null)
            while (res.moveToNext()) {
                data.add(res.getString(res.getColumnIndex(COL2_VAKKEN)))
            }
            return data
        }

    fun deleteVak(naam: String, kleur: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME1, "VAK=? and KLEUR=?", arrayOf(naam, kleur))
    }

    fun getVakKleur(naam: String): String {
        val db = this.writableDatabase
        val c =
            db.rawQuery("SELECT * FROM " + TABLE_NAME1 + " WHERE TRIM(VAK) = '" + naam.trim { it <= ' ' } + "'",
                null)
        return if (c.moveToNext()) {
            c.getString(c.getColumnIndex(COL3_KLEUR))
        } else "#FAECE2"
    }

    fun deleteOpdracht(id: Int) {
        val db = this.writableDatabase
        db.execSQL("delete from $TABLE_NAME where $COL1_ID='$id'")
    }

    fun getOpdracht(ID: Int): OpdrachtItem {
        val db = this.writableDatabase
        val c = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE ID = '$ID'",
            null
        )
        c.moveToNext()
        return OpdrachtItem(
            c.getInt(c.getColumnIndex(COL_1)),
            c.getString(c.getColumnIndex(COL_2)),
            c.getString(c.getColumnIndex(COL_3)), c.getString(c.getColumnIndex(COL_4)),
            c.getString(c.getColumnIndex(COL_5)), c.getString(c.getColumnIndex(COL_6)),
            c.getInt(c.getColumnIndex(COL_7)),
            null, null
        )
    }

    fun SetBelangerijk(id: Int, waarde: Int) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_7, waarde)
        db.update(TABLE_NAME, contentValues, "ID = '$id'", arrayOf())
    }

    fun replaceVakken(vakken : ArrayList<VakItem>) {
        val db = this.writableDatabase

        db.execSQL("delete from $TABLE_NAME1")

        for (vak in vakken){
            insertData(vak.vaknaam, vak.vakColor)
        }
    }

    companion object {
        const val DATABASE_NAME = "opdracht.db"
        const val TABLE_NAME = "opdracht_tabel"
        const val COL_1 = "ID"
        const val COL_2 = "TYPE_OPDRACHT"
        const val COL_3 = "VAK"
        const val COL_4 = "TITEL"
        const val COL_5 = "DATUM"
        const val COL_6 = "BESCHRIJVING"
        const val COL_7 = "BELANGRIJK"
        const val TABLE_NAME1 = "vakken_tabel"
        const val COL1_ID = "ID"
        const val COL2_VAKKEN = "VAK"
        const val COL3_KLEUR = "KLEUR"
    }
}