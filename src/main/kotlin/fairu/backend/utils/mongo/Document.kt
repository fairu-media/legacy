package fairu.backend.utils.mongo

interface Document {
    /** Save this document in the database. */
    suspend fun save()

    /**
     * Delete this document from the database.
     *
     * @return `true` if this document was deleted from the Database. *
     */
    suspend fun delete(): Boolean
}
