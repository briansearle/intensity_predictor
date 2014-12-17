package edu.washington.maccoss.intensity_predictor;

public class Logger {
	public static void writeLog(String message) {
		System.out.println(message);
	}
	public static void writeError(String message) {
		System.err.println(message);
	}
	public static void writeLog(Throwable t) {
		t.printStackTrace(System.out);
	}
	public static void writeError(Throwable t) {
		t.printStackTrace(System.err);
	}
}
