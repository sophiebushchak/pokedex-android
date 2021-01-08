package com.example.pokedata.utils

import androidx.recyclerview.widget.RecyclerView

/**
 * Generic endless scrolling implementation.
 * @param onReachBottom: Pass a function that can be triggered when the RecyclerView can no
 * longer scroll downwards vertically.
 * @param canCall: Public property that can be set to true or false as needed from outside this class.
 * This is used to determine if the [onReachBottom] function should be called when hitting the bottom
 * of the screen right now or not. It can for example not be called while resources are already
 * being loaded.
 */
class EndlessRecyclerViewScroll(private val onReachBottom: () -> Unit, var canCall: Boolean) :
    RecyclerView.OnScrollListener() {
    @Override
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (canCall) {
            val directionDown = 1;
            if (!recyclerView.canScrollVertically(directionDown) && dy > 0) {
                println("Calling onReachBottom!")
                onReachBottom()
            }
        }
    }
}