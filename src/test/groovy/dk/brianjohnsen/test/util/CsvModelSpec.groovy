package dk.brianjohnsen.test.util


import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class CsvModelSpec extends Specification {

    public static final String TEST_CSV_FIL = 'src/test/resources/csv/test_csv_input.csv'


    void "læs CSV fra tester"() {
        when:
            List<CsvModel> models = CsvModel.readCsvFile('src/test/resources/csv/test_input.csv')

        then:
            models.size() == 16

        and:
            with(models.first()) {
                PersonCPRNummer == ['0000005429', '0000020326', '0000035246']
                IndkomstAar == [2017]
                status == 200
            }

        and:
            with(models.find { it.linjenummer == 7 }) {
                PersonCPRNummer == null
                IndkomstAar == []
                status == 400
            }
        and:
            with(models.find { it.linjenummer == 15 }) {
                PersonCPRNummer == ['0000005429', 'XXX0020326', '0000035246']
                IndkomstAar == [2017]
                status == 400
            }
    }


    void "read CSV lines"() {
        when:
            List<CsvModel> models = CsvModel.readCsvText(separator: '|', new File(TEST_CSV_FIL).text)

        then:
            models.size() == 5

        and:
            with(models.first()) {
                PersonCPRNummer == ['0000005429', '0000020326', '0000035246']
                IndkomstÅr == [2015, 2016, 2017]
                status == 200
            }

        and:
            with(models[1]) {
                !PersonCPRNummer
                !IndkomstÅr
                !status
            }

        and:
            with(models[2]) {
                PersonCPRNummer == ['42']
                IndkomstÅr == [42]
                status == null
            }

        and:
            with(models[3]) {
                PersonCPRNummer == ['42']
                IndkomstÅr == null
                status == 42
            }

        and:
            with(models.last()) {
                PersonCPRNummer == []
                IndkomstÅr == []
                status == 0
            }
    }


    void "read CSV lines med specielle tegn"() {
        when:
            List<CsvModel> models = CsvModel.readCsvText([separator: ','], csv)

        then:
            with(models.first()) {
                PersonCPRNummer == ["00000054|29"]
                IndkomstÅr == [2015, 2017]
                status == 200
            }
    }


    def csv = '''PersonCPRNummer,IndkomstÅr,status
"['00000054|29']","[2015,2017]","200"
'''

}
