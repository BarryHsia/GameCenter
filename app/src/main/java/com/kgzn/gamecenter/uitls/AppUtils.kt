package com.kgzn.gamecenter.uitls

import android.app.ActivityThread
import android.content.pm.PackageManager
import android.os.SystemProperties
import com.kgzn.gamecenter.BuildConfig
import java.net.NetworkInterface
import java.util.Locale

object AppUtils {

    fun getAppId(): String {
        val packageManager = ActivityThread.currentApplication().packageManager
        val applicationInfo =
            packageManager?.getApplicationInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_META_DATA)
        return applicationInfo?.metaData?.getString("ProductAppID", null) ?: "tv001"
    }

    fun getLanguage(): String {
        val locale = Locale.getDefault() // zh_CN
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
        return SystemProperties.get("kgzn.ota.customer", "test1")
    }

    fun getModel(): String {
        return SystemProperties.get("kgzn.ota.model", "3588")
    }

    fun getType(): String {
        return SystemProperties.get("kgzn.ota.type", "TV_MTK9653_AN14_Neutral")
    }

    fun getMac(): String {
        return runCatching {
            NetworkInterface.getNetworkInterfaces().asSequence().firstOrNull {
                it.name == "eth0"
            }?.let { it.hardwareAddress?.joinToString(":") { String.format("%02X", it) } }
        }.getOrNull() ?: ""
    }

    fun getSda(): String {
        return SystemProperties.get("ro.product.sda", "65966")
    }

    fun getRegion(): String {
        return SystemProperties.get("persist.sys.kgzn.country", "CN")
    }

}