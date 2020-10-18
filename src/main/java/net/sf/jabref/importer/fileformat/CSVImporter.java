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
import net.sf.jabref.model.entry.MonthUtil;

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
	public List<BibEntry> importEntries(InputStream inStream, OutputPrinter status) throws IOException {
		if (inStream == null) {
			throw new IOException("No input given.");
		}

		List<BibEntry> bibitems = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		List<String> entries = new ArrayList<>();

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

			for (int i = 0; i < fields.length; i++) {
				//primeira linha = metadados
				if(fields[0].equals(types[0])) {
					break;
				}
				
				String Type = "";
				String PT = "";
				String pages = "";
				hm.clear();
				
				if(types[i].equals("Author")) {
					hm.put("author", fields[i]);
				}else if(types[i].contentEquals("Publisher")) {
					hm.put("publisher", fields[i]);
				}else if(types[i].equals("Title")) {
					hm.put("title", fields[i]);
				}else if(types[i].equals("Pages")) {
					hm.put("pages", fields[i]);
				}else if(types[i].equals("Month")) {
					hm.put("month", fields[i]);
				}else if(types[i].equals("Booktitle")) {
					hm.put("booktitle", fields[i]);
				}else if(types[i].equals("Year")) {
					hm.put("year", fields[i]);
				}else if(types[i].equals("Number")) {
					hm.put("number", fields[i]);
				}else if(types[i].equals("Volume")) {
					hm.put("volume", fields[i]);
				}else if(types[i].equals("Journal")) {
					hm.put("journal", fields[i]);
				}
			}

			BibEntry b = new BibEntry(DEFAULT_BIBTEXENTRY_ID, "article");
			
			 // Remove empty fields:
			 List<Object> toRemove = new ArrayList<>();
	            for (Map.Entry<String, String> field : hm.entrySet()) {
	                String content = field.getValue();
	                if ((content == null) || content.trim().isEmpty()) {
	                    toRemove.add(field.getKey());
	                }
	            }
	            for (Object aToRemove : toRemove) {
	                hm.remove(aToRemove);

	            }
	            
	            b.setField(hm);
	            bibitems.add(b);
		}

		return bibitems;
	}

	//peguei de outra função não sei se vai precisar
	private static String parsePages(String value) {
		int lastDash = value.lastIndexOf('-');
		return value.substring(0, lastDash) + "--" + value.substring(lastDash + 1);
	}

	//peguei de outra função não sei se vai precisar
	public static String parseMonth(String value) {

		String[] parts = value.split("\\s|\\-");
		for (String part1 : parts) {
			MonthUtil.Month month = MonthUtil.getMonthByShortName(part1.toLowerCase());
			if (month.isValid()) {
				return month.bibtexFormat;
			}
		}

		// Try two digit month
		for (String part : parts) {
			try {
				int number = Integer.parseInt(part);
				MonthUtil.Month month = MonthUtil.getMonthByNumber(number);
				if (month.isValid()) {
					return month.bibtexFormat;
				}
			} catch (NumberFormatException ignored) {
				// Ignored
			}
		}
		return null;
	}
}
