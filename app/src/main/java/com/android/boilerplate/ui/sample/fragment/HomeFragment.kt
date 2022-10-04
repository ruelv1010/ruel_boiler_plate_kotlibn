package com.android.boilerplate.ui.sample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.boilerplate.databinding.FragmentHomeBinding
import com.android.boilerplate.utils.dialog.WebviewDialog
import com.android.boilerplate.utils.setOnSingleClickListener

class HomeFragment: Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
    }

    private fun setClickListeners() = binding.run {
        loadWebViewDialogButton.setOnSingleClickListener {
            openWebViewDialog()
        }
    }

    private fun openWebViewDialog(){
        WebviewDialog.openDialog(
            childFragmentManager,
            "https://www.pmti.biz/"
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}