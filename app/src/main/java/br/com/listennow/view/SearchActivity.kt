package br.com.listennow.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.listennow.databinding.SearchBinding
import br.com.listennow.to.TOSongYTSearch
import br.com.listennow.ui.activity.AbstractUserActivity
import br.com.listennow.ui.recyclerview.ListSongsYTAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

class SearchActivity: AbstractUserActivity() {
    private val binding by lazy {
        SearchBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: ListSongsYTAdapter
    private var songs: List<TOSongYTSearch> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = binding.root
        setContentView(view)

        lifecycleScope.launch {
            user.filterNotNull().collect {user ->
                adapter = ListSongsYTAdapter(this@SearchActivity, user.id, songs)

                configRecyclerSongs()
            }
        }
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

    private fun search(song: String) {
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

                    val ytSong = TOSongYTSearch(id, title, smallThumb, artist)

                    songs.add(ytSong)
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