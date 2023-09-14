package fairu.utils

object Version {
    val FULL by lazy {
        Version::class.java.classLoader.getResourceAsStream("version.txt")
            ?.readAllBytes()
            ?.decodeToString()
            ?: "0.0"
    }
}
