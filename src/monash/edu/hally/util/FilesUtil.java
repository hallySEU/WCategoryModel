package monash.edu.hally.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import monash.edu.hally.constant.ModelConstants;
import monash.edu.hally.main.ModelParameters;

public class FilesUtil {
	
	static{
		fileDirCheck();
	}
	
	/**
	 * ���ã�����ļ�Ŀ¼�����ڣ����Զ�������
	 */
	public static void fileDirCheck()
	{
		File fileDir=new File(ModelConstants.DOCUMENTSS_PATH);
		if(!fileDir.exists())
			fileDir.mkdirs();
		fileDir=new File(ModelConstants.RESULTS_PATH);
		if(!fileDir.exists())
			fileDir.mkdirs();
		fileDir=new File(ModelConstants.STOPWORDS_PATH);	
		if(!fileDir.exists())
			fileDir.mkdirs();
//		fileDir=new File(ModelConstants.PARAMETERS_PATH);
//		if(!fileDir.exists())
//			fileDir.mkdirs();
	}
	
	/**
	 * ���ã���һƪ�ĵ���һ������Ϊ��λ����ArrayList�У�Ŀ��Ϊ�˷ִʡ�
	 * @param documentName:Դ�ļ��ľ���·����
	 */
	public static ArrayList<String> readDocument(String documentName)
	{
		try {
			BufferedReader reader=new BufferedReader(new FileReader(new File(documentName)));
			String line;
			ArrayList<String> documentLines=new ArrayList<String>();
			while((line=reader.readLine())!=null)
			{
				documentLines.add(line.trim());
			}
			return documentLines;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * ���ã����浱ǰ���������� �ĵ�-����ֲ� �� ����-���Ϸֲ�
	 * @param currentIterition ��������
	 * @param theta �ĵ�-����ֲ�
	 * @param pi ����-���ֲ�
	 */
	public static void saveDistributions(double[][]theta,double[][]pi)
	{
		try {
			BufferedWriter writer=new BufferedWriter(new FileWriter(new File(
					ModelConstants.RESULTS_PATH+ModelConstants.MODEL_NAME+".theta")));
			for (int m = 0; m < theta.length; m++) {
				for (int k = 0; k < theta[0].length; k++) {
					writer.write(theta[m][k]+"\t");
				}
				writer.write(ModelConstants.CR_LF);
			}
			writer.close();
			writer=new BufferedWriter(new FileWriter(new File(
					ModelConstants.RESULTS_PATH+ModelConstants.MODEL_NAME+".pi")));
			for (int k = 0; k < pi.length; k++) {
				for (int g = 0; g < pi[0].length; g++) {
					writer.write(pi[k][g]+"\t");
				}
				writer.write(ModelConstants.CR_LF);
			}
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���ã������ĵ��дʻ������������
	 * @param currentIterition ��ǰ��������
	 * @param documents	�ĵ���
	 * @param z �ĵ��������дʻ���������
	 */
	public static void saveTopicAndSetAssignment(Documents documents,int[][]z,int g[][],int e[][])
	{
		try {
			BufferedWriter writer=new BufferedWriter(new FileWriter(new File(
					ModelConstants.RESULTS_PATH+ModelConstants.MODEL_NAME+".topic-set_assignment")));
			for (int m = 0; m < z.length; m++) {
				Document document=documents.docs.get(m);
				for (int n = 0; n < document.docWords.length; n++) {
					int termIndex=document.docWords[n];
					String category=documents.modelVariables.categoryDictionary.get(g[m][n]);
					String entity=documents.modelVariables.entityDictionary.get(e[m][n]);
//					String entity=documents.categoryToEntityListMap.get(category).get(e[m][n]);
					writer.write(documents.modelVariables.termDictionary.get(termIndex)+"["+termIndex+"]:"+z[m][n]+":"+
							category+"["+g[m][n]+"]:"+entity+"["+e[m][n]+"]");
					writer.write("\t");
				}
				writer.write(ModelConstants.CR_LF);
			}
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * ���ã��������������£�������ߵ�TOP_NUM��Ĭ��Ϊ10��������
	 * @param currentIterition ��ǰ��������
	 * @param documents �ĵ���
	 * @param omega ����-�ʻ�ֲ�
	 */
	public static void saveTopWords(Documents documents,double[][]omega,int topNum)
	{
		try {
			BufferedWriter writer=new BufferedWriter(new FileWriter(new File(
					ModelConstants.RESULTS_PATH+ModelConstants.MODEL_NAME+".topwords")));
			for (int k = 0; k < omega.length; k++) {
				ArrayList<Integer> arrayList=new ArrayList<Integer>();
				for (int v = 0; v < omega[0].length; v++) {
					arrayList.add(new Integer(v));				
				}
				Collections.sort(arrayList,new TopWordComparable(omega[k]));
				writer.write("topic "+(k+1)+" : ");
				for (int i = 0; i < topNum; i++) {
					writer.write(documents.modelVariables.termDictionary.get(arrayList.get(i))+"\t");
					writer.write(omega[k][arrayList.get(i)]+"\t");
				}
				writer.write(ModelConstants.CR_LF);
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * ���ã�������ִ�.(Ӣ��Ĭ�ϲ�����)
	 * @param line
	 */
	public static String chineseTokenize(String line)
	{
		 StringReader sr=new StringReader(line);  	 
         IKSegmenter ik=new IKSegmenter(sr, true);  
         Lexeme lex=null;
         String newLine="";
         try {
			while((lex=ik.next())!=null){  
				newLine += lex.getLexemeText()+" ";  
			 }
			return newLine;
		} catch (IOException e) {
			e.printStackTrace();
		}  
         return null;
	}
	/**
	 * ���ã��ִ�
	 * @param documentLines
	 */
	public static ArrayList<String> tokenize(ArrayList<String> documentLines)
	{
		ArrayList<String> tokens=new ArrayList<String>();
		for (String line : documentLines) {
			StringTokenizer tokenizer=new StringTokenizer(chineseTokenize(line));
			while(tokenizer.hasMoreTokens())
			{
				String token=tokenModify(tokenizer.nextToken());	//ȥ���ʻ���󲿷ֵı�����	
				if(!isNoiseWord(token)&&!Stopwords.isContains(token))//ȥ���ȥͣ�ô�
					tokens.add(token.toLowerCase().trim());
			}	
		}
		return tokens;
	}
	
	/**
	 * ���ã�ȥ���ʻ���󲿷ֵı�����	
	 */
	public static String tokenModify(String token)
	{
		String subToken=token.substring(0, token.length()-1);
		return subToken+token.substring(token.length()-1).replaceAll("\\pP|\\pS", "");
	}

	/**
	 * ���ã�ȥ��
	 * @param token
	 */
	public static boolean isNoiseWord(String token) {
		token = token.toLowerCase().trim();
		// filter @xxx and URL
		if(token.matches(".*www\\..*") || token.matches(".*\\.com.*") || 
				token.matches(".*http:.*") )
			return true;
		return false;
	}

	
	/**
	 * ���ã���ӡ�ɹ�����Ϣ
	 */
	public static void printSuccessMessage()
	{
		String resultPath=System.getProperty("user.dir")+"\\data\\results";
//		JOptionPane.showMessageDialog(null, "Results are reserved in "+resultPath);
		System.out.println("Results are reserved in "+resultPath);
		try {	
			int choice=JOptionPane.showConfirmDialog(null, "Results are reserved in "+resultPath+
					"\nDo you want to open the dir of results ?", "Make a choice", JOptionPane.YES_NO_OPTION);
			if(choice==JOptionPane.OK_OPTION)
				java.awt.Desktop.getDesktop().open(new File(resultPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * ���ã�����Ĭ�ϵĲ����ļ� �����ļ��ж����������ڷ�ͼ�λ����棩
	 */
	public static void createParametersFile()
	{
		File file=new File(ModelConstants.PARAMETERS_PATH+ModelConstants.PARAMETERS_FILE);
		try {
			file.createNewFile();
			PrintWriter writer=new PrintWriter(file);
			
			writer.print("K (Number of topics):"+ModelConstants.SPLIT_FLAG+"5"+ModelConstants.CR_LF);
			writer.print("Top number:"+ModelConstants.SPLIT_FLAG+"10"+ModelConstants.CR_LF);
			writer.print("Iterations:"+ModelConstants.SPLIT_FLAG+"100"+ModelConstants.CR_LF);
			writer.print("Burn_in:"+ModelConstants.SPLIT_FLAG+"80"+ModelConstants.CR_LF);
			writer.print("SaveStep:"+ModelConstants.SPLIT_FLAG+"10"+ModelConstants.CR_LF);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * ���ã��Ӳ����ļ��ж�ȡģ�Ͳ��������ļ��ж����������ڷ�ͼ�λ����棩
	 */
	public static ModelParameters readParametersFile()
	{
		File file=new File(ModelConstants.PARAMETERS_PATH+ModelConstants.PARAMETERS_FILE);
		ModelParameters modelParameters=new ModelParameters();
		if(!file.exists())
			createParametersFile();
		
		ArrayList<String> lines = readDocument(file.getAbsolutePath());
		for (String line : lines) {
			String[] para=line.split(ModelConstants.SPLIT_FLAG);
			switch (para[0]) {
			case "Iterations:":
				modelParameters.setIterations(Integer.valueOf(para[1]));
				break;
			case "Burn_in:":
				modelParameters.setBurn_in(Integer.valueOf(para[1]));
				break;
			case "SaveStep:":
				modelParameters.setSaveStep(Integer.valueOf(para[1]));
				break;
			case "Top number:":
				modelParameters.setTopNum(Integer.valueOf(para[1]));
				break;
			default:
				modelParameters.setK(Integer.valueOf(para[1]));
				break;
			}
		}
		return modelParameters;
	}
	
}
