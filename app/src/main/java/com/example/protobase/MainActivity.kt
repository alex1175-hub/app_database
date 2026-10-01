package com.example.protobase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.protobase.databinding.ActivityMainBinding
import com.example.protobase.databinding.DialogAlumnoBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DBHelperAlumno
    private lateinit var adapter: AlumnoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelperAlumno(this)

        setupRecyclerView()
        cargarAlumnos()

        binding.fabAgregar.setOnClickListener {
            mostrarDialogoAlumno(alumnoAEditar = null)
        }
    }

    private fun setupRecyclerView() {
        adapter = AlumnoAdapter(
            listaAlumnos = emptyList(),
            onEditClick = { alumno ->
                mostrarDialogoAlumno(alumnoAEditar = alumno)
            },
            onDeleteClick = { alumno ->
                confirmarEliminarAlumno(alumno)
            }
        )

        binding.rvAlumnos.layoutManager = LinearLayoutManager(this)
        binding.rvAlumnos.adapter = adapter
    }

    private fun cargarAlumnos() {
        val lista = dbHelper.getAllAlumnos()
        adapter.updateList(lista)

        if (lista.isEmpty()) {
            binding.tvSinAlumnos.visibility = View.VISIBLE
            binding.rvAlumnos.visibility = View.GONE
        } else {
            binding.tvSinAlumnos.visibility = View.GONE
            binding.rvAlumnos.visibility = View.VISIBLE
        }
    }

    private fun mostrarDialogoAlumno(alumnoAEditar: Alumno?) {
        val dialogBinding = DialogAlumnoBinding.inflate(LayoutInflater.from(this))

        val titulo = if (alumnoAEditar != null) getString(R.string.editar_alumno) else getString(R.string.agregar_alumno)

        if (alumnoAEditar != null) {
            dialogBinding.etNombre.setText(alumnoAEditar.nombre)
            dialogBinding.etCuenta.setText(alumnoAEditar.cuenta)
            dialogBinding.etCorreo.setText(alumnoAEditar.correo)
            dialogBinding.etImagen.setText(alumnoAEditar.imagen)
        }

        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.guardar)) { dialog, _ ->
                val nombre = dialogBinding.etNombre.text.toString().trim()
                val cuenta = dialogBinding.etCuenta.text.toString().trim()
                val correo = dialogBinding.etCorreo.text.toString().trim()
                val imagen = dialogBinding.etImagen.text.toString().trim()

                if (nombre.isEmpty() || cuenta.isEmpty() || correo.isEmpty()) {
                    Toast.makeText(this, "Por favor completa Nombre, Cuenta y Correo", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val nuevoAlumno = Alumno(
                    nombre = nombre,
                    cuenta = cuenta,
                    correo = correo,
                    imagen = imagen
                )

                if (alumnoAEditar != null) {
                    val exito = dbHelper.updateAlumno(originalCuenta = alumnoAEditar.cuenta, alumno = nuevoAlumno)
                    if (exito) {
                        Toast.makeText(this, "Alumno actualizado correctamente", Toast.LENGTH_SHORT).show()
                        cargarAlumnos()
                    } else {
                        Toast.makeText(this, "Error al actualizar alumno", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val exito = dbHelper.insertAlumno(nuevoAlumno)
                    if (exito) {
                        Toast.makeText(this, "Alumno registrado correctamente", Toast.LENGTH_SHORT).show()
                        cargarAlumnos()
                    } else {
                        Toast.makeText(this, "Error al agregar alumno (cuenta duplicada)", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancelar)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun confirmarEliminarAlumno(alumno: Alumno) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.eliminar_alumno))
            .setMessage(getString(R.string.confirmar_eliminar_msg, alumno.nombre))
            .setPositiveButton(getString(R.string.eliminar)) { _, _ ->
                val exito = dbHelper.deleteAlumno(alumno.cuenta)
                if (exito) {
                    Toast.makeText(this, "Alumno eliminado correctamente", Toast.LENGTH_SHORT).show()
                    cargarAlumnos()
                } else {
                    Toast.makeText(this, "Error al eliminar alumno", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.cancelar), null)
            .show()
    }
}
