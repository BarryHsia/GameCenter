package com.kgzn.gamecenter.uitls

import android.app.Application
import android.content.pm.PackageManager
import com.kgzn.gamecenter.BuildConfig
import java.net.NetworkInterface
import java.util.Locale

object AppUtils {

    private fun getCurrentApplication(): Application? {
        return try {
            val clazz = Class.forName("android.app.ActivityThread")
            val method = clazz.getMethod("currentApplication")
            method.invoke(null) as? Application
        } catch (e: Exception) {
            null
        }
    }

    private fun getSystemProperty(key: String, defaultValue: String): String {
        return try {
            val clazz = Class.forName("android.os.SystemProperties")
            val method = clazz.getMethod("get", String::class.java, String::class.java)
            method.invoke(null, key, defaultValue) as? String ?: defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }

    fun getAppId(): String {
        val app = getCurrentApplication() ?: return "tv001"
        val applicationInfo =
            app.packageManager?.getApplicationInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_META_DATA)
        return applicationInfo?.metaData?.getString("ProductAppID", null) ?: "tv001"
    }

    fun getLanguage(): String {
        val locale = Locale.getDefault()
        return when (locale.language) {
            "zh" -> if ("CN" == locale.country) {
                locale.language + "-r" + locale.country
            } else {
                locale.language + "-rHK"
            }

            "en" -> locale.language + "-rUS"
            else -> locale.language
        }
    }

    fun getChannelCode(): String {
        val customer = getCustomer()
        val model = getModel()
        val type = getType()

        return "$customer${if (customer.isNotEmpty() && model.isNotEmpty()) "_" else ""}$model${if ((customer.isNotEmpty() || model.isNotEmpty()) && type.isNotEmpty()) "_" else ""}$type"
    }

    fun getCustomer(): String {
        return getSystemProperty("kgzn.ota.customer", "test1")
    }

    fun getModel(): String {
        return getSystemProperty("kgzn.ota.model", "3588")
    }

    fun getType(): String {
        return getSystemProperty("kgzn.ota.type", "TV_MTK9653_AN14_Neutral")
    }

    fun getMac(): String {
        return runCatching {
            NetworkInterface.getNetworkInterfaces().asSequence().firstOrNull {
                it.name == "eth0"
            }?.let { it.hardwareAddress?.joinToString(":") { String.format("%02X", it) } }
        }.getOrNull() ?: ""
    }

    fun getSda(): String {
        return getSystemProperty("ro.product.sda", "65966")
    }

    fun getRegion(): String {
        return getSystemProperty("persist.sys.kgzn.country", "CN")
    }

}
