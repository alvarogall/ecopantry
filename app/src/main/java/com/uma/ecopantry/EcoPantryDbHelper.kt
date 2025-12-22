package com.uma.ecopantry

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class EcoPantryDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "EcoPantry.db"
        const val DATABASE_VERSION = 1

        const val TABLE_PRODUCTOS = "productos"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_CATEGORIA = "categoria"
        const val COLUMN_FECHA = "fecha_caducidad"
        const val COLUMN_ESTADO = "estado"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_PRODUCTOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_CATEGORIA TEXT,
                $COLUMN_FECHA INTEGER,
                $COLUMN_ESTADO INTEGER DEFAULT 0
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTOS")
        onCreate(db)
    }

    fun addProducto(producto: Producto): Long {
        val db = this.writableDatabase
        val values = android.content.ContentValues().apply {
            put(COLUMN_NOMBRE, producto.nombre)
            put(COLUMN_CATEGORIA, producto.categoria)
            put(COLUMN_FECHA, producto.fechaCaducidad)
            put(COLUMN_ESTADO, producto.estado)
        }
        val id = db.insert(TABLE_PRODUCTOS, null, values)
        db.close()
        return id
    }

    fun getAllProductosEnDespensa(): ArrayList<Producto> {
        val listaProductos = ArrayList<Producto>()
        val selectQuery = "SELECT * FROM $TABLE_PRODUCTOS WHERE $COLUMN_ESTADO = 0 ORDER BY $COLUMN_FECHA ASC"

        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val producto = Producto(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                    categoria = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIA)),
                    fechaCaducidad = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA)),
                    estado = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ESTADO))
                )
                listaProductos.add(producto)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listaProductos
    }

    fun deleteProducto(id: Int): Int {
        val db = this.writableDatabase
        val filasAfectadas = db.delete(TABLE_PRODUCTOS, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
        return filasAfectadas
    }

    fun updateEstadoProducto(id: Int, nuevoEstado: Int): Int {
        val db = this.writableDatabase
        val values = android.content.ContentValues()
        values.put(COLUMN_ESTADO, nuevoEstado)

        val filas = db.update(TABLE_PRODUCTOS, values, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
        return filas
    }

    fun getConteoPorEstado(): Pair<Int, Int> {
        val db = this.readableDatabase
        val cursorActivos = db.rawQuery("SELECT COUNT(*) FROM $TABLE_PRODUCTOS WHERE $COLUMN_ESTADO = 0", null)
        cursorActivos.moveToFirst()
        val activos = cursorActivos.getInt(0)
        cursorActivos.close()

        val cursorConsumidos = db.rawQuery("SELECT COUNT(*) FROM $TABLE_PRODUCTOS WHERE $COLUMN_ESTADO = 1", null)
        cursorConsumidos.moveToFirst()
        val consumidos = cursorConsumidos.getInt(0)
        cursorConsumidos.close()

        db.close()
        return Pair(activos, consumidos)
    }
}