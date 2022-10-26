package fairu.mongo

import naibu.logging.logging
import org.bson.conversions.Bson

open class DocumentClass<D : Document>(val collection: DatabaseCollection<D>) {
    companion object {
        /**
         * The global document logger.
         */
        val log by logging {  }
    }

    private val prefix: String = "Database[${collection.documentClass.simpleName}]"

    /**
     * Used to find a Document inside the provided [collection]
     *
     * @param filter Filter
     */
    suspend fun find(filter: () -> Bson): D? =
        find(filter())

    /**
     * Used to find a Document inside the provided [collection]
     *
     * @param filter Filter
     */
    suspend fun find(filter: Bson): D? =
        collection.findOne(filter)

    /**
     * Saves a document.
     *
     * @param document The document to save.
     * @param filter The filter to use.
     */
    suspend fun save(document: D, filter: Bson) {
        try {
            collection.findOneAndReplace(filter, document) ?: collection.insertOne(document)

            log.debug { "$prefix Inserted $document into the database." }
        } catch (ex: Throwable) {
            log.error(ex) { "$prefix Exception occurred while attempting to insert $document into the database:" }
        }
    }

    /**
     * Removes a Document from the database.
     *
     * @param filter The filter to use when removing the document.
     */
    suspend fun remove(filter: Bson) {
        try {
            collection.deleteOne(filter)
            log.debug { "$prefix Deleted an item using filter: $filter" }
        } catch (ex: Throwable) {
            log.error(ex) { "$prefix Exception occurred while deleting item from collection:" }
        }
    }
}
