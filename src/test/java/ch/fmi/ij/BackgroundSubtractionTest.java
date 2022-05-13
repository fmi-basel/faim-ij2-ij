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

import static net.imglib2.test.ImgLib2Assert.assertImageEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.command.CommandModule;
import org.scijava.command.CommandService;

import ij.IJ;
import ij.ImagePlus;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;

public class BackgroundSubtractionTest {

	private Context context;
	private CommandService commandService;

	@Before
	public void setUp() throws Exception {
		context = new Context();
		commandService = context.service(CommandService.class);
	}

	@After
	public void tearDown() throws Exception {
		context.dispose();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testEqualParameters() throws InterruptedException, ExecutionException {
		ImagePlus imp = IJ.openImage("https://imagej.net/images/Spindly-GFP.zip");
		ImagePlus expected = imp.duplicate();
		IJ.run(expected, "Subtract Background...", "rolling=15 sliding stack");
		HashMap<String,Object> inputMap = new HashMap<>();
		inputMap.put("imp", imp);
		inputMap.put("radius", 15.0);
		CommandModule module = commandService.run(BackgroundSubtraction.class, false, inputMap).get();
		ImagePlus output = (ImagePlus) module.getOutput("imp");
		assertNotNull(output);
		assertImageEquals((RandomAccessibleInterval)ImageJFunctions.wrap(expected), (RandomAccessibleInterval) ImageJFunctions.wrap(output));
	}

	@Test
	public void testUnequalParameters() throws InterruptedException, ExecutionException {
		ImagePlus imp = IJ.openImage("https://imagej.net/images/Spindly-GFP.zip");
		ImagePlus expected = imp.duplicate();
		IJ.run(expected, "Subtract Background...", "rolling=50 sliding stack");
		HashMap<String,Object> inputMap = new HashMap<>();
		inputMap.put("imp", imp);
		inputMap.put("radius", 10.0);
		CommandModule module = commandService.run(BackgroundSubtraction.class, false, inputMap).get();
		ImagePlus output = (ImagePlus) module.getOutput("imp");
		assertNotNull(output);
		assertNotEquals(expected.getPixel(0, 0), output.getPixel(0, 0));
	}
}
