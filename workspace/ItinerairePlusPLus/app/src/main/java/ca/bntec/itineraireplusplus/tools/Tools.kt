package ca.bntec.itineraireplusplus.tools

class Tools {

    companion object {
        const val SECONDS = 60
        const val MINUTES = 60
        const val HOUR = (SECONDS * MINUTES)
        const val DAY = (24 * HOUR)
        const val WEEK = (7 * DAY)

        const val FMT_OTHER = 0
        const val FMT_HMS_SHORT = 1
        const val FMT_HMS_LONG = 2
        const val FMT_HM_SHORT = 3
        const val FMT_HM_LONG = 4

        /** ---------------------------------------------------------------------------------------
         * Convertir un nombre de seconde(s) en une chaîne de caractère lisible par un humain.
         * En format court ou long.
         *
         * Formats de sortie:
         *
         * format long
         *  [9]9 hr(s) [9]9 min(s) [9]9 sec(s)
         *  [9]9 hr(s) [9]9 min(s)
         * format court
         *  hh:mm:ss
         *  hh:mm
         * autre format
         *  9s 9j hh:mm:ss
         *
         * */
        fun convertSecondsToTime(
            seconds: Int,               /** le nombre de secondes a convertir */
            type: Int                   /** type de format désiré */
        )
            : String                    /** voir formats de sortie dans le description */
        {
            var seconds = seconds
            var stringFormatted = ""

            val w = seconds / WEEK
            seconds -= w * WEEK
            val d = seconds / DAY
            seconds -= d * DAY
            val h = seconds / HOUR
            seconds -= h * HOUR
            val m = seconds / MINUTES
            seconds -= m * MINUTES
            val s = seconds

            val sh = if(h > 1) "s" else ""
            val sm = if(m > 1) "s" else ""
            val ss = if(s > 1) "s" else ""

            stringFormatted =
                when (type) {
                    FMT_HMS_SHORT -> String.format("%02d:%02d%02d", h, m, s)
                    FMT_HMS_LONG ->
                        if (s > 0 && h > 0) String.format("%d hr%s %d min%s %d sec%s", h, sh, m, sm, s, ss)
                        else if (s > 0 && m > 0) String.format("%d min%s %d sec%s", m, sm, s, ss)
                        else if (s > 0) String.format("%d sec%s", s, ss)
                        else if (m > 0) String.format("%d min%s", m, sm)
                        else String.format("%d hr%s", h, sh)
                    FMT_HM_SHORT -> String.format("%02d:%02d", h, m)
                    FMT_HM_LONG ->
                        if (m > 0 && h > 0) String.format("%d hr%s %d min%s", h, sh, m, sm)
                        else if (h > 0) String.format("%d hr%s", h, sh)
                        else String.format("%d min%s", m, sm)
                    FMT_OTHER -> otherFormat(w, d, h, m, s)
                    else -> otherFormat(w, d, h, m, s)
                }

            return stringFormatted
        }
        private fun otherFormat(w: Int, d: Int, h: Int, m: Int, s: Int): String {

            return if (w > 0) String.format("%ds %dj %02d:%02d:%02d", w, d, h, m, s)
            else if (d > 0) String.format("%dj %02d:%02d:%02d", d, h, m, s)
            else String.format("%02d:%02d:%02d", h, m, s)
        }
    }
}