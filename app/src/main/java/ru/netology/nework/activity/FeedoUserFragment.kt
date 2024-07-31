package ru.netology.nework.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nework.R
import ru.netology.nework.auth.viewmodel.LoginViewModel
import ru.netology.nework.viewmodel.AuthViewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FeedoUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FeedoUserFragment : Fragment() {
    val authViewModel by viewModels<AuthViewModel>()

    lateinit var inflated: View


    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflated = inflater.inflate(R.layout.fragment_feedo_user, container, false)

        val textMessage = inflated.findViewById<TextView>(R.id.message)

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
            }
        }


        return inflated.rootView
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
