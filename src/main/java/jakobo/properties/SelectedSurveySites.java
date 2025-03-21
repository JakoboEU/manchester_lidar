package jakobo.properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectedSurveySites {

    private final List<String> selectedTitles;

    public SelectedSurveySites(List<String> selectedTitles) {
        this.selectedTitles = Collections.unmodifiableList(selectedTitles);
    }

    public static SelectedSurveySites getSelectedSurveySites() throws IOException {
        final List<String> selectedTitles = new ArrayList<>();

        try (
                Reader reader = new BufferedReader(new InputStreamReader(SelectedSurveySites.class.getClassLoader().getResourceAsStream("surveyed_sites.csv")));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())
        ) {

            for (CSVRecord csvRecord : csvParser) {
                final String title = csvRecord.get("title");
                selectedTitles.add(title);
            }

        }

        return new SelectedSurveySites(selectedTitles);
    }

    public boolean contains(String surveyGridTitle) {
        return this.selectedTitles.contains(surveyGridTitle);
    }
}
