package ma.ensaj.pets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ma.ensaj.pets.adapter.PetsAdapter
import ma.ensaj.pets.api.PetApi
import ma.ensaj.pets.config.RetrofitClient
import ma.ensaj.pets.dto.Pet
import ma.ensaj.pets.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PetsActivity : AppCompatActivity() {
    private lateinit var petsRecyclerView: RecyclerView
    private lateinit var addPetButton: Button
    private lateinit var petsAdapter: PetsAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pets)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialiser le SessionManager
        sessionManager = SessionManager(this)
        val ownerId = sessionManager.fetchUserId()

        // Vérifier si l'utilisateur est connecté
        if (ownerId == -1L || sessionManager.fetchAuthToken() == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        Log.d("userid", "${ownerId}")

        setupViews()
        loadPets(ownerId)

    }

    private fun setupViews() {
        petsRecyclerView = findViewById(R.id.petsRecyclerView)
        addPetButton = findViewById(R.id.addPetButton)

        // Configuration du RecyclerView
        petsRecyclerView.layoutManager = LinearLayoutManager(this)
        petsAdapter = PetsAdapter(emptyList())
        petsRecyclerView.adapter = petsAdapter

        // Configuration du bouton d'ajout
        addPetButton.setOnClickListener {
            val intent = Intent(this, AddPetActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadPets(ownerId: Long) {
        val petApi = RetrofitClient.instance.create(PetApi::class.java)

        petApi.getPetsByOwnerId(ownerId).enqueue(object : Callback<List<Pet>> {
            override fun onResponse(call: Call<List<Pet>>, response: Response<List<Pet>>) {
                Log.d("PetsActivity", "Response code: ${response.code()}")
                Log.d("PetsActivity", "Response headers: ${response.headers()}")
                if (response.isSuccessful) {
                    val petsList = response.body()
                    if (petsList != null) {
                        petsAdapter.updatePetsList(petsList)
                    } else {
                        Toast.makeText(this@PetsActivity, "No pets found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Gérer les erreurs d'authentification
                    if (response.code() == 401) {
                        // Token expiré ou invalide
                        sessionManager.clearSession()
                        startActivity(Intent(this@PetsActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@PetsActivity,
                            "Failed to load pets: ${response.body()}, ${ownerId}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<Pet>>, t: Throwable) {
                Toast.makeText(
                    this@PetsActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // Option : Ajouter un menu pour la déconnexion
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        sessionManager.clearSession()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}