package eu.ase.grupa1088.licenta.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import eu.ase.grupa1088.licenta.models.MedicalAppointment
import eu.ase.grupa1088.licenta.models.User
import eu.ase.grupa1088.licenta.repo.*
import eu.ase.grupa1088.licenta.utils.AppResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class AccountViewModel(
    private val accountService: AccountService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    ViewModel() {

    var isDoctor: Boolean = false
    val uiStateFlow = MutableStateFlow<AppResult<User>?>(null)
    val resetPasswordStateFlow = MutableStateFlow<AppResult<Boolean>?>(null)
    val medicalAppointmentLiveData = MutableLiveData<AppResult<List<MedicalAppointment>>>()
    val deleteLiveData = MutableLiveData<AppResult<Int>>()
    val bulkUserLiveData = MutableLiveData<AppResult<List<User>>>()

    fun registerUser(
        email: String,
        parola: String,
        nume: String,
        telefon: String,
        cnp: String? = null,
        doctorID: String? = null
    ) {
        viewModelScope.launch(dispatcher) {
            accountService.registerUser(email, parola, nume, telefon, cnp, doctorID) { res ->
                uiStateFlow.update { res }
            }
        }
    }

    fun loginUser(email: String, parola: String) {
        viewModelScope.launch(dispatcher) {
            accountService.loginUser(email, parola) { res ->
                uiStateFlow.update { res }
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch(dispatcher) {
            uiStateFlow.update { AppResult.Progress }
            accountService.resetPassword(email) { res ->
                resetPasswordStateFlow.update { res }
            }
        }
    }

    fun getUserInfo() {
        getUserAccountDetails { res ->
            viewModelScope.launch(dispatcher) {
                uiStateFlow.update { res }
            }
        }
    }

    fun getUserAppointments() {
        getUserAppointmentsFirebase { res ->
            medicalAppointmentLiveData.postValue(res)
        }
    }

    fun deleteAppointment(appointment: MedicalAppointment, pos: Int) {
        appointment.id?.let { id ->
            deleteAppointmentFirebase(id, pos) {
                deleteLiveData.postValue(it)
            }
        }
    }

    fun getAvailableDoctors(selectedSpeciality: String) {
        getDoctors(selectedSpeciality.uppercase(Locale.getDefault())) {
            bulkUserLiveData.postValue(it)
        }
    }

    class Factory(
        private val accountService: AccountService,
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AccountViewModel(accountService, dispatcher) as T
        }
    }
}