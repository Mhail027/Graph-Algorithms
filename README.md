README
====

***Title: Graph Algorithms***

**Author: Necula Mihail**

**Group: 323CAa**

**University year: 2024–2025**

---

Chapter 1
====

<h3>&nbsp;&nbsp;&nbsp;Solution</h3>

<pre style="font-family: inherit; font-size: inherit; line-height: inherit; color: inherit; background: transparent; border: none">
When we read the BFS vector, we find the length of the shortest path from root
(node 1) to any node. We group the nodes by this distance because it will help
us to build the graph later. My approach consist of 2 steps:
    
    1. Verify if can be built a graph with the given BFS vector.
       Need to validate the next conditions:
            -> we must have just one node on first layer in this one to be
                node 1
            -> in restriction it says that the max distance between root
                and a node is N, where N is the number of nodes; though,
                no node can practically be on that layer (the lvl N); in the
                slowest case, we discover a node per layer

                layer 0 => 1 visited node
                layer 1 => 2 visited nodes
                ...,
                layer N - 1 => n visited node

            -> if we have a empty level, all the levels after it must be
                the same; for example,  we can not travel from layer 1
                to 3, without to have a node in layer 2 through which to go

    2. Build the graph we long for.
        We will create a tree such as to have the read BFS vector if we
        start from node 1. Why did we choose a tree? Because is a minimal
        connected graph. It has the minimum number of edges to be connected.
        Also, it avoids the cycles which would have been awful for us to manage
        them.

        We go from level to level in ascending order from layer 1. Every node
        from the current layer will be tied of first node from the previous one.
        (Does not matter of which vertex from the previous level, we tied them.)
</pre>

<h3>&nbsp;&nbsp;&nbsp;Complexity</h3>

<pre style="font-family: inherit; font-size: inherit; line-height: inherit; color: inherit; background: transparent; border: none">
Space complexity is Θ(n), where n is the number of nodes. This happens because
we use an array of lists in which group the nodes by their shortest distance to root.
We have n + 1 entries in array and total number of elements from all list is n.

Time complexity is O(n), where n is the number of nodes. Firstly, we check if can
create a graph. The first two rules are be verified in Θ(1), while the last one takes
O(n), being compelled to go from all possible read BFS levels. We stop earlier, just if
something bad happens. Second step of the algorithm, if we get there, takes Θ(n),
building n - 1 edges.
</pre>

---

Chapter 2
====

<h3>&nbsp;&nbsp;&nbsp;Solution</h3>

<pre style="font-family: inherit; font-size: inherit; line-height: inherit; color: inherit; background: transparent; border: none">
We have an indirect graph, where every position from matrix with its value
represent a node. So, a vertex is of type ((i, j), value). Every nodes has
maximum 4 edges, which connect it with his neighbours (left, right, up, down).
We must answer at the question: Which is the maximum number of nodes which
we can visit them if we start from a node ((i, j), value) and can not go at a vertex
which has the value further than with k than the value of other visited node?
So, to can explore a node: abs(value of new node - value of visited vertex) <= k,
for all visited nodes.

The initial approach would be to start a DFS from every node and every valid interval.
If we start from a node ((i, j), a), makes sense that the intervals to be of form [b, b + k], 
and a to be in that interval. So, for every node we will have k + 1 intervals for which we
must do a DFS. If we start a DFS with the interval [b, b + k], we can visit just nodes whose
value belong to that interval. The problem is that takes a lot of time, O(n ^ 2 * m ^ 2 * k)
because are n ^ m nodes, every node has k + 1 intervals and the DFS takes place in
O(n * m).
*In my approach i used DFS because was easier to implement it. Is not wrong to use BFS.

If we start the DFSs with nodes which has the smallest value and continue in ascending
order, we can observe something. Let's be a = the smallest value and b = the second 
smallest value, b - a <= k. When we start the traversal from the node ((i1, j1), a),
makes sense that the interval [a, a + k] will give the biggest area from that vertex.
After, we go at the node ((i2, j2), b), we have 2 cases in which we are interested:
[b - k, b], [b, b + k]. But, when we use the interval [b - k, b], we can not obtain
a better result in same time than [b, b + k] and  [a, a + k] from vertex ((i1, j1), a).

In a DFS, if we use numbers smaller than the root's value, that means that we will not
obtain an area bigger than all than the previous ones. Let's be ((i3, j3), c) the smallest
visited in DFS. Than the area obtained will be smaller or equal with the one obtained
if we would have started from ((i3, j3), c) with the interval [c, c + k]. So, makes sense for
every node to try the DFS with just an interval. If the start vertex has the number x, we will
try just the interval [x, x + k].

We went trough nodes in ascending order, just to be easier to visualize what happened.
The order does not influence the final result. So, we need just to do n * m DFSs, one from
every position of the matrix. But, in a DFS if we reach a vertex y with the same value as the
root x, that means the the DFS from x and from y will visit the same nodes and will have the
same area. So, when we get to y, we do not need to start a DFS from it and we can jump over
it.
</pre>

<h3>&nbsp;&nbsp;&nbsp;Complexity</h3>

<pre style="font-family: inherit; font-size: inherit; line-height: inherit; color: inherit; background: transparent; border: none">
Space complexity is Θ(n * m), where n is the number of matrix's lines and m is the number of
matrix's columns. We have a matrix n x m to keep the read values. Another matrix n x m to
mark all the positions from which a DFS took place. Also a DFS takes just Θ(n * m) space
because it uses a n x m matrix to mark the visited nodes and the recursion has around
max(n, m) levels in the same time on stack.

Time complexity is O(n ^ 2 * m ^ 2), where n is the number of matrix's lines and m is the
number of matrix's columns. In the worst case, we need to start a DFS from almost every
node. So, we will have approximately n * m DFSs. Also, if the luck is not on our side, the
majority of the traversals will take Θ(n * m) time.
</pre>

---

Chapter 3
====

<h3>&nbsp;&nbsp;&nbsp;Solution</h3>

<pre style="font-family: inherit; font-size: inherit; line-height: inherit; color: inherit; background: transparent; border: none">
The solution of the third problem has 2 steps:

    1. Create the image of the lake for every timestamp

        We will have T images of the lake, where T is the maximum timestamp. For every
        picture of the lake we will have a list in which we save all the positions in which
        a log is. A position is characterized trough 3 fields (x-coordinate, y-coordinate and
        index of the log). We need the last field because more logs can have the same
        coordinates.

        To create this list for a timestamp we simulate the movements of the logs. For every
        log, we go from an end to the other and add all the positions through we go in list.

    2. Find all the possible positions of Robin Hood for every timestamp

        We use a backtracking approach combined with BFS, to be time efficient. For
        every position we store a history which contains the actions and the energy
        used to get there. To save these datas, we use a hashmap where the position
        it's the key and its history is the value.

        When we enter in a new level in backtracking (we jump at the next timestamp),
        we go through all possible positions to check if we found a more optimal way
        to maid Marian. After, we determinate all the possible positions for the next
        timestamp. 

        When we are timestamp t, we compute all Robin Hood possible positions from
        the timestamp t + 1 . Let's take a position p. We make all the possible actions
        from there:
            -> try to go N, S, E, V - the position is valid if we remained on same log
            -> stay on same position - always valid
            -> jump on every log with which we intersect
        Every action is combined with the log movement from time t to get to the
        position from t + 1.

        In a timestamp, Robin Hood is possible to be got to the same position using 2
        different path of actions. In this case, we keep the path which uses the least
        energy.
</pre>

<h3>&nbsp;&nbsp;&nbsp;Complexity</h3>

<pre style="font-family: inherit; font-size: inherit; line-height: inherit; color: inherit; background: transparent; border: none">
    Space complexity is O(T * N * L), where T = max timestamp, N = number of logs,
    L = max length of a log. We have T + 1 images of the lake, every of them has saved
    maximum N * (L + 1) positions. The backtracking part needs T + 1 levels on stack
    because Java does not use tail-recursion. During this process, we need to keep in
    memory maximum 2 hashmaps. Every hashmap will have maximum N * (L + 1)
    positions. Every position will have saved the history of actions to know how we got
    there. No history will have more than T actions. So, the backtracking uses O(T * N * L)
    space.

    Time complexity is O(T ^ 2 * N ^ 2 * L), where T = max timestamp, N = number of logs,
    L = max length of a log. To create all the images of the lakes takes O(T * N * L). At
    backtracking we spend a lot of time. We have T + 1 layers.  Every level will have at most
    N * (L - 1) nodes. When we explore the possible positions from where we can go at the next
    level, a current position can branch in maximum (N + 3) spots. Every spot has its own list
    of actions which can be built in O(T). So, a lvl from backtracking takes O(N * L * N * T) and
    the whole backtracking costs O(T * N * L * N * T) time , which is equal with O(T ^ 2 * N ^ 2 * L).
</pre>
