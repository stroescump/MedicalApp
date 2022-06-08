package eu.ase.grupa1088.licenta.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import eu.ase.grupa1088.licenta.User
import eu.ase.grupa1088.licenta.repo.AccountService
import eu.ase.grupa1088.licenta.ui.login.LoginUiState
import eu.ase.grupa1088.licenta.utils.AppResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(
    private val accountService: AccountService,
    private val dispatcher: CoroutineDispatcher
) :
    ViewModel() {

    val uiStateFlow = MutableStateFlow<AppResult<User>?>(null)
    val loginUiStateFlow = MutableStateFlow(LoginUiState())

    fun registerUser(email: String, parola: String, nume: String, cnp: String, telefon: String) {
        viewModelScope.launch(dispatcher) {
            uiStateFlow.update { AppResult.Progress }
            accountService.registerUser(email, parola, nume, cnp, telefon) { it ->
                uiStateFlow.update { it }
            }
        }
    }

    fun loginUser(email: String, parola: String) {
        viewModelScope.launch(dispatcher) {
            uiStateFlow.update { AppResult.Progress }
            accountService.loginUser(email, parola) {
                uiStateFlow.update { it }
            }
        }
    }

    class Factory(
        private val accountService: AccountService,
        private val dispatcher: CoroutineDispatcher
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AccountViewModel(accountService, dispatcher) as T
        }
    }
}