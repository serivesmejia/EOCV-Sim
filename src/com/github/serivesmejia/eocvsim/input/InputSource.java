package com.github.serivesmejia.eocvsim.input;

import org.opencv.core.Mat;

public abstract class InputSource {

	public transient boolean isDefault = false;

	public void init(){}
	public void reset(){}
	public void close(){}

	public Mat update() { return null; }

	public InputSource cloneSource() { return null; }

}
