package ru.netology.nework.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import ru.netology.nework.adapter.NotesAdapter
import ru.netology.nework.adapter.OnNoteInteractionListenerImpl
import ru.netology.nework.auth.viewmodel.AuthViewModel
import ru.netology.nework.databinding.FragmentFeedoPostBinding
import ru.netology.nework.R
import ru.netology.nework.ui.showToast
import ru.netology.nework.viewmodel.PostViewModel
import kotlin.getValue


@AndroidEntryPoint
class FeedoPostFragment : Fragment() {
    val authViewModel: AuthViewModel by viewModels()
    val noteViewModel: PostViewModel by activityViewModels()

    private lateinit var binding: FragmentFeedoPostBinding

    // adapter, interactionListener
    private val interactionListener by lazy { OnNoteInteractionListenerImpl(this) }
    val adapter by lazy { NotesAdapter(interactionListener) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFeedoPostBinding.inflate(
            inflater,
            container,
            false
        )


        binding.listPost.adapter =
            adapter   // val adapter определяется выше by lazy

        val ttt = activity?.let { it.getString(R.string.app_name) }

        setListeners(binding)

        subscribe(binding)


        return binding.root
    }

    private fun bind(binding: FragmentFeedoPostBinding) {
        // Привязка для макета (если захотим вынести в отдельный метод)

    }

    /* Лиснеры */
    private fun setListeners(binding: FragmentFeedoPostBinding) {

    }

    /* Подписки */
    private fun subscribe(binding: FragmentFeedoPostBinding) {


        // Подписка на адаптер
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.listPost.smoothScrollToPosition(0)
                }
            }
        })


 //TODO - ПОЧЕМУ при раскомментаривании КОМПИЛЯТОР РУГАЕТСЯ НА SQL-строку?
  // android.database.sqlite.SQLiteException: no such column: true (code 1 SQLITE_ERROR): , while compiling: SELECT NoteEntity.*, A.authenicated AS ownedByMe FROM NoteEntity LEFT JOIN (SELECT id, authenicated FROM AuthEntity WHERE authenicated) AS A  ON NoteEntity.authorId = A.id WHERE noteTypeCode == ? And (Not ? Or A.authenicated) And (CASE WHEN ? Is Null THEN true ELSE NoteEntity.authorId == ? END)

        // Используем модель
        lifecycleScope.launch {
            noteViewModel.data.collectLatest {
                try {
                    adapter.submitList(it)

                } catch (e: Exception) {

                }
            }
        }

    }

}
