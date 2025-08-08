package com.clinic.data.repository

import com.clinic.data.mapper.toPatient
import com.clinic.data.mapper.toPatientMap
import com.clinic.domain.model.Patient
import com.clinic.domain.repository.PatientRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PatientRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PatientRepository {

    private val patientsCollection = firestore.collection("patients")

    override fun getAllPatients(): Flow<List<Patient>> = callbackFlow {
        val listener = patientsCollection
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val patients = snapshot?.documents?.mapNotNull { doc ->
                    doc.toPatient()
                } ?: emptyList()

                trySend(patients)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getPatientById(id: String): Patient? {
        return try {
            val document = patientsCollection.document(id).get().await()
            document.toPatient()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun searchPatients(query: String): List<Patient> {
        return try {
            val queryLower = query.lowercase()
            
            // البحث بالاسم
            val nameResults = patientsCollection
                .orderBy("name")
                .startAt(queryLower)
                .endAt(queryLower + "\uf8ff")
                .get()
                .await()
                .documents
                .mapNotNull { it.toPatient() }

            // البحث برقم الهاتف
            val phoneResults = patientsCollection
                .whereGreaterThanOrEqualTo("phone", query)
                .whereLessThanOrEqualTo("phone", query + "\uf8ff")
                .get()
                .await()
                .documents
                .mapNotNull { it.toPatient() }

            // دمج النتائج وإزالة المكررات
            (nameResults + phoneResults).distinctBy { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addPatient(patient: Patient): Result<String> {
        return try {
            val patientWithId = if (patient.id.isEmpty()) {
                patient.copy(id = patientsCollection.document().id)
            } else {
                patient
            }

            patientsCollection
                .document(patientWithId.id)
                .set(patientWithId.toPatientMap())
                .await()

            Result.success(patientWithId.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePatient(patient: Patient): Result<Unit> {
        return try {
            val updatedPatient = patient.copy(updatedAt = System.currentTimeMillis())
            
            patientsCollection
                .document(patient.id)
                .set(updatedPatient.toPatientMap())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePatient(id: String): Result<Unit> {
        return try {
            patientsCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPatientsByPhone(phone: String): List<Patient> {
        return try {
            patientsCollection
                .whereEqualTo("phone", phone)
                .get()
                .await()
                .documents
                .mapNotNull { it.toPatient() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

