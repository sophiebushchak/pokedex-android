package com.example.pokedata.utils

import androidx.recyclerview.widget.RecyclerView

/**
 * Generic endless scrolling implementation.
 * @param onReachBottom: Pass a lambda function that can be triggered when the RecyclerView can no
 * longer scroll downwards vertically.
 */
class EndlessRecyclerViewScroll(private val onReachBottom: () -> Unit): RecyclerView.OnScrollListener() {
    @Override
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val directionDown = 1;
        if (!recyclerView.canScrollVertically(directionDown) && dy > 0) {
            onReachBottom()
        }
    }
}