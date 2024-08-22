package ru.netology.nework.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nework.R
import ru.netology.nework.exept.AlertInfo
import ru.netology.nework.ui.alertImplementation
import ru.netology.nework.ui.showToast
import ru.netology.nework.viewmodel.AuthViewModel

class FeedoUserFragment : Fragment() {
    val authViewModel by viewModels<AuthViewModel>()

    lateinit var inflated: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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


        val testingButton = inflated.findViewById<Button>(R.id.testing)
        testingButton.setOnClickListener {

            val alertInfo =
                AlertInfo(R.string.alert_wrong_server_response, listOf(999, "My message"))
            this@FeedoUserFragment.context?.let {
                try {
                    it.showToast(
                        alertInfo.alertImplementation(it)
                    ) // пока так
                } catch (e: Exception) {
                    Log.d("(ERR_NO_TOAST)", e.message.toString())
                }
            } ?: try {
                Log.d(
                    "(NO_ALERT)",
                    String.format("Wrong server response %1\$d %2\$s", alertInfo.args)
                )
            } catch (e: Exception) {
                Log.d("(ERR_NO_ALERT)", e.message.toString())
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
