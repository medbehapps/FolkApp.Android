package ge.baqar.gogia.gefolk.utility.permission

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class BgPermission {

    class Builder {

        fun requestCode(code: Int): RuntimePermissions {
            requestCode = code
            return runtimePermissions
        }
    }

    class RuntimePermissions {
        fun permission(permission: String): RuntimePermissions {
            allPermissions[counter] = permission
            counter++
            return runtimePermissions
        }

        fun callBack(
            onGrantPermissions: (ArrayList<out String>) -> Unit,
            onDenyPermissions: (ArrayList<out String>) -> Unit,
            onFailure: (Exception) -> Unit
        ): BgPermission {
            grantPermissions = onGrantPermissions
            denyPermissions = onDenyPermissions
            failure = onFailure
            return runtimePermission
        }
    }

    fun onPermissionsResult(code: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == code) {
            grantResults.forEachIndexed { index, i ->
                val permission = permissions[index]
                val grantType = grantResults[index]
                if (grantType == PackageManager.PERMISSION_GRANTED) {
                    grantedPermissions.add(permission)
                } else {
                    deniedPermissions.add(permission)
                }
            }
            grantPermissions?.invoke(grantedPermissions)
            denyPermissions?.invoke(deniedPermissions)
        }
    }

    fun request(activity: Activity) {
        try {
            val tempArr = arrayOfNulls<String>(allPermissions.size)
            allPermissions.forEach {
                if (ContextCompat.checkSelfPermission(
                        activity,
                        it.value
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    tempArr[it.key] = it.value
                }
            }
            ActivityCompat.requestPermissions(activity, tempArr, requestCode)
        } catch (e: Exception) {
            failure?.invoke(e)
        }

    }

    companion object {
        private var requestCode = 0
        private val runtimePermissions: RuntimePermissions by lazy { RuntimePermissions() }
        private val runtimePermission: BgPermission by lazy { BgPermission() }
        private val grantedPermissions = arrayListOf<String>()
        private val deniedPermissions = arrayListOf<String>()
        private var counter = 0
        private val builder: Builder by lazy { Builder() }
        private var denyPermissions: ((ArrayList<String>) -> Unit)? = null
        private var grantPermissions: ((ArrayList<String>) -> Unit)? = null
        private var failure: ((Exception) -> Unit)? = null

        @SuppressLint("UseSparseArrays")
        private val allPermissions = HashMap<Int, String>()

        fun builder(): Builder? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                builder
            } else {
                null
            }
        }
    }
}