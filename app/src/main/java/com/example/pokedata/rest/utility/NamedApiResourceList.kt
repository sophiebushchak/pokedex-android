package com.example.pokedata.rest.utility

/**
 * Representation of the response given by the API when an endpoint is queried without any parameters.
 * This is used for pagination.
 */
data class NamedApiResourceList(
        /**
         * The total number of resources available from this API.
         */
        val count: Int,
        /**
         * The URL for the next page in the list.
         */
        val next: String,
        /**
         * The URL for the previous page in the list.
         */
        val previous: String,
        /**
         * A list of named API resources.
         */
        val results: List<NamedApiResource>
)