package app.simple.felicity.callbacks;

import android.view.View;
import android.widget.ImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import app.simple.felicity.repository.models.Album;
import app.simple.felicity.repository.models.Artist;
import app.simple.felicity.repository.models.Audio;
import app.simple.felicity.repository.models.Folder;
import app.simple.felicity.repository.models.Genre;
import app.simple.felicity.repository.models.YearGroup;

public interface GeneralAdapterCallbacks {
    
    default void onPanelItemClicked(@StringRes int title, @NonNull View view) {
    
    }
    
    default void onSongClicked(@NonNull List <Audio> songs, int position, View view) {
    
    }
    
    default void onSongLongClicked(@NonNull List <Audio> songs, int position, @Nullable ImageView imageView) {
    
    }
    
    default void onPlayClicked(List <Audio> audios, int position) {
    
    }
    
    default void onShuffleClicked(List <Audio> audios, int position) {
    
    }
    
    /**
     * Invoked when the "resume" button in a page header is tapped.
     *
     * <p>The callback receives the page's songs in their current sort order; resolving
     * the saved song hash to an index and restoring the seek offset is the host's job,
     * because only the host knows the page key and its saved progress.</p>
     *
     * @param songs The page's songs, already ordered by the active sort style.
     */
    default void onContinueClicked(@NonNull List <Audio> songs) {
    
    }
    
    default void onShuffleLongClicked(List <Audio> audios, int position) {
    
    }
    
    default void onArtistClicked(@NonNull List <Artist> artist, int position, @NonNull View view) {
    
    }
    
    default void onArtistLongClicked(@NonNull List <Artist> artists, int position, @Nullable ImageView imageView) {
    
    }
    
    default void onAlbumArtistClicked(@NonNull List <Artist> artists, int position, @NonNull View view) {
    
    }
    
    default void onAlbumArtistLongClicked(@NonNull List <Artist> artists, int position, @Nullable ImageView imageView) {
    
    }
    
    default void onAlbumClicked(List <Album> albums, int position, View view) {
    
    }
    
    default void onAlbumLongClicked(List <Album> albums, int position, @Nullable ImageView imageView) {
    
    }
    
    default void onMenuClicked(@NonNull View view) {
    
    }
    
    default void onFilterClicked(@NonNull View view) {
    
    }
    
    default void onSortClicked(@NonNull View view) {
    
    }
    
    default void onSearchClicked(@NonNull View view) {
    
    }
    
    default void onGenreClicked(@NonNull Genre genre, @NonNull View view) {
    
    }
    
    default void onFolderClicked(@NonNull Folder folder, @NonNull View view) {
    
    }
    
    default void onYearGroupClicked(@NonNull YearGroup yearGroup, @NonNull View view) {
    
    }
    
    default void onComposerClicked(@NonNull List <Artist> composers, int position, @NonNull View view) {
    
    }
    
    default void onComposerLongClicked(@NonNull List <Artist> composers, int position, @Nullable ImageView imageView) {
    
    }
}
