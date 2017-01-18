package monash.edu.hally.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import monash.edu.hally.constant.ModelConstants;
import monash.edu.hally.knowledge.RelatedLinks;
import monash.edu.hally.knowledge.RelatedSets;
import monash.edu.hally.main.ModelVariables;

public class Documents {
	
	public ArrayList<Document> docs=new ArrayList<Document>();
	
//	public ArrayList<String> termDictionary=new ArrayList<String>();
//	
//	public Map<String, Integer> termToIndexMap=new HashMap<String, Integer>();
//	
//	public ArrayList<String> entityDictionary=new ArrayList<String>();
//	
//	public Map<String, Integer> entityToIndexMap=new HashMap<String, Integer>();
//	
//	public ArrayList<String> categoryDictionary=new ArrayList<String>();
//	
//	public Map<String, Integer> categoryToIndexMap=new HashMap<String, Integer>();
//	public Map<String, ArrayList<String>> entityToTermListMap=new HashMap<String, ArrayList<String>>();
//	public Map<String, ArrayList<String>> categoryToEntityListMap=new HashMap<String, ArrayList<String>>();
//	
//	public Map<String, ArrayList<String>> termToEntityListMap=new HashMap<String, ArrayList<String>>();
//	public Map<String, ArrayList<String>> entityToCategoryListMap=new HashMap<String, ArrayList<String>>();
//	
//	public Map<String, RelatedLinks> termToRelatedLinksMap=new HashMap<String, RelatedLinks>();
	
	private ArrayList<File> files=new ArrayList<File>();
	public ModelVariables modelVariables;
	
	public Documents(ModelVariables variables)
	{
		this.modelVariables=variables;
	}


	
	
	public int getIndexFromListMap(String key,String value,Map<String, ArrayList<String>> keyToValueListMap) {
		
		ArrayList<String> entityList=keyToValueListMap.get(key);
		for (int i = 0; i < entityList.size(); i++) {
			
			if(entityList.get(i).equals(value))
				return i;
		}
		return -1;
	}

	
	public int getTermIndex(String entity,String term) {
		
		ArrayList<String> termList=modelVariables.categoryToEntityListMap.get(entity);
		for (int i = 0; i < termList.size(); i++) {
			
			if(termList.get(i).equals(term))
				return i;
		}
		return -1;
	}
	
	/**
	 * 作用：对所有文档索引化
	 */
	public void processAllDocuments()
	{
		if(new File(ModelConstants.DOCUMENTSS_PATH).listFiles().length==0){
			JOptionPane.showMessageDialog(null, "Original documents are null, please add documents.", "Error", JOptionPane.ERROR_MESSAGE);
			System.err.println("Original documents are null, please add documents.");
			System.exit(0);
		}
//		Stopwords.readStopwords();	//读取停用词
		findDocuments(ModelConstants.DOCUMENTSS_PATH);
		System.out.println("Begin to extend dictionary and index documents.");
		if(!files.isEmpty())
		{
			int i=0;
			for (File file : files) {
				String documentName=file.getAbsolutePath();
				System.out.println("Reading document["+(++i)+"] "+documentName);
				Document document=new Document(documentName, modelVariables);
				document.indexProcess();
				docs.add(document);
			}
		}
//		constructIndexMap();
	}

	/**
	 * 作用：递归找到语料库中的所有文件
	 * @param fileDir: 当前目录路径
	 */
	public void findDocuments(String fileDir)
	{	
		for (File file : new File(fileDir).listFiles())
		{
			if(file.isFile())
				files.add(file);
			else
				findDocuments(file.getAbsolutePath());
		}
	}
	
	public static void main(String[] args) {
//		Documents documents=new Documents();
//		documents.processAllDocuments();
//		System.out.println(documents.termDictionary.size());
//		
//		for (Entry<String, RelatedLinks> entry : documents.termToRelatedLinksMap.entrySet()) {  
//			  
//		    System.out.println(entry.getKey()+"\t"+entry.getValue().toString());  	  
//		} 
//		
		
//		for (String term : documents.termDictionary) {
//			System.out.println(term+"\t"+documents.termToIndexMap.get(term));
//		}
//		System.out.println(documents.entityDictionary.size());
//		for (String term : documents.entityDictionary) {
//			System.out.println(term+"\t"+documents.entityToIndexMap.get(term));
//		}
//		System.out.println(documents.categoryDictionary.size());
//		for (String term : documents.categoryDictionary) {
//			System.out.println(term+"\t"+documents.categoryToIndexMap.get(term));
//		}
//		
//		for (Entry<String, ArrayList<String>> entry : documents.categoryToEntityListMap.entrySet()) {  
//			  
//		    System.out.println("Category = " + entry.getKey() + ", EntityList = " + entry.getValue());  
//		  
//		}  
//		for (Entry<String, ArrayList<String>> entry : documents.entityToTermListMap.entrySet()) {  
//			  
//		    System.out.println("Index = " + documents.entityToIndexMap.get(entry.getKey()) + ",Entity = " + entry.getKey() + ", TermList = " + entry.getValue());  
//		  
//		} 
	}

}
	
