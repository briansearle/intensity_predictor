package edu.washington.maccoss.intensity_predictor.math;

import java.util.HashSet;
import java.util.Set;

import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameter;
import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameterGrid;
import edu.berkeley.compbio.jlibsvm.MutableSvmProblem;
import edu.berkeley.compbio.jlibsvm.SVM;
import edu.berkeley.compbio.jlibsvm.SolutionModel;
import edu.berkeley.compbio.jlibsvm.SvmException;
import edu.berkeley.compbio.jlibsvm.binary.BinaryClassificationSVM;
import edu.berkeley.compbio.jlibsvm.binary.C_SVC;
import edu.berkeley.compbio.jlibsvm.binary.MutableBinaryClassificationProblemImpl;
import edu.berkeley.compbio.jlibsvm.binary.Nu_SVC;
import edu.berkeley.compbio.jlibsvm.kernel.GaussianRBFKernel;
import edu.berkeley.compbio.jlibsvm.kernel.KernelFunction;
import edu.berkeley.compbio.jlibsvm.kernel.LinearKernel;
import edu.berkeley.compbio.jlibsvm.kernel.PolynomialKernel;
import edu.berkeley.compbio.jlibsvm.kernel.PrecomputedKernel;
import edu.berkeley.compbio.jlibsvm.kernel.SigmoidKernel;
import edu.berkeley.compbio.jlibsvm.labelinverter.StringLabelInverter;
import edu.berkeley.compbio.jlibsvm.multi.MultiClassificationSVM;
import edu.berkeley.compbio.jlibsvm.multi.MutableMultiClassProblemImpl;
import edu.berkeley.compbio.jlibsvm.oneclass.OneClassSVC;
import edu.berkeley.compbio.jlibsvm.regression.EpsilonSVR;
import edu.berkeley.compbio.jlibsvm.regression.MutableRegressionProblemImpl;
import edu.berkeley.compbio.jlibsvm.regression.Nu_SVR;
import edu.berkeley.compbio.jlibsvm.regression.RegressionSVM;
import edu.berkeley.compbio.jlibsvm.scaler.LinearScalingModelLearner;
import edu.berkeley.compbio.jlibsvm.scaler.NoopScalingModel;
import edu.berkeley.compbio.jlibsvm.scaler.NoopScalingModelLearner;
import edu.berkeley.compbio.jlibsvm.scaler.ScalingModelLearner;
import edu.berkeley.compbio.jlibsvm.scaler.ZscoreScalingModelLearner;
import edu.berkeley.compbio.jlibsvm.util.SparseVector;

public class SupportVectorMachine {
	private SVM svm;
	private MutableSvmProblem problem; // set by read_problem
	private ImmutableSvmParameter param;
	private SolutionModel model;

	/* svm_type */
	static final int C_SVC=0;
	static final int NU_SVC=1;
	static final int ONE_CLASS=2;
	static final int EPSILON_SVR=3;
	static final int NU_SVR=4;

	/* kernel_type */
	static final int LINEAR=0;
	static final int POLY=1;
	static final int RBF=2;
	static final int SIGMOID=3;
	static final int PRECOMPUTED=4;

	private static final Float UNSPECIFIED_GAMMA=-1F;

	public static void main(String[] args) {
		SupportVectorMachine machine=new SupportVectorMachine();
		machine.buildSVM();

		float[] x= {0, 0, 1, 1, 2, 2};
		float[][] data=new float[][] { {5, 6, 5, 1, 7, 6, 4}, {4, 2, 3, 2, 8, 5, 5}, {2, 3, 4, 1, 7, 7, 6}, {9, 7, 2, 2, 3, 2, 4}, {5, 4, 3, 3, 4, 4, 5}, {6, 8, 1, 2, 4, 3, 3}};
		machine.buildProblem(x, data);

		if (machine.svm instanceof BinaryClassificationSVM) {
			machine.svm=new MultiClassificationSVM((BinaryClassificationSVM)machine.svm);
		}
		machine.train();
	}

	public void train() {
		model=svm.train(problem, param);
	}

	public void buildProblem(float[] x, float[][] data) {

		// build problem
		if (svm instanceof RegressionSVM) {
			problem=new MutableRegressionProblemImpl(x.length);
		} else {
			Set<Float> uniqueClasses=new HashSet<Float>();
			for (int i=0; i<x.length; i++) {
				uniqueClasses.add(x[i]);
			}
			int numClasses=uniqueClasses.size();
			if (numClasses==1) {
				problem=new MutableRegressionProblemImpl(x.length);
			} else if (numClasses==2) {
				problem=new MutableBinaryClassificationProblemImpl(String.class, x.length);
			} else {
				problem=new MutableMultiClassProblemImpl<String, SparseVector>(String.class, new StringLabelInverter(), x.length, new NoopScalingModel<SparseVector>());
			}
		}
		MutableRegressionProblemImpl<SparseVector> problem=new MutableRegressionProblemImpl<SparseVector>(data.length);
		for (int i=0; i<data.length; i++) {
			SparseVector y=new SparseVector(data[i].length);
			for (int j=0; j<data.length; j++) {
				y.indexes[j]=j;
				y.values[j]=data[i][j];
			}
			problem.addExampleFloat(y, x[i]);
		}

	}

	public void buildSVM() {

		int i;

		// SvmParameter
		ImmutableSvmParameterGrid.Builder builder=ImmutableSvmParameterGrid.builder();
		// SvmParameterVariableBuilder vparam = new
		// SvmParameterVariableBuilder();

		// default values
		/*
		 * param.svm_type = svm_parameter.C_SVC; param.kernel_type =
		 * svm_parameter.RBF; param.degree = 3; param.gamma = 0; param.coef0 =
		 * 0;
		 */
		builder.nu=0.5f;
		builder.cache_size=100;
		builder.eps=1e-3f;
		builder.p=0.1f;
		builder.shrinking=true;
		builder.probability=false;
		builder.redistributeUnbalancedC=true;
		// param.nr_weight = 0;
		// param.weightLabel = new int[0];
		// param.weight = new float[0];

		ScalingModelLearner<SparseVector> scalingModelLearner=new NoopScalingModelLearner<SparseVector>();

		String scalingType=null;
		int scalingExamples=1000;
		boolean normalizeL2=false;
		int svm_type=0;
		int kernel_type=2;
		int degree=3;
		Set<Float> gammaSet=new HashSet<Float>();
		// float gamma = 0;
		float coef0=0;

		if (scalingType==null) {
			// do nothing
		} else if (scalingType.equals("linear")) {
			scalingModelLearner=new LinearScalingModelLearner(scalingExamples, normalizeL2);
		} else if (scalingType.equals("zscore")) {
			scalingModelLearner=new ZscoreScalingModelLearner(scalingExamples, normalizeL2);
		}

		// determine filenames

		if (gammaSet.isEmpty()) {
			gammaSet.add(UNSPECIFIED_GAMMA);
		}

		builder.kernelSet=new HashSet<KernelFunction>();

		switch (kernel_type) {
			case LINEAR:
				builder.kernelSet.add(new LinearKernel());
				break;
			case POLY:
				for (Float gamma : gammaSet) {
					builder.kernelSet.add(new PolynomialKernel(degree, gamma, coef0));
				}
				break;
			case RBF:
				for (Float gamma : gammaSet) {
					builder.kernelSet.add(new GaussianRBFKernel(gamma));
				}
				break;
			case SIGMOID:
				for (Float gamma : gammaSet) {
					builder.kernelSet.add(new SigmoidKernel(gamma, coef0));
				}
				break;
			case PRECOMPUTED:
				builder.kernelSet.add(new PrecomputedKernel());
				break;
			default:
				throw new SvmException("Unknown kernel type: "+kernel_type);
		}

		builder.scalingModelLearner=scalingModelLearner;
		// param.kernel = kernel;

		this.param=builder.build();

		// ivparam = new SvmParameterVariable(vparam);
		switch (svm_type) {
			case C_SVC:
				svm=new C_SVC();
				break;
			case NU_SVC:
				svm=new Nu_SVC();
				break;
			case ONE_CLASS:
				svm=new OneClassSVC();
				break;
			case EPSILON_SVR:
				svm=new EpsilonSVR();
				break;
			case NU_SVR:
				svm=new Nu_SVR();
				break;
			default:
				throw new SvmException("Unknown svm type: "+kernel_type);
		}
	}
}
