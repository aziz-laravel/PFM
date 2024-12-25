package ma.ensaj.pets.dto

import java.time.LocalDateTime

data class Appointment(
    val id: Long? = null,
    val pet: Pet,
    val veterinarian: Veterinarian,
    val appointmentDateTime: LocalDateTime,
    val reason: String? = null,
    val status: AppointmentStatus = AppointmentStatus.SCHEDULED,
    val notes: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
