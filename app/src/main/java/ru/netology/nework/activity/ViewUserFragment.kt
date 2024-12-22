package ru.netology.nework.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import ru.netology.nework.auth.viewmodel.AuthViewModel
import ru.netology.nework.databinding.CardUserDetailsBinding
import ru.netology.nework.databinding.FragmentViewUserBinding
import ru.netology.nework.ui.loadImageFromUrl
import ru.netology.nework.viewmodel.UserViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewUserFragment : Fragment() {
    //val authViewModel by viewModels<AuthViewModel>()
    //val userViewModel by viewModels<UserViewModel>()
    //private
    val userViewModel: UserViewModel by activityViewModels()

    private lateinit var binding: FragmentViewUserBinding
    private lateinit var bindingInternal: CardUserDetailsBinding

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("ViewUserFragment", "Before fragment inflating")
        /*      val inflated = inflater.inflate(R.layout.fragment_view_user, container, false)
                return inflated*/
        binding = FragmentViewUserBinding.inflate(
            inflater,
            container,
            false
        )
        Log.d("ViewUserFragment", "After fragment inflating")


        bindingInternal = CardUserDetailsBinding.inflate(
            inflater,
            container,
            false
        )
        //bindingInternal.toolbar.title = "TTT"  // Это не работает, не присваивается


        //setListeners(binding)

        subscribe(binding)

        return this.binding.root
    }

    /* Подписки */

    private fun subscribe(binding: FragmentViewUserBinding, bindingInt: CardUserDetailsBinding = bindingInternal) {
        // Подписка на изменение выбранного пользователя
        userViewModel.selected.observe(viewLifecycleOwner) { user ->
            binding.userViewUsername.text = user.name // Собственный элемент фрагмента меняется

            // НЕ ВЫХОДИТ управлять элементами включенной карточки
            bindingInt.toolbar.title = "ttt"
            // И после всех привязок начинаем, наконец, грузить картинку
            val url = user.avatar    // "${BASE_URL}/avatars/${user.avatar}"
            loadImageFromUrl(url, bindingInt.userPhoto)
        }
    }
}
