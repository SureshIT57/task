package com.project.gatherly.Dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.gatherly.R
import com.project.gatherly.databinding.BottomSheetMediaBinding


class MediaBottomSheet(
    private val imagePicker: ActivityResultLauncher<String>,
    private val videoPicker: ActivityResultLauncher<String>
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetMediaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnChooseImage.setOnClickListener {
            imagePicker.launch("image/*")
            dismiss() // Close Bottom Sheet after selection
        }

        binding.btnChooseVideo.setOnClickListener {
            videoPicker.launch("video/*")
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss() // Close the Bottom Sheet
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
