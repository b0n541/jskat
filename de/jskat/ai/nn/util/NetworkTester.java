/*

@ShortLicense@

Author: @JS@

Released: @ReleaseDate@

*/

package de.jskat.ai.nn.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test class for NeuralNetwork
 */
public class NetworkTester {

	private static Log log = LogFactory.getLog(NetworkTester.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//testBooleanFunction();
		testSkat();
	}
	
	private static void testSkat() {
		
		List<Double> opponent1 = new ArrayList<Double>();
		List<Double> opponent2 = new ArrayList<Double>();
		List<Double> opponent3 = new ArrayList<Double>();
		
		opponent1.add(0.5); // A --> first opponent
		opponent1.add(0.5); // K
		opponent1.add(0.5); // Q
		opponent1.add(0.0); // J
		opponent1.add(0.5); // T
		opponent1.add(-1.0); // 9
		opponent1.add(0.0); // 8
		opponent1.add(0.0); // 7
		opponent1.add(0.5); // A --> second opponent
		opponent1.add(0.5); // K
		opponent1.add(0.5); // Q
		opponent1.add(0.0); // J
		opponent1.add(0.5); // T
		opponent1.add(0.0); // 9
		opponent1.add(-1.0); // 8
		opponent1.add(0.0); // 7
		opponent1.add(0.0); // A --> me
		opponent1.add(0.0); // K
		opponent1.add(0.0); // Q
		opponent1.add(0.5); // J
		opponent1.add(0.0); // T
		opponent1.add(0.0); // 9
		opponent1.add(0.0); // 8
		opponent1.add(-1.0); // 7
		opponent2.add(0.5); // A --> first opponent
		opponent2.add(0.5); // K
		opponent2.add(0.5); // Q
		opponent2.add(0.0); // J
		opponent2.add(0.5); // T
		opponent2.add(-1.0); // 9
		opponent2.add(0.0); // 8
		opponent2.add(0.0); // 7
		opponent2.add(0.5); // A --> second opponent
		opponent2.add(0.5); // K
		opponent2.add(0.5); // Q
		opponent2.add(0.0); // J
		opponent2.add(0.5); // T
		opponent2.add(0.0); // 9
		opponent2.add(-1.0); // 8
		opponent2.add(0.0); // 7
		opponent2.add(0.0); // A --> me
		opponent2.add(0.0); // K
		opponent2.add(0.0); // Q
		opponent2.add(-0.5); // J
		opponent2.add(0.0); // T
		opponent2.add(0.0); // 9
		opponent2.add(0.0); // 8
		opponent2.add(0.5); // 7
		opponent3.add(0.5); // A --> first opponent
		opponent3.add(0.5); // K
		opponent3.add(0.5); // Q
		opponent3.add(0.0); // J
		opponent3.add(-0.5); // T
		opponent3.add(-1.0); // 9
		opponent3.add(0.0); // 8
		opponent3.add(0.0); // 7
		opponent3.add(0.5); // A --> second opponent
		opponent3.add(0.5); // K
		opponent3.add(0.5); // Q
		opponent3.add(0.0); // J
		opponent3.add(0.0); // T
		opponent3.add(0.0); // 9
		opponent3.add(-1.0); // 8
		opponent3.add(0.0); // 7
		opponent3.add(0.0); // A --> me
		opponent3.add(0.0); // K
		opponent3.add(0.0); // Q
		opponent3.add(-1.0); // J
		opponent3.add(0.0); // T
		opponent3.add(0.0); // 9
		opponent3.add(0.0); // 8
		opponent3.add(0.5); // 7
		
		double input[][] = new double[3][opponent1.size()];
		
		for (int i = 0; i < opponent1.size(); i++) {
			
			input[0][i] = opponent1.get(i);
			input[1][i] = opponent2.get(i);
			input[2][i] = opponent3.get(i);
		}
		
		double output[][] = {{1.0}, {-1.0}, {1.0}};
		
		int[] hiddenNeurons = {2};
		NetworkTopology topo = new NetworkTopology(input[0].length, output[0].length, 1, hiddenNeurons);
		
		NeuralNetwork net = new NeuralNetwork(topo);
		
		log.debug(net);
		
		int goodGuess = 0;
		int i = 0;
		
		while (goodGuess < input.length) {
			
			net.adjustWeights(input[i % input.length], output[i % input.length]);
			
			if (Math.abs(net.getAvgDiff()) < 0.1) {
				goodGuess++;
			}
			else {
				goodGuess = 0;
			}
			
			if (i % 1000 == 0) {
				
				log.debug(i + " iterations " + goodGuess + " good guesses...");
			}
			i++;
		}
		log.debug("Learned pattern after " + i + " iterations.");
//		log.debug(net);
//		
		for (i = 0; i < input.length; i++) {
			
			net.getPredictedOutcome(input[i % input.length]);
			log.debug(net.getInputParameters());
			log.debug(net.getOutputParameters());
		}
		
		net.saveNetwork("asdf.net");
		
		log.debug("Re-loading network");
		
		NeuralNetwork net2 = new NeuralNetwork();
		net2.loadNetwork("asdf.net");

		goodGuess = 0;
		i = 0;
		while (goodGuess < input.length) {
			net.adjustWeights(input[i % input.length], output[i % input.length]);
			
			if (Math.abs(net.getAvgDiff()) < 0.1) {
				goodGuess++;
			}
			else {
				goodGuess = 0;
			}
			
			if (i % 1000 == 0) {
				
				log.debug(i + " iterations " + goodGuess + " good guesses...");
			}
			i++;
		}
		log.debug("Learned pattern after " + i + " iterations.");

		for (i = 0; i < input.length; i++) {
			
			net.getPredictedOutcome(input[i]);
			log.debug(net.getInputParameters());
			log.debug(net.getOutputParameters());
		}
	}
	
	private static void testBooleanFunction() {
		
		NeuralNetwork net = new NeuralNetwork();
		log.debug(net);
//		double[][] input = {{1.0, 1.0},
//				{1.0, 0.0},
//				{0.0, 1.0},
//				{0.0, 0.0}};
//		double[][] output = {{0.0},
//						 {1.0},
//						 {1.0},
//						 {0.0}};
		double[][] input = {{1.0, 1.0, 1.0},
				{1.0, 1.0, 0.0},
				{1.0, 0.0, 1.0},
				{1.0, 0.0, 0.0},
				{0.0, 1.0, 1.0},
				{0.0, 1.0, 0.0},
				{0.0, 0.0, 1.0},
				{0.0, 0.0, 0.0}};
		double[][] output = {{1.0}, // A and B or C
				 {1.0},
				 {1.0},
				 {0.0},
				 {1.0},
				 {0.0},
				 {1.0},
				 {0.0}};
		int goodGuess = 0;
		
		for (int i = 0; i < 10000; i++) {
			net.adjustWeights(input[i % input.length], output[i % input.length]);
			
			if (net.getAvgDiff() < 0.1) {
				goodGuess++;
			}
			else {
				goodGuess = 0;
			}
			
			if (goodGuess > input.length) {
				
				log.debug("Learned pattern after " + i + " iterations.");
				break;
			}
		}
//		log.debug(net);
//		
		for (int i = 0; i < input.length; i++) {
			
			net.getPredictedOutcome(input[i]);
			log.debug(net.getInputParameters());
			log.debug(net.getOutputParameters());
		}
	}
}
