package dk.brianjohnsen.test.util

import com.xlson.groovycsv.CsvParser

/**
 * Indlæser CSV og konverterer hver linje til et model objekt.
 *
 * Se: https://github.com/xlson/groovycsv
 */
class CsvModel {

    private static final String STANDARD_SEPARATOR = ','

    Map data

    /**
     * Laver model objekter udfra hver linje i den angivne CSV streng.
     */
    static List<CsvModel> readCsvText(Map args = [:], String csvText) {
        args.separator = args.separator ?: STANDARD_SEPARATOR
        def data = new CsvParser().parse(args, csvText)
        int lineindex = 1 //fraregnet header
        data.collect {
            def map = it.toMap() << [linjenummer: ++lineindex]
            new CsvModel(data: map)
        }
    }

    /**
     * Laver model objekter udfra hver linje i den angivne CSV fil.
     */
    static List<CsvModel> readCsvFile(String filnavn) {
        readCsvText(new File(filnavn).text)
    }

    /**
     * Sørger for type konvertering.
     */
    @Override
    Object getProperty(String propertyName) {
        if (propertyName == 'data') {
            return data
        }

        String value = data[propertyName].toString().trim()
        if (!value || value == 'null') {
            return null
        } else if (value.startsWith('[') && value.endsWith(']')) { // liste
            return Eval.me(value)
        } else if (value in ['true', 'false']) { // booleans
            return value.toBoolean()
        } else if (value.integer) { // int's
            return value.toInteger()
        }
        return value
    }

    @Override
    String toString() {
        data.toMapString()
    }
}
