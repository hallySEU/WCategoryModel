package monash.edu.hally.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import monash.edu.hally.knowledge.RelatedSets;

public class ModelVariables {
	
public ArrayList<String> termDictionary=new ArrayList<String>();
	
	public Map<String, Integer> termToIndexMap=new HashMap<String, Integer>();
	
	public ArrayList<String> entityDictionary=new ArrayList<String>();
	
	public Map<String, Integer> entityToIndexMap=new HashMap<String, Integer>();
	
	public ArrayList<String> categoryDictionary=new ArrayList<String>();
	
	public Map<String, Integer> categoryToIndexMap=new HashMap<String, Integer>();
	
	public Map<String, ArrayList<String>> entityToTermListMap=new HashMap<String, ArrayList<String>>();
	
	public Map<String, ArrayList<String>> categoryToEntityListMap=new HashMap<String, ArrayList<String>>();
	
	public Map<String, ArrayList<String>> termToEntityListMap=new HashMap<String, ArrayList<String>>();
	
	public Map<String, ArrayList<String>> entityToCategoryListMap=new HashMap<String, ArrayList<String>>();
	
	public Map<String, RelatedSets> termToRelatedSetsMap=new HashMap<String, RelatedSets>();

}
