package ru.netology.nework.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nework.adapter.OnUserInteractionListenerImpl
import ru.netology.nework.adapter.UsersAdapter
import ru.netology.nework.auth.viewmodel.AuthViewModel
import ru.netology.nework.databinding.FragmentFeedoUserBinding
import ru.netology.nework.dto.User
import ru.netology.nework.viewmodel.UserViewModel

//import ru.netology.nework.ui.loadImageFromUrl

class FeedoUserFragment : Fragment() {
    val authViewModel by viewModels<AuthViewModel>()
    //val userViewModel by viewModels<UserViewModel>()
    //private
    val userViewModel: UserViewModel by activityViewModels()

    private lateinit var binding: FragmentFeedoUserBinding

    // adapter, interactionListener
    private val interactionListener by lazy { OnUserInteractionListenerImpl(this) }
    val adapter by lazy { UsersAdapter(interactionListener) }

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
        binding.listUser.adapter =
            adapter   // val adapter определяется выше by lazy

        setListeners(binding)

        subscribe(binding)


        return binding.root
    }

    /* Лиснеры */
    private fun setListeners(binding: FragmentFeedoUserBinding) {


        /*binding.testingButton.setOnClickListener {
            val userReloader = userViewModel.reloadUsers()

            binding.info.text = "Reloading: $userReloader"

        }*/

    }

    /* Подписки */
    private fun subscribe(binding: FragmentFeedoUserBinding) {

        /*val textMessage = binding.message
        val imageAvatar = binding.avatarka*/

        //Получаем данные об аутентификации  //lifecycleScope.launchWhenCreated
        lifecycleScope.launch {
            authViewModel.data.collectLatest {
                /*textMessage.text =
                    "Hello, ${authViewModel.currentUserName}!\n"*/

                Log.d("CALLED", "auth.data.collectLatest")
            }
        }
        //Получаем данные о текущем пользователе  //lifecycleScope.launchWhenCreated
        lifecycleScope.launch {
            authViewModel.currentUser.collectLatest {
                /*textMessage.text =
                    "Hello, ${authViewModel.currentUserName}!\n"*/

                Log.d("CALLED", "auth.currentUser.collectLatest")

                /*// Прогрузка аватарки
                loadImageFromUrl(authViewModel.currentUser.value?.avatar ?: "", imageAvatar)
                */

            }
        }

/*       TODO         // Подписка на изменение данных модели
        viewModel.data.observe(viewLifecycleOwner) { data ->
            adapter.submitList(data.posts)
            binding.emptyText.isVisible = data.empty
        }*/

// Подписка на адаптер
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.listUser.smoothScrollToPosition(0)
                }
            }
        })

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
            userViewModel.data.collectLatest {
                try {
                    /*val userCount = it?.count() ?: -1
                    val userFirst = it.firstOrNull() ?: ""
                    val usersFirst: List<User> = if (userCount > 3) it.subList(0, 3) else it
                    val userNames = usersFirst
                        .map { user -> user.name }
                        .joinToString(separator = ", ")*/

                    adapter.submitList(it)


                    /*binding.info.text = "There are $userCount user(s)\n$userNames..."*/
                } catch (e: Exception) {
                    /*binding.info.text = "ERROR of DataApi"*/
                }
            }
        }

    }


}
