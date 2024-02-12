package ge.baqar.gogia.gefolk.ui.media.artists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import ge.baqar.gogia.gefolk.R
import ge.baqar.gogia.gefolk.model.Artist


class ArtistsAdapter(
    private val dataSource: MutableList<Artist>,
    val clicked: (Artist) -> Unit
) : RecyclerView.Adapter<ArtistsAdapter.EnsembleViewHolder>() {
    inner class EnsembleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: AppCompatTextView by lazy {
            itemView.findViewById(R.id.artistTitle)
        }

        fun bind(artist: Artist) {
            name.text = artist.name

            itemView.setOnClickListener {
                clicked.invoke(artist)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistsAdapter.EnsembleViewHolder {
        return EnsembleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_artist, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ArtistsAdapter.EnsembleViewHolder, position: Int) {
        holder.bind(dataSource[position])
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }
}