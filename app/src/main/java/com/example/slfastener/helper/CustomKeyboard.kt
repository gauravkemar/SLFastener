package com.example.slfastener.helper

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText

import android.widget.PopupWindow
import android.widget.TextView
import com.example.slfastener.R
import com.example.slfastener.view.GRNAddActivity
import com.google.android.material.card.MaterialCardView

class CustomKeyboard(private val context: Context, private val keyboardView: View) {

    private val popupWindow: PopupWindow = PopupWindow(keyboardView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true)
    private var targetEditText: EditText? = null

    init {
        setupKeyboardButtons()
    }

    private fun setupKeyboardButtons() {
        val cardViewIds = arrayOf(
            R.id.mcv_num_1, R.id.mcv_num_2, R.id.mcv_num_3,
            R.id.mcv_num_4, R.id.mcv_num_5, R.id.mcv_num_6,
            R.id.mcv_num_7, R.id.mcv_num_8, R.id.mcv_num_9,
            R.id.mcv_dot, R.id.mcv_num_0, R.id.mcv_clear
        )

        cardViewIds.forEach { cardViewId ->
            keyboardView.findViewById<MaterialCardView>(cardViewId)?.setOnClickListener { view ->
                onKeyboardButtonClicked(cardViewId)
            }
        }
    }

    private fun onKeyboardButtonClicked(cardViewId: Int) {
        val cardView = keyboardView.findViewById<MaterialCardView>(cardViewId)
        targetEditText?.let { editText ->
            val buttonText = when (cardViewId) {
                R.id.mcv_num_1 -> cardView.findViewById<TextView>(R.id.tv_1).text.toString()
                R.id.mcv_num_2 -> cardView.findViewById<TextView>(R.id.tv_2).text.toString()
                R.id.mcv_num_3 -> cardView.findViewById<TextView>(R.id.tv_3).text.toString()
                R.id.mcv_num_4 -> cardView.findViewById<TextView>(R.id.tv_4).text.toString()
                R.id.mcv_num_5 -> cardView.findViewById<TextView>(R.id.tv_5).text.toString()
                R.id.mcv_num_6 -> cardView.findViewById<TextView>(R.id.tv_6).text.toString()
                R.id.mcv_num_7 -> cardView.findViewById<TextView>(R.id.tv_7).text.toString()
                R.id.mcv_num_8 -> cardView.findViewById<TextView>(R.id.tv_8).text.toString()
                R.id.mcv_num_9 -> cardView.findViewById<TextView>(R.id.tv_9).text.toString()
                R.id.mcv_num_0 -> cardView.findViewById<TextView>(R.id.tv_0).text.toString()
                R.id.mcv_dot -> cardView.findViewById<TextView>(R.id.tv_dot).text.toString()
                R.id.mcv_clear -> cardView.findViewById<TextView>(R.id.tv_clear).text.toString()
                else -> ""
            }

            if (buttonText.equals("clear", ignoreCase = true)) {
                editText.text.clear()
            } else {
                val selectionStart = editText.selectionStart
                val selectionEnd = editText.selectionEnd
                editText.text.replace(
                    minOf(selectionStart, selectionEnd),
                    maxOf(selectionStart, selectionEnd),
                    buttonText
                )
            }
        }
    }

    fun setTargetEditText(editText: EditText) {
        this.targetEditText = editText
    }
    /*   if (!popupWindow.isShowing) {
                val xOffset = anchor.width / 2 - popupWindow.contentView.measuredWidth / 2
                val yOffset = -anchor.height
                popupWindow.showAsDropDown(anchor, xOffset, yOffset)
            }*/
/*   fun showAt(anchor: View) {

        if (!popupWindow.isShowing) {
            // Calculate xOffset to display the popup window further to the right
            Log.e("measure","anchor${anchor.width / 2}///${popupWindow.contentView.measuredWidth}}")
            val xOffset = (anchor.width / 2)+100 - popupWindow.contentView.measuredWidth
            val yOffset = -anchor.height
            popupWindow.showAsDropDown(anchor, xOffset, yOffset)
        }
    }*/

  fun showAt(anchor: View) {
      if (!popupWindow.isShowing) {
          // Measure the screen height
          val displayMetrics = context.resources.displayMetrics
          val screenHeight = displayMetrics.heightPixels

          // Get the location of the anchor view on the screen
          val location = IntArray(2)
          anchor.getLocationOnScreen(location)
          val anchorY = location[1]
          val anchorHeight = anchor.height

          // Calculate xOffset for centering the popup
          val xOffset = /*(anchor.width / 2) */anchor.width   - popupWindow.contentView.measuredWidth

          // Calculate yOffset to ensure the popup does not go below the screen
          val popupHeight = popupWindow.contentView.measuredHeight
          val spaceBelowAnchor = screenHeight - (anchorY + anchorHeight)
          val yOffset = if (spaceBelowAnchor >= popupHeight) {
              -anchorHeight // Default offset if there's enough space below the anchor
          } else {
              -anchorHeight - popupHeight // Shift above the anchor to fit on the screen
          }

          // Show the popup window
          popupWindow.showAsDropDown(anchor, xOffset, yOffset)
      }
  }
    fun dismiss() {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }
}