package ma.ensaj.pets.adapter

import ma.ensaj.pets.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ma.ensaj.pets.dto.Pet

class PetsAdapter(
    private var petsList: List<Pet>,
    private val onEditClick: (Pet) -> Unit,
    private val onDeleteClick: (Pet) -> Unit,
    private val onPetClick: (Pet) -> Unit // Ajout de la fonction pour gérer le clic sur l'animal
) : RecyclerView.Adapter<PetsAdapter.PetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pet, parent, false)
        return PetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = petsList[position]
        holder.bind(pet)
    }

    override fun getItemCount(): Int = petsList.size

    fun updatePetsList(newPetsList: List<Pet>) {
        petsList = newPetsList
        notifyDataSetChanged()
    }

    inner class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.petNameTextView)
        private val typeTextView: TextView = itemView.findViewById(R.id.petTypeTextView)
        private val editButton: Button = itemView.findViewById(R.id.editButton)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        fun bind(pet: Pet) {
            nameTextView.text = pet.name
            typeTextView.text = pet.species

            // Clic sur l'animal pour passer à l'activité des vaccinations
            itemView.setOnClickListener {
                onPetClick(pet) // Appelle la fonction qui passera à VaccinationsActivity
            }

            // Gérer le clic pour modifier un animal
            editButton.setOnClickListener {
                onEditClick(pet)
            }

            // Gérer le clic pour supprimer un animal
            deleteButton.setOnClickListener {
                onDeleteClick(pet)
            }
        }
    }
}
