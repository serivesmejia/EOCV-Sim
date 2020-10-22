package com.github.serivesmejia.eocvsim.input;

import com.github.serivesmejia.eocvsim.EOCVSim;
import org.opencv.core.Mat;

public abstract class InputSource {

	public transient boolean isDefault = false;
	public transient EOCVSim eocvSim = null;

	public boolean init(){ return false; }
	public void reset(){}
	public void close(){}

	public Mat update() { return null; }

	public InputSource cloneSource() { return null; }

}
