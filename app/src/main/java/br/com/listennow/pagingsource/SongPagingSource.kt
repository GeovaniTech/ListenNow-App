package br.com.listennow.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import br.com.listennow.model.Song
import br.com.listennow.repository.SongRepository

class SongPagingSource (
    private val songRepository: SongRepository,
    private val filter: String?
): PagingSource<Int, Song>() {
    private val START = 0

    override fun getRefreshKey(state: PagingState<Int, Song>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        val start = params.key ?: START
        val data = songRepository.getSongs(start, params.loadSize, filter)

        return LoadResult.Page(
            data = data,
            prevKey = if (start == START) null else start - params.loadSize,
            nextKey = if (data.isEmpty()) null else start + params.loadSize
        )
    }
}