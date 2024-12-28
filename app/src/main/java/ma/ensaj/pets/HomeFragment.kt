package ma.ensaj.pets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate le layout pour ce fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialisation des vues avec findViewById
        button1 = view.findViewById(R.id.button)
        button2 = view.findViewById(R.id.button2)
        button3 = view.findViewById(R.id.button3)
        button4 = view.findViewById(R.id.button4)
        bottomNavigationView = view.findViewById(R.id.bottomNavigationView)

        // Gestion des clics sur les boutons
        button1.setOnClickListener { openActivity(PetsVaccinationsActivity::class.java) }
        button2.setOnClickListener { openActivity(AddPetActivity::class.java) }
        button3.setOnClickListener { openActivity(PetsVaccinationsActivity::class.java) }
        button4.setOnClickListener { openActivity(PetsMedicationsActivity::class.java) }

        // Gestion des clics sur la barre de navigation
        bottomNavigationView.setOnItemSelectedListener { item ->
            (activity as HomeActivity).onNavigationItemSelected(item)
        }
    }

    // Méthode pour ouvrir une activité
    private fun openActivity(activityClass: Class<*>) {
        val intent = Intent(activity, activityClass)
        startActivity(intent)
    }
}