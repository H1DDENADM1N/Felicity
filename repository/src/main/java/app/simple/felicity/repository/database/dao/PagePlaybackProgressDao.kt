package app.simple.felicity.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.simple.felicity.repository.models.PagePlaybackProgress

/**
 * Handles reads and writes for the {@code page_playback_progress} table.
 *
 * <p>Writes go through [upsert] so each page keeps exactly one row — the newest playback
 * position always replaces the previous one. Reads are normally served from the in-memory
 * cache in {@code PageProgressRepository}; the queries here back that cache and the
 * periodic cleanup.</p>
 *
 * @author Hamza417
 */
@Dao
interface PagePlaybackProgressDao {

    /** Returns every stored page record. Used to seed the in-memory cache once per process. */
    @Query("SELECT * FROM page_playback_progress")
    suspend fun getAll(): List<PagePlaybackProgress>

    /** Returns the record for a single page, or null if that page has never been played. */
    @Query("SELECT * FROM page_playback_progress WHERE page_key = :pageKey LIMIT 1")
    suspend fun get(pageKey: String): PagePlaybackProgress?

    /** Inserts or replaces the record for a page. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: PagePlaybackProgress)

    /** Removes the record for a single page. */
    @Query("DELETE FROM page_playback_progress WHERE page_key = :pageKey")
    suspend fun delete(pageKey: String)

    /**
     * Drops records pointing at songs that are no longer in the library.
     *
     * <p>Called after a library scan and once on startup, so deleted files do not leave
     * dangling "resume" entries behind forever.</p>
     */
    @Query("DELETE FROM page_playback_progress WHERE audio_hash NOT IN (SELECT hash FROM audio)")
    suspend fun deleteOrphans()

    /**
     * Keeps only the [maxEntries] most recently updated records.
     *
     * <p>Page keys include album/artist/genre/folder ids, so a large library can produce
     * thousands of records over time. The cap keeps the table — and the cache seeded from
     * it — bounded without affecting pages the user actually revisits.</p>
     */
    @Query("""
        DELETE FROM page_playback_progress
        WHERE page_key NOT IN (
            SELECT page_key FROM page_playback_progress
            ORDER BY updated_at DESC
            LIMIT :maxEntries
        )
    """)
    suspend fun prune(maxEntries: Int)
}
