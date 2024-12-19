package ru.netology.nework.ui

import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.netology.nework.BuildConfig
import ru.netology.nework.R

fun loadImageFromUrl(url: String?, imageView: ImageView) {
    if (url != null) {
        // Сначала сбросим старое изображение
        imageView.setImageDrawable(null)

        // Сохраненное изображение загрузим с сервера/кэша (url берем "как есть")
        val imgUrl = url
        // Условие для загрузки: wrap_content для высоты imageView
        Glide.with(imageView)
            .load(imgUrl)
            .placeholder(R.drawable.ic_loading_100dp)
            .error(R.drawable.ic_error_100dp)
            .timeout(10_000)
            .into(imageView)

    }
}
