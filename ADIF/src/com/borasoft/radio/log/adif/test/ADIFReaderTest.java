package com.borasoft.radio.log.adif.test;

import java.io.*;

import com.borasoft.radio.log.adif.ADIFReader;
import com.borasoft.radio.log.adif.ADIFStream;

public class ADIFReaderTest {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		@SuppressWarnings("unused")
		ADIFStream adif = new ADIFStream();
		FileInputStream stream = new FileInputStream("va3pen_2010_12_31_total_export.ADI");
		InputStreamReader reader = new InputStreamReader(stream);
		ADIFReader adifReader = new ADIFReader(reader);
		adif = adifReader.readADIFStream();
		reader.close();
	}

}
