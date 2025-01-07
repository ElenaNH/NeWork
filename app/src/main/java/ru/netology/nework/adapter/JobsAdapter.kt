package ru.netology.nework.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.BuildConfig.BASE_URL
import ru.netology.nework.R
import ru.netology.nework.activity.ViewUserFragment
import ru.netology.nework.databinding.CardJobBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.ui.loadImageFromUrl

interface OnJobInteractionListener {
    fun onRemove(job: Job) {}
}

/*interface OnInteractionListener {
    fun onLike(job: Job) {}
    fun onShare(job: Job) {}
    fun onEdit(job: Job) {}
    fun onRemove(job: Job) {}
    fun onVideoLinkClick(job: Job) {}
    fun onViewSingle(job: Job) {}
}*/


class JobsAdapter(private val onJobInteractionListener: OnJobInteractionListener) :
    ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = CardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(
            binding,
            onJobInteractionListener
        )
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position)
        holder.bind(job)
    }

}

// Отрисовка карточки пользователя в списке осуществляется функцией bind
// Требуется полная перерисовка всех элементов, т.к. они многоразового использования

class JobViewHolder(
    private val binding: CardJobBinding,
    private val onJobInteractionListener: OnJobInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    // Отрисовка карточки пользователя в списке
    fun bind(job: Job) {

        binding.apply {

            jobCardCompanyName.text = job.name
            jobCardPeriod.text = buildString {
                append(job.start.toString())
                append(" ")
                append(job.finish.toString())
            }
            jobCardPosition.text = job.position

            jobCardLink.text = job.link
            when (job.link) {
                null, "" -> jobCardLink.isGone = true // Пустой элемент убираем с экрана
                else -> jobCardLink.isVisible = true
            }
            /*jobCardLink.isGone = (job.link ?: "" == "") // Пустой элемент убираем с экрана
            jobCardLink.isVisible = !jobCardLink.isGone*/

            // Обработчики кликов

            jobCardDelete.setOnClickListener {
                onJobInteractionListener.onRemove(job)
            }

            // TODO - Card menu в списке и в details будет у постов, событий, но не у пользователей, не у работ
            // TODO - Пункты меню: изменить, удалить
            // TODO - у пользователей в списке возможна галочка выбран/не выбран
            // TODO - Edit card menu будет только "сохранить" (always или ifRoom?)


            // Картинок тут нет, не грузим
        }
    }
}

/*class PostInteractionListenerImpl(viewModelInput: PostViewModel, fragmentInput: Fragment) :
    OnInteractionListener*/

class OnJobInteractionListenerImpl(fragmentInput: Fragment) : OnJobInteractionListener {

    private val fragment0 = fragmentInput

    override fun onRemove(job: Job) {
        super.onRemove(job)

        if (fragment0 is ViewUserFragment) fragment0.userViewModel.removeJob(job)


    }
}
