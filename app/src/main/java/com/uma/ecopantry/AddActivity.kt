package com.uma.ecopantry

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class AddActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etCategory: TextInputEditText
    private lateinit var btnDate: Button
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var selectedDateInMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        etName = findViewById(R.id.etName)
        etCategory = findViewById(R.id.etCategory)
        btnDate = findViewById(R.id.btnDate)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        btnDate.setOnClickListener { showDatePicker() }
        btnSave.setOnClickListener { saveProduct() }
        btnCancel.setOnClickListener { finish() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(year, month, dayOfMonth)
                newCalendar.set(Calendar.HOUR_OF_DAY, 0)
                newCalendar.set(Calendar.MINUTE, 0)
                newCalendar.set(Calendar.SECOND, 0)
                newCalendar.set(Calendar.MILLISECOND, 0)
                selectedDateInMillis = newCalendar.timeInMillis

                btnDate.text = getString(R.string.date_format, dayOfMonth, month + 1, year)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun saveProduct() {
        val name = etName.text.toString().trim()
        val category = etCategory.text.toString().trim()

        if (name.isEmpty()) {
            etName.error = getString(R.string.error_name_required)
            return
        }

        if (selectedDateInMillis == 0L) {
            Toast.makeText(this, getString(R.string.error_date_required), Toast.LENGTH_SHORT).show()
            return
        }

        val nuevoProducto = Producto(
            nombre = name,
            categoria = if (category.isNotEmpty()) category else getString(R.string.default_category),
            fechaCaducidad = selectedDateInMillis
        )

        val dbHelper = EcoPantryDbHelper(this)
        val resultado = dbHelper.addProducto(nuevoProducto)

        if (resultado > -1) {
            Toast.makeText(this, getString(R.string.msg_saved), Toast.LENGTH_SHORT).show()
            finish()
        } else {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title_error))
                .setMessage(getString(R.string.dialog_msg_save_error))
                .setPositiveButton(getString(R.string.dialog_btn_ok), null)
                .show()
        }
    }
}
