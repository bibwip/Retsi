package com.retsi.dabijhouder


class OpdrachtItem(val id: Int, val typeOpdracht: String, val vakNaam: String, val titel: String,
                   val datum: String, val beschrijving: String, var belangerijk: Int, val datumTagSorter: Int?,
                   val typeOpdracht_key: String?) {
    var isExpanded = false

    constructor() : this(-1, "error", "error", "error", "00-00-0000", "", 0, null, null)
}