package monash.edu.hally.util;

import java.util.ArrayList;
import java.util.Map;

import monash.edu.hally.knowledge.RelatedSet;
import monash.edu.hally.knowledge.RelatedSets;
import monash.edu.hally.main.ModelVariables;


public class Document
{
	public int docWords[];
	
	
	private String documentName;
	private ArrayList<String> termDictionary;
	private Map<String, Integer> termToIndexMap;
	
	private ArrayList<String> entityDictionary;
	private Map<String, Integer> entityToIndexMap;
	
	private ArrayList<String> categoryDictionary;
	private Map<String, Integer> categoryToIndexMap;
	
//	public Map<String, Integer> entityToCategoryIndexMap = null;
	public Map<String, ArrayList<String>> categoryToEntityListMap = null;
	public Map<String, ArrayList<String>> entityToTermListMap = null;
	
	public Map<String, ArrayList<String>> termToEntityListMap = null;
	
	public Map<String, ArrayList<String>> entityToCategoryListMap = null;
	
	public Map<String, RelatedSets> termToRelatedSetsMap = null;
	
	
	public Document(String documentName,ModelVariables modelVariables)
	{
		this.documentName=documentName;
		this.termDictionary=modelVariables.termDictionary;
		this.termToIndexMap=modelVariables.termToIndexMap;
		this.entityDictionary=modelVariables.entityDictionary;
		this.entityToIndexMap=modelVariables.entityToIndexMap;
		this.categoryDictionary=modelVariables.categoryDictionary;
		this.categoryToIndexMap=modelVariables.categoryToIndexMap;
		
		this.categoryToEntityListMap=modelVariables.categoryToEntityListMap;
		this.entityToTermListMap=modelVariables.entityToTermListMap;
		this.termToEntityListMap=modelVariables.termToEntityListMap;
		this.entityToCategoryListMap=modelVariables.entityToCategoryListMap;
		this.termToEntityListMap=modelVariables.termToEntityListMap;
		this.termToRelatedSetsMap=modelVariables.termToRelatedSetsMap;
	}
	
	public void extendCategoryEntitySets(String entity,String category)
	{
		if(!categoryToEntityListMap.containsKey(category)){
			
			categoryToIndexMap.put(category, categoryDictionary.size());
			categoryDictionary.add(category);
			ArrayList<String> entityList=new ArrayList<String>();
			entityList.add(entity);
		
			categoryToEntityListMap.put(category, entityList);
		
		}
		else{
			ArrayList<String> entityList=categoryToEntityListMap.get(category);
			if(!entityList.contains(entity))
				entityList.add(entity);
		}
//		if(!xxxToIndexMap.keySet().contains(knowledge))// dictionary.contains(token))
//		{
//			int dictionarySize=dictionary.size();
//			xxxToIndexMap.put(knowledge, dictionarySize);
//			dictionary.add(knowledge);
//		}
	}
	
	public void extendEntityTermSets(String term,String entity)
	{
		if(!entityToTermListMap.containsKey(entity)){
			
			entityToIndexMap.put(entity, entityDictionary.size());
			entityDictionary.add(entity);
			ArrayList<String> termList=new ArrayList<String>();
			termList.add(term);
		
			entityToTermListMap.put(entity, termList);	
		}
		else{
			ArrayList<String> termList=entityToTermListMap.get(entity);
			if(!termList.contains(term))
				termList.add(term);
		}
	}
	
	public void extendMap(String key,String value,Map<String, ArrayList<String>> xxxToXXXListMap)
	{
		if(!xxxToXXXListMap.containsKey(key)){
			xxxToXXXListMap.put(key, new ArrayList<String>());
		}
		xxxToXXXListMap.get(key).add(value);
	}
	
	public void extendRelatedSets(String token,String entity,String categories[])
	{
		
		if(!termToRelatedSetsMap.containsKey(token)){
			RelatedSets relatedSets = new RelatedSets();
			relatedSets.add(new RelatedSet(entity,categories));
			termToRelatedSetsMap.put(token, relatedSets);
		}
		else
		{
			RelatedSets relatedSets=termToRelatedSetsMap.get(token);
			if(!relatedSets.isContain(entity)){
				relatedSets.add(new RelatedSet(entity,categories));
				termToRelatedSetsMap.put(token, relatedSets);
			}
		}
	}
	
	/**
	 * 作用：将文档中的词汇添加到字典中。
	 * 文档中的每一个词汇索引n，用docWords[n]来表示在字典中对应的词项索引
	 */
	public void indexProcess()
	{
		ArrayList<String> documentLines=FilesUtil.readDocument(documentName);
		String[] knowledgeItems=documentLines.get(0).split("#===#")[1].split("\t"); 
//		String tokens[]=documentLines.get(0).split(ModelConstants.SPLIT_FLAG);
//		ArrayList<String> tokens = FilesUtil.tokenize(documentLines);
		docWords=new int[knowledgeItems.length];
		for (int n = 0; n < knowledgeItems.length; n++) {
			String knowledge[]=knowledgeItems[n].split("###");
			String token=knowledge[0];
			if(!termToIndexMap.keySet().contains(token))// dictionary.contains(token))
			{
				int dictionarySize=termDictionary.size();
				termToIndexMap.put(token, dictionarySize);
				docWords[n]=dictionarySize;
				termDictionary.add(token);		
				
			}
			else
				docWords[n]=termToIndexMap.get(token);
			extendMap(token, knowledge[1], termToEntityListMap);
			extendEntityTermSets(token,knowledge[1]);
			String categories[]=knowledge[2].split("@@@");	
			extendRelatedSets(token, knowledge[1], categories);
			
			for (String category : categories) {
				extendCategoryEntitySets(knowledge[1], category);
				extendMap(knowledge[1], category, entityToCategoryListMap);
			}
		}
	}
}
	
