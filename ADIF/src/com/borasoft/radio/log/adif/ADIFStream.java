package com.borasoft.radio.log.adif;

import java.util.Vector;

public class ADIFStream {
	private ADIFHeader header;
	private Vector<ADIFObject> records;
	
	public ADIFHeader getHeader() {
		return header;
	}
	
	public void setHeader(ADIFHeader header) {
		this.header = header;
	}
	
	public Vector<ADIFObject> getRecords() {
		return records;
	}
	
	public void addRecord(ADIFObject record) {
		if(records==null) {
			records = new Vector<ADIFObject>();
		}
		records.add(record);
	}
}
