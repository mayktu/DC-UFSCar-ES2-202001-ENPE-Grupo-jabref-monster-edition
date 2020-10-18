package net.sf.jabref.importer.fileformat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jabref.importer.ImportFormatReader;
import net.sf.jabref.importer.OutputPrinter;
import net.sf.jabref.model.entry.BibEntry;

public class CSVImporter extends ImportFormat {

    /**
     * Return the name of this import format.
     */
    @Override
    public String getFormatName() {
        return "CSV";
    }

    @Override
    public boolean isRecognizedFormat(InputStream in) throws IOException {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public String getExtensions() {
        return "csv";
    }

    @Override
    public String getCLIId() {
        return "csv";
    }

    @Override
    public List<BibEntry> importEntries(InputStream inStream, OutputPrinter status) throws IOException {
        if (inStream == null) {
            throw new IOException("No input given.");
        }

        List<BibEntry> bibitems = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        List<String> entries = new ArrayList<>();
        String tipo = "";
        try (BufferedReader input = new BufferedReader(ImportFormatReader.getReaderDefaultEncoding(inStream))) {
            String str;

            while ((str = input.readLine()) != null) {
                entries.add(str);
            }
        }


        Map<String, String> hm = new HashMap<>();
        String[] types = entries.get(0).split(",");
        for (String entry : entries) {
            String[] fields = entry.split(",");

            //primeira linha = metadados
            if (fields[0].equals(types[0])) {
                continue;
            }
            for (int i = 0; i < fields.length; i++) {

                if (types[i].equals("Author")) {
                    hm.put("author", fields[i].replaceAll("\"", ""));
                } else if (types[i].contentEquals("Publisher")) {
                    hm.put("publisher", fields[i].replaceAll("\"", ""));
                } else if (types[i].equals("Title")) {
                    hm.put("title", fields[i].replaceAll("\"", ""));
                } else if (types[i].equals("Pages")) {
                    hm.put("pages", fields[i]);
                } else if (types[i].equals("Month")) {
                    hm.put("month", fields[i]);
                } else if (types[i].equals("Booktitle")) {
                    hm.put("booktitle", fields[i].replaceAll("\"", ""));
                } else if (types[i].equals("Year")) {
                    hm.put("year", fields[i]);
                } else if (types[i].equals("Number")) {
                    hm.put("number", fields[i]);
                } else if (types[i].equals("Volume")) {
                    hm.put("volume", fields[i]);
                } else if (types[i].equals("Journal")) {
                    hm.put("journal", fields[i].replaceAll("\"", ""));
                }
            }

            switch (Integer.parseInt(fields[0])) {
            case 1:
                tipo = "book";
                break;
            case 2:
                tipo = "booklet";
                break;
            case 3:
                tipo = "proceedings";
                break;
            case 5:
                tipo = "inbook";
                break;
            case 6:
                tipo = "inproceedings";
                break;
            case 7:
                tipo = "article";
                break;
            case 8:
                tipo = "manual";
                break;
            case 9:
                tipo = "mastersthesis";
                break;
            case 10:
                tipo = "conference";
                break;
            case 13:
                tipo = "techreport";
                break;
            case 14:
                tipo = "unpublished";
                break;

            default:
                tipo = "article";
            }

            BibEntry b = new BibEntry(DEFAULT_BIBTEXENTRY_ID, tipo);

            b.setField(hm);
            bibitems.add(b);

        }

        return bibitems;
    }

}
