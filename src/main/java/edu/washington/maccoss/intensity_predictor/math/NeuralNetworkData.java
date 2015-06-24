/**
 * Copyright 2015 Brian C. Searle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.washington.maccoss.intensity_predictor.math;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.learning.BackPropagation;

import edu.washington.maccoss.intensity_predictor.Logger;
import edu.washington.maccoss.intensity_predictor.properties.AbstractProperty;


public class NeuralNetworkData {
	private static final String minArrayName="min array";
	private static final String maxArrayName="max array";
	private static final String nnName="neural_network_classifier.nn";
	private static final String nnMetaName="neural_network_metadata.nn";
	private static final String nnPropertiesName="neural_network_properties.nn";

	public static void saveNetwork(BackPropNeuralNetwork nn, File dir) {
		File classifier=new File(dir, nnName);
		File metadata=new File(dir, nnMetaName);
		File properties=new File(dir, nnPropertiesName);

		if (dir.exists()) {
			if (!dir.isDirectory()) {
				dir.delete();
			} else {
				if (classifier.exists()) classifier.delete();
				if (metadata.exists()) metadata.delete();
				if (properties.exists()) properties.delete();
			}
		} else {
			dir.mkdirs();
		}

		nn.getNeuralNetwork().save(classifier.getAbsolutePath());

		Properties prop=new Properties();
		prop.setProperty(minArrayName, General.toPropertyString(nn.getMin()));
		prop.setProperty(maxArrayName, General.toPropertyString(nn.getMax()));
		try {
			prop.store(new FileOutputStream(metadata), "Neural Network Intensity Predictor Metadata");
		} catch (FileNotFoundException fnfe) {
			Logger.writeError("FileNotFoundException writing Neural Network metadata file.");
			Logger.writeError(fnfe);
		} catch (IOException ioe) {
			Logger.writeError("IOException writing Neural Network metadata file.");
			Logger.writeError(ioe);
		}
		
		ArrayList<AbstractProperty> propertyList=nn.getPropertyList();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(properties)));
            out.writeObject(propertyList);
            out.flush();
        } catch (IOException ioe) {
			Logger.writeError("IOException writing Neural Network properties file.");
			Logger.writeError(ioe);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
	}

	public static BackPropNeuralNetwork readNetwork(File dir) {
		File classifier=new File(dir, nnName);
		File metadata=new File(dir, nnMetaName);
		File properties=new File(dir, nnPropertiesName);

		NeuralNetwork<BackPropagation> nn=NeuralNetwork.createFromFile(classifier);

		Properties props=new Properties();

		FileInputStream in=null;
		ObjectInputStream oistream=null;
		try {
			in=new FileInputStream(metadata);
			props.load(in);
			double[] min=General.fromPropertyString(props.getProperty(minArrayName));
			double[] max=General.fromPropertyString(props.getProperty(maxArrayName));
			
            oistream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(properties)));
			ArrayList<AbstractProperty> propertyList=(ArrayList<AbstractProperty>)oistream.readObject();
			return new BackPropNeuralNetwork(nn, min, max, propertyList);

		} catch (ClassNotFoundException cnfe) {
			Logger.writeError("ClassNotFoundException reading Neural Network properties file.");
			Logger.writeError(cnfe);
			return null;
		} catch (FileNotFoundException fnfe) {
			Logger.writeError("FileNotFoundException reading Neural Network metadata file.");
			Logger.writeError(fnfe);
			return null;
		} catch (IOException ioe) {
			Logger.writeError("IOException reading Neural Network properties file.");
			Logger.writeError(ioe);
			return null;
		} finally {
			try {
			if (in!=null) in.close();
			if (oistream!=null) oistream.close();
			} catch (IOException ioe) {
				Logger.writeError("IOException closing read Neural Network properties file.");
				Logger.writeError(ioe);
			}
		}
	}

	public static BackPropNeuralNetwork readNetwork(InputStream classifier, InputStream metadata, InputStream properties) {
		Properties props=new Properties();

		ObjectInputStream oistream=null;
		try {
			NeuralNetwork nn = (NeuralNetwork)new ObjectInputStream(new BufferedInputStream(classifier)).readObject();
			props.load(metadata);
			double[] min=General.fromPropertyString(props.getProperty(minArrayName));
			double[] max=General.fromPropertyString(props.getProperty(maxArrayName));

			oistream = new ObjectInputStream(new BufferedInputStream(properties));
			ArrayList<AbstractProperty> propertyList=(ArrayList<AbstractProperty>)oistream.readObject();
			return new BackPropNeuralNetwork(nn, min, max, propertyList);

		} catch (ClassNotFoundException cnfe) {
			Logger.writeError("ClassNotFoundException reading Neural Network properties file.");
			Logger.writeError(cnfe);
			return null;
		} catch (FileNotFoundException fnfe) {
			Logger.writeError("FileNotFoundException reading Neural Network metadata file.");
			Logger.writeError(fnfe);
			return null;
		} catch (IOException ioe) {
			Logger.writeError("IOException reading Neural Network properties file.");
			Logger.writeError(ioe);
			return null;
		} finally {
			try {
				if (oistream!=null) oistream.close();
			} catch (IOException ioe) {
				Logger.writeError("IOException closing read Neural Network properties file.");
				Logger.writeError(ioe);
			}
		}
	}
}
