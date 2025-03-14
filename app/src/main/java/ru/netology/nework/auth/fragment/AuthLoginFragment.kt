package ru.netology.nework.auth.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.auth.authdto.LoginInfo
import ru.netology.nework.auth.viewmodel.LoginViewModel
import ru.netology.nework.databinding.FragmentAuthLoginBinding
import ru.netology.nework.ui.alertImplementation
import ru.netology.nework.util.AndroidUtils

@AndroidEntryPoint
class AuthLoginFragment : Fragment() {

    val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentAuthLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAuthLoginBinding.inflate(layoutInflater, container, false)

        // По идее, при старте фрагмента кнопка заблокирована - но вдруг?
        binding.signIn.isEnabled = viewModel.completed()

        subscribe()     // все подписки, которые могут нам потребоваться в данном фрагменте
        setListeners()  // все лиснеры всех элементов данном фрагменте

        return binding.root
    }

    private fun setListeners() {

        binding.signIn.setOnClickListener {

            AndroidUtils.hideKeyboard(requireView())  // Скрыть клавиатуру

            // Попытка логина

            if (viewModel.completed()) {
                // Блокируем кнопку, чтобы дважды не нажимать
                binding.signIn.isEnabled = false
                // Делаем попытку залогиниться
                viewModel.doLogin()
            } // else {} // НЕ будем уведомлять. Все регулируется доступностью кнопки после resetLoginInfo
        }

        //Обработчики ввода текста в поля, чтобы проверять, когда логин/пароль непусты

        binding.login.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                resetInfo()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        binding.password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                resetInfo()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

    }

    private fun subscribe() {

        viewModel.loginInfo.observe(viewLifecycleOwner) {
            binding.signIn.isEnabled = viewModel.completed()
        }

        viewModel.loginSuccessEvent.observe(viewLifecycleOwner) {
            // При успехе уходим
            // (но для страховки почистим поля - если предыдущий фрагмент тот же самый)
            clearForm()
            AndroidUtils.hideKeyboard(requireView())  // Скрыть клавиатуру (если вдруг она опять открылась)

            // Закрытие текущего фрагмента (переход к нижележащему в стеке)
            findNavController().navigateUp()
            Log.d("INFO", "LoginFragment was leaved")
        }

        viewModel.stateWaiting.observe(viewLifecycleOwner) {
            binding.signIn.isEnabled = viewModel.completed()
        }

        viewModel.alertWarning.observe(viewLifecycleOwner) { alertInfo ->
            if (alertInfo.rStringCode == 0) return@observe  // Начальное значение лайвдаты не обрабатываем

            // Предупреждение (локализуемое), которое требуется вывести пользователю
            val msg = alertInfo.alertImplementation(this.requireContext())
            showToast(msg)
        }

    }

    private fun clearForm() {
        binding.login.text.clear()
        binding.password.text.clear()
        // автоматически должна на каждое поле сработать resetInfo()
    }

    private fun resetInfo() {
        viewModel.resetLoginInfo(
            LoginInfo(
                binding.login.text.toString(),
                binding.password.text.toString()
            )
        )
        binding.signIn.isEnabled = viewModel.completed()
    }

    private fun showToast(textInformation: String) {
        val warnToast = Toast.makeText(
            activity,
            textInformation,
            Toast.LENGTH_SHORT
        )
        warnToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        warnToast.show()
    }

}
