package com.example.newasteriodapp

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.example.newasteriodapp.domain.Asteroid
import com.example.newasteriodapp.domain.PictureOfDay
import com.example.newasteriodapp.main.MainAdapter

class BindingAdapters {
    companion object {

        @JvmStatic
        @BindingAdapter("statusIcon")
        fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
            if (isHazardous) {
                imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
            } else {
                imageView.setImageResource(R.drawable.ic_status_normal)
            }
        }

        @JvmStatic
        @BindingAdapter("asteroidStatusImage")
        fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
            if (isHazardous) {
                imageView.setImageResource(R.drawable.asteroid_hazardous)
            } else {
                imageView.setImageResource(R.drawable.asteroid_safe)
            }
        }

        @JvmStatic
        @BindingAdapter("astronomicalUnitText")
        fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
            val context = textView.context
            textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
        }

        @JvmStatic
        @BindingAdapter("kmUnitText")
        fun bindTextViewToKmUnit(textView: TextView, number: Double) {
            val context = textView.context
            textView.text = String.format(context.getString(R.string.km_unit_format), number)
        }

        @JvmStatic
        @BindingAdapter("velocityText")
        fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
            val context = textView.context
            textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
        }

        @JvmStatic
        @BindingAdapter("goneIfNotNull")
        fun goneIfNotNull(view: View, it: Any?) {
            view.visibility = if (it != null) View.GONE else View.VISIBLE
        }

        @JvmStatic
        @BindingAdapter("imageUrl")
        fun setImageUrl(imageView: ImageView, pic: PictureOfDay?) {
            if (pic != null) {
                Picasso.get().load(pic.url).into(imageView)
            }
        }

        @JvmStatic
        @BindingAdapter("listData")
        fun bindRecyclerView(recyclerView: RecyclerView, data: List<Asteroid>?) {
            val adapter = recyclerView.adapter as MainAdapter
            adapter.submitList(data)
        }
    }
}
