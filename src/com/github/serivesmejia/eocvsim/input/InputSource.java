package com.github.serivesmejia.eocvsim.input;

import org.opencv.core.Mat;

public interface InputSource {

	void init();
	
	Mat update();
	
}
