package com.example.submissionawalstoryapp.ui.customview

import android.content.Context
import android.view.LayoutInflater
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.example.submissionawalstoryapp.databinding.CustomErrorLayoutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CustomDialog(
    private val context: Context,
    private val message: String,
    private val animationRawRes: Int,
    private val action: (() -> Unit)? = null
) {

    fun show() {
        val dialogBinding = CustomErrorLayoutBinding.inflate(LayoutInflater.from(context))

        val dialog = MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setView(dialogBinding.root)
            .show()

        dialogBinding.tvError.text = message

        val animationView = dialogBinding.dialogLogo
        loadLottieAnimation(animationView, animationRawRes)

        dialogBinding.btnDismiss.setOnClickListener {
            dialog.dismiss()
            action?.invoke()
        }
    }

    private fun loadLottieAnimation(animationView: LottieAnimationView, rawRes: Int) {
        LottieCompositionFactory.fromRawRes(context, rawRes).addListener { composition ->
            if (composition != null) {
                animationView.setComposition(composition)
                animationView.repeatCount = LottieDrawable.INFINITE // Atur properti animasi jika diperlukan
                animationView.playAnimation()
            }
        }
    }
}


