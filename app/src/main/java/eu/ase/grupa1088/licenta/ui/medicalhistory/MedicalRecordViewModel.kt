package eu.ase.grupa1088.licenta.ui.medicalhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eu.ase.grupa1088.licenta.repo.getMedicalRecordForDoctor
import eu.ase.grupa1088.licenta.repo.getMedicalRecordForPatient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class MedicalRecordViewModel(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) :
    ViewModel() {

    fun fetchMedicalRecordAsDoctor(doctorID: String?) =
        doctorID?.let {
            getMedicalRecordForDoctor(it)
        }

    fun fetchMedicalRecordAsPatient(patientID: String?) =
        patientID?.let {
            getMedicalRecordForPatient(it)
        }


    class Factory(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MedicalRecordViewModel(dispatcher) as T
        }
    }
}