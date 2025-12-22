package com.uma.ecopantry

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StatsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val dbHelper = EcoPantryDbHelper(this)
        val tvActive = findViewById<TextView>(R.id.tvCountActive)
        val tvExpired = findViewById<TextView>(R.id.tvCountExpired)
        val tvConsumed = findViewById<TextView>(R.id.tvCountConsumed)

        val conteos = dbHelper.getConteoPorEstado()
        val totalActivosEnBBDD = conteos.first
        val totalConsumidos = conteos.second

        val listaProductos = dbHelper.getAllProductosEnDespensa()

        val hoy = System.currentTimeMillis()
        var contCaducados = 0

        for (p in listaProductos) {
            val diferencia = p.fechaCaducidad - hoy
            val dias = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diferencia)

            if (dias < 0) {
                contCaducados++
            }
        }

        val contFrescos = totalActivosEnBBDD - contCaducados

        tvActive.text = contFrescos.toString()
        tvExpired.text = contCaducados.toString()
        tvConsumed.text = totalConsumidos.toString()
    }
}