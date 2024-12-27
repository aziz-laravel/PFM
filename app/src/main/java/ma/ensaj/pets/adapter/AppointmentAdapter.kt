package ma.ensaj.pets.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ma.ensaj.pets.R
import ma.ensaj.pets.dto.Appointment
import org.threeten.bp.format.DateTimeFormatter

class AppointmentAdapter(
    private val appointments: List<Appointment>,
    private val onEditClick: (Appointment) -> Unit,
    private val onDeleteClick: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    // Définir le formateur pour le format dd/MM/yyyy HH:mm
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]

        // Formater la date et l'heure avant de l'afficher
        holder.dateTimeTextView.text = appointment.appointmentDateTime.format(dateFormatter)
        holder.reasonTextView.text = appointment.reason

        holder.editButton.setOnClickListener {
            onEditClick(appointment)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(appointment)
        }
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTimeTextView: TextView = itemView.findViewById(R.id.textViewAppointmentDate)
        val reasonTextView: TextView = itemView.findViewById(R.id.textViewAppointmentReason)
        val editButton: Button = itemView.findViewById(R.id.btnEditAppointment)
        val deleteButton: Button = itemView.findViewById(R.id.btnDeleteAppointment)
    }
}
