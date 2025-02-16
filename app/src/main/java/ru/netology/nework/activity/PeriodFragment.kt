package ru.netology.nework.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import ru.netology.nework.databinding.FragmentPeriodBinding
import ru.netology.nework.viewmodel.PeriodViewModel
import java.util.Calendar


class PeriodFragment : DialogFragment() {

    private lateinit var binding: FragmentPeriodBinding
    val periodViewModel: PeriodViewModel by activityViewModels()

    private lateinit var inputDateView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        periodViewModel.clearPeriod() // Предварительно чистим период

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPeriodBinding.inflate(
            inflater,
            container,
            false
        )

        setListeners(binding)

        //periodViewModel.clearPeriod()

        return binding.root

    }

    private fun setListeners(binding: FragmentPeriodBinding) {

        binding.internalDialog.areaDateStart.setOnClickListener {
            // Устанавливаем целевое поле для выбора даты, чтобы его могла отслеживать кнопка очистки
            inputDateView = binding.internalDialog.inputDateStart
            pickDate(binding)
        }

        binding.internalDialog.areaDateFinish.setOnClickListener {
            // Устанавливаем целевое поле для выбора даты, чтобы его могла отслеживать кнопка очистки
            inputDateView = binding.internalDialog.inputDateFinish
            pickDate(binding)
        }

        binding.internalDialog.clearDatePicker.setOnClickListener {
            inputDateView.text = ""
            binding.internalDialog.datePickerContainer.isGone = true
        }

        binding.internalDialog.escapeDatePicker.setOnClickListener {
            inputDateView.text = inputDateView.text.trimEnd() //Поскольку добавляли в конец пробел
            binding.internalDialog.datePickerContainer.isGone = true
        }

        binding.internalDialog.buttonCancel.setOnClickListener {
            periodViewModel.notifyDialogClosed()
            this.dialog?.hide()
//            this.dismiss()     // TODO - разобраться, как полностью закрыть диалог
//            this.dismissNow()
        }

        binding.internalDialog.buttonOk.setOnClickListener {
            // Нужно передать выбранные значения в вызывающий фрагмент
            periodViewModel.setPeriod(
                binding.internalDialog.inputDateStart.text.toString(),
                binding.internalDialog.inputDateFinish.text.toString()
            )
            periodViewModel.notifyDialogClosed()
            dismiss()   //this.dialog?.hide()
        }

    }

    private fun pickDate(binding: FragmentPeriodBinding) {
        inputDateView.text = "${inputDateView.text} " // без костыля не работало
        var pickedDateFormatted = ""

        val dateContainer = binding.internalDialog.datePickerContainer
        val datePicker = binding.internalDialog.datePicker
        val today = Calendar.getInstance()

        dateContainer.isVisible = true

        datePicker.init(
            today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { view, year, month, day ->
            val month = month + 1

            pickedDateFormatted =
                "${if (month < 10) "0" else ""}$month/${if (day < 10) "0" else ""}$day/$year"

            // Если целевое поле изменилось, то календарь можно скрыть
            if (pickedDateFormatted != inputDateView.text) {
                inputDateView.text = pickedDateFormatted
                dateContainer.isGone = true
            }

        }
    }


}
