package ru.netology.nework.activity

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Response
import ru.netology.nework.R
import ru.netology.nework.auth.api.UserApi
import ru.netology.nework.auth.dto.Token
import ru.netology.nework.auth.dto.UserResponse
import ru.netology.nework.ui.getCurrentFragment
import ru.netology.nework.ui.getRootFragment
import ru.netology.nework.ui.goToLogin
import ru.netology.nework.ui.goToRegister
import ru.netology.nework.util.AndroidUtils
import ru.netology.nework.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Date


class AppActivity : AppCompatActivity() {

    val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        updateMenuInScope(authViewModel, this)

/*
        val textMessage = findViewById<TextView>(R.id.message)
        val buttonRegister = findViewById<Button>(R.id.register)
        val buttonLogon = findViewById<Button>(R.id.logon)
        val buttonLogoff = findViewById<Button>(R.id.logoff)


        lastUserName =
            authViewModel.currentUser.value?.name ?: "User" // TODO сделать сравнение по id

        //Получаем данные об аутентификации  //lifecycleScope.launchWhenCreated
        lifecycleScope.launch {
            authViewModel.data.collectLatest {
                textMessage.text =
                    "${testCountPositive} authorisation(s). " +
                            "Hello, ${if (it == null) "Anonymous" else lastUserName}!\n"
                Log.d("CALLED", "auth.data.collectLatest")
            }
        }
        //Получаем данные о текущем пользователе  //lifecycleScope.launchWhenCreated
        lifecycleScope.launch {
            authViewModel.currentUser.collectLatest {
                textMessage.text =
                    "${testCountPositive} authorisation(s). " +
                            "Hello, ${if (it == null) "Anonymous" else it.name}!\n"
                Log.d("CALLED", "auth.currentUser.collectLatest")
            }
        }
*/

    }

    private fun updateMenuInScope(viewModel: AuthViewModel, context: Context) {
        lifecycleScope.launch {
            updateMenu(viewModel, context)
        }
    }

    private suspend fun updateMenu(viewModel: AuthViewModel, context: Context) {
        var oldMenuProvider: MenuProvider? = null
        //viewModel.data.observe(this) {
        viewModel.data.collectLatest {
            oldMenuProvider?.let(::removeMenuProvider) // Удаляем старые меню, если они были

            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_auth, menu)
                    val authorized = viewModel.isAuthorized
                    menu.setGroupVisible(R.id.authorized, authorized)
                    menu.setGroupVisible(R.id.unauthorized, !authorized)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    // Определяем текущий отображаемый фрагмент
                    var currentFragment = supportFragmentManager.getCurrentFragment()
                    val rootFragment = supportFragmentManager.getRootFragment()

                    // Обработка выбора меню и возврат true для обработанных
                    return when (menuItem.itemId) {
                        R.id.auth -> {
                            if (currentFragment != null) {
                                goToLogin(currentFragment)
                            } else {
                                val stop1 = 1 // мы тут не должны оказаться по идее
                            }
                            // Возвращаем true как признак обработки
                            true
                        }

                        R.id.register -> {
                            if (currentFragment != null) {
                                goToRegister(currentFragment)
                            } else {
                                val stop1 = 1 // мы тут не должны оказаться по идее
                            }
                            // Возвращаем true как признак обработки
                            true
                        }

                        R.id.logout -> {
                            if (currentFragment != null) {
                                AndroidUtils.hideKeyboard(currentFragment.requireView())  // Скрыть клавиатуру

                                // Логоф
                                authViewModel.clearAuth()


                                /*// Подтверждение логофа //LENGTH_LONG?? //it.rootView??
                                val builder: AlertDialog.Builder =
                                    AlertDialog.Builder(this@AppActivity)
                                builder
                                    .setMessage(getString(R.string.logout_confirm_request))
                                    .setTitle(getString(R.string.action_confirm_title))
                                    .setPositiveButton(getString(R.string.action_continue)) { dialog, which ->
                                        // Do something
                                        // Логоф
                                        AppAuth.getInstance().clearAuth()
                                        // Уходим из режима редактирования в режим чтения
                                        if (currentFragment is NewPostFragment)
                                            rootFragment.navController.navigateUp()
                                    }
                                    .setNegativeButton(getString(R.string.action_cancel)) { dialog, which ->
                                        // Do nothing
                                    }
                                val dialog: AlertDialog = builder.create()
                                dialog.show()*/
                            }
                            // Возвращаем true как признак обработки
                            true
                        }

                        else -> {
                            false
                        }
                    }
                }

            }.apply {
                oldMenuProvider = this
            }, this)
        }

    }

}
