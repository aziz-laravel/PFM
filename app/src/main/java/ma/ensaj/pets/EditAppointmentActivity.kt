package ma.ensaj.pets

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ma.ensaj.pets.api.AppointmentApi
import ma.ensaj.pets.api.VeterinarianApi
import ma.ensaj.pets.config.RetrofitClient
import ma.ensaj.pets.dto.Appointment
import ma.ensaj.pets.dto.AppointmentStatus
import ma.ensaj.pets.dto.Veterinarian
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditAppointmentActivity : AppCompatActivity() {

    //private lateinit var vetIdInput: EditText
    private lateinit var vetSpinner: Spinner
    private lateinit var dateTimeInput: EditText
    private lateinit var reasonInput: EditText
    private lateinit var notesInput: EditText
    private lateinit var saveButton: Button
    private var currentAppointment: Appointment? = null
    private var selectedDateTime: LocalDateTime? = null

    private var appointmentId: Long = -1
    private var veterinarians: List<Veterinarian> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_appointment)

        //vetIdInput = findViewById(R.id.vetIdInput)
        vetSpinner = findViewById(R.id.vetSpinner)
        dateTimeInput = findViewById(R.id.dateTimeInput)
        reasonInput = findViewById(R.id.reasonInput)
        notesInput = findViewById(R.id.notesInput)
        saveButton = findViewById(R.id.saveAppointmentButton)

        appointmentId = intent.getLongExtra("APPOINTMENT_ID", -1)

        if (appointmentId != -1L) {
            loadAppointmentDetails(appointmentId)
        } else {
            Toast.makeText(this, "Invalid appointment ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupDateTimePicker() // Configurer le sélecteur de date/heure
        saveButton.setOnClickListener {
            updateAppointment()
        }
    }

    private fun setupDateTimePicker() {
        dateTimeInput.isFocusable = false
        dateTimeInput.isClickable = true

        dateTimeInput.setOnClickListener {
            showDateTimePicker()
        }
    }

    private fun showDateTimePicker() {
        val currentDateTime = selectedDateTime ?: LocalDateTime.now()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute, 0)
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        dateTimeInput.setText(selectedDateTime?.format(formatter))
                    },
                    currentDateTime.hour,
                    currentDateTime.minute,
                    true // Format 24h
                ).show()
            },
            currentDateTime.year,
            currentDateTime.monthValue - 1,
            currentDateTime.dayOfMonth
        )

        // Désactiver la sélection des dates passées
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun loadVeterinarians(city: String) {
        val vetApi = RetrofitClient.instance.create(VeterinarianApi::class.java)
        vetApi.getVeterinariansByCity(city).enqueue(object : Callback<List<Veterinarian>> {
            override fun onResponse(call: Call<List<Veterinarian>>, response: Response<List<Veterinarian>>) {
                if (response.isSuccessful && response.body() != null) {
                    veterinarians = response.body()!!
                    populateVetSpinner()
                } else {
                    Toast.makeText(this@EditAppointmentActivity, "Failed to load veterinarians", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Veterinarian>>, t: Throwable) {
                Toast.makeText(this@EditAppointmentActivity, "Error loading veterinarians", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populateVetSpinner() {
        val vetNames = mutableListOf("Please select a veterinarian")
        vetNames.addAll(veterinarians.map { it.lastName })
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, vetNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vetSpinner.adapter = adapter

        // Sélectionner le vétérinaire actuel dans le spinner
        currentAppointment?.veterinarian?.let {
            val index = veterinarians.indexOfFirst { vet -> vet.id == it.id }
            if (index != -1) {
                vetSpinner.setSelection(index + 1)  // L'index commence à 1 car le premier élément est "Please select..."
            }
        } ?: run {
            vetSpinner.setSelection(0)  // Désélectionner si aucun vétérinaire n'est choisi
        }
    }

    private fun loadAppointmentDetails(appointmentId: Long) {
        val appointmentApi = RetrofitClient.instance.create(AppointmentApi::class.java)
        appointmentApi.getAppointmentById(appointmentId).enqueue(object : Callback<Appointment> {
            override fun onResponse(call: Call<Appointment>, response: Response<Appointment>) {
                if (response.isSuccessful) {
                    val appointment = response.body()
                    if (appointment != null) {
                        currentAppointment = appointment // Sauvegarder l'appointment courant
                        populateFields(appointment)
                        loadVeterinarians("El Jadida")
                    } else {
                        Toast.makeText(
                            this@EditAppointmentActivity,
                            "Appointment not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<Appointment>, t: Throwable) {
                Toast.makeText(
                    this@EditAppointmentActivity,
                    "Failed to load appointment",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun populateFields(appointment: Appointment) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        //vetIdInput.setText(appointment.veterinarian?.id?.toString() ?: "")
        dateTimeInput.setText(appointment.appointmentDateTime.format(formatter)) // Formater la date
        reasonInput.setText(appointment.reason ?: "")
        notesInput.setText(appointment.notes ?: "")

        // Initialiser selectedDateTime avec la date de l'appointment actuel
        selectedDateTime = appointment.appointmentDateTime
    }

    private fun updateAppointment() {
        val selectedVetIndex = vetSpinner.selectedItemPosition
        if (selectedVetIndex == AdapterView.INVALID_POSITION) {
            Toast.makeText(this, "Please select a veterinarian", Toast.LENGTH_SHORT).show()
            return
        }

        val vetId = veterinarians[selectedVetIndex - 1].id
        val dateTime = selectedDateTime
        val reason = reasonInput.text.toString()
        val notes = notesInput.text.toString()

        if (vetId == null || dateTime == null) {
            Toast.makeText(this, "Invalid input data", Toast.LENGTH_SHORT).show()
            return
        }

        // Récupérer d'abord le Pet actuel depuis l'appointment existant
        val vetApi = RetrofitClient.instance.create(VeterinarianApi::class.java)

        currentAppointment?.pet?.let { currentPet ->
            vetApi.getVeterinarian(vetId).enqueue(object : Callback<Veterinarian> {
                override fun onResponse(call: Call<Veterinarian>, response: Response<Veterinarian>) {
                    if (response.isSuccessful && response.body() != null) {
                        val veterinarian = response.body()!!

                        val updatedAppointment = Appointment(
                            id = appointmentId,
                            pet = currentPet,
                            veterinarian = veterinarian,
                            appointmentDateTime = dateTime,
                            reason = reason,
                            notes = notes,
                            status = currentAppointment?.status ?: AppointmentStatus.SCHEDULED
                        )

                        val appointmentApi = RetrofitClient.instance.create(AppointmentApi::class.java)
                        appointmentApi.updateAppointment(appointmentId, updatedAppointment)
                            .enqueue(object : Callback<Appointment> {
                                override fun onResponse(
                                    call: Call<Appointment>,
                                    response: Response<Appointment>
                                ) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(
                                            this@EditAppointmentActivity,
                                            "Appointment updated successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@EditAppointmentActivity,
                                            "Failed to update appointment",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onFailure(call: Call<Appointment>, t: Throwable) {
                                    Toast.makeText(
                                        this@EditAppointmentActivity,
                                        "Error: ${t.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    } else {
                        Toast.makeText(
                            this@EditAppointmentActivity,
                            "Failed to get veterinarian details",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Veterinarian>, t: Throwable) {
                    Toast.makeText(
                        this@EditAppointmentActivity,
                        "Error getting veterinarian: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } ?: run {
            Toast.makeText(this, "Current appointment data is invalid", Toast.LENGTH_SHORT).show()
        }
    }
}
