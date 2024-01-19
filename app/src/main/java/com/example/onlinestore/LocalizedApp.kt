package com.example.onlinestore

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import java.util.Locale

open class LocalizedApp : Application() {
    companion object {
        const val LOCALE_EN = "en"
        const val LOCALE_AR = "ar"

        private val resourcesMap = mutableMapOf<String, Resources>()
        private val layoutDirectionMap = mapOf(
            LOCALE_EN to View.LAYOUT_DIRECTION_LTR,
            LOCALE_AR to View.LAYOUT_DIRECTION_RTL,
        )
        private val textDirectionMap = mapOf(
            LOCALE_EN to View.TEXT_DIRECTION_LTR,
            LOCALE_AR to View.TEXT_DIRECTION_RTL,
        )
        private val scaleXMap = mapOf(
            LOCALE_EN to 1f,
            LOCALE_AR to -1f,
        )
        var locale: String = LOCALE_EN
            private set
        val localeLiveData = MutableLiveData(LOCALE_EN)

        fun updateLocale(locale: String) {
            this.locale = locale
            localeLiveData.postValue(locale)
        }

        fun getResources(locale: String?): Resources {
            return resourcesMap[locale] ?: error("Unable to obtain resources for locale $locale")
        }

        fun getLayoutDirection(locale: String?): Int {
            return layoutDirectionMap[locale]
                ?: error("Unable to obtain layout direction for locale: $locale")
        }

        fun getTextDirection(locale: String?): Int {
            return textDirectionMap[locale]
                ?: error("Unable to obtain layout direction for locale: $locale")
        }

        fun getMirroringScaleX(locale: String?): Float {
            return scaleXMap[locale] ?: error("Invalid locale $locale")
        }

        fun getMirroringScaleX(): Float = getMirroringScaleX(locale)

        fun getString(@StringRes stringId: Int, arguments: Any? = null): String =
            getResources(locale).getString(stringId, arguments)

        fun getString(@StringRes stringId: Int): String =
            getResources(locale).getString(stringId)

        fun getText(@StringRes stringId: Int): CharSequence =
            getResources(locale).getText(stringId)

        fun getColor(@ColorRes colorId: Int): Int =
            ResourcesCompat.getColor(getResources(locale), colorId, null)

        fun getDrawable(@DrawableRes drawableId: Int, autoMirror: Boolean? = false): Drawable? =
            ResourcesCompat.getDrawable(getResources(locale), drawableId, null)?.apply {
                this.isAutoMirrored = autoMirror ?: false
            }
    }

    override fun onCreate() {
        super.onCreate()
        val appLocale = LOCALE_EN
        resourcesMap[LOCALE_EN] = createLocaleConfiguredResources(this, LOCALE_EN)
        resourcesMap[LOCALE_AR] = createLocaleConfiguredResources(this, LOCALE_AR)
        createLocaleConfiguredResources(this, appLocale)
        updateLocale(appLocale)
    }

    private fun createLocaleConfiguredResources(context: Context, localeStr: String): Resources {
        val locale = Locale.forLanguageTag(localeStr)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        val newContext = context.createConfigurationContext(configuration)
        return newContext.resources
    }
}