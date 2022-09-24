package ca.bntec.itineraireplusplus.tools

class Tools {

    companion object {
        private const val SECONDS = 60
        private const val MINUTES = 60
        private const val HOUR = (SECONDS * MINUTES)
        private const val DAY = (24 * HOUR)
        private const val WEEK = (7 * DAY)

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
         *  [9]9 heure(s) [9]9 minute(s) [9]9 seconde(s)
         *  [9]9 heure(s) [9]9 minute(s)
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

//            stringFormatted = String.format("%d semaine(s) %d jour(s) %d heure(s) %d minute(s) %d seconde(s)", w, j , h , m , s);
            val otherFormat =
                if (w > 0) String.format("%ds %dj %02d:%02d:%02d", w, d, h, m, s)
                else if (d > 0) String.format("%dj %02d:%02d:%02d", d, h, m, s)
                else String.format("%02d:%02d:%02d", h, m, s)

            stringFormatted =
                when (type) {
                    FMT_HMS_SHORT -> String.format("%02d:%02d%02d", h, m, s)
                    FMT_HMS_LONG ->
                        if (h > 0)
                            String.format("%d heure%s %d minute%s %d seconde%s", h, if(h > 1) "s" else "", m, if(m > 1) "s" else "", s, if(s > 1) "s" else "")
                        else if (m > 0)
                            String.format("%d minute%s %d seconde%s", m, if(m > 1) "s" else "", s, if(s > 1) "s" else "")
                        else
                            String.format("%d seconde%s", s, if(s > 1) "s" else "")
                    FMT_HM_SHORT -> String.format("%02d:%02d", h, m)
                    FMT_HM_LONG ->
                        if (h > 0) String.format("%d heure%s %d minute%s", h, if(h > 1) "s" else "", m, if(m > 1) "s" else "")
                        else String.format("%d minute%s", m, if(m > 1) "s" else "")
                    FMT_OTHER -> otherFormat
                    else -> otherFormat
                }

            if (s > 0)
                if (m > 0)
                    if (h > 0)
                        String.format("%d heure%s %d minute%s %d seconde%s", h, if(h > 1) "s" else "", m, if(m > 1) "s" else "", s, if(s > 1) "s" else "")
                    else
                        String.format("%d minute%s %d seconde%s", h, if(h > 1) "s" else "", m, if(m > 1) "s" else "", s, if(s > 1) "s" else "")
                else
                    if (h > 0)
                        String.format("%d heure%s %d minute%s %d seconde%s", h, if(h > 1) "s" else "", m, if(m > 1) "s" else "", s, if(s > 1) "s" else "")
                    else
                        String.format("%d seconde%s", h, if(h > 1) "s" else "", m, if(m > 1) "s" else "", s, if(s > 1) "s" else "")
            else if (m > 0)
                    if (h > 0)
                        String.format("%d heure%s %d minute%s", m, if(m > 1) "s" else "", s, if(s > 1) "s" else "")
                    else
                        String.format("%d minute%s", m, if(m > 1) "s" else "", s, if(s > 1) "s" else "")
                else
                    if (h > 0)
                        String.format("%d heure%s", m, if(m > 1) "s" else "", s, if(s > 1) "s" else "")
                    else
                        String.format("n/a", m, if(m > 1) "s" else "", s, if(s > 1) "s" else "")


            return stringFormatted
        }
    }
}