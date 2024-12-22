package ru.netology.nework.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.BuildConfig.BASE_URL
import ru.netology.nework.R
import ru.netology.nework.activity.FeedoPostFragment
import ru.netology.nework.activity.FeedoUserFragment
import ru.netology.nework.databinding.CardUserBinding
import ru.netology.nework.dto.User
import ru.netology.nework.ui.loadImageFromUrl

interface OnUserInteractionListener {
    fun onViewDetails(user: User) {}   // Аналог onViewSingle
}

/*interface OnInteractionListener {
    fun onLike(user: User) {}
    fun onShare(user: User) {}
    fun onEdit(user: User) {}
    fun onRemove(user: User) {}
    fun onVideoLinkClick(user: User) {}
    fun onViewSingle(user: User) {}
}*/


class UsersAdapter(private val onUserInteractionListener: OnUserInteractionListener) :
    ListAdapter<User, UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(
            binding,
            onUserInteractionListener
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

}

// Отрисовка карточки пользователя в списке осуществляется функцией bind
// Требуется полная перерисовка всех элементов, т.к. они многоразового использования

class UserViewHolder(
    private val binding: CardUserBinding,
    private val onUserInteractionListener: OnUserInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    // Отрисовка карточки пользователя в списке
    fun bind(user: User) {
        binding.apply {
            userCardUserName.text = user.name

            // Обработчики кликов

            userCardAvatar.setOnClickListener {
                onUserInteractionListener.onViewDetails(user)
            }

            userCardUserName.setOnClickListener {
                onUserInteractionListener.onViewDetails(user)
            }

            // TODO - Card menu в списке и в details будет у постов, событий, работ, но не у пользователей
            // TODO - Пункты меню: изменить, удалить
            // TODO - у пользователей в списке возможна галочка выбран/не выбран
            // TODO - Edit card menu будет только "сохранить" (always или ifRoom?)

            /*ibtnMenuMoreActions.isVisible = user.ownedByMe
            // Пока выбор меню обработаем в любом случае, а не только для ownedByMe
            // Может, позже вставим условие if (post.ownedByMe)
            ibtnMenuMoreActions.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onUserInteractionListener.onRemove(user)
                                true
                            }

                            R.id.edit -> {
                                onUserInteractionListener.onEdit(user)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }*/

            // И после всех привязок начинаем, наконец, грузить картинку
            val url = user.avatar    // "${BASE_URL}/avatars/${user.avatar}"
            loadImageFromUrl(url, binding.userCardAvatar)

        }
    }
}

/*class PostInteractionListenerImpl(viewModelInput: PostViewModel, fragmentInput: Fragment) :
    OnInteractionListener*/

class OnUserInteractionListenerImpl(fragmentInput: Fragment) : OnUserInteractionListener {

    private val fragment0 = fragmentInput

    override fun onViewDetails(user: User) {
        super.onViewDetails(user)

        // TODO - открыть фрагмент ViewUserFragment
        val action_from_to =
            when {
                (fragment0 is FeedoUserFragment) -> {
                    // Фиксируем выбор карточки пользователя для дальнейшей обработки
                    fragment0.userViewModel.selectUser(user)
                    // Рассчитываем переход к новому фрагменту
                    R.id.action_feedoUserFragment_to_viewUserFragment
                }

                else -> null
            }

        if (action_from_to == null) return

        fragment0.findNavController().navigate(
            action_from_to,
            Bundle()
        )

    }
}
