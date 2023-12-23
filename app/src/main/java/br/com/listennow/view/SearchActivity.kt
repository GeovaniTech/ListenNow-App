package br.com.listennow.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.listennow.databinding.SearchBinding
import br.com.listennow.model.Song
import br.com.listennow.to.TOSongYTSearch
import br.com.listennow.ui.recyclerview.ListSongsYTAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

class SearchActivity: AppCompatActivity() {
    private lateinit var binding: SearchBinding
    private lateinit var adapter: ListSongsYTAdapter
    private var songs: List<TOSongYTSearch> = emptyList<TOSongYTSearch>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        adapter = ListSongsYTAdapter(this, songs)

        configRecyclerSongs()
        configButtonSearch()
    }

    private fun configButtonSearch() {
        binding.btnPesquisar.setOnClickListener {
            search(binding.barraDePesquisa.text.toString())
        }
    }

    private fun configRecyclerSongs() {
        val listSongs = binding.listSongsYT

        listSongs.layoutManager = LinearLayoutManager(this)
        listSongs.setHasFixedSize(true)
        listSongs.adapter = adapter
    }

    fun search(song: String) {
        val songs = mutableListOf<TOSongYTSearch>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val results = URL("https://api.devpree.com.br/listennow/search/${song}").readText()

                val jsonArray = JSONArray(results)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val id = jsonObject.getString("videoId")
                    val title = jsonObject.getString("title")
                    val thumbnail = jsonObject.getString("thumbnails")
                    val artists = jsonObject.getString("artists")

                    val jsonArtists = JSONArray(artists)
                    val jsonThumbNails = JSONArray(thumbnail)

                    val jsonThumb = jsonThumbNails.getJSONObject(0)
                    val smallThumb = jsonThumb.getString("url")

                    val jsonArtist = jsonArtists.getJSONObject(0)
                    val artist = jsonArtist.getString("name")

                    val song = TOSongYTSearch(id, title, smallThumb, artist)

                    songs.add(song)
                }

                withContext(Dispatchers.Main) {
                    adapter.update(songs)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}