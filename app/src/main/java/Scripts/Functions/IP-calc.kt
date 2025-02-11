fun calculateNetworkInfo(ipStr: String, maskStr: String): NetworkInfo {
    val ip = IPAddress.fromString(ipStr)
    val mask = parseMask(maskStr)
    val networkAddress = IPAddress(ip.address and mask)
    // Инверсия маски: в Kotlin можно использовать .inv()
    val broadcastAddress = IPAddress(networkAddress.address or mask.inv())

    // Определяем количество единиц в маске (CIDR префикс)
    val prefix = Integer.bitCount(mask)
    val totalAddresses = if (prefix == 32) 1 else 1 shl (32 - prefix)

    // Специальные случаи для /31 и /32
    val hostCount = when {
        prefix == 31 -> 2   // В некоторых случаях /31 используется для point-to-point соединений
        prefix == 32 -> 1
        else -> totalAddresses - 2
    }
    val firstHost = if (prefix < 31) networkAddress + 1 else null
    val lastHost = if (prefix < 31) broadcastAddress - 1 else null

    return NetworkInfo(networkAddress, broadcastAddress, firstHost, lastHost, hostCount)
}
