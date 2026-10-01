package com.example.protobase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.protobase.databinding.ItemPersonaBinding

class AlumnoAdapter(
    private var listaAlumnos: List<Alumno>,
    private val onEditClick: (Alumno) -> Unit,
    private val onDeleteClick: (Alumno) -> Unit
) : RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder>() {

    class AlumnoViewHolder(val binding: ItemPersonaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val binding = ItemPersonaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlumnoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        val alumno = listaAlumnos[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvNombre.text = alumno.nombre
            tvCuenta.text = context.getString(R.string.cuenta_format, alumno.cuenta)

            Glide.with(context)
                .load(alumno.imagen)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(ivFoto)

            btnEditar.setOnClickListener {
                onEditClick(alumno)
            }

            btnEliminar.setOnClickListener {
                onDeleteClick(alumno)
            }
        }
    }

    override fun getItemCount(): Int = listaAlumnos.size

    fun updateList(nuevaLista: List<Alumno>) {
        listaAlumnos = nuevaLista
        notifyDataSetChanged()
    }
}
