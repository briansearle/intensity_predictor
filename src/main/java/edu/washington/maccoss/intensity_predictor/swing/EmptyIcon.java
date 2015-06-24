package edu.washington.maccoss.intensity_predictor.swing;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * An empty icon with arbitrary width and height.
 */
public final class EmptyIcon implements Icon {

	private int width;
	private int height;

	public EmptyIcon() {
		this(0, 0);
	}

	public EmptyIcon(int width, int height) {
		this.width=width;
		this.height=height;
	}

	public int getIconHeight() {
		return height;
	}

	public int getIconWidth() {
		return width;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
	}

}