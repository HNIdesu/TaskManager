package com.hnidesu.taskmanager.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hnidesu.taskmanager.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var mFragmentHomeBinding: FragmentHomeBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentHomeBinding.inflate(layoutInflater).also {
            mFragmentHomeBinding = it
        }.root
    }
}
