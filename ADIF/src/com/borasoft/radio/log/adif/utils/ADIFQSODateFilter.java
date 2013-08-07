package com.borasoft.radio.log.adif.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;

import com.borasoft.radio.log.adif.ADIFReader;
import com.borasoft.radio.log.adif.ADIFStream;
import com.borasoft.radio.log.adif.ADIFWriter;
import com.borasoft.radio.log.adif.ADIFObject;

public class ADIFQSODateFilter {
	private int totalRecords = 0;
	private int filteredRecords = 0;

	/**
	 * @param args
	 * arg[0] - Input ADIF filename
	 * arg[1] - Output ADIF filename
	 * arg[2] - Starting QSO date (yyyymmdd)
	 * arg[3] - Ending QSO date (yyyymmdd)
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		System.out.println("\nADIF QSO date filter, 2011, BoraSoft(c)\n");

		if (args.length != 4) {
			System.out.println("Syntax Error: \n");
			System.out.println("java ADIFQSOFilter [input file][output file][starting QSO date(yyyymmdd)][ending QSO date]\n");
			System.exit(-1);
		}
				
		System.out.println("Processing QSO records between [" + args[2] + "] and [" + args[3] + "] >>>");
		
		String inputFilename = args[0];
		String outputFilename = args[1];
		String startingDate = args[2];
		String endingDate = args[3];

		ADIFQSODateFilter filter = new ADIFQSODateFilter();
		filter.processADIF(inputFilename, outputFilename, startingDate, endingDate);
		
		System.out.println("Procesing completed successfully.");
	}
	
	public void processADIF(String inputFilename, String outputFilename, String startingDate, String endingDate) 
			throws FileNotFoundException, IOException {
		ADIFStream adif = new ADIFStream();
		FileInputStream stream = new FileInputStream(inputFilename);
		InputStreamReader reader = new InputStreamReader(stream);
		ADIFReader adifReader = new ADIFReader(reader);
		adif = adifReader.readADIFStream();
		reader.close();
		
		FileOutputStream ostream = new FileOutputStream(outputFilename);
		OutputStreamWriter writer = new OutputStreamWriter(ostream);
		
		Vector<ADIFObject> records = adif.getRecords();
		ADIFStream newADIF = new ADIFStream();
		ADIFObject record;
		ADIFObject prevRecord=null; // remove duplicated records
		String qsoDate;
		newADIF.setHeader(adif.getHeader());
		int totalFiltered = 0;
		if (records!=null) {
			totalRecords = records.size();
			for (int i=0; i<totalRecords; i++) {
				record = records.elementAt(i);
				if(!isDup(prevRecord,record)) {
					// Temporary: Set the QSL sent/received to false.
					// These values may be set to "Y" when exported from LoTW and/or eQSL.
					//record.setQSLSent("N");
					//record.setQSLReceived("N");
					qsoDate = record.getQSODate();
					if (Integer.decode(qsoDate)>=Integer.decode(startingDate) && Integer.decode(qsoDate)<=Integer.decode(endingDate)) {
						newADIF.addRecord(record);
						filteredRecords++;
					}
				} else {
					System.out.println("Removed a duplicated entry:");
					System.out.print("\t" + record.getCall());
					System.out.print(" on " + record.getQSODate() +", ");
					System.out.println(record.getTimeOn() +" UTC.");
				}
				prevRecord = record;
			}
			ADIFWriter adifWriter = new ADIFWriter(writer,newADIF);
			adifWriter.writeADIFStream();
			writer.close();	
		}
		
		System.out.println("Total number of QSO records processed: " + totalRecords + ".");
		System.out.println("Total number of QSO records written to the new file: " + filteredRecords + ".");
	}

	public int getTotalRecords() {
		return totalRecords;
	}
	
	public int getFilteredRecords() {
		return filteredRecords;
	}
	
	private boolean isDup(ADIFObject prev, ADIFObject current) {
		if(prev==null) {
			return false; // first record
		}
		if(prev.getCall().equalsIgnoreCase(current.getCall())
		   && prev.getMode().equalsIgnoreCase(current.getMode())
		   && prev.getQSODate().equalsIgnoreCase(current.getQSODate())
		   && prev.getTimeOn().equalsIgnoreCase(current.getTimeOn())) {
			return true;
		} else {
			return false;
		}
	}

}
