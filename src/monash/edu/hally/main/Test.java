package monash.edu.hally.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import monash.edu.hally.constant.ModelConstants;
import monash.edu.hally.util.FilesUtil;

public class Test {
	
	public static void writeFile(int i,String line)
	{	
		
		try {
			BufferedWriter writer=new BufferedWriter(new FileWriter(new File(
					ModelConstants.RESULTS_PATH+i+".txt")));
			writer.write(line+ModelConstants.CR_LF);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		String documentName="(corporation)_20news-18828_remove_noise.txt";
		ArrayList<String> contents=FilesUtil.readDocument(ModelConstants.DOCUMENTSS_PATH+documentName);
		int i=0;
		for (String content : contents) {
			if(content.endsWith("#===#")) continue;
			writeFile(++i,content);
		}
		
		
	}

}
