/* ***** BEGIN LICENSE BLOCK *****
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Martín Lamas
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * ***** END LICENSE BLOCK ***** */

package com.martinlamas.dicomserver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martinlamas.dicomserver.net.DicomServerApplicationEntity;
import com.martinlamas.dicomserver.net.dicomstorescpserver.DicomStoreSCPServer;
import com.martinlamas.dicomserver.net.dicomstorescpserver.IDicomStoreSCPServer;

public class DicomServer {
	private static Logger logger = LoggerFactory.getLogger(DicomServer.class);
	
	private static final int DEFAULT_PORT = 104;
	
	private static final String DEFAULT_AE_TITLE = "DICOMRCV";
	private static final String DEFAULT_STORAGE_DIRECTORY = "C:\\DICOMDATA";
	
	private static Options options;
	
	private static Options getOptions() {
		if(options != null)
			return options;
		
		options = new Options();
		
		options.addOption("d", true, "Storage directory");
		options.addOption("p", true, "Listening port");
		options.addOption("t", true, "AE Title");
		
		return options;
	}
	
	private static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("dicomserver", getOptions());
	}
	
	private static void showBanner() {
		System.out.println(
				".---. .-. .--.  .--. .-..-.   .--.  .--. .---. .-..-. .--. .---. \n"	+
				": .  :: :: .--': ,. :: `' :  : .--': .--': .; :: :: :: .--': .; :\n"	+
				": :: :: :: :   : :: :: .. :  `. `. : `;  :   .': :: :: `;  :   .'\n"	+
				": :; :: :: :__ : :; :: :; :   _`, :: :__ : :.`.: `' ;: :__ : :.`.\n"	+
				":___.':_;`.__.'`.__.':_;:_;  `.__.'`.__.':_;:_; `.,' `.__.':_;:_;\n\n"	+
				" v.0.9.1-SNAPSHOT\n");
	}
	
	public static void main(String [] args) {
		int port = DEFAULT_PORT;
		
		String aeTitle = DEFAULT_AE_TITLE;
		File storageDirectory = new File(DEFAULT_STORAGE_DIRECTORY);
		
		try {
			CommandLine line = new DefaultParser().parse(getOptions(), args);
			
			if(line.hasOption("p"))
				port = Integer.valueOf(line.getOptionValue("p"));
			
			if(line.hasOption("d"))
				storageDirectory = new File(line.getOptionValue("d"));
			
			if(line.hasOption("t"))
				aeTitle = line.getOptionValue("t");
			
			List<DicomServerApplicationEntity> applicationEntities =
					new ArrayList<DicomServerApplicationEntity>();
			
			DicomServerApplicationEntity applicationEntity =
					new DicomServerApplicationEntity(aeTitle, storageDirectory);
			
			applicationEntities.add(applicationEntity);
			
			showBanner();
			
			IDicomStoreSCPServer server = new DicomStoreSCPServer(port,
					applicationEntities);
			server.start();
		} catch(ParseException e) {
			printUsage();
		} catch(Exception e) {
			logger.error("Unable to start DICOM server: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
