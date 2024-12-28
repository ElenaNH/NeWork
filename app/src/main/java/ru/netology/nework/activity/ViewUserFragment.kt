package ru.netology.nework.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.adapter.JobsAdapter
import ru.netology.nework.adapter.OnJobInteractionListenerImpl
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
    //val authViewModel by viewModels<AuthViewModel>() // Создание отдельной независимой модели
    //val userViewModel by viewModels<UserViewModel>()
    //private
    val userViewModel: UserViewModel by activityViewModels()

    private lateinit var binding: FragmentViewUserBinding

    // jobsAdapter, jobInteractionListener
    private val jobInteractionListener by lazy { OnJobInteractionListenerImpl(this) }
    val jobsAdapter by lazy { JobsAdapter(jobInteractionListener) }


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

        binding.internal.listJob.adapter =
            jobsAdapter   // val jobsAdapter определяется выше by lazy
        setListeners(binding)

        subscribe(binding)

        return this.binding.root
    }

    /* Лиснеры */
    private fun setListeners(binding: FragmentViewUserBinding) {

        // Первая кнопка меню - wall
        binding.internal.toolbar.menu.getItem(0).setOnMenuItemClickListener {

            binding.internal.wall.isVisible = true
            binding.internal.listJob.isGone = true

            true
        }

        // Вторая кнопка меню - jobs
        binding.internal.toolbar.menu.getItem(1).setOnMenuItemClickListener {

            binding.internal.listJob.isVisible = true
            binding.internal.wall.isGone = true

            true
        }

    }

    /* Подписки */

    private fun subscribe(binding: FragmentViewUserBinding) {
        // Подписка на изменение выбранного пользователя
        userViewModel.selected.observe(viewLifecycleOwner) { user ->

            binding.internal.toolbar.title = user.name

            activity?.title = user.name

            // И после всех привязок начинаем, наконец, грузить картинку
            val url = user.avatar    // "${BASE_URL}/avatars/${user.avatar}"
            loadImageFromUrl(url, binding.internal.userPhoto)
        }

        // Подписка на изменение списка работ (заново связываем с адаптером)
        userViewModel.selectedJobs.observe(viewLifecycleOwner) { listOfJobs ->
            jobsAdapter.submitList(listOfJobs)
        }

// Подписка на адаптер
        jobsAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.internal.listJob.smoothScrollToPosition(0)
                }
            }
        })

    }
}
