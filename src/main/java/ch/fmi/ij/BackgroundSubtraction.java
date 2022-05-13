/*-
 * #%L
 * A collection of ImageJ2 plugins wrapping several ImageJ commands to make them work headless e.g. in KNIME. Developed at the FMI Basel.
 * %%
 * Copyright (C) 2022 Friedrich Miescher Institute for Biomedical Research, Basel
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package ch.fmi.ij;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.plugin.filter.BackgroundSubtracter;
import ij.process.ImageProcessor;

/**
 * Headless version of the 'Subtract Background' command.
 * 
 * @author Jan Eglinger
 */
@Plugin(type = Command.class, headless = true, menuPath = "FMI>Subtract Background")
public class BackgroundSubtraction extends ContextCommand {

	@Parameter(label = "Input image", type = ItemIO.BOTH)
	private ImagePlus imp;

	@Parameter
	private Double radius;

	@Parameter
	private Boolean createBackground = false;

	@Parameter
	private Boolean lightBackground = false;

	@Parameter
	private Boolean useParaboloid = true;

	@Parameter
	private Boolean doPresmooth = true;

	@Parameter
	private Boolean correctCorners = true;

	@Override
	public void run() {
		BackgroundSubtracter subtracter = new BackgroundSubtracter();
		for (int i=1; i <= imp.getImageStackSize(); i++) {
			imp.setSlice(i);
			ImageProcessor ip = imp.getProcessor();
			subtracter.rollingBallBackground(ip, radius, createBackground, lightBackground, useParaboloid, doPresmooth, correctCorners);
		}
		imp.updateAndDraw();
	}
}
