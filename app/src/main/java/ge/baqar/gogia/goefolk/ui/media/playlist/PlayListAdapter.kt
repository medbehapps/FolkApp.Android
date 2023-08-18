package ge.baqar.gogia.goefolk.ui.media.playlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import ge.baqar.gogia.goefolk.R
import ge.baqar.gogia.goefolk.http.response.PlayList

class PlayListAdapter(
    val dataSource: MutableList<PlayList>,
    val itemClicked: (PlayList) -> Unit,
    val itemsChecked: (MutableList<PlayList>) -> Unit
) :
    RecyclerView.Adapter<PlayListAdapter.PlayListViewHolder>() {

    inner class PlayListViewHolder(val itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val name: AppCompatTextView by lazy {
            itemView.findViewById(R.id.playlistName)
        }
        private val checkBox: AppCompatCheckBox by lazy {
            itemView.findViewById(R.id.deletePlaylistCheckbox)
        }

        fun bind(playList: PlayList, position: Int) {
            itemView.setOnClickListener {
                itemClicked.invoke(playList)
            }
            name.text = playList.name

            checkBox.isChecked = playList.isSelected
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                playList.isSelected = isChecked
                itemsChecked.invoke(dataSource.filter { a -> a.isSelected }.toMutableList())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {
        return PlayListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    override fun onBindViewHolder(holder: PlayListViewHolder, position: Int) {
        val playList = dataSource[position]
        holder.bind(playList, position)
    }
}