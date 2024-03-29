package com.example.slfastener.helper

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.slfastener.R


class CustomArrayAdapter (context: Context, resource: Int, items: List<String>) :
    ArrayAdapter<String>(context, resource, items) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView

        if (!isEnabled(position)) {
            // Set text color for disabled item in the drop-down
            view.setTextColor(ContextCompat.getColor(context, R.color.black))
        } else {
            // Set text color for enabled item in the drop-down
            view.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        }

        return view
    }
}