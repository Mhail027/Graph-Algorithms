import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;


public class P3 {
	static class Pos {
		final int logIdx;
		int x, y;

		public Pos(int logIdx, int x, int y) {
			this.logIdx = logIdx;
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(final Object o) {
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			Pos pos = (Pos) o;
			return logIdx == pos.logIdx && x == pos.x && y == pos.y;
		}

		@Override
		public int hashCode() {
			return Objects.hash(logIdx, x, y);
		}

		private void moveToNorth() {
			y++;
		}

		private void moveToSouth() {
			y--;
		}

		private void moveToEast() {
			x++;
		}

		private void moveToWest() {
			x--;
		}

		public void move(char direction) {
			switch (direction) {
				case 'N':
					moveToNorth();
					return;
				case 'S':
					moveToSouth();
					return;
				case 'E':
					moveToEast();
					return;
				case 'V':
					moveToWest();
					return;
				default:
					throw new IllegalArgumentException("Invalid direction.");
			}
		}
	}

	static class History {
		Stack<String> actions;
		int energy;

		public History(final Stack<String> actions, final int energy) {
			this.actions = new Stack<>();
			this.actions.addAll(actions);

			this.energy = energy;
		}

		public History() {
			actions = new Stack<>();
			energy = 0;
		}

		public void add(final String action, int cost) {
			actions.push(action);
			energy += cost;
		}
	}

	static class Log {
		int idx;
		Pos startPos;
		Pos endPos;

		public Log(int idx, int xStart, int yStart, int xEnd, int yEnd) {
			this.idx = idx;
			startPos = new Pos(idx, xStart, yStart);
			endPos = new Pos(idx, xEnd, yEnd);
		}

		public void move(char direction) {
			startPos.move(direction);
			endPos.move(direction);
		}
	}

	static class Task {
		public static final String INPUT_FILE = "p3.in";
		public static final String OUTPUT_FILE = "p3.out";

		int time, numLogs;
		int E1, E2, E3;
		Pos startPos, maidPos;
		Log[] logs;
		String[] directions;

		Set<Pos>[] lake; /* lake[i] = valid positions at timestamp i */
		History bestHistory; /* to get to maid Marian */

		public void solve() {
			readInput();
			getResult();
			writeOutput();
		}

		private void readInput() {
			try {
				Scanner sc = new Scanner(new BufferedReader(new FileReader(
						INPUT_FILE)));

				time = sc.nextInt();
				numLogs = sc.nextInt();
				maidPos = new Pos(-1, sc.nextInt(), sc.nextInt());
				E1 = sc.nextInt();
				E2 = sc.nextInt();
				E3 = sc.nextInt();

				/* Read the initial positions of the logs. */
				logs = new Log[numLogs + 1];
				for (int i = 1; i <= numLogs; ++i) {
					int xStart = sc.nextInt();
					int yStart = sc.nextInt();
					int xEnd = sc.nextInt();
					int yEnd = sc.nextInt();

					logs[i] = new Log(i, xStart, yStart, xEnd, yEnd);
				}

				/* Get the start position. */
				startPos = new Pos(logs[1].idx, logs[1].startPos.x, logs[1].startPos.y);

				/* Read the directions to which they will move. */
				directions = new String[numLogs + 1];
				for (int i = 1; i <= numLogs; ++i) {
					directions[i] = sc.next();
				}

				sc.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private void writeOutput() {
			try {
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
						OUTPUT_FILE)));

				pw.printf("%d\n", bestHistory.energy);

				if (bestHistory == null) {
					pw.close();
					return;
				}

				pw.printf("%d\n", bestHistory.actions.size());

				List<String> movesList = new ArrayList<>(bestHistory.actions);
				while (!movesList.isEmpty()) {
					pw.printf("%s\n", movesList.removeFirst());
				}

				pw.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		/* After we discovered a new backtracking level using BFS, we call
		 * this function to check if we found a more optim route to maid Marian.
		 */
		private void updateBestHistory(Map<Pos, History> bktLvl) {
			for (Map.Entry<Pos, History> entry : bktLvl.entrySet()) {
				Pos pos = entry.getKey();
				History history = entry.getValue();

				if (pos.x == maidPos.x && pos.y == maidPos.y) {
					if (bestHistory == null || history.energy < bestHistory.energy) {
						bestHistory = history;
					}
				}
			}
		}

		/**
		 * Add a new position in a backtracking layer. If the position already is there,
		 * update its route such as to remain the most optimal route to it.
		 *
		 * @param bktLvl hashmap where key = position and value = how did we get at position?
		 * @param pos info
		 * @param history how did we get there?
		 */
		private void addPos(final Map<Pos, History> bktLvl ,final Pos pos,
							final History history) {
			if (!bktLvl.containsKey(pos)) {
				bktLvl.put(pos, history);
			} else {
				History prevHistory = bktLvl.get(pos);
				if (prevHistory.energy > history.energy) {
					bktLvl.replace(pos, history);
				}
			}
		}

		/**
		 * Realize a backtracking for the all possible routes, using BFS.
		 * While we do this, we always keep the best discovered route to maid Mariane.
		 *
		 * @param timeIdx current timestamp
		 * @param bktLvl hashmap where key = position and value = how did we get at position?;
		 *               this level saves all the possible positions of Robin at the previous
		 *               timestamp
		 */
		private void backtrack(int timeIdx, final Map<Pos, History> bktLvl) {
			updateBestHistory(bktLvl);
			if (timeIdx == time) {
				return;
			}

			Map<Pos, History> nextBktLvl = new HashMap<>();
			for (Map.Entry<Pos, History> entry : bktLvl.entrySet()) {
				Pos pos = entry.getKey();
				History history = entry.getValue();

				/* Does it make sense to explore more this position? */
				if ((pos.x == maidPos.x && pos.y == maidPos.y)
					|| (bestHistory != null && bestHistory.energy < history.energy)) {
					continue;
				}

				/* East */
				if (lake[timeIdx].contains(new Pos(pos.logIdx, pos.x + 1, pos.y))) {
					Pos newPos = new Pos(pos.logIdx, pos.x + 1, pos.y);
					newPos.move(directions[newPos.logIdx].charAt(timeIdx));

					History newHistory = new History(history.actions, history.energy);
					newHistory.add("E", E2);

					addPos(nextBktLvl, newPos, newHistory);
				}

				/* West */
				if (lake[timeIdx].contains(new Pos(pos.logIdx, pos.x - 1, pos.y))) {
					Pos newPos = new Pos(pos.logIdx, pos.x - 1, pos.y);
					newPos.move(directions[newPos.logIdx].charAt(timeIdx));

					History newHistory = new History(history.actions, history.energy);
					newHistory.add("V", E2);

					addPos(nextBktLvl, newPos, newHistory);
				}

				/* North */
				if (lake[timeIdx].contains(new Pos(pos.logIdx, pos.x, pos.y + 1))) {
					Pos newPos = new Pos(pos.logIdx, pos.x, pos.y + 1);
					newPos.move(directions[newPos.logIdx].charAt(timeIdx));

					History newHistory = new History(history.actions, history.energy);
					newHistory.add("N", E2);

					addPos(nextBktLvl, newPos, newHistory);
				}

				/* South */
				if (lake[timeIdx].contains(new Pos(pos.logIdx, pos.x, pos.y - 1))) {
					Pos newPos = new Pos(pos.logIdx, pos.x, pos.y - 1);
					newPos.move(directions[newPos.logIdx].charAt(timeIdx));

					History newHistory = new History(history.actions, history.energy);
					newHistory.add("S", E2);

					addPos(nextBktLvl, newPos, newHistory);
				}

				/* Hold */
				{
					Pos newPos = new Pos(pos.logIdx, pos.x, pos.y);
					newPos.move(directions[newPos.logIdx].charAt(timeIdx));

					History newHistory = new History(history.actions, history.energy);
					newHistory.add("H", E1);

					addPos(nextBktLvl, newPos, newHistory);
				}

				/* Jump */
				for (int nextLogIdx = 1; nextLogIdx <= numLogs; ++nextLogIdx) {
					/* Cannot jump on the same log. */
					if (nextLogIdx == pos.logIdx) {
						continue;
					}

					/* Verify if we can jump. */
					if (!lake[timeIdx].contains(new Pos(nextLogIdx, pos.x, pos.y))) {
						continue;
					}

					Pos newPos = new Pos(nextLogIdx, pos.x, pos.y);
					newPos.move(directions[nextLogIdx].charAt(timeIdx));

					History newHistory = new History(history.actions, history.energy);
					String action = String.format("J %d", nextLogIdx);
					newHistory.add(action, E3);

					addPos(nextBktLvl, newPos, newHistory);
				}
			}

			/* Continue the process */
			backtrack(timeIdx + 1, nextBktLvl);
		}

		/* Place a vertical log on the lake at the given timestamp. */
		private void putVerticalLog(int timeIdx, final Log log) {
			for (int y = log.startPos.y; y <= log.endPos.y; ++y) {
				Pos pos = new Pos(log.idx, log.startPos.x, y);
				lake[timeIdx].add(pos);
			}
		}

		/* Place a horizontal log on the lake at the given timestamp. */
		private void putHorizontalLog(int timeIdx, final Log log) {
			for (int x = log.startPos.x; x <= log.endPos.x; ++x) {
				Pos pos = new Pos(log.idx, x, log.startPos.y);
				lake[timeIdx].add(pos);
			}
		}

		/* Place a log on the lake at the given timestamp. */
		private void putLog(final int timeIdx, final Log log) {
			if (lake[timeIdx] == null) {
				lake[timeIdx] = new HashSet<>();
			}

			if (log.startPos.x == log.endPos.x) {
				putVerticalLog(timeIdx, log);
			} else if (log.startPos.y == log.endPos.y) {
				putHorizontalLog(timeIdx, log);
			} else {
				throw new IllegalArgumentException("Invalid coordinates for a log.");
			}
		}

		/**
		 * For every timestamp, find all valid positions in which a log is.
		 */
		@SuppressWarnings("unchecked")
		private void createLake() {
			/* TIMESTAMP 0 */
			lake = new HashSet[time + 1];
			for (int i = 1; i <= numLogs; ++i) {
				putLog(0, logs[i]);
			}

			/* The other timestamps. To complete them, we simulate
			 * the movements of the logs. */
			for (int timeIdx = 0; timeIdx < time; timeIdx++) {
				for (int logIdx = 1; logIdx <= numLogs; logIdx++) {
					char direction = directions[logIdx].charAt(timeIdx);
					logs[logIdx].move(direction);
					putLog(timeIdx + 1, logs[logIdx]);
				}
			}
		}

		private void getResult() {
			Map<Pos, History> bktLvl = new HashMap<>();
			bktLvl.put(startPos, new History());

			createLake();
			backtrack(0, bktLvl);
		}
	}

	public static void main(String[] args) {
		new Task().solve();
	}
}
