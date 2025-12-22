package com.uma.ecopantry

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ProductoAdapter(
    private var listaProductos: List<Producto>,
    private val onClick: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvCategoria: TextView = view.findViewById(R.id.tvCategoria)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val cardView: CardView = view.findViewById(R.id.cardView)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = listaProductos[position]

        holder.tvNombre.text = producto.nombre
        holder.tvCategoria.text = producto.categoria

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.tvFecha.text = sdf.format(Date(producto.fechaCaducidad))

        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val hoy = calendar.timeInMillis

        val diferenciaMilis = producto.fechaCaducidad - hoy
        val diasRestantes = TimeUnit.MILLISECONDS.toDays(diferenciaMilis)

        val context = holder.itemView.context

        when {
            diasRestantes < 0 -> {
                holder.cardView.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(context, R.color.status_expired))
                holder.tvEstado.text = context.getString(R.string.status_expired)
                holder.tvEstado.setTextColor(android.graphics.Color.RED)
            }
            diasRestantes == 0L -> {
                holder.cardView.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(context, R.color.status_urgent))
                holder.tvEstado.text = context.getString(R.string.status_today)
                holder.tvEstado.setTextColor(android.graphics.Color.parseColor("#FBC02D"))
            }
            diasRestantes < 3 -> {
                holder.cardView.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(context, R.color.status_urgent))
                holder.tvEstado.text = context.getString(R.string.status_urgent, diasRestantes)
                holder.tvEstado.setTextColor(android.graphics.Color.parseColor("#FBC02D"))
            }
            else -> {
                holder.cardView.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(context, R.color.status_fresh))
                holder.tvEstado.text = context.getString(R.string.status_fresh, diasRestantes)
                holder.tvEstado.setTextColor(android.graphics.Color.parseColor("#388E3C"))
            }
        }

        holder.itemView.setOnClickListener {
            onClick(producto)
        }
    }

    override fun getItemCount(): Int = listaProductos.size

    fun actualizarLista(nuevaLista: List<Producto>) {
        listaProductos = nuevaLista
        notifyDataSetChanged()
    }
}