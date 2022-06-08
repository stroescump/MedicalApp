package eu.ase.grupa1088.licenta.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.ase.grupa1088.licenta.User
import eu.ase.grupa1088.licenta.repo.AccountService
import eu.ase.grupa1088.licenta.utils.AppResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(val accountService: AccountService, val dispatcher: CoroutineDispatcher) :
    ViewModel() {

    val uiStateFlow = MutableStateFlow<AppResult<User>>(AppResult.Progress)

    fun registerUser(email: String, parola: String, nume: String, cnp: String, telefon: String) {
        viewModelScope.launch(dispatcher) {
            uiStateFlow.update { AppResult.Progress }
            accountService.registerUser(email, parola, nume, cnp, telefon) { res ->
                uiStateFlow.update { res }
            }
        }
    }
}