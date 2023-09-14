package fairu.utils.auth

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory

object Hash {
    private val argon: Argon2 = Argon2Factory.create()

    /**
     * Hash
     *
     * @param content
     * @return
     */
    fun create(content: CharArray): String = try {
        argon.hash(10, 65536, 1, content)
    } finally {
        argon.wipeArray(content)
    }

    /**
     * Verify a [plain content][plain] against the [hashed content][hashed].
     *
     * @param hashed Hashed content to verify [plain] against
     * @param plain  Plain content to be verified
     * @return `true` if [plain] matches [hashed]
     */
    fun verify(hashed: String, plain: CharArray): Boolean = try {
        argon.verify(hashed, plain)
    } finally {
        argon.wipeArray(plain)
    }
}
