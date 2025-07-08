package com.example.slfastener.view

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.demorfidapp.helper.Constants
import com.example.demorfidapp.helper.Constants.KEY_ISLOGGEDIN
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.SessionManager
import com.example.demorfidapp.helper.Utils
import com.example.demorfidapp.repository.SLFastenerRepository
import com.example.slfastener.R
import com.example.slfastener.databinding.ActivityLoginBinding
import com.example.slfastener.model.login.LoginRequest
import com.example.slfastener.viewmodel.login.LoginViewModel
import com.example.slfastener.viewmodel.login.LoginViewModelFactory
import es.dmoral.toasty.Toasty

class  LoginActivity : AppCompatActivity() {
    lateinit var binding:ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var progress: ProgressDialog
    private lateinit var session: SessionManager
    private lateinit var userDetails: HashMap<String, Any?>
    private var baseUrl: String =""
    private var serverIpSharedPrefText: String? = null
    private var serverHttpPrefText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_login)
        session = SessionManager(this)
        progress = ProgressDialog(this)
        progress.setMessage("Please Wait...")
        userDetails = session.getUserDetails()
        serverIpSharedPrefText = userDetails!![Constants.KEY_SERVER_IP].toString()
        serverHttpPrefText = userDetails!![Constants.KEY_HTTP].toString()
        baseUrl = "$serverHttpPrefText://$serverIpSharedPrefText/service/api/"
        val slFastenerRepository =SLFastenerRepository()
        val viewModelProviderFactory = LoginViewModelFactory(application, slFastenerRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[LoginViewModel ::class.java]


        if (Utils.getSharedPrefsBoolean(this@LoginActivity, Constants.KEY_ISLOGGEDIN, false)) {
            if(Utils.getSharedPrefsBoolean(this@LoginActivity, Constants.KEY_ISLOGGEDIN, true)) {
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finish()
            }
        }
        binding.loginBtn.setOnClickListener {
            login()
        }

        binding.clearBtn.setOnClickListener {
            clear()
        }

        viewModel.loginMutableLiveData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            session.createLoginSession(
                                resultResponse.firstName,
                                resultResponse.lastName,
                                resultResponse.email,
                                resultResponse.mobileNumber.toString(),
                                resultResponse.userName,
                                resultResponse.jwtToken,
                                resultResponse.roleName,
                                resultResponse.refreshToken
                            )
                            Utils.setSharedPrefsBoolean(this@LoginActivity, KEY_ISLOGGEDIN, true)
                            startActivity()

                        } catch (e: Exception) {
                            Toasty.warning(
                                this@LoginActivity,
                                "hello"+e.printStackTrace().toString(),
                                Toasty.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@LoginActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }
    fun startActivity()
    {
        var intent=Intent(this@LoginActivity,HomeActivity::class.java)
        startActivity( intent)

        finish()
    }
    fun startAdmin()
    {
        var intent=Intent(this@LoginActivity,AdminSettingPageActivity::class.java)
        intent.putExtra("key_user_type","Admin")
        startActivity(intent)
        finish()
    }
    private fun clear(){
        binding.edittextUsername.setText("")
        binding.editTextPassword.setText("")
    }
    private fun validateInput(userId: String, password: String): String? {
        return when {
            userId.isEmpty() || password.isEmpty() -> "Please enter valid credentials"
            userId.length < 5 -> "Please enter at least 5 characters for the username"
            password.length < 6 -> "Please enter a password with more than 6 characters"
            else -> null
        }
    }
    fun login() {
        try {
            // Fetching user credentials from input fields
            val userId = binding.edittextUsername.text.toString().trim()
            val password = binding.editTextPassword .text.toString().trim()

            // Validate user input
            if(userId=="admin" && password== "Pass@123")
            {
                startAdmin()
            }
            else
            {
                val validationMessage = validateInput(userId, password)
                if (validationMessage == null) {
                    val loginRequest = LoginRequest( password, userId )
                    viewModel.login(this,baseUrl, loginRequest)
                } else {
                    showErrorMessage(validationMessage)
                }
            }

        } catch (e: Exception) {
            showErrorMessage(e.printStackTrace().toString())
        }
    }

    private fun showProgressBar() {
        progress.show()
    }

    private fun hideProgressBar() {
        progress.cancel()
    }
    private fun showErrorMessage(message: String) {
        Toasty.warning(this@LoginActivity, message, Toasty.LENGTH_SHORT).show()
    }
}



/*    private fun login()
{
var username=binding.edittextUsername.text.toString().trim()
var password=binding.editTextPassword.text.toString().trim()

if(username.isNotEmpty() && password.isNotEmpty())
{
    if(username.equals("supervisor") && password.equals("Pass@123"))
    {
        var intent= Intent(this@LoginActivity,HomeActivity::class.java)
        startActivity(intent)
        Utils.setSharedPrefsBoolean(this@LoginActivity, Constants.KEY_ISLOGGEDIN, true)
    }
    else{
        Toast.makeText(this@LoginActivity,"Please enter correct details",Toast.LENGTH_SHORT).show()
    }
}
else
{
    Toast.makeText(this@LoginActivity,"Please fill the details",Toast.LENGTH_SHORT).show()
}
}*/
