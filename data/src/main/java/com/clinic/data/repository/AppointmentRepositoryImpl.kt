package com.clinic.data.repository

import com.clinic.data.mapper.toAppointment
import com.clinic.data.mapper.toAppointmentMap
import com.clinic.data.mapper.toDoctor
import com.clinic.data.mapper.toPatient
import com.clinic.domain.model.Appointment
import com.clinic.domain.model.EnrichedAppointment
import com.clinic.domain.repository.AppointmentRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AppointmentRepository {

    private val appointmentsCollection = firestore.collection("appointments")
    private val patientsCollection = firestore.collection("patients")
    private val doctorsCollection = firestore.collection("doctors")

    override fun getAllAppointments(): Flow<List<EnrichedAppointment>> = callbackFlow {
        val listener = appointmentsCollection
            .orderBy("date", Query.Direction.DESCENDING)
            .orderBy("startTime", Query.Direction.ASC)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val appointments = snapshot?.documents?.mapNotNull { doc ->
                    doc.toAppointment()
                } ?: emptyList()

                // إثراء المواعيد ببيانات المرضى والأطباء
                enrichAppointments(appointments) { enrichedAppointments ->
                    trySend(enrichedAppointments)
                }
            }

        awaitClose { listener.remove() }
    }

    private suspend fun enrichAppointments(
        appointments: List<Appointment>,
        callback: (List<EnrichedAppointment>) -> Unit
    ) {
        try {
            val enrichedAppointments = appointments.mapNotNull { appointment ->
                val patient = patientsCollection.document(appointment.patientId).get().await().toPatient()
                val doctor = doctorsCollection.document(appointment.doctorId).get().await().toDoctor()

                if (patient != null && doctor != null) {
                    EnrichedAppointment(
                        appointment = appointment,
                        patient = patient,
                        doctor = doctor
                    )
                } else {
                    null
                }
            }
            callback(enrichedAppointments)
        } catch (e: Exception) {
            callback(emptyList())
        }
    }

    override suspend fun getAppointmentById(id: String): Appointment? {
        return try {
            val document = appointmentsCollection.document(id).get().await()
            document.toAppointment()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getAppointmentsByPatient(patientId: String): List<Appointment> {
        return try {
            appointmentsCollection
                .whereEqualTo("patientId", patientId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents
                .mapNotNull { it.toAppointment() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getAppointmentsByDoctor(doctorId: String): List<Appointment> {
        return try {
            appointmentsCollection
                .whereEqualTo("doctorId", doctorId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents
                .mapNotNull { it.toAppointment() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getAppointmentsByDate(date: String): List<Appointment> {
        return try {
            appointmentsCollection
                .whereEqualTo("date", date)
                .orderBy("startTime", Query.Direction.ASC)
                .get()
                .await()
                .documents
                .mapNotNull { it.toAppointment() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addAppointment(appointment: Appointment): Result<String> {
        return try {
            val appointmentWithId = if (appointment.id.isEmpty()) {
                appointment.copy(id = appointmentsCollection.document().id)
            } else {
                appointment
            }

            appointmentsCollection
                .document(appointmentWithId.id)
                .set(appointmentWithId.toAppointmentMap())
                .await()

            Result.success(appointmentWithId.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAppointment(appointment: Appointment): Result<Unit> {
        return try {
            val updatedAppointment = appointment.copy(updatedAt = System.currentTimeMillis())
            
            appointmentsCollection
                .document(appointment.id)
                .set(updatedAppointment.toAppointmentMap())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAppointment(id: String): Result<Unit> {
        return try {
            appointmentsCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchAppointments(query: String): List<EnrichedAppointment> {
        return try {
            // البحث في المواعيد بناءً على السبب
            val appointments = appointmentsCollection
                .orderBy("reason")
                .startAt(query.lowercase())
                .endAt(query.lowercase() + "\uf8ff")
                .get()
                .await()
                .documents
                .mapNotNull { it.toAppointment() }

            // إثراء النتائج
            appointments.mapNotNull { appointment ->
                val patient = patientsCollection.document(appointment.patientId).get().await().toPatient()
                val doctor = doctorsCollection.document(appointment.doctorId).get().await().toDoctor()

                if (patient != null && doctor != null) {
                    EnrichedAppointment(
                        appointment = appointment,
                        patient = patient,
                        doctor = doctor
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

