package de.biovoxxel.threshold.test;

import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.LUT;

/**
 * 
 * This is a demo to show thresholding on an image with a specific color LUT
 * 
 * @author Jan Brocher (BioVoxxel)
 *
 */
@Plugin(type = Command.class, menuPath = "Test>LUT")
public class ThresholdLut extends DynamicCommand {

	@Parameter (initializer = "setup")
	ImagePlus imp;
	
	@Parameter(callback = "adjustLut", style = "slider", min = "0", max = "255")
	Double min = 0.0;
	
	@Parameter(callback = "adjustLut",style = "slider", min = "0", max = "255")
	Double max = 255.0;
	
	ImageProcessor ip;
	LUT originalLUT;

	private double minValue;

	private double maxValue;
	
	public ThresholdLut() {
		
	}
	
	public LUT getThresholdLut(ImageProcessor ip, LUT originalLUT, double min, double max) {
				
		byte[] reds = new byte[256];
		byte[] greens = new byte[256];
		byte[] blues = new byte[256];

		originalLUT.getReds(reds);
		originalLUT.getGreens(greens);
		originalLUT.getBlues(blues);
		
		byte[] transientReds = new byte[256];
		byte[] transientGreens = new byte[256];
		byte[] transientBlues = new byte[256];
		
		for (int i = 0; i < 256; i++) {
			if (i >= min && i <= max) {
				transientReds[i] = (byte)255;
				transientGreens[i] = (byte)0;
				transientBlues[i] = (byte)0;
			} else {
				transientReds[i] = reds[i];
				transientGreens[i] = greens[i];
				transientBlues[i] = blues[i];
			}
		}
		
		LUT transientLUT = new LUT(transientReds, transientGreens, transientBlues);
		
		return transientLUT;
		
	}
	
	
	public void setup() {
		ip = imp.getProcessor();
		originalLUT = ip.getLut();
		minValue = ip.getMin();
		maxValue = ip.getMax();
	}
	

	public void adjustLut() {	
		
		double minimum = min > max ? max : min;
		double maximum = max < min ? min : max;
		
		final MutableModuleItem<Double> min = getInfo().getMutableInput("min", Double.class);
		final MutableModuleItem<Double> max = getInfo().getMutableInput("max", Double.class);
		
		min.setValue(this, minimum);
		max.setValue(this, maximum);
		
		setThreshold();
		
	}
	
	public void setThreshold() {
		
		ThresholdLut thrLut = new ThresholdLut();
		LUT outputLUT = thrLut.getThresholdLut(ip, originalLUT, min, max);
		
		imp.setLut(outputLUT);
		ip.setMinAndMax(minValue, maxValue);
	}
	
	public void run() {
		//not implemented since just a demo
	}
	
	public void cancel() {
		imp.setLut(originalLUT);
	}

}
