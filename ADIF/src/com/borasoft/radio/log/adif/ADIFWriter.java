package com.borasoft.radio.log.adif;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Vector;

public class ADIFWriter {
	private PrintWriter writer;
	private ADIFStream adifStream;

	public ADIFWriter(OutputStreamWriter writer, ADIFStream adifStream) {
		this.writer = new PrintWriter(writer);
		this.adifStream = adifStream;
	}

	public void writeADIFStream() {
		writeHeader();
		writeRecords();
	}

	private void writeRecords() {
		Vector<ADIFObject> records = adifStream.getRecords();
		ADIFObject obj;
		String s;
		for(int i=0;i<records.size();i++) {
			obj = records.get(i);
			if((s=obj.getCall())!=null) {
				writer.print("<CALL:" + s.length() + ">" + s);
			}
			if((s=obj.getBand())!=null) {
				writer.print("<BAND:" + s.length() + ">" + s);
			}
			if((s=obj.getFreq())!=null) {
				writer.print("<FREQ:" + s.length() + ">" + s);
			}
			if((s=obj.getMode())!=null) {
				writer.print("<MODE:" + s.length() + ">" + s);
			}
			if((s=obj.getName())!=null) {
				writer.print("<NAME:" + s.length() + ">" + s);
			}
			if((s=obj.getQSODate())!=null) {
				writer.print("<QSO_DATE:" + s.length() + ">" + s);
			}
			if((s=obj.getRSTReceived())!=null) {
				writer.print("<RST_RCVD:" + s.length() + ">" + s);
			}
			if((s=obj.getRSTSent())!=null) {
				writer.print("<RST_SENT:" + s.length() + ">" + s);
			}
			if((s=obj.getQSLReceived())!=null) {
				writer.print("<QSL_RCVD:" + s.length() + ">" + s);
			}
			if((s=obj.getQSLSent())!=null) {
				writer.print("<QSL_SENT:" + s.length() + ">" + s);
			}
			if((s=obj.getTimeOff())!=null) {
				writer.print("<TIME_OFF:" + s.length() + ">" + s);
			}
			if((s=obj.getTimeOn())!=null) {
				writer.print("<TIME_ON:" + s.length() + ">" + s);
			}
			
			if((s=obj.getComment())!=null) {
				writer.print("<COMMENT:" + s.length() + ">" + s);
			}
			if((s=obj.getQTH())!=null) {
				writer.print("<QTH:" + s.length() + ">" + s);
			}
			if((s=obj.getTXPWR())!=null) {
				writer.print("<TX_PWR:" + s.length() + ">" + s);
			}
			if((s=obj.getITUZone())!=null) {
				writer.print("<ITUZ:" + s.length() + ">" + s);
			}
			if((s=obj.getCQZone())!=null) {
				writer.print("<CQZ:" + s.length() + ">" + s);
			}
			if((s=obj.getDXCC())!=null) {
				writer.print("<DXCC:" + s.length() + ">" + s);
			}
			writer.println("<EOR>");
		}
	}

	private void writeHeader() {
		ADIFHeader header = adifStream.getHeader();
		String s;
		if((s=header.getFile())!=null) {
			writer.println("File: " + s);
		}
		if((s=header.getADIFVer())!=null) {
			writer.println("<ADIF_VER:" + s.length() + ">" + s);
		}
		if((s=header.getProgramID())!=null) {
			writer.println("<PROGRAM_ID:" + s.length() + ">" + s);
		}
		if((s=header.getProgramVersion())!=null) {
			writer.println("<PROGRAM_VERSION:" + s.length() + ">" + s);
		}
		writer.println("<EOH>");
	}

}
