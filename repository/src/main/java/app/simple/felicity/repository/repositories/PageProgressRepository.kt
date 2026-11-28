package app.simple.felicity.repository.repositories

import android.content.Context
import android.util.Log
import app.simple.felicity.repository.database.instances.AudioDatabase
import app.simple.felicity.repository.models.PagePlaybackProgress
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores and caches the "where did I leave off" record for every browsing page.
 *
 * <p>Reading is deliberately synchronous through [get]: the page header asks for the
 * record on every rebind, and a database round-trip there would be visible while
 * scrolling. The whole table is therefore loaded once into [progress] and kept in sync
 * on every write.</p>
 *
 * <p>Writing happens from the playback service (see {@code FelicityPlayerService}), which
 * is the only component that knows both the active page (through the queue's origin key)
 * and the live seek position.</p>
 *
 * @author Hamza417
 */
@Singleton
class PageProgressRepository @Inject constructor(
        @param:ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "PageProgressRepository"

        /** Upper bound on stored rows; the least recently updated pages are dropped first. */
        const val MAX_ENTRIES = 500
    }

    private val database: AudioDatabase by lazy {
        AudioDatabase.getInstance(context)
    }

    private val _progress = MutableStateFlow<Map<String, PagePlaybackProgress>>(emptyMap())

    /** Snapshot of every stored page record, keyed by [PagePlaybackProgress.pageKey]. */
    val progress: StateFlow<Map<String, PagePlaybackProgress>> = _progress.asStateFlow()

    private val loadMutex = Mutex()

    @Volatile
    private var loaded = false

    private fun dao() = database.pagePlaybackProgressDao()

    /**
     * Loads the table into [progress] once per process. Safe to call from any coroutine —
     * concurrent callers are serialised and only the first one hits the database.
     */
    suspend fun ensureLoaded() {
        if (loaded) return
        loadMutex.withLock {
            if (loaded) return
            runCatching { dao().getAll() }
                .onSuccess { rows ->
                    _progress.value = rows.associateBy { it.pageKey }
                    loaded = true
                }
                .onFailure {
                    // Leave [loaded] false so the next caller retries — the database may
                    // simply not be ready yet during a cold start.
                    Log.w(TAG, "Unable to load page playback progress", it)
                }
        }
    }

    /** Returns the cached record for [pageKey], or null if that page has no saved position. */
    fun get(pageKey: String): PagePlaybackProgress? = _progress.value[pageKey]

    /**
     * Records the current song and offset for [pageKey].
     *
     * <p>The in-memory cache is updated first so the UI reflects the new state immediately;
     * the database write follows. A failed write only costs the record on a later cold
     * start, so it is logged rather than propagated.</p>
     */
    suspend fun record(pageKey: String, audioHash: Long, positionMs: Long) {
        ensureLoaded()

        val row = PagePlaybackProgress(
                pageKey = pageKey,
                audioHash = audioHash,
                positionMs = positionMs.coerceAtLeast(0L),
                updatedAt = System.currentTimeMillis()
        )

        _progress.update { it + (pageKey to row) }

        runCatching { dao().upsert(row) }
                .onFailure { Log.w(TAG, "Unable to persist progress for $pageKey", it) }
    }

    /** Forgets the record for a single page. */
    suspend fun clear(pageKey: String) {
        ensureLoaded()
        _progress.update { it - pageKey }
        runCatching { dao().delete(pageKey) }
                .onFailure { Log.w(TAG, "Unable to clear progress for $pageKey", it) }
    }

    /**
     * Drops records whose song is no longer in the library and enforces [MAX_ENTRIES],
     * then refreshes the cache. Intended for startup and post-scan, on an IO dispatcher.
     */
    suspend fun cleanup() {
        runCatching {
            dao().deleteOrphans()
            dao().prune(MAX_ENTRIES)
            _progress.value = dao().getAll().associateBy { it.pageKey }
            loaded = true
        }.onFailure {
            Log.w(TAG, "Page progress cleanup failed", it)
        }
    }
}
