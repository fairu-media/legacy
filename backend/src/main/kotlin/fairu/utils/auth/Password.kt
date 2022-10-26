package fairu.utils.auth

import de.mkammerer.argon2.Argon2Factory

object Password {
    val argon = Argon2Factory.create()

    /**
     * Hash
     *
     * @param given
     * @return
     */
    fun hash(given: CharArray): String {
        return try {
            argon.hash(10, 65536, 1, given)
        } finally {
            argon.wipeArray(given)
        }
    }

    /**
     * Verify a [given password][given] against the [stored password][stored].
     *
     * @param stored Hashed password stored in our databases
     * @param given  Given content to be verified
     * @return `true` if [given] matches [stored]
     */
    fun verify(stored: String, given: CharArray): Boolean {
        return try {
            argon.verify(stored, given)
        } finally {
            argon.wipeArray(given)
        }
    }
}
