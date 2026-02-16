import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class P1 {
	static class Task {
		public static final String INPUT_FILE = "p1.in";
		public static final String OUTPUT_FILE = "p1.out";

		/* nodesByDist[d] = a list with the nodes whose shortest path
		 * from the source node (node 1) has length exactly d*/
		ArrayList<Integer>[] nodesByDist;
		int numNodes;

		PrintWriter pw;

		public void solve() {
			readInput();
			getResult();
		}

		@SuppressWarnings("unchecked")
		private void readInput() {
			try {
				Scanner sc = new Scanner(new BufferedReader(new FileReader(
						INPUT_FILE)));

				numNodes = sc.nextInt();

				/* Allocate memory. */
				nodesByDist = new ArrayList[numNodes + 1];
				for (int node = 0; node <= numNodes; node++) {
					nodesByDist[node] = new ArrayList<>();
				}

				/* Read the lvl of every node from BFS. */
				for (int node = 1; node <= numNodes; node++) {
					int distance = sc.nextInt();
					nodesByDist[distance].addFirst(node);
				}

				sc.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Verify if a graph can be built such as to can obtain the given
		 * BFS vector if we start from node 1.
		 *
		 * @return true, if it can be built
		 * 		   false, else
		 */
		private boolean existGraph() {
			/* Just one node can be the root, and that one must be node 1. */
			if (nodesByDist[0].size() != 1 || nodesByDist[0].getFirst() != 1) {
				pw.printf("%d\n", -1);
				return false;
			}

			/* At every lvl of the BFS, we discover at least a node.
			 * In the slowest case, we discover all nodes only at lvl (numNodes - 1).
			 * After that, no vertexes are left to be discovered. */
			if (!nodesByDist[numNodes].isEmpty()) {
				pw.printf("%d\n", -1);
				return false;
			}

			/* Check that we do not have any gap. If we choose any 2 levels populated
			 * with nodes, all the layers between them should be populated (to have at
			 * least one node each level). So, if we find an uncopied lvl, the only
			 * valid option is that all the other layers after it to be empty. */
			for (int i = 1; i <= numNodes; ++i) {
				if (!nodesByDist[i].isEmpty() && nodesByDist[i - 1].isEmpty()) {
					pw.printf("%d\n", -1);
					return false;
				}
			}

			/* Everything is all right. */
			return true;
		}

		/**
		 * We will create a tree such as to have the read BFS vector
		 * if we start from node 1. Why did we choose a tree? Because
		 * is a minimal connected graph. It has the minimum number of
		 * edges to be connected.
		 */
		private void printGraph() {
			/* Print the number of edges */
			pw.printf("%d\n", numNodes - 1);

			/* Prin the edges. Tie the nodes from a layer with a vertex from
			 * the previous level. */
			for (int dist = 1; dist < numNodes; ++dist) {
				if (nodesByDist[dist].isEmpty()) {
					return;
				}

				int source = nodesByDist[dist - 1].getFirst();
				for (int destination : nodesByDist[dist]) {
					pw.printf("%d %s\n", source, destination);
				}
			}
		}

		private void getResult() {
			try {
				pw = new PrintWriter(new BufferedWriter(new FileWriter(
						OUTPUT_FILE)));

				if (existGraph()) {
					printGraph();
				}

				pw.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void main(String[] args) {
		new Task().solve();
	}
}
