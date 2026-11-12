
package fr.estiam.traillog.data.local

import androidx.paging.PagingSource
import androidx.paging.PagingState

class TrailPagingSource(
    private val source: PagingSource<Int, TrailEntity>
) : PagingSource<Int, fr.estiam.traillog.model.Trail>() {

    init {
        source.registerInvalidatedCallback {
            invalidate()
        }
    }

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, fr.estiam.traillog.model.Trail> {

        return when (val result = source.load(params)) {
            is LoadResult.Page -> LoadResult.Page(
                data = result.data.map { it.toTrail() },
                prevKey = result.prevKey,
                nextKey = result.nextKey,
                itemsBefore = result.itemsBefore,
                itemsAfter = result.itemsAfter
            )

            is LoadResult.Error -> LoadResult.Error(result.throwable)

            is LoadResult.Invalid -> LoadResult.Invalid()
        }
    }

    override fun getRefreshKey(
        state: PagingState<Int, fr.estiam.traillog.model.Trail>
    ): Int? = state.anchorPosition
}
