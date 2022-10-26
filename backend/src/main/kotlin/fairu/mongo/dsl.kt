package fairu.mongo

import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

typealias DatabaseCollection<T> = CoroutineCollection<T>

typealias DatabaseClient = CoroutineClient

typealias Database = CoroutineDatabase
