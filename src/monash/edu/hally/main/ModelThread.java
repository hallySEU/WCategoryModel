package monash.edu.hally.main;

import monash.edu.hally.util.Documents;
import monash.edu.hally.util.FilesUtil;


public class ModelThread implements Runnable{
	
	private ModelParameters modelParameters;
	
	public ModelThread(){}
	
	/**
	 * ���ã�����ģ�Ͳ���
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
		//�ĵ���
		Documents documents=new Documents(modelVariables);
		//�����ֵ䣬�����ĵ����е������ĵ������������ֵ�������ʹʻ��������ϵ�ϣ�
		documents.processAllDocuments();

		//ģ�ʹ���
		WCModel ldaModel=new WCModel(documents,modelParameters,modelVariables);
		//��ʼ��ģ�Ͳ���
		ldaModel.initialiseModel();
		//�ƶϺͱ���ģ�͵�Ǳ�ڱ���
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


