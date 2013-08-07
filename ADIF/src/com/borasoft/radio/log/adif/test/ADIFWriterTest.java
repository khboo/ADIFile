package com.borasoft.radio.log.adif.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.borasoft.radio.log.adif.ADIFReader;
import com.borasoft.radio.log.adif.ADIFStream;
import com.borasoft.radio.log.adif.ADIFWriter;

public class ADIFWriterTest {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		ADIFStream adif = new ADIFStream();
		FileInputStream stream = new FileInputStream("VA3PEN_eQSL.ADI");
		InputStreamReader reader = new InputStreamReader(stream);
		ADIFReader adifReader = new ADIFReader(reader);
		adif = adifReader.readADIFStream();
		reader.close();
		
		FileOutputStream ostream = new FileOutputStream("VA3PEN_eQSL_copy.ADI");
		OutputStreamWriter writer = new OutputStreamWriter(ostream);
		ADIFWriter adifWriter = new ADIFWriter(writer,adif);
		adifWriter.writeADIFStream();
		writer.close();
	}
}
