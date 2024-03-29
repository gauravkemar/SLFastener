package   com.example.slfastener.viewmodel.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.demorfidapp.repository.SLFastenerRepository


class LoginViewModelFactory(
    private val application: Application,
    private val rfidRepository: SLFastenerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(application, rfidRepository) as T
    }
}