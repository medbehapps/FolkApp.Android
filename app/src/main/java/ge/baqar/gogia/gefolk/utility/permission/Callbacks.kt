package ge.baqar.gogia.gefolk.utility.permission

interface OnDenyPermissions {
    operator fun get(deniedPermissions: List<String>)
}

interface OnFailure {
    fun fail(e: Exception)
}

interface OnGrantPermissions {
    operator fun get(grantedPermissions: List<String>)
}