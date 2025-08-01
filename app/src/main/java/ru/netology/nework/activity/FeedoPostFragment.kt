package ru.netology.nework.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentFeedoPostBinding


@AndroidEntryPoint
class FeedoPostFragment : Fragment() {

    private lateinit var binding: FragmentFeedoPostBinding


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

        bind(binding)

        /*setListeners(binding)

        subscribe(binding)*/


        return binding.root
    }

    private fun bind(binding: FragmentFeedoPostBinding) {
        //binding.listPost
    }

}
