package app.simple.felicity.repository.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Remembers, per browsing page, the last song that was playing and how far into it the
 * user got, so the page can offer a "resume" action instead of always starting from the
 * first track.
 *
 * <p>The row is keyed by [pageKey] and references the song through [audioHash] rather
 * than through a queue index. That is what makes the record survive sort-order changes:
 * the position of a song inside an album page changes as soon as the user picks another
 * sort style, but its hash never does.</p>
 *
 * <p>Rows are intentionally not foreign-keyed to {@code audio}. A deleted track must not
 * cascade away the page record, because the record is also the thing that lets the UI
 * explain "the last played song is gone" instead of silently losing the page's state.
 * Orphaned rows are pruned periodically instead.</p>
 *
 * @author Hamza417
 */
@Entity(
        tableName = "page_playback_progress",
        indices = [Index("updated_at")]
)
data class PagePlaybackProgress(
        /** Encoded page identity, e.g. {@code album:42} or {@code folder:1234}. */
        @PrimaryKey @ColumnInfo(name = "page_key") val pageKey: String,
        /** XXHash64 fingerprint of the last played song. */
        @ColumnInfo(name = "audio_hash") val audioHash: Long,
        /** Playback offset inside that song, in milliseconds. */
        @ColumnInfo(name = "position_ms") val positionMs: Long,
        /** Wall-clock time of the last write, used for pruning. */
        @ColumnInfo(name = "updated_at") val updatedAt: Long
)
