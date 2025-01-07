package ru.netology.nework.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentNewJobBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.viewmodel.PeriodViewModel
import ru.netology.nework.viewmodel.UserViewModel
import java.text.SimpleDateFormat


class NewJobFragment : Fragment() {

    private lateinit var binding: FragmentNewJobBinding

    val userViewModel: UserViewModel by activityViewModels()
    val periodViewModel: PeriodViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewJobBinding.inflate(
            inflater,
            container,
            false
        )

        setListeners(binding)

        subscribe(binding)

        return binding.root
    }

    private fun setListeners(binding: FragmentNewJobBinding) {
        // Вызов диалога выбора периода
        binding.areaJobPeriod.setOnClickListener {
            val dialogPeriodFragment = PeriodFragment()
            activity?.let {
                val manager = it.supportFragmentManager
                dialogPeriodFragment.show(manager, "dialogPeriod")
            }

        }

        // Сохранение работы (новой либо измененной)
        binding.jobSaveButton.setOnClickListener {
            val jobId = userViewModel.editedJob.value?.id ?: 0L
            val userId = userViewModel.selected.value?.id ?: 0L
            val inputDateFormat = SimpleDateFormat("MM/dd/yyyy")
            val outputDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date1: String = periodViewModel.period.value?.dateStart?.let{
                buildString {
                    append(outputDateFormat.format(inputDateFormat.parse(it)))
                    append("T00:00:01.000Z")
                }
            } ?: ""
            val date2: String = periodViewModel.period.value?.dateFinish?.let{
                buildString {
                    append(outputDateFormat.format(inputDateFormat.parse(it)))
                    append("T23:59:59.000Z")
                }
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

    }

}
