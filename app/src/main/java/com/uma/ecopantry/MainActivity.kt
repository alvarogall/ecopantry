package com.uma.ecopantry

import android.view.Menu
import android.view.MenuItem
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var tvEmptyState: TextView
    private lateinit var adapter: ProductoAdapter
    private lateinit var dbHelper: EcoPantryDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = EcoPantryDbHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        fabAdd = findViewById(R.id.fabAdd)
        tvEmptyState = findViewById(R.id.tvEmptyState)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ProductoAdapter(emptyList()) { producto ->
            mostrarDialogoAcciones(producto)
        }
        recyclerView.adapter = adapter

        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        cargarProductos()
    }

    private fun cargarProductos() {
        val lista = dbHelper.getAllProductosEnDespensa()
        if (lista.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            adapter.actualizarLista(lista)
        }
    }

    private fun mostrarDialogoAcciones(producto: Producto) {
        AlertDialog.Builder(this)
            .setTitle(producto.nombre)
            .setMessage(getString(R.string.dialog_msg_action))

            .setPositiveButton(getString(R.string.action_consume)) { _, _ ->
                consumirProducto(producto)
            }

            .setNegativeButton(getString(R.string.action_delete)) { _, _ ->
                borrarProducto(producto)
            }

            .setNeutralButton(getString(R.string.btn_cancel), null)
            .create()
            .show()
    }

    private fun consumirProducto(producto: Producto) {
        dbHelper.updateEstadoProducto(producto.id, 1)
        cargarProductos()
        Toast.makeText(this, getString(R.string.msg_consumed), Toast.LENGTH_SHORT).show()
    }

    private fun borrarProducto(producto: Producto) {
        dbHelper.deleteProducto(producto.id)
        cargarProductos()

        Snackbar.make(recyclerView, getString(R.string.msg_deleted), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.action_undo)) {
                dbHelper.addProducto(producto)
                cargarProductos()
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == R.id.action_stats) {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}