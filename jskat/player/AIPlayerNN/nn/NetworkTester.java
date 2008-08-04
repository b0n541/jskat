package jskat.player.AIPlayerNN.nn;

import java.util.Vector;

/**
 * Test class for NeuralNetwork
 * 
 * @author Jan Sch&auml;fer <j@nschaefer.net>
 */
public class NetworkTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//testBooleanFunction();
		testSkat();
	}
	
	private static void testSkat() {
		
		Vector<Double> opponent1 = new Vector<Double>();
		Vector<Double> opponent2 = new Vector<Double>();
		Vector<Double> opponent3 = new Vector<Double>();
		
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
		
		int[] hiddenLayers = {25};
		NetworkTopology topo = new NetworkTopology(input[0].length, output[0].length, 1, hiddenLayers);
		
		NeuralNetwork net = new NeuralNetwork(topo);
		
		System.out.println(net);
		
		int goodGuess = 0;
		int i = 0;
		
		while (goodGuess < input.length) {
			net.setInputParameter(input[i % input.length]);
			net.propagateForward();
			net.setOutputParameter(output[i % input.length]);
			net.propagateBackward();
			
			if (Math.abs(net.getAvgDiff()) < 0.1) {
				goodGuess++;
			}
			else {
				goodGuess = 0;
			}
			
			if (i % 1000 == 0) {
				
				System.out.println(i + " iterations " + goodGuess + " good guesses...");
			}
			i++;
		}
		System.out.println("Learned pattern after " + i + " iterations.");
//		System.out.println(net);
//		
		for (i = 0; i < input.length; i++) {
			
			net.setInputParameter(input[i]);
			net.propagateForward();
			System.out.println(net.getInputParameters());
			System.out.println(net.getOutputParameters());
		}
		
	}
	
	private static void testBooleanFunction() {
		
		NeuralNetwork net = new NeuralNetwork();
		System.out.println(net);
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
			net.setInputParameter(input[i % input.length]);
			net.propagateForward();
			net.setOutputParameter(output[i % input.length]);
			net.propagateBackward();
			
			if (net.getAvgDiff() < 0.1) {
				goodGuess++;
			}
			else {
				goodGuess = 0;
			}
			
			if (goodGuess > input.length) {
				
				System.out.println("Learned pattern after " + i + " iterations.");
				break;
			}
		}
//		System.out.println(net);
//		
		for (int i = 0; i < input.length; i++) {
			
			net.setInputParameter(input[i]);
			net.propagateForward();
			System.out.println(net.getInputParameters());
			System.out.println(net.getOutputParameters());
		}
	}
}
