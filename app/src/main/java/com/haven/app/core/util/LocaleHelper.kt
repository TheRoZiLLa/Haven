package com.haven.app.core.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import java.util.Locale

class LocaleContextWrapper(val originalContext: Context, configContext: Context) : 
    ContextWrapper(configContext), 
    ActivityResultRegistryOwner {
    
    override val activityResultRegistry: ActivityResultRegistry
        get() = (originalContext as ActivityResultRegistryOwner).activityResultRegistry
}

object LocaleHelper {
    fun wrapContext(context: Context, languageTag: String): Context {
        val locale = Locale.forLanguageTag(languageTag)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        val configContext = context.createConfigurationContext(config)
        return LocaleContextWrapper(context, configContext)
    }
}

fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) {
            return ctx
        }
        if (ctx is LocaleContextWrapper) {
            val original = ctx.originalContext
            if (original is Activity) return original
            ctx = original
            continue
        }
        ctx = ctx.baseContext
    }
    return null
}

