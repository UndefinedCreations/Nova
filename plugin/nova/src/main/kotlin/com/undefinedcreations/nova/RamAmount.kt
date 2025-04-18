package com.undefinedcreations.nova

/**
 * This enum is used to select the ram type to be selected.
 *
 * @param flag The jvm flag name.
 * @since 1.0
 */
enum class RamAmount(val flag: String) {
    /**
     * Represents a gigabyte.
     */
    GIGABYTE("G"),

    /**
     * Represents a megabyte.
     */
    MEGABYTE("M")
}