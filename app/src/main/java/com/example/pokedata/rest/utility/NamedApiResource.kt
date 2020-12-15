package com.example.pokedata.rest.utility

/**
 * Representation of an API resource.
 * Has a name and a URL to send an HTTP request to in order to access it.
 */
data class NamedApiResource (
        /**
         * The name of the referenced resource.
         */
        val name: String,
        /**
         * The URL of the referenced resource.
         */
        val url: String
)