package edu.washington.maccoss.intensity_predictor.math;

public class MatrixMath {

	public static void print(double[][] m) {
		for (int i=0; i<m.length; i++) {
			System.out.print("[");
			for (int j=0; j<m[i].length; j++) {
				if (j>0) System.out.print(", ");
				System.out.print(m[i][j]);
			}
			System.out.println("]");
		}
	}

	public static void print(double[] m) {
		System.out.print("[");
		for (int i=0; i<m.length; i++) {
			if (i>0) System.out.print(", ");
			System.out.print(m[i]);
		}
		System.out.println("]");
	}

	/**
	 * from http://www.sanfoundry.com/java-program-find-inverse-matrix/
	 * @param a
	 * @return
	 */
	public static double[][] invert(double a[][]) {
		int n=a.length;
		double x[][]=new double[n][n];
		double b[][]=new double[n][n];
		int index[]=new int[n];
		for (int i=0; i<n; ++i)
			b[i][i]=1;

		// Transform the matrix into an upper triangle
		gaussian(a, index);

		// Update the matrix b[i][j] with the ratios stored
		for (int i=0; i<n-1; ++i)
			for (int j=i+1; j<n; ++j)
				for (int k=0; k<n; ++k)
					b[index[j]][k]-=a[index[j]][i]*b[index[i]][k];

		// Perform backward substitutions
		for (int i=0; i<n; ++i) {
			x[n-1][i]=b[index[n-1]][i]/a[index[n-1]][n-1];
			for (int j=n-2; j>=0; --j) {
				x[j][i]=b[index[j]][i];
				for (int k=j+1; k<n; ++k) {
					x[j][i]-=a[index[j]][k]*x[k][i];
				}
				x[j][i]/=a[index[j]][j];
			}
		}
		return x;
	}

	// Method to carry out the partial-pivoting Gaussian
	// elimination. Here index[] stores pivoting order.

	public static void gaussian(double a[][], int index[]) {
		int n=index.length;
		double c[]=new double[n];

		// Initialize the index
		for (int i=0; i<n; ++i)
			index[i]=i;

		// Find the rescaling factors, one from each row
		for (int i=0; i<n; ++i) {
			double c1=0;
			for (int j=0; j<n; ++j) {
				double c0=Math.abs(a[i][j]);
				if (c0>c1) c1=c0;
			}
			c[i]=c1;
		}

		// Search the pivoting element from each column
		int k=0;
		for (int j=0; j<n-1; ++j) {
			double pi1=0;
			for (int i=j; i<n; ++i) {
				double pi0=Math.abs(a[index[i]][j]);
				pi0/=c[index[i]];
				if (pi0>pi1) {
					pi1=pi0;
					k=i;
				}
			}

			// Interchange rows according to the pivoting order
			int itmp=index[j];
			index[j]=index[k];
			index[k]=itmp;
			for (int i=j+1; i<n; ++i) {
				double pj=a[index[i]][j]/a[index[j]][j];

				// Record pivoting ratios below the diagonal
				a[index[i]][j]=pj;

				// Modify other elements accordingly
				for (int l=j+1; l<n; ++l)
					a[index[i]][l]-=pj*a[index[j]][l];
			}
		}
	}

	public static double[][] multiply(double[][] m, double[][] n) {
		double[][] r=new double[m.length][n[0].length];
		for (int i=0; i<m.length; i++) {
			for (int j=0; j<n[0].length; j++) {
				for (int k=0; k<m[0].length; k++) {
					r[i][j]+=m[i][k]*n[k][j];
				}
			}
		}
		return r;
	}
	
	public static double[][] multiply(double[][] m, double v) {
		double[][] n=new double[m.length][];
		for (int i=0; i<m.length; i++) {
			n[i]=new double[m[i].length];
			for (int j=0; j<m[i].length; j++) {
				n[i][j]=m[i][j]*v;
			}
		}
		return n;
	}
	
	public static double[] multiply(double[][] m, double[] array) {
		double[] n=new double[m.length];
		for (int i=0; i<m.length; i++) {
			for (int j=0; j<m[i].length; j++) {
				n[i]+=m[i][j]*array[j];
			}
		}
		return n;
	}
	
	public static double multiply(double[] a, double[] b) {
		double d=0.0;
		for (int i=0; i<a.length; i++) {
			d+=a[i]*b[i];
		}
		return d;
	}
	
	public static double[][] subtract(double[][] m, double[] array) {
		double[][] n=new double[m.length][];
		for (int i=0; i<m.length; i++) {
			n[i]=new double[m[i].length];
			for (int j=0; j<m[i].length; j++) {
				n[i][j]=m[i][j]-array[j];
			}
		}
		return n;
	}
	
	public static double[] subtract(double[] a, double[] b) {
		double[] r=new double[a.length];
		for (int i=0; i<r.length; i++) {
			r[i]=a[i]-b[i];
		}
		return r;
	}
	
	public static double getRange(double[] array) {
		double min=Double.MAX_VALUE;
		double max=-Double.MAX_VALUE;
		for (double value : array) {
			if (value>max) max=value;
			if (value<min) min=value;
		}
		return max-min;
	}

	public static double[] getColumn(double[][] m, int col) {
		double[] v=new double[m.length];
		for (int i=0; i<v.length; i++) {
			v[i]=m[i][col];
		}
		return v;
	}
	public static double[][] transpose(double[][] m) {
		double[][] n=new double[m[0].length][];
		for (int i=0; i<n.length; i++) {
			n[i]=new double[m.length];
		}
		for (int i=0; i<m.length; i++) {
			for (int j=0; j<m[i].length; j++) {
				n[j][i]=m[i][j];
			}
		}
		return n;
	}
}
