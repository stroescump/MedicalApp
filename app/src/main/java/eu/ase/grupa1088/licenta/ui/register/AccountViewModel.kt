package eu.ase.grupa1088.licenta.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import eu.ase.grupa1088.licenta.models.User
import eu.ase.grupa1088.licenta.repo.AccountService
import eu.ase.grupa1088.licenta.repo.getCurrentUserNode
import eu.ase.grupa1088.licenta.utils.AppResult
import eu.ase.grupa1088.licenta.utils.observeValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(
    private val accountService: AccountService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    ViewModel() {

    val uiStateFlow = MutableStateFlow<AppResult<User>?>(null)
    val resetPasswordStateFlow = MutableStateFlow<AppResult<Boolean>?>(null)
    val userInfoLiveData = MutableLiveData<AppResult<DataSnapshot>>()

    fun registerUser(email: String, parola: String, nume: String, cnp: String, telefon: String) {
        viewModelScope.launch(dispatcher) {
            uiStateFlow.update { AppResult.Progress }
            accountService.registerUser(email, parola, nume, cnp, telefon) { res ->
                uiStateFlow.update { res }
            }
        }
    }

    fun loginUser(email: String, parola: String) {
        viewModelScope.launch(dispatcher) {
            uiStateFlow.update { AppResult.Progress }
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
        getCurrentUserNode().observeValue {
            userInfoLiveData.postValue(it)
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