package ru.netology.nework.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.R
import ru.netology.nework.activity.FeedoPostFragment
import ru.netology.nework.databinding.CardNoteDetailsBinding
import ru.netology.nework.dto.Note
import ru.netology.nework.ui.loadImageFromUrl
import ru.netology.nework.ui.showToast

//import androidx.core.graphics.drawable.toDrawable
//import ru.netology.nework.activity.FeedoUserFragment
//import ru.netology.nework.databinding.CardUserBinding





interface OnNoteInteractionListener {
    fun onViewDetails(note: Note) {}   // Аналог onViewSingle
//    fun onLike(note: Note) {}
//    fun onShare(note: Note) {}
//    fun onEdit(note: Note) {}
//    fun onRemove(note: Note) {}
}

/*interface OnInteractionListener {
    fun onVideoLinkClick(note: Note) {}
    fun onViewSingle(note: Note) {}
}*/


class NotesAdapter(private val onNoteInteractionListener: OnNoteInteractionListener) :
    ListAdapter<Note, NoteViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = CardNoteDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(
            binding,
            onNoteInteractionListener
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
    }
}

// Отрисовка карточки в списке осуществляется функцией bind
// Требуется полная перерисовка всех элементов, т.к. они многоразового использования

class NoteViewHolder(
    private val binding: CardNoteDetailsBinding,
    private val onNoteInteractionListener: OnNoteInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    // Отрисовка карточки в списке
    fun bind(note: Note) {
        binding.apply {
            userCardUserName.text= "${if (note.ownedByMe) "Me: " else ""}${note.author}"
            content.text = "${note.content}"

            /*elementBackground.background = if (note.ownedByMe) {
                R.drawable.bg_card_state_me.toDrawable()
            }
            else R.drawable.bg_card_state.toDrawable()*/

            // Обработчики кликов
            noteCard.setOnClickListener {
                onNoteInteractionListener.onViewDetails(note)
            }

            /*noteCardAvatar.setOnClickListener {
                onNoteInteractionListener.onViewDetails(note)
            }


            noteCardNoteName.setOnClickListener {
                onNoteInteractionListener.onViewDetails(note)
            }*/

            // TODO - Card menu в списке и в details будет у постов, событий, работ, но не у пользователей
            // TODO - Пункты меню: изменить, удалить
            // TODO - у пользователей в списке возможна галочка выбран/не выбран
            // TODO - Edit card menu будет только "сохранить" (always или ifRoom?)

            // И после всех привязок начинаем, наконец, грузить картинку
                val url = note.authorAvatar    // "${BASE_URL}/avatars/${note.avatar}"
                loadImageFromUrl(url, binding.userCardAvatar)

        }
    }
}

/*class PostInteractionListenerImpl(viewModelInput: PostViewModel, fragmentInput: Fragment) :
    OnInteractionListener*/

class OnNoteInteractionListenerImpl(private val fragmentInput: Fragment) : OnNoteInteractionListener {

    override fun onViewDetails(note: Note) {
        super.onViewDetails(note)
        val stop = 1
        // TODO - открыть фрагмент ViewNoteFragment
        val action_from_to =
            when {
                (fragmentInput is FeedoPostFragment) -> {
                    // Фиксируем выбор карточки пользователя для дальнейшей обработки
                    fragmentInput.noteViewModel.selectNote(note)
                    // TODO Test showToast
                    fragmentInput.context?.showToast("The Note will be shown once")
                    // TODO Рассчитываем переход к новому фрагменту R.id.action_feedoPostFragment_to_viewPostFragment
                    R.id.action_feedoPostFragment_to_feedoUserFragment
                }

                else -> null
            }

        if (action_from_to == null) return

        // TODO
//        fragmentInput.findNavController().navigate(
//            action_from_to,
//            Bundle()
//        )

    }
}
