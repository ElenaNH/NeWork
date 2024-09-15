package ru.netology.nework.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nework.api.DataApi
import ru.netology.nework.databinding.FragmentFeedoUserBinding
import ru.netology.nework.ui.loadImageFromUrl
import ru.netology.nework.auth.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.UserViewModel

class FeedoUserFragment : Fragment() {
    val authViewModel by viewModels<AuthViewModel>()
    private val viewModel by viewModels<UserViewModel>()

    private lateinit var binding: FragmentFeedoUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFeedoUserBinding.inflate(
            inflater,
            container,
            false
        )
        val textMessage = binding.message
        val imageAvatar = binding.avatarka

        //Получаем данные об аутентификации  //lifecycleScope.launchWhenCreated
        lifecycleScope.launch {
            authViewModel.data.collectLatest {
                textMessage.text =
                    "Hello, ${authViewModel.currentUserName}!\n"
                Log.d("CALLED", "auth.data.collectLatest")
            }
        }
        //Получаем данные о текущем пользователе  //lifecycleScope.launchWhenCreated
        lifecycleScope.launch {
            authViewModel.currentUser.collectLatest {
                textMessage.text =
                    "Hello, ${authViewModel.currentUserName}!\n"
                Log.d("CALLED", "auth.currentUser.collectLatest")

                // Прогрузка аватарки
                loadImageFromUrl(authViewModel.currentUser.value?.avatar ?: "", imageAvatar)
            }
        }


        /*        // Тестируем запрос данных через другой сервис (непосредственно)
                lifecycleScope.launch {
                    try {
                        val testUsersResponse = DataApi.retrofitService.getAllUsers()
                        val userCount = testUsersResponse.body()?.count() ?: -1
                        binding.info.text = "There are $userCount user(s)"
                    } catch (e: Exception) {
                        binding.info.text = "ERROR of DataApi"
                    }
                }*/

        // Тестируем модель
        lifecycleScope.launch {
            try {
                viewModel.data.collectLatest {
                    try {
                        val userCount = it?.count() ?: -1
                        val userFirst = it.firstOrNull() ?: ""

                        binding.info.text = "There are $userCount user(s)\n$userFirst..."
                    } catch (e: Exception) {
                        binding.info.text = "ERROR of DataApi"
                    }
                }
            } catch (e:Exception) {
                Log.e("ERR", "Error of viewmodel data collecting")
            }
        }

        return binding.root
    }


    /*companion object {
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment FeedoUserFragment.
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FeedoUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }*/
}
