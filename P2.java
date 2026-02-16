import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class P2 {
	static class Task {
		public static final String INPUT_FILE = "p2.in";
		public static final String OUTPUT_FILE = "p2.out";

		int n, m; /* Number of lines, respectively columns of the matrix. */
		int[][] matrix;
		int k; /* In any area, max(area) - min(area) <= k. */
		boolean[][] bfs; /* Mark the nodes whose BFS is known. */

		public void solve() {
			readInput();
			writeOutput(getResult());
		}

		private void readInput() {
			try {
				Scanner sc = new Scanner(new BufferedReader(new FileReader(
						INPUT_FILE)));

				n = sc.nextInt();
				m = sc.nextInt();
				k = sc.nextInt();

				matrix = new int[n][m];
				for (int i = 0; i < n; ++i) {
					for (int j = 0; j < m; ++j) {
						matrix[i][j] = sc.nextInt();
					}
				}

				sc.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private void writeOutput(int solution) {
			try {
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
						OUTPUT_FILE)));

				pw.printf("%d", solution);

				pw.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Verify if a position is inside the matrix.
		 *
		 * @param i line's index
		 * @param j col's index
		 * @return true, if access a valid position from matrix
		 * 		   false, else
		 */
		private boolean posIsValid(final int i, final int j) {
			return 0 <= i && i < n  && 0 <= j && j < m;
		}

		/**
		 * Helper for the function getMaxArea().
		 *
		 * @param i line's index
		 * @param j col's index
		 * @param visited matrix
		 * @param lowLimit for the search interval
		 * @return the maximum area which can be obtained if we follow 2 rules:
		 *     -> we cannot visit positions that we have already been to
		 *     -> the numbers must be in the interval [lowLimit, lowLimit + k]
		 */
		private int explore(final int i, final int j, final boolean[][] visited,
							final int lowLimit) {
			/* Verify the first rule. */
			if (!posIsValid(i, j) || visited[i][j]) {
				return 0;
			}

			/* Verify the second rule. */
			if (!(lowLimit <= matrix[i][j] && matrix[i][j] <= lowLimit + k)) {
				return 0;
			}

			/* We cannot come back. */
			visited[i][j] = true;

			/* Detect the nodes for which we will go through the same vertexes
			 * as the current DFS if we start a DFS from them. */
			if (matrix[i][j] == lowLimit) {
				bfs[i][j] = true;
			}

			/* Explore all the 4 possible directions */
			return 1 + explore(i - 1, j, visited, lowLimit) + explore(i + 1, j, visited, lowLimit)
				+ explore(i, j - 1, visited, lowLimit) + explore(i, j + 1, visited, lowLimit);
		}


		/**
		 * Calculate the maximum area which can be obtained if we follow 2 rules:
		 * 		-> we start from the given position
		 * 		-> no number from area can be smaller than the one from which we started
		 *
		 * @param startLine index
		 * @param startCol index
		 * @return area (number of nodes from DFS)
		 */
		private int getMaxArea(final int startLine, final int startCol) {
			boolean[][] visited = new boolean[n][m];
			return explore(startLine, startCol, visited, matrix[startLine][startCol]);
		}

		private int getResult() {
			int maxArea = 0;
			bfs =  new boolean[n][m];

			for (int i = 0; i < n; ++i) {
				for (int j = 0; j < m; ++j) {
					if (!bfs[i][j]) {
						maxArea = Math.max(maxArea, getMaxArea(i, j));
					}
				}
			}

			return maxArea;
		}
	}

	public static void main(String[] args) {
		new Task().solve();
	}
}
