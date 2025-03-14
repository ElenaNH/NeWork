package ru.netology.nework.auth.fragment

import android.app.Activity
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.auth.authdto.RegisterInfo
import ru.netology.nework.auth.viewmodel.RegisterViewModel
import ru.netology.nework.databinding.FragmentAuthRegisterBinding
import ru.netology.nework.ui.alertImplementation
import ru.netology.nework.util.AndroidUtils

@AndroidEntryPoint
class AuthRegisterFragment : Fragment() {

    val viewModel by viewModels<RegisterViewModel>()  //private val viewModel:RegisterViewModel by viewModels()
    private lateinit var binding: FragmentAuthRegisterBinding
    private val photoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != Activity.RESULT_OK) {
                return@registerForActivityResult
            }
            val uri =
                requireNotNull(it.data?.data)   //1-я data - это интент, а вторая - ресурс данного интента
            val file =
                uri.toFile() // Если бы ранее не потребовали существования uri, то так: uri?.toFile()

            viewModel.setPhoto(uri, file)
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAuthRegisterBinding.inflate(layoutInflater, container, false)

        // По идее, при старте фрагмента кнопка заблокирована - но вдруг?
        binding.register.isEnabled = viewModel.completed()

        refreshAvatarBlock() // Также перепроверим отрисовку аватарки и кнопки очистки

        subscribe()     // все подписки, которые могут нам потребоваться в данном фрагменте
        setListeners()  // все лиснеры всех элементов данном фрагменте

        return binding.root
    }

    private fun setListeners() {
        // Кнопка регистрации
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

        // Выбор аватарки
        binding.avatar.setOnClickListener {
            ImagePicker.Builder(this)  // this в нашем случае это текущий фрагмент
                .crop()
                .maxResultSize(2048, 2048)
                .createIntent(photoLauncher::launch)
        }

        // Удаление аватарки
        binding.clear.setOnClickListener{
            viewModel.clearPhoto()
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

    }

    private fun subscribe() {

        viewModel.avatar.observe(viewLifecycleOwner) { photo ->
            // Отрисовка оттача
            refreshAvatarBlock()

        }

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

        viewModel.stateWaiting.observe(viewLifecycleOwner) {
            binding.register.isEnabled = viewModel.completed()
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

    private fun refreshAvatarBlock() {

        val avatar = viewModel.avatar.value

        if (avatar == null) {
            binding.avatar.setImageResource(R.drawable.icset_photocamera_24)
            binding.clear.isGone = true
        } else {
            binding.avatar.setImageURI(avatar.uri)
            binding.clear.isVisible = true
        }
    }

}

/*
// TODO - Потом подумаем, как сокращенно записать для всех полей
private fun EditText.setOnTextChange(fragment: AuthRegisterFragment) {
    val addTextChangedListener = this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            fragment.viewModel.resetInfo()
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    })
}*/







