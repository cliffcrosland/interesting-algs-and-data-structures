package com.cliffcrosland.kruskal;

import com.cliffcrosland.graph.Graph;
import com.cliffcrosland.graph.GraphEdge;
import com.cliffcrosland.graph.GraphNode;

import java.util.*;

public class KruskalMinSpanningTree {
    // Runtime: O(E log V) + O(E) + O(E log* V) = O(E log V). where log* is the iterated log function.
    //
    // What is the iterated log function? Put a number n into your calculator and keep pressing the log button until
    // the value drops to 1 or below. The number of times you pressed log is the log* function of n.
    //
    // log* n is a ridiculously slow growing function. For example log*(2^65,536) = 5. Holy crap. The number of atoms in
    // the known universe is estimated to be 10^80, and 2^65,536 is around 10^19,000. Basically, this means that
    // whatever practical input you ever feed into a log* n function, you'll never get anything larger than 5. Hence,
    // O(n log* n) is, for all practical purposes, equivalent to saying O(n * 5) = O(n).
    //
    // Proof-ish of Kruskal algorithm runtime using lazy-union find data structure:
    //
    // - First, we sort all of the edges in O(E log E) time, and since E is O(V^2), we can re-write as O(E log V^2) =
    //   O(E 2*log V) = O(E log V)
    // - Next, we loop through all of the edges one at a time, O(E).
    // - We add an edge if it does not create a cycle. To determine whether an edge creates a cycle, we use the lazy
    //   union-find set data structure, starting with a forest of V lazy union-find sets, one for each node. If you
    //   look at the endpoints of an edge, and they belong to the same set, don't add the edge. If the endpoints belong
    //   to different sets, merge the two sets together into one new set.
    //
    // Here's how a lazy union-find data structure works:
    // - Each node has a rank value and a parent pointer.
    // - There is a "root" node in each set that is considered to be the set's leader. So, when you want to lookup
    //   which set a node belongs to, you look up its parent, which looks up its parent, and so on until you get the
    //   root. To see if two sets are equal, you simply see if they have the same root node.
    // - The rank of a node is the length of the longest possible path from a leaf node to that node walking along
    //   parent pointers.
    // - There are two operations: Union and Find.
    // - You call Find on a node to look up its root, walking along parent pointers until you get there.
    // - You call Union on two nodes to merge the sets that the nodes belong to together. It is implemented by
    //   calling Find on one node to find the root of one set, calling Find on the other node to find the root of
    //   the other set, and then taking the root of smaller rank and updating its parent pointer to point to the
    //   root of larger rank.
    // - Why is it called the lazy union-find data structure? An eager union-find data structure might loop through all
    //   of the nodes in the smaller set and update all of their parent pointers to point to the new root. The lazy one
    //   only updates the parent of the smaller root. Later, when you call find on some node, you'll need to do
    //   up to log(n) work to find the root by walking along parent pointers where n is the number of nodes in the
    //   set. Why log(n) work? There are (n / 2^r) nodes of rank r in a set of size n. The root has the maximum rank,
    //   and it is the only node that has that rank. The maximum rank occurs when (n / 2^r) = 1, which occurs when
    //   r = log(n). Note that rank equals the length of the longest possible path from a leaf node to that node, so
    //   the longest Find operation will take O(log n) work.
    //
    // How much work does the series union-find lookup and merge operations contribute to the overall runtime?
    // - First, we try to see what the runtime will be given a step in Kruskal's algorithm where we are deciding whether
    //   or not to add an edge.
    //   - We just said that each find operation takes at most log n time where n is the size of the set. We'll be doing
    //     two finds and potentially one union, which is 2 * O(log V) + O(1) = O(log V).
    //   - Hence, for every edge, we'll be doing at most O(log V) work, so we can say that we'll be doing O(E log V)
    //     work as we loop through edges and decide whether to add each edge to our final tree.
    //
    // BUT THERE IS AN AWESOME OPTIMIZATION WE CAN ADD! This optimization will reduce our work from O(E log V) to
    // O(E log* V) which for all practical input sizes, nay even for input sizes unimaginably bigger than the number of
    // atoms in the universe, is O(V).
    //
    // Here is the optimization:
    // - We're lazy and only update one parent pointer during a Union operation.
    // - However, let's buckle down and fix things up during each Find operation. When we call Find on a node, let's
    //   walk up to find the root, and then remember that root for the next time we call Find. In fact, let's take
    //   every node on the path from the original node to the root and update all of their parent pointers to point
    //   to the root.
    // - That way, if we call Find again on a node, we can jump up to that original root in O(1) time.
    //
    // This improves the O(E log V) runtime all the way to O(E log* V)! The reason for this is that every Find operation
    // improves future Find operations significantly, allowing near constant time lookup of the root during every Find
    // operation.
    //
    // Here's a proof-ish of this new runtime. It's kind of ingenious, and we owe it to some great computer scientists
    // from the 70's, Hopcroft and Ullman:
    //
    // The idea is that we want to show that the distance between the rank of a node and its parent gets really big
    // quickly, helping us avoid unnecessary work during Find operations.
    //
    // There's a strange concept they introduce called the rank block. Here are the first few rank blocks:
    // {0}, {1}, {2 3 4}, {5 6 ... 16}, {17 18 ... 65536}, {65537 65538 ... 2^65536}, ...
    // The first three are "hard-coded", but after the first three, each rank block looks like {k+1 k+2 ... 2^k} where
    // k is the final value in the previous rank block.
    //
    // Alrighty, now that we have the concept of a rank block, we call a node "good" if it is a root, if it is a
    // direct descendant of a root, or if its parent's rank is in a different rank block than its own rank. Basically,
    // good nodes are those where there is a huge jump from its rank to its parent's rank, which means we can get close
    // to the root faster. If a node is not "good" then we call it "bad".
    //
    // How much work is contributed by "good" nodes to Kruskal's algorithm, and how much work is contributed by "bad"
    // nodes? We can analyze the work contributed by "good" nodes by looking at how much good nodes contribute during
    // each iteration of Kruskal's where we look at an edge and decide whether or not to add it to our tree, and we can
    // analyze the work contributed by "bad" nodes by taking a look at the algorithm from the perspective of an
    // individual bad node throughout the global execution of the whole algorithm.
    //
    // The work contributed by good nodes is O(E log* V). Here's how:
    // - Consider a single iteration of Kruskal's where we look at a single edge and decide whether or not to add it
    //   to our set. Say we call Find on one of the endpoints of the edge, and are now looking for the root. Let's
    //   just consider the work contributed by good nodes along the path from the original node to the root. Each time
    //   we encounter a good node, we jump a huge distance to the next rank block. How many such jumps can occur? It's
    //   the number of rank blocks that there are for V elements. How many is that? log* V .
    // - Hence, good nodes contribute O(log* V) work for each iteration of Kruskal's, so all together, good nodes
    //   contribute O(E log* V) to the runtime.
    //
    // The work contributed by bad nodes is O(V log* V). Here's how:
    // - A bad node will be bad as long as its parent is still in the same rank block. If you call Find enough times on
    //   a bad node, it will eventually become good because its parent will be updated far enough to be in a new rank
    //   block. Hence, we need to find out how much work is done on each bad node before it becomes good, and we also
    //   need to find out how many bad nodes there are.
    // - How many bad nodes are there? Well, its the same as the number of bad nodes in each rank block, summed up over
    //   all of the rank blocks.
    // - How many bad nodes are there in rank block {k+1 k+2 ... 2^k}? Recall that the number of nodes of rank r in a
    //   union-find set is n / 2^r where n is the number of nodes in the union-find set. The maximum number of nodes
    //   there can be in a union-find set is V. Hence, the number of nodes in this rank block is:
    //   V/2^(k+1) + V/2^(k+2) + ... + V/2^(2^k), which is a telescoping series where each term is half of the one
    //   before. For example, 1/2 + 1/4 + 1/8 + ... + 1/2^i + ... <= 1.
    //   So the sum is V * (1/2^(k+1) + 1/2^(k+2) + ...) <= V * 1/2^k = V/2^k.
    // - Now that we know how many bad nodes there are in a rank block, how much work does each bad node contribute
    //   before it becomes a good node? During each visit to a bad node, it will be updated to have a new parent with
    //   strictly larger rank. Why does the rank strictly increase? The only time that visiting a node does not increase
    //   its parent's rank is when that node is either the root or a direct descendant of the root. Since this node is
    //   a bad node, it is not the root nor is it a direct descendant of the root. Hence, its parent will be updated
    //   every time it is visited to one with strictly larger rank. The number of times that a bad node's parent can be
    //   updated until the bad node becomes a good node is = 2^k - k+1 = O(2^k) times.
    // - It takes O(2^k) work to turn a bad node into a good node, and there are V/2^k bad nodes in each rank block.
    //   Hence, the amount of work contributed by bad nodes in a given rank block is 2^k * V/2^k = V. Sweet!
    // - How many rank blocks are there? There are log* V rank blocks. Each rank block contributes V work, so we have
    //   a total of O(V log* V) work contributed all-together by bad nodes.
    //
    // Thus, the total amount of work contributed by good nodes and bad nodes is O(E log* V) + O(V log* V) =
    // O(E log* V).
    //
    // In conclusion, here is the total runtime of Kruskal's algorithm using lazy union-find sets:
    // - It takes O(E log E) = O(E log V^2) = O(E * 2 log V) = O(E log V) time to sort the edges.
    // - It then takes O(E log* V) work to walk through edges, check for cycles, and add appropriate edges to our MST.
    // - O(E log V) + O(E log* V) = O(E log V). Amazingly, the sorting of edges is the bottleneck here.
    //
    // Future work - there is actually an even tighter bound than O(E log* V) on the edge processing routine. It's
    // O(E alpha V) where alpha is the inverse Ackerman function. But that is a tale for another day.

    public static Set<GraphEdge> findMinSpanningTree(Graph graph) {
        List<GraphEdge> sortedEdges = new ArrayList<GraphEdge>(graph.edges);
        sortedEdges.sort(new Comparator<GraphEdge>() {
            @Override
            public int compare(GraphEdge a, GraphEdge b) {
                if (a.value < b.value) return -1;
                if (a.value > b.value) return 1;
                return 0;
            }
        });
        Map<GraphNode, UnionFindTreeNode> unionFindMap = new HashMap<GraphNode, UnionFindTreeNode>();
        int label = 0;
        for (GraphNode node : graph.nodes) {
            unionFindMap.put(node, new UnionFindTreeNode(++label));
        }
        Set<GraphEdge> minSpanningTree = new HashSet<GraphEdge>();
        for (GraphEdge edge : sortedEdges) {
            UnionFindTreeNode fromTreeRoot = unionFindMap.get(edge.from).getRoot();
            UnionFindTreeNode toTreeRoot = unionFindMap.get(edge.to).getRoot();
            if (fromTreeRoot.label == toTreeRoot.label) continue;
            UnionFindTreeNode.merge(fromTreeRoot, toTreeRoot);
            minSpanningTree.add(edge);
        }
        return minSpanningTree;
    }

    private static class UnionFindTreeNode {
        public int label;
        public int rank; // upper bound for longest path from a leaf node to this node
        public UnionFindTreeNode parent;

        public UnionFindTreeNode(int label) {
            this.label = label;
            this.rank = 0;
            this.parent = null;
        }

        public UnionFindTreeNode getRoot() {
            if (parent == null) {
                return this;
            }
            // To improve the run-time of future calls, connect self directly to root so that future getRoot() calls
            // run faster.
            //
            // This optimization is kind of AMAZING. O(E log V) work done to find root nodes becomes O(E log* V).
            // You can read more about the function in the intro text, but for example, log*(2^65536) =
            // log*(a number unimaginably bigger than the estimated number of atoms in the universe) = 5.
            parent = parent.getRoot();
            return parent;
        }

        public static void merge(UnionFindTreeNode rootA, UnionFindTreeNode rootB) {
            assertIsRoot(rootA);
            assertIsRoot(rootB);
            UnionFindTreeNode smaller, larger;
            if (rootA.rank < rootB.rank) {
                smaller = rootA;
                larger = rootB;
            } else {
                smaller = rootB;
                larger = rootA;
            }
            smaller.parent = larger;
            if (larger.rank == smaller.rank) {
                larger.rank += 1;
            }
        }

        private static void assertIsRoot(UnionFindTreeNode root) {
            if (root.parent != null) {
                throw new IllegalArgumentException("root's parent is not null, so it is not a root");
            }
        }
    }

}
