package eu.ase.grupa1088.licenta.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import eu.ase.grupa1088.licenta.models.MedicalAppointment
import eu.ase.grupa1088.licenta.models.User
import eu.ase.grupa1088.licenta.repo.*
import eu.ase.grupa1088.licenta.utils.AppResult
import eu.ase.grupa1088.licenta.utils.Event
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
    var isPatient = isDoctor.not()
    var doctorID: String = ""
    var selectedDate = ""
    var initialPasswd = ""
    val uiStateFlow = MutableStateFlow<AppResult<User>?>(null)
    val medicalAppointmentStateFlow = MutableStateFlow<AppResult<MedicalAppointment>?>(null)
    val resetPasswordStateFlow = MutableStateFlow<AppResult<Boolean>?>(null)
    val medicalAppointmentLiveData = MutableLiveData<AppResult<List<MedicalAppointment>>>()
    val deleteLiveData = MutableLiveData<Event<AppResult<Pair<Int, Boolean>>>>()
    val bulkUserLiveData = MutableLiveData<AppResult<List<User>>>()
    val sendAppointment = MutableLiveData<AppResult<Boolean>>()
    val updateProfile = MutableLiveData<AppResult<Boolean>>()

    fun registerUser(
        email: String,
        parola: String,
        nume: String,
        telefon: String,
        cnp: String? = null,
        doctorID: String? = null,
        speciality: String?
    ) {
        viewModelScope.launch(dispatcher) {
            accountService.registerUser(email, parola, nume, telefon, cnp, doctorID, speciality) { res ->
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
        getUserAppointmentsFirebase(isDoctor, doctorID, { res ->
            medicalAppointmentLiveData.postValue(res)

        }) { res ->
            medicalAppointmentStateFlow.update { res }
        }
    }

    fun deleteAppointment(appointment: MedicalAppointment, pos: Int, isMarkedForDeletion: Boolean) {
        deleteAppointmentFirebase(appointment, pos, isMarkedForDeletion, isDoctor) {
            deleteLiveData.postValue(Event(it))
        }
    }

    fun getAvailableDoctors(selectedSpeciality: String) {
        getDoctors(selectedSpeciality.uppercase(Locale.getDefault())) {
            bulkUserLiveData.postValue(it)
        }
    }

    fun getAvailableTimetables(doctorID: String, date: String) {
        selectedDate = date
        getDoctorAvailability(doctorID, date) { result ->
            medicalAppointmentLiveData.postValue(result)
        }
    }

    fun showAvailableDates(
        isToday: Boolean,
        appointmentsRemoteList: List<MedicalAppointment>,
        callback: (List<MedicalAppointment>) -> Unit
    ) {
        viewModelScope.launch(dispatcher) {
            eu.ase.grupa1088.licenta.utils.showAvailableDates(
                isToday,
                appointmentsRemoteList
            ) { filteredAvailability ->
                viewModelScope.launch(Dispatchers.Main) {
                    callback(filteredAvailability)
                }
            }
        }
    }

    fun bookAppointment(appointment: MedicalAppointment) {
        sendAppointment(appointment) {
            sendAppointment.postValue(it)
        }
    }

    fun updateProfile(phone: String, password: String) {
        if (password != initialPasswd) {
            accountService.changePassword(password) {
                updateProfileRemote(phone, password) {
                    updateProfile.postValue(it)
                }
            }
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