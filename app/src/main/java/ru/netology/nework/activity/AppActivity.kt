package ru.netology.nework.activity

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nework.R
import ru.netology.nework.ui.getCurrentFragment
import ru.netology.nework.ui.getRootFragment
import ru.netology.nework.ui.goToLogin
import ru.netology.nework.ui.goToRegister
import ru.netology.nework.util.AndroidUtils
import ru.netology.nework.auth.viewmodel.AuthViewModel


class AppActivity : AppCompatActivity() {

    val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        updateMenuInScope(authViewModel, this)

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
            oldMenuProvider =
                addOrReplaceMenu(
                    viewModel,
                    oldMenuProvider
                ) // Без этого присвоения менюшки размножаются
        }
        viewModel.currentUser.collectLatest {
            oldMenuProvider =
                addOrReplaceMenu(
                    viewModel,
                    oldMenuProvider
                ) // Без этого присвоения менюшки размножаются
        }

    }

    private fun addOrReplaceMenu(
        viewModel: AuthViewModel,
        oldMenuProvider0: MenuProvider?
    ): MenuProvider? {
        var oldMenuProvider = oldMenuProvider0

        oldMenuProvider?.let { menuProvider ->
            removeMenuProvider(menuProvider)
        } // Удаляем старые меню, если они были

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_auth, menu)
                val authorized = viewModel.isAuthorized
                menu.setGroupVisible(R.id.authorized, authorized)
                menu.setGroupVisible(R.id.unauthorized, !authorized)
                val logoutMenuItemText =
                    viewModel.currentUser.value?.let { currentUser ->
                        String.format(
                            //context.getString(R.string.logout_user),
                            this@AppActivity.getString(R.string.logout_user),
                            currentUser.name
                        )
                    }
                        ?: this@AppActivity.getString(R.string.logout)  //context.getString(R.string.logout)
                val logoutMenuItem = menu.findItem(R.id.logout)
                logoutMenuItem.setTitle(logoutMenuItemText)
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

        return oldMenuProvider
    }

}
