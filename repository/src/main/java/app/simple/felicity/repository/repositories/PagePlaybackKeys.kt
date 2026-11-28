package app.simple.felicity.repository.repositories

/**
 * Canonical encoding of the keys used by [PageProgressRepository].
 *
 * <p>A page key is {@code kind:id}, where {@code id} is the entity id the corresponding
 * page was opened with:</p>
 *
 * <ul>
 *   <li>{@code album:42} — album page</li>
 *   <li>{@code artist:7} / {@code album_artist:7} — the two artist pages share the same
 *       entity type but must keep separate progress, hence two kinds</li>
 *   <li>{@code composer:7} — composer page</li>
 *   <li>{@code genre:3} — genre page</li>
 *   <li>{@code year:2020} — year page (the id is a hash of the year string)</li>
 *   <li>{@code playlist:5} — user playlist</li>
 *   <li>{@code folder:1234} — folder page (the id is a hash of the folder path)</li>
 * </ul>
 *
 * <p>Keeping the construction in one place matters: these strings are written to the
 * database and read back on later launches, so a typo in a single page would silently
 * break resume for that page only.</p>
 *
 * @author Hamza417
 */
object PagePlaybackKeys {

    const val ALBUM = "album"
    const val ARTIST = "artist"
    const val ALBUM_ARTIST = "album_artist"
    const val COMPOSER = "composer"
    const val GENRE = "genre"
    const val YEAR = "year"
    const val PLAYLIST = "playlist"
    const val FOLDER = "folder"

    fun of(kind: String, id: Long): String = "$kind:$id"
}
