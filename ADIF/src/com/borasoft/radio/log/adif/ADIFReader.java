package com.borasoft.radio.log.adif;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ADIFReader {
	private BufferedReader reader;
	private String buffer;
	
	public ADIFReader(InputStreamReader reader) {
		this.reader = new BufferedReader(reader);
		buffer = "";
	}
	
	public ADIFStream readADIFStream() throws IOException {
		ADIFObject obj;
		ADIFStream stream = new ADIFStream();
		
		stream.setHeader(preprocessADIFStream());
		while((obj=readADIF()) != null) {
			stream.addRecord(obj);
		}
		postprocessADIFStream();
		return stream;
	}
	
	private ADIFHeader preprocessADIFStream() throws IOException {
		String line;
		String subbuffer;
		int index;
		
		Pattern p = Pattern.compile(".*<EOH>.*",Pattern.CASE_INSENSITIVE);
		Matcher m;
		// the content of "buffer" is an empty string at this point.
		while((line=reader.readLine())!=null) {
			buffer = buffer.concat(line);			
			m = p.matcher(buffer);
			if(m.matches()) {
				index = buffer.toUpperCase().indexOf("<EOH>");
				subbuffer = buffer.substring(0,index); // string to be processed now
				buffer = buffer.substring(index+5); // string to be processed next
				return processHeader(subbuffer);
			}
		}
		return null; // null header
	}
	
	private ADIFHeader processHeader(String s) {
		//System.out.println("Processing ADIF input Header: \n\t" + s);
		int index;
		ADIFHeader obj = new ADIFHeader();
		
		if((index=s.indexOf("File:"))!= -1) {
			parseFile(s,index+5,obj); // obj is updated.
		}
		if((index=s.toUpperCase().indexOf("<ADIF_VER:"))!= -1) {
			parseADIFVer(s,index+10,obj); // obj is updated.
		}		
		if((index=s.toUpperCase().indexOf("<PROGRAMID:"))!= -1) {
			parseProgramID(s,index+11,obj); // obj is updated.
		}		
		if((index=s.toUpperCase().indexOf("<PROGRAMVERSION:"))!= -1) {
			parseProgramVersion(s,index+16,obj); // obj is updated.
		}	
		
		return obj;
	}
	
	private void parseProgramVersion(String s, int start, ADIFHeader obj) {
		String version = parseStringValue(s,start);
		obj.setProgramVersion(version);
	}

	private void parseProgramID(String s, int start, ADIFHeader obj) {
		String id = parseStringValue(s,start);
		obj.setProgramID(id);
	}

	private void parseADIFVer(String s, int start, ADIFHeader obj) {
		String version = parseStringValue(s,start);
		obj.setADIFVer(version);
	}

	private void parseFile(String s, int start, ADIFHeader obj) {
		int end;
		String filename;
		end = s.indexOf("<",start);
		filename = s.substring(start, end).trim();	
		obj.setFile(filename);
	}

	private ADIFObject readADIF() throws IOException {
		String line;
		String subbuffer;
		int index;
		
		Pattern p = Pattern.compile(".*<EOR>.*",Pattern.CASE_INSENSITIVE);
		Matcher m;
		// the content of "buffer" may not be an empty string at this point.
		while((line=reader.readLine())!=null) {
			buffer = buffer.concat(line);			
			m = p.matcher(buffer);
			if(m.matches()) {
				index = buffer.toUpperCase().indexOf("<EOR>");
				subbuffer = buffer.substring(0,index); // string to be processed now
				buffer = buffer.substring(index+5); // string to be processed next
				return processADIFRecord(subbuffer);
			}
		}		
		return null; // no more ADIF records
	}
	
	private ADIFObject processADIFRecord(String s) {
		int index;
		ADIFObject obj = new ADIFObject();
		if((index=s.toUpperCase().indexOf("<CALL:"))!= -1) {
			parseCall(s,index+6,obj); // obj is updated.
		}
		if((index=s.toUpperCase().indexOf("<BAND:"))!= -1) {
			parseBand(s,index+6,obj); // obj is updated.
		}
		if((index=s.toUpperCase().indexOf("<FREQ:"))!= -1) {
			parseFreq(s,index+6,obj); // obj is updated.
		}		
		if((index=s.toUpperCase().indexOf("<MODE:"))!= -1) {
			parseMode(s,index+6,obj); // obj is updated.
		}		
		if((index=s.toUpperCase().indexOf("<NAME:"))!= -1) {
			parseName(s,index+6,obj); // obj is updated.
		}		
		if((index=s.toUpperCase().indexOf("<QSO_DATE:"))!= -1) {
			parseQSODate(s,index+10,obj); // obj is updated.
		}		
		if((index=s.toUpperCase().indexOf("<RST_RCVD:"))!= -1) {
			parseRSTReceived(s,index+10,obj); // obj is updated.
		}		
		if((index=s.toUpperCase().indexOf("<RST_SENT:"))!= -1) {
			parseRSTSent(s,index+10,obj); // obj is updated.
		}
		if((index=s.toUpperCase().indexOf("<QSL_RCVD:"))!= -1) {
			parseQSLReceived(s,index+10,obj); // obj is updated.
		}		
		if((index=s.toUpperCase().indexOf("<QSL_SENT:"))!= -1) {
			parseQSLSent(s,index+10,obj); // obj is updated.
		}	
		if((index=s.toUpperCase().indexOf("<TIME_ON:"))!= -1) {
			parseTimeOn(s,index+9,obj); // obj is updated.
		}		
		if((index=s.toUpperCase().indexOf("<TIME_OFF:"))!= -1) {
			parseTimeOff(s,index+10,obj); // obj is updated.
		}
		
		if((index=s.toUpperCase().indexOf("<COMMENT:"))!= -1) {
			parseComment(s,index+9,obj); // obj is updated.
		}
		if((index=s.toUpperCase().indexOf("<QTH:"))!= -1) {
			parseQTH(s,index+5,obj); // obj is updated.
		}
		if((index=s.toUpperCase().indexOf("<TX_PWR:"))!= -1) {
			parseTXPWR(s,index+8,obj); // obj is updated.
		}
		if((index=s.toUpperCase().indexOf("<ITUZ:"))!= -1) {
			parseITUZ(s,index+6,obj); // obj is updated.
		}
		if((index=s.toUpperCase().indexOf("<CQZ:"))!= -1) {
			parseCQZ(s,index+5,obj); // obj is updated.
		}
		if((index=s.toUpperCase().indexOf("<DXCC:"))!= -1) {
			parseDXCC(s,index+6,obj); // obj is updated.
		}
		
		return obj;
	}
	
	private void parseComment(String s, int start, ADIFObject obj) {
		String comment = parseStringValue(s,start);
		obj.setComment(comment);	
	}
	
	private void parseQTH(String s, int start, ADIFObject obj) {
		String qth = parseStringValue(s,start);
		obj.setQTH(qth);		
	}
	
	private void parseTXPWR(String s, int start, ADIFObject obj) {
		String txPWR = parseStringValue(s,start);
		obj.setTXPWR(txPWR);		
	}
	
	private void parseITUZ(String s, int start, ADIFObject obj) {
		String ituz = parseStringValue(s,start);
		obj.setITUZone(ituz);		
	}
	
	private void parseCQZ(String s, int start, ADIFObject obj) {
		String cqz = parseStringValue(s,start);
		obj.setCQZone(cqz);		
	}
	
	private void parseDXCC(String s, int start, ADIFObject obj) {
		String dxcc = parseStringValue(s,start);
		obj.setDXCC(dxcc);		
	}
	
	private void parseQSLReceived(String s, int start, ADIFObject obj) {
		String qsl = parseStringValue(s,start);
		obj.setQSLReceived(qsl);
	}

	private void parseQSLSent(String s, int start, ADIFObject obj) {
		String qsl = parseStringValue(s,start);
		obj.setQSLSent(qsl);
	}

	private void parseBand(String s, int start, ADIFObject obj) {
		String band = parseStringValue(s,start);
		obj.setBand(band);
	}

	private void parseTimeOff(String s, int start, ADIFObject obj) {
		String timeOff = parseStringValue(s,start);
		obj.setTimeOff(timeOff);	
	}

	private void parseTimeOn(String s, int start, ADIFObject obj) {
		String timeOn = parseStringValue(s,start);
		obj.setTimeOn(timeOn);
	}

	private void parseRSTSent(String s, int start, ADIFObject obj) {
		String rstSent = parseStringValue(s,start);
		obj.setRSTSent(rstSent);
	}

	private void parseRSTReceived(String s, int start, ADIFObject obj) {
		String rstReceived = parseStringValue(s,start);
		obj.setRSTReceived(rstReceived);
	}

	private void parseQSODate(String s, int start, ADIFObject obj) {
		String qsoDate = parseStringValue(s,start);
		obj.setQSODate(qsoDate);
	}

	private void parseName(String s, int start, ADIFObject obj) {
		String name = parseStringValue(s,start);
		obj.setName(name);
	}

	private void parseMode(String s, int start, ADIFObject obj) {
		String mode = parseStringValue(s,start);
		obj.setMode(mode);
	}

	private void parseFreq(String s, int start, ADIFObject obj) {
		String freq = parseStringValue(s,start);
		obj.setFreq(freq);
	}

	private void parseCall(String s, int start, ADIFObject obj) {
		String call = parseStringValue(s,start);
		obj.setCall(call);
	}		
	
	private String parseStringValue(String s, int start) {
		int size;
		int end;
		end = s.indexOf(">",start);
		
		// Handle the additional data type field - e.g. ":D" in <QSO_DATE:8:D> 
		String temp = s.substring(start-1, end);
		if(temp.indexOf(":")!=temp.lastIndexOf(":")) {
			size = Integer.parseInt(s.substring(start, end-2)); // We could process ":D" later if necessary.
		} else {
			size = Integer.parseInt(s.substring(start, end));
		}
		return s.substring(end+1, end+1+size); // "end" is the position of ">"		
	}
	
	private void postprocessADIFStream() {
		// Nothing to do.
	}
		
}
