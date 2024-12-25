package ma.ensaj.pets.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ma.ensaj.pets.R
import ma.ensaj.pets.dto.Vaccination

class VaccinationAdapter(
    private val vaccinations: List<Vaccination>,
    private val onEditClick: (Vaccination) -> Unit,
    private val onDeleteClick: (Vaccination) -> Unit
) : RecyclerView.Adapter<VaccinationAdapter.VaccinationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaccinationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vaccination, parent, false)
        return VaccinationViewHolder(view)
    }

    override fun onBindViewHolder(holder: VaccinationViewHolder, position: Int) {
        val vaccination = vaccinations[position]
        holder.bind(vaccination)
    }

    override fun getItemCount() = vaccinations.size

    inner class VaccinationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val vaccinationName: TextView = itemView.findViewById(R.id.vaccinationName)
        private val vaccinationDate: TextView = itemView.findViewById(R.id.vaccinationDate)
        private val vaccinationNextDueDate: TextView = itemView.findViewById(R.id.vaccinationNextDueDate)
        private val vaccinationDescription: TextView = itemView.findViewById(R.id.vaccinationDescription)
        private val btnEditVaccination: Button = itemView.findViewById(R.id.btnEditVaccination)
        private val btnDeleteVaccination: Button = itemView.findViewById(R.id.btnDeleteVaccination)

        fun bind(vaccination: Vaccination) {
            vaccinationName.text = vaccination.name
            vaccinationDate.text = "Date Administered: ${vaccination.dateAdministered}"
            vaccinationNextDueDate.text = "Next Due Date: ${vaccination.nextDueDate}"
            vaccinationDescription.text = "Description: ${vaccination.description}"

            btnEditVaccination.setOnClickListener { onEditClick(vaccination) }
            btnDeleteVaccination.setOnClickListener { onDeleteClick(vaccination) }
        }
    }
}
