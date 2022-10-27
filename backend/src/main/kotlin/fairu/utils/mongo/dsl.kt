package fairu.utils.mongo

import naibu.ext.koin.get
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

typealias DatabaseCollection<T> = CoroutineCollection<T>

typealias DatabaseClient = CoroutineClient

typealias Database = CoroutineDatabase

inline fun <reified T : Any> collection() : DatabaseCollection<T> = get<Database>().getCollection()
