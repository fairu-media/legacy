package fairu.utils.ext

import kotlinx.datetime.Instant
import java.util.*

fun Date(instant: Instant): Date = Date(instant.toEpochMilliseconds())
