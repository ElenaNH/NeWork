package ru.netology.nework.auth.fragment

import android.app.ProgressDialog
import android.graphics.Color
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
import ru.netology.nework.auth.dto.RegisterInfo
import ru.netology.nework.auth.viewmodel.RegisterViewModel
import ru.netology.nework.databinding.FragmentAuthRegisterBinding
import ru.netology.nework.R
import ru.netology.nework.auth.dto.LoginInfo
import ru.netology.nework.ui.alertImplementation
import ru.netology.nework.util.AndroidUtils

class AuthRegisterFragment : Fragment() {

    val viewModel by viewModels<RegisterViewModel>()
    private lateinit var binding: FragmentAuthRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAuthRegisterBinding.inflate(layoutInflater, container, false)

        // По идее, при старте фрагмента кнопка заблокирована - но вдруг?
        binding.register.isEnabled = viewModel.completed()

        subscribe()     // все подписки, которые могут нам потребоваться в данном фрагменте
        setListeners()  // все лиснеры всех элементов данном фрагменте

        return binding.root
    }

    private fun setListeners() {

        binding.register.setOnClickListener {

            AndroidUtils.hideKeyboard(requireView())  // Скрыть клавиатуру

            // Попытка регистрации

            if (viewModel.completed()) {
                // Блокируем кнопку, чтобы дважды не нажимать
                binding.register.isEnabled = false
                // Делаем попытку регистрации
                viewModel.doRegister()
            } // else {} // НЕ будем уведомлять. Все регулируется доступностью кнопки после resetRegisterInfo
        }

        // Обработчики ввода текста в поля, чтобы проверять, когда логин/пароль непусты

        binding.username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                resetInfo()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

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

        binding.password2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                resetInfo()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        // TODO - разобраться с аватаркой

    }

    private fun subscribe() {

        viewModel.registerInfo.observe(viewLifecycleOwner) {
            // информировать о разных паролях
            // TODO - разобраться с правильным цветом
            if (viewModel.passwordsMatch())
                binding.password2.setTextColor(Color.parseColor("#FF000000"))
            else binding.password2.setTextColor(Color.parseColor("#FFFF0000"))

            binding.register.isEnabled = viewModel.completed()
        }

        viewModel.registerSuccessEvent.observe(viewLifecycleOwner) {
            // При успехе уходим
            // (но для страховки почистим поля - если предыдущий фрагмент тот же самый)
            clearForm()
            AndroidUtils.hideKeyboard(requireView())  // Скрыть клавиатуру (если вдруг она опять открылась)

            // Закрытие текущего фрагмента (переход к нижележащему в стеке)
            findNavController().navigateUp()
            Log.d("INFO", "RegisterFragment was leaved")
        }

        viewModel.alertWarning.observe(viewLifecycleOwner) { alertInfo ->
            if (alertInfo.rStringCode == 0) return@observe  // Начальное значение лайвдаты не обрабатываем

            // Предупреждение (локализуемое), которое требуется вывести пользователю
            val msg = alertInfo.alertImplementation(this.requireContext())
            showToast(msg)
        }

    }

    private fun clearForm() {
        binding.username.text.clear()
        binding.login.text.clear()
        binding.password.text.clear()
        binding.password2.text.clear()
        // автоматически должна на каждое поле сработать resetInfo()
        // TODO разобраться с аватаркой
    }

    private fun resetInfo() {
        viewModel.resetRegisterInfo(
            RegisterInfo(
                binding.username.text.toString(),
                binding.login.text.toString(),
                binding.password.text.toString(),
                binding.password2.text.toString(),
                "",     // TODO разобраться с аватаркой
            )
        )
        binding.register.isEnabled = viewModel.completed()
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
