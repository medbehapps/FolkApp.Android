package ge.baqar.gogia.goefolk.ui.media.playlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import ge.baqar.gogia.goefolk.R
import ge.baqar.gogia.goefolk.model.Song

class PlayListSongsAdapter(
    var dataSource: MutableList<Song>,
    private val callback: (Int, Song) -> Unit,
    private val itemsChecked: (MutableList<Song>) -> Unit
) : RecyclerView.Adapter<PlayListSongsAdapter.PlayListSongsViewHolder>() {

    inner class PlayListSongsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songName: AppCompatTextView by lazy {
            itemView.findViewById(R.id.songName)
        }
        private val checkBox: AppCompatCheckBox by lazy {
            itemView.findViewById(R.id.songChecked)
        }

        fun bind(position: Int, song: Song) {
            songName.text = song.detailedName()
            itemView.setOnClickListener {
                callback(position, song)
            }
            checkBox.isChecked = song.isSelected
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                song.isSelected = isChecked
                itemsChecked.invoke(dataSource.filter { a -> a.isSelected }.toMutableList())
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayListSongsViewHolder {
        return PlayListSongsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_playlist_song_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlayListSongsViewHolder, position: Int) {
        holder.bind(position, dataSource[position])
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }
}