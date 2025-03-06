package ru.netology.nework.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentNewJobBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.ui.showToast
import ru.netology.nework.util.*
import ru.netology.nework.viewmodel.PeriodViewModel
import ru.netology.nework.viewmodel.UserViewModel

@AndroidEntryPoint
class NewJobFragment : Fragment() {

    private lateinit var binding: FragmentNewJobBinding

    val userViewModel: UserViewModel by activityViewModels()
    val periodViewModel: PeriodViewModel by activityViewModels()

    val dialogPeriodFragment = PeriodFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // В первую очередь - воплощение макета и прогрузка данных!
        binding = bind(inflater, container)

        periodViewModel.clearPeriod()

        setListeners(binding)

        subscribe(binding)

        return binding.root
    }

    private fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewJobBinding {
        // Inflate the layout for this fragment - воплощаем макет
        binding = FragmentNewJobBinding.inflate(
            inflater,
            container,
            false
        )
        with(userViewModel.editedJob) {
            this.value?.let {
                binding.editJobCompanyName.setText(it.name)
                binding.editJobPosition.setText(it.position)
                binding.editJobLink.setText(it.link)

                binding.jobPeriod.text = "${it.start} - ${it.finish}" // TODO рассчитать функцию
            }

        }


        return binding
    }


    private fun setListeners(binding: FragmentNewJobBinding) {
        // Вызов диалога выбора периода
        binding.areaJobPeriod.setOnClickListener {
            //val dialogPeriodFragment = PeriodFragment() - перенесли на уровень класса
            activity?.let {
                val manager = it.supportFragmentManager
                dialogPeriodFragment.show(manager, "dialogPeriod") // TODO почему всплывает при повороте
            }

        }

        // Сохранение работы (новой либо измененной)
        binding.jobSaveButton.setOnClickListener {
            val jobId = userViewModel.editedJob.value?.id ?: 0L
            val userId = userViewModel.selected.value?.id ?: 0L
            val date1: String = periodViewModel.period.value?.dateStart?.let {
                if (it.isEmpty()) ""
                else
                    visibleDateToStored(it)
            } ?: ""
            val date2: String = periodViewModel.period.value?.dateFinish?.let {
                if (it.isEmpty()) ""
                else
                    visibleDateToStored(it, true)
            } ?: ""

            val editedJob = Job(
                jobId,
                binding.editJobCompanyName.text.toString(),
                binding.editJobPosition.text.toString(),
                date1,
                date2,
                binding.editJobLink.text.toString(),
                userId
            )
            Log.d("editedJob", editedJob.toString())
            userViewModel.saveJob(editedJob)
            // После записи выходим в предыдущий фрагмент
            findNavController().navigateUp()
        }
    }

    private fun subscribe(binding: FragmentNewJobBinding) {

        periodViewModel.period.observe(viewLifecycleOwner) {
            binding.jobPeriod.text = it.toString()
        }

        periodViewModel.flagPeriodDialogClosed.observe(viewLifecycleOwner) {
            //dialogPeriodFragment.dismissNow()
            dialogPeriodFragment.dismiss()
//            activity?.getSupportFragmentManager()?.beginTransaction()
//                ?.remove(dialogPeriodFragment)?.commit()
//                ?: Log.e("flagPeriodDialogClosed","WRONG COMMIT")
        }

    }

}
