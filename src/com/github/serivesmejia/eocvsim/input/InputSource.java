package com.github.serivesmejia.eocvsim.input;

import org.opencv.core.Mat;

public abstract class InputSource {

	transient boolean isDefault = false;

	void init(){}
	void reset(){}
	void close(){}

	Mat update() { return null; }

	InputSource cloneSource() { return null; }

}
