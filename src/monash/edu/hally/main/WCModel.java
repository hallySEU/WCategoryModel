package monash.edu.hally.main;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import monash.edu.hally.knowledge.RelatedSet;
import monash.edu.hally.knowledge.RelatedSets;
import monash.edu.hally.util.Document;
import monash.edu.hally.util.Documents;
import monash.edu.hally.util.FilesUtil;

public class WCModel extends Thread{
	
	
	float alpha; // doc-topic dirichlet prior parameter

	double beta; // topic-word dirichlet prior parameter
	float gamma; // topic-concept dirichlet prior parameter
	
	double rho;// topic-category prior parameter

	double mu;// concept-mention dirichlet prior parameter

	int[][] nmk;// given document d, count times of topic k. M*K

	int[][] nkt;// given topic k, count times of term t. K*V

	int[][] nkg;// given topic k, count times of category g. K*G

	int[][][] nkge;// given topic k and category g, count times of concept c.
					// K*G*G.length

	int[][] nge;// // given category g, count times of concept c. K*C

	int[][] net;// given concept c,count times of mention m. C*c.length

	int[] nmkSum;// Sum for each row in ndk

	int[] nktSum;// Sum for each row in nkt
	int[][] nkgeSum;// Sum for each row in nkgc
	int[] nkgSum;// Sum for each row in nkg
	int[] ngeSum;// Sum for each row in ngc

	int[] netSum;// Sum for each row in ncm
	
	int categoryAndEntitySize=0;
	
	private int M,K,V;	//分别表示文档集中文档的个数，主题个数，字典中的词汇个数
	
	private int G,E;	//category and concept 个数

	double[][] phi;// Parameters for topic-word distribution K*V
	
	double[][][] delta;// Parameters for topic-category concept distribution K*G*g.length

	double[][] pi;// Parameters for topic-category distribution K*G

	double[][] lambda;// Parameters for concept-mention distribution C*c.length

	double[][] theta;// Parameters for doc-topic distribution M*K
	
	double[][] temp;// Parameters for doc-topic distribution K*E
	
	private int[][] z;		//每一项为：特定文档下的特定词汇的主题
	private int[][] g; // category index assignment for each word.
	private int[][] e; // concept index assignment for each word.
	
	private int iterations;	//迭代次数
	private int burn_in;	//burn-in 时期
	private int saveStep;	//burn-in 过后，每saveStep次迭代保存一次结果
	private int topNum;	//显示主题下概率最高的前topNum词项
	
	private Documents documents;	//文档集
	private ModelParameters modelParameters;	//模型参数
	
	private ModelVariables modelVariables;


	
	public WCModel(Documents documents, ModelParameters modelParameters, ModelVariables modelVariables)
	{
		this.documents=documents;
		this.modelParameters=modelParameters;
		this.modelVariables=modelVariables;
		setModelParameters();
	}
	
	
	/**
	 * 作用：设置需要预先指定的参数
	 */
	public void setModelParameters()
	{
//		System.out.println("Read model parameters.");//另一种读取参数的方式
//		ModelParameters modelParameters=FilesUtil.readParametersFile();
	
		alpha=1;	//一般为 50/K
		beta=0.1;	//一般为 0.1
		gamma=1;
		rho=0.1;
		mu=0.1;
		K=modelParameters.getK();
		
		iterations=modelParameters.getIterations();
		burn_in=modelParameters.getBurn_in();
		saveStep=modelParameters.getSaveStep();
		topNum=modelParameters.getTopNum();
	}
	
	
	private void allocateMemoryForVariables() 
	{
			
		M=documents.docs.size();
		V=modelVariables.termDictionary.size();
		E=modelVariables.entityDictionary.size();
		G=modelVariables.categoryDictionary.size();
		
		pi=new double[K][V];
		theta=new double[M][K];
		pi=new double[K][G];
		
		lambda=new double[E][];
		net=new int[E][];
		for (int e = 0; e < E; ++e) {
			String concept=modelVariables.entityDictionary.get(e);
			int size = modelVariables.entityToTermListMap.get(concept).size();
			lambda[e]=new double[size];
			net[e] = new int[size];
			for (int i = 0; i < size; ++i) {
				lambda[e][i] = 0.0;
				net[e][i]=0;
			}
		}
		
		delta = new double[K][G][];
		nkge = new int[K][G][];
		for (int k = 0; k < K; ++k) {
			for (int g = 0; g < G; ++g) {
				String category=modelVariables.categoryDictionary.get(g);
				int size = modelVariables.categoryToEntityListMap.get(category).size();
				delta[k][g] = new double[size];
				nkge[k][g] = new int[size];
				for (int i = 0; i < size; ++i) {
					delta[k][g][i] = 0.0;
					nkge[k][g][i]=0;
				}
			}
		}
		
		nmk=new int[M][K];
		nkt=new int[K][V];
		nkg=new int[K][G];
		nge=new int[G][];
		
		for (int g = 0; g < G; ++g) {
			String category=modelVariables.categoryDictionary.get(g);
			int size = modelVariables.categoryToEntityListMap.get(category).size();
			nge[g] = new int[size];
			categoryAndEntitySize += size;
			for (int i = 0; i < size; ++i) {
				nge[g][i]=0;
			}
		}
		
		nktSum=new int[K];
		nmkSum=new int[M];
		nkgeSum=new int[K][G];
		nkgSum=new int[K];
		ngeSum=new int[G];
		netSum=new int[E];
		
		z=new int[M][];
		g=new int[M][];
		e=new int[M][];
		
	}
	
	public void countChange(int m,String word,int zmn,int gmn,int emn,boolean flag)
	{
//		System.out.println(zmn+"\t"+gmn+"\t"+emn+"\t"+flag);
		String category=modelVariables.categoryDictionary.get(gmn);
		String entity=modelVariables.categoryToEntityListMap.get(category).get(emn);
		int e=modelVariables.entityToIndexMap.get(entity);
		int e_v=documents.getIndexFromListMap(entity, word, modelVariables.entityToTermListMap);
//		System.out.println(category+"\t"+documents.categoryToEntityListMap.get(category).size());
		if(flag==true)
		{
			nmk[m][zmn]++;
			nmkSum[m]++;
			nkg[zmn][gmn]++;
			nkgSum[zmn]++;
			
			nkge[zmn][gmn][emn]++; 
			nkgeSum[zmn][gmn]++;
			nge[gmn][emn]++;
			ngeSum[gmn]++;
			net[e][e_v]++;
			netSum[e]++;
		}
		else{
			nmk[m][zmn]--;
			nmkSum[m]--;
			nkg[zmn][gmn]--;
			nkgSum[zmn]--;
			
			nkge[zmn][gmn][emn]--; 
			nkgeSum[zmn][gmn]--;
			nge[gmn][emn]--;
			ngeSum[gmn]--;
			net[e][e_v]--;
			netSum[e]--;
		}
		
	}
	
	/**
	 * 作用：初始化模型
	 * 1.初始化模型参数（根据需要学习的文档集得到）
	 * 2.给文档中的词汇随机分配主题
	 */
	public void initialiseModel()
	{
		
		allocateMemoryForVariables();
		System.out.println("Model begins learning.");
		
		for (int m = 0; m < M; m++) {
			Document document=documents.docs.get(m);
			int Nm=document.docWords.length;	//第m篇文档的词数（长度）
			z[m]=new int[Nm];
			g[m]=new int[Nm];
			e[m]=new int[Nm];
			for (int n = 0; n < Nm; n++) {
				int zmn=(int) (Math.random()*(K));	//随机分配
				z[m][n]=zmn;
				
				int termIndex=documents.docs.get(m).docWords[n];
				String token=modelVariables.termDictionary.get(termIndex);
				
				ArrayList<String> entityList=modelVariables.termToEntityListMap.get(token);		
				int emn=(int) (Math.random()*(entityList.size()));	//只随机分配跟当前word关联的mustset
			
				ArrayList<String> categoryList=modelVariables.entityToCategoryListMap.get(entityList.get(emn));	
				int mg=(int) (Math.random()*(categoryList.size()));	
				int gmn = modelVariables.categoryToIndexMap.get(categoryList.get(mg));	
				g[m][n]=gmn;
				emn=documents.getIndexFromListMap(categoryList.get(mg), entityList.get(emn), modelVariables.categoryToEntityListMap);
				e[m][n]=emn;
				countChange(m, token, z[m][n], g[m][n], e[m][n], true);	

//				updateUrn(smn, zmn, documents.termDictionary.get(termIndex), +1);
			}
			
		}
	}
	
	/**
	 * 作用：采用Gibbs采样算法，来推断模型参数
	 */
	public void inferenceModel()
	{
		
		for (int currentIteration = 1; currentIteration <= iterations; currentIteration++) {
			System.out.println("Iteration "+currentIteration);
			if(currentIteration == iterations)
				saveLatentVariables();
			else if((currentIteration >= burn_in) && (currentIteration % saveStep==0))
				calLatentVariables(false);
			else
			{	//不停的采样，直到过了burn-in时期
				for (int m = 0; m < M; m++) {
					Document document=documents.docs.get(m);
					for (int n = 0; n < document.docWords.length; n++) {
						sampleTopicEntityCategory(m,n);
					}
				}
			}
		}
		System.out.println("Learn over!");
	}
	
	/**
	 * Gibbs采样算法，给当前词汇重新分配新的主题
	 * @return 新的主题
	 */
	public void sampleTopicEntityCategory(int m,int n)
	{
		int termIndex=documents.docs.get(m).docWords[n];	
		String token=modelVariables.termDictionary.get(termIndex);
		countChange(m,token, z[m][n], g[m][n], e[m][n], false);	
		
//		updateUrn(oldSet, oldTopic, documents.termDictionary.get(termIndex), -1);
		
		RelatedSets relatedSets=modelVariables.termToRelatedSetsMap.get(token);
		int rulesSize=relatedSets.getRulesSize();
		
		double[] p = new double[K * rulesSize];
		int i=0;
		
		Map<Integer, int[]> allocationMap=new HashMap<Integer, int[]>();
		
		for (int k = 0; k < K; k++) {
			for (int j = 0; j < relatedSets.getSize(); j++) {
				RelatedSet relatedSet=relatedSets.getRelatedSet(j);
				String entity=relatedSet.getItemFromIndex(0);
				for (int j2 = 1; j2 < relatedSet.getSize(); j2++) {
					
					String category=relatedSet.getItemFromIndex(j2);
//					String allocation[] = {""+k,entity,category};
//					allocationMap.put(i, allocation);
					int g=modelVariables.categoryToIndexMap.get(category);
					int g_e=documents.getIndexFromListMap(category, entity, modelVariables.categoryToEntityListMap);
					int e=modelVariables.entityToIndexMap.get(entity);
					int e_v=documents.getIndexFromListMap(entity, token, modelVariables.entityToTermListMap);
					
					int allocation[] = {k, g, g_e};
					allocationMap.put(i, allocation);
					
					p[i++] = (nkge[k][g][g_e] + gamma)
							/ (nkgeSum[k][g] + rulesSize * gamma)
							* (nmk[m][k] + alpha) / (nmkSum[m] + K * alpha)
							* (nkg[k][g] + rho) / (nkgSum[k] + G * rho)
							* (net[e][e_v] + mu)
							/ (netSum[e] + V * mu);
				}
			}
		}
		
		for (int k= 1; k < K * rulesSize; k++) {
			p[k] += p[k-1];
		}
		double u= Math.random()*p[K * rulesSize-1];
		int index = 0;
		for (int k = 0; k < K * rulesSize; k++) {
			if(u < p[k]){
				index = k;
				break;
			}
		}

		int newAllocation[]=allocationMap.get(index);
		int newTopic= newAllocation[0];
		int newCategory=newAllocation[1];
		int newEntity=newAllocation[2];
		z[m][n]=newTopic;
		g[m][n]=newCategory;
		e[m][n]=newEntity;
		countChange(m,token, z[m][n], g[m][n], e[m][n], true);		
	}
	
	/**
	 * 对每一个s中的word,改变nks计数变量的值
	 * @param s
	 * @param k
	 * @param word
	 * @param flag
	 */
//	public void updateUrn(int s,int k,String word,int flag)
//	{
//		CategoryEntitySet mustSet=mustSets.getMustSet(s);
//		for (int i = 0; i < mustSet.size(); i++) {
//			if(mustSet.getWordstr(i).equals(word)){
//				if(flag==1){
//					nks[k][s]++;
//					nksSum[k]++;
//				}
//				else{
//					nks[k][s]--;
//					nksSum[k]--;
//				}
//			}
//			else{
//				if(flag==1){
//					nks[k][s]+=0.2;
//					nksSum[k]+=0.2;
//				}
//				else{
//					nks[k][s]-=0.2;
//					nksSum[k]-=0.2;
//				}
//			}
//		}
//		
//	}
	
	/**
	 * 作用：根据计数变量来更新模型变量
	 * @param isFinalIteration 是否是最后一次迭代，如果是就要把前面几次保存的结果求平均
	 */
	public void calLatentVariables(boolean isFinalIteration)
	{
		
		for (int m = 0; m < M; m++) {
			for (int k = 0; k < K; k++) {
				theta[m][k] += (nmk[m][k]+alpha)/(nmkSum[m]+K*alpha);
				if(isFinalIteration)
					theta[m][k] = theta[m][k] / ((iterations-burn_in) / saveStep + 1); //saveTime;
			}
		}
		for (int k = 0; k < K; k++) {
			for (int g = 0; g < G; g++) {
				pi[k][g] += (nkg[k][g]+rho)/(nkgSum[k]+G*rho);
				if(isFinalIteration)
					pi[k][g] = pi[k][g] / ((iterations-burn_in) / saveStep + 1);//saveTime;
			}
		}
		for (int e = 0; e < E; e++) {
			String concept=modelVariables.entityDictionary.get(e);
			int size = modelVariables.entityToTermListMap.get(concept).size();
			for (int v = 0; v < size; v++) {
				lambda[e][v] += (net[e][v]+mu)/(netSum[e]+V*mu);
				if(isFinalIteration)
					lambda[e][v] = lambda[e][v] / ((iterations-burn_in) / saveStep + 1);//saveTime;
			}
		}
		for (int k = 0; k < K; k++) {
			for (int g = 0; g < G; g++) {	
				String category=modelVariables.categoryDictionary.get(g);
				int size = modelVariables.categoryToEntityListMap.get(category).size();
				for (int i = 0; i < size; i++) {
					delta[k][g][i] += (nkge[k][g][i] + gamma) / (nkgeSum[k][g] + size*gamma);
					if(isFinalIteration)
						delta[k][g][i] = delta[k][g][i] / ((iterations-burn_in) / saveStep + 1);//saveTime;
				}	
			}
		}	
	}
	
	/**
	 * Topic-word distribution: omega[][][], not estimated from the model, but
	 * computed using phi and eta.
	 * 
	 * temp[k][e] = sum_{g} lamda[k][g] * eta[k][g][e]
	 * phi[k][w] = sum_{e} temp[k][e]* lamda[e][w]
	 */
	public void computeTopicWordDistribution() {
		phi=new double[K][V];
		temp=new double[K][E];
		// Initialized to 0.
		for (int k = 0; k < K; ++k) {
			for (int w = 0; w < V; ++w) {
				phi[k][w] = 0;		
			}
			for (int i = 0; i < E; i++) {
				temp[k][i]=0;
			}
		}

		for (int k = 0; k < K; ++k) {		
			for (int g = 0; g < G; g++) {	
				String category=modelVariables.categoryDictionary.get(g);
				ArrayList<String> entityList = modelVariables.categoryToEntityListMap.get(category);
				int size = entityList.size();
				for (int i = 0; i < size; ++i) {
					double prob = pi[k][g] * delta[k][g][i];
					String entity=entityList.get(i);
					temp[k][modelVariables.entityToIndexMap.get(entity)]+= prob;
				}
			}
		}
		
		for (int k = 0; k < K; ++k) {		
			for (int v = 0; v < V; v++) {
				ArrayList<String> entityList=modelVariables.termToEntityListMap.get(modelVariables.termDictionary.get(v));
				if(entityList==null) continue;
				for (String entity : entityList) {
					int e=modelVariables.entityToIndexMap.get(entity);
					int e_v=documents.getIndexFromListMap(entity, modelVariables.termDictionary.get(v), modelVariables.entityToTermListMap);
					double prob = temp[k][e]*lambda[e][e_v];
					phi[k][v]+=prob;
				}
			}
		}
	}
	
	/**
	 * 作用：保存当前迭代次数学习到的模型变量
	 * @param currentIterition： 当前迭代次数
	 */
	public void saveLatentVariables()
	{
		System.out.println("Save results at iteration ("+iterations+").");
		calLatentVariables(true);
		computeTopicWordDistribution();
		FilesUtil.saveDistributions(theta, pi);
		FilesUtil.saveTopicAndSetAssignment(documents, z, g, e);
		FilesUtil.saveTopWords(documents, phi, topNum);

	}
	
}
