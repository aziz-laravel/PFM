package ma.ensaj.pets.adapter

import ma.ensaj.pets.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ma.ensaj.pets.dto.Pet

class PetsAdapter(private var petsList: List<Pet>) : RecyclerView.Adapter<PetsAdapter.PetViewHolder>() {

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

    class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.petNameTextView)
        private val typeTextView: TextView = itemView.findViewById(R.id.petTypeTextView)

        fun bind(pet: Pet) {
            nameTextView.text = pet.name
            typeTextView.text = pet.species
        }
    }
}
