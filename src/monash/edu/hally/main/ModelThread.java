package monash.edu.hally.main;

import monash.edu.hally.util.Documents;
import monash.edu.hally.util.FilesUtil;


public class ModelThread implements Runnable{
	
	private ModelParameters modelParameters;
	
	public ModelThread(){}
	
	/**
	 * 作用：设置模型参数
	 */
	public static ModelParameters setParameters()
	{
		ModelParameters modelParameters=new ModelParameters();
		modelParameters.setK(3);
		modelParameters.setTopNum(3);
		modelParameters.setIterations(100);
		modelParameters.setBurn_in(80);
		modelParameters.setSaveStep(10);
		modelParameters.setSamplingEquation(1);
		
		return modelParameters;
	}
	
	public ModelThread(ModelParameters modelParameters)
	{
		this.modelParameters=modelParameters;
	}

	@Override
	public void run() {

		long startTime=System.currentTimeMillis();
		ModelVariables modelVariables=new ModelVariables();
		//文档集
		Documents documents=new Documents(modelVariables);
		//扩充字典，并对文档集中的所有文档索引化（将字典的索引和词汇的索引联系上）
		documents.processAllDocuments();

		//模型处理
		WCModel ldaModel=new WCModel(documents,modelParameters,modelVariables);
		//初始化模型参数
		ldaModel.initialiseModel();
		//推断和保存模型的潜在变量
		ldaModel.inferenceModel();
		
		long endTime=System.currentTimeMillis();
		System.out.println("Runtime "+(endTime-startTime)/1000+"s.");
		
		FilesUtil.printSuccessMessage();
	}
	
	public static void main(String[] args) {
		
		Thread modelThread= new Thread(new ModelThread(setParameters()));
		modelThread.start();
	}

}


