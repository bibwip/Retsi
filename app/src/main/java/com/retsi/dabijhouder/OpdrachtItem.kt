package com.retsi.dabijhouder


class OpdrachtItem(val id: Int, val typeOpdracht: String, val vakNaam: String, val titel: String,
                   val datum: String, val beschrijving: String, val datumTagSorter: Int?,
                   val typeOpdracht_key: String?) {
    var isExpanded = false
}