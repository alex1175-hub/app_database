package com.example.protobase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelperAlumno(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Alumnos.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "Alumnos"
        const val COLUMN_CUENTA = "cuenta"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_CORREO = "correo"
        const val COLUMN_IMAGEN = "imagen"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_CUENTA TEXT PRIMARY KEY, " +
                "$COLUMN_NOMBRE TEXT, " +
                "$COLUMN_CORREO TEXT, " +
                "$COLUMN_IMAGEN TEXT)")
        db.execSQL(createTableQuery)

        // Datos iniciales de ejemplo
        val sampleStudents = listOf(
            Alumno("Carlos Mendoza", "20230001", "carlos.mendoza@alumnos.edu", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200"),
            Alumno("Ana Gutiérrez", "20230002", "ana.gutierrez@alumnos.edu", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200"),
            Alumno("Roberto Sánchez", "20230003", "roberto.sanchez@alumnos.edu", "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=200")
        )

        for (alumno in sampleStudents) {
            val values = ContentValues().apply {
                put(COLUMN_CUENTA, alumno.cuenta)
                put(COLUMN_NOMBRE, alumno.nombre)
                put(COLUMN_CORREO, alumno.correo)
                put(COLUMN_IMAGEN, alumno.imagen)
            }
            db.insert(TABLE_NAME, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertAlumno(alumno: Alumno): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CUENTA, alumno.cuenta)
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CORREO, alumno.correo)
            put(COLUMN_IMAGEN, alumno.imagen)
        }
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result != -1L
    }

    fun getAllAlumnos(): List<Alumno> {
        val listaAlumnos = mutableListOf<Alumno>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val cuenta = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CUENTA))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE))
                val correo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CORREO))
                val imagen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN))

                listaAlumnos.add(Alumno(nombre = nombre, cuenta = cuenta, correo = correo, imagen = imagen))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listaAlumnos
    }

    fun updateAlumno(originalCuenta: String, alumno: Alumno): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CUENTA, alumno.cuenta)
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CORREO, alumno.correo)
            put(COLUMN_IMAGEN, alumno.imagen)
        }
        val result = db.update(TABLE_NAME, values, "$COLUMN_CUENTA = ?", arrayOf(originalCuenta))
        db.close()
        return result > 0
    }

    fun deleteAlumno(cuenta: String): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_CUENTA = ?", arrayOf(cuenta))
        db.close()
        return result > 0
    }
}
