package com.example.delivery.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.delivery.R
import com.example.delivery.activities.client.home.ClientHomeActivity
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.providers.UsersProvider
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    var imageViewGoToLogin: ImageView? = null
    var editTextName: EditText? = null
    var editTextLastName: EditText? = null
    var editTextPhone: EditText? = null
    var editTextEmail: EditText? = null
    var editTextPassword: EditText? = null
    var editTextPasswordConfirmation: EditText? = null
    var buttonRegister: Button? = null
    var userProvider = UsersProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        imageViewGoToLogin = findViewById(R.id.imageview_go_to_login)
        editTextName = findViewById(R.id.edittext_name)
        editTextLastName = findViewById(R.id.edittext_lastname)
        editTextPhone = findViewById(R.id.edittext_phone)
        editTextEmail = findViewById(R.id.edittext_email_register)
        editTextPassword = findViewById(R.id.edittext_password_register)
        editTextPasswordConfirmation = findViewById(R.id.edittext_password_confirm)
        buttonRegister = findViewById(R.id.btn_register)

        imageViewGoToLogin?.setOnClickListener { goToLogin() }
        buttonRegister?.setOnClickListener { register() }
    }

    private fun register() {
        val TAG = "RegisterActivity"
        val name = editTextName?.text.toString()
        val lastname = editTextLastName?.text.toString()
        val phone = editTextPhone?.text.toString()
        val email = editTextEmail?.text.toString()
        val password = editTextPassword?.text.toString()
        val confirmPassword = editTextPasswordConfirmation?.text.toString()


        if (isValidForm(name, lastname, phone, email, password, confirmPassword)) {

            val user = User(
                name = name,
                lastname = lastname,
                email = email,
                phone = phone,
                password = password
            )

            userProvider.register(user)?.enqueue(object : Callback<ResponseHttp> {
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {
                    if (response.body()?.isSuccess == true) {
                        saveUserInSession(response.body()?.data.toString())
                        goToClientHome()
                    }
                    Toast.makeText(
                        this@RegisterActivity,
                        response.body()?.message,
                        Toast.LENGTH_LONG
                    ).show()

                    Log.d(TAG, "Response: ${response}")
                    Log.d(TAG, "Body: ${response.body()}")
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Se produjo un error ${t.message}")
                    Toast.makeText(
                        this@RegisterActivity,
                        "Se produjo un error ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            })
        }
    }

    private fun saveUserInSession(data: String) {
        val sharedPref = SharedPref(this)
        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref.save("user", user)
    }

    private fun goToClientHome() {
        val i = Intent(this, SaveImageActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    private fun isValidForm(
        name: String,
        lastname: String,
        phone: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (name.isBlank() || lastname.isBlank() || phone.isBlank() || email.isBlank() ||
            password.isBlank() || confirmPassword.isBlank() || !email.isEmailValid()
        ) {
            Toast.makeText(this, "Datos invalidos", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches()
    }

    private fun goToLogin() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
}