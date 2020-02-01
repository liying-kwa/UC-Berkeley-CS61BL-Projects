package bearmaps.utils.graph;

import bearmaps.utils.pq.DoubleMapPQ;
import bearmaps.utils.pq.MinHeapPQ;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {

    private SolverOutcome outcome = SolverOutcome.UNSOLVABLE;
    private LinkedList<Vertex> solution = new LinkedList<>();
    private double solutionWeight;
    private int numStatesExplored = 0;
    private double explorationTime;


    public AStarSolver(AStarGraph<Vertex> G, Vertex start, Vertex end, double timeout) {

        MinHeapPQ<Vertex> fringe = new MinHeapPQ<>();
        HashMap<Vertex, Double> distanceTo = new HashMap<>();
        HashMap<Vertex, Vertex> parents = new HashMap<>();

        // start timing
        Stopwatch sw = new Stopwatch();
        // insert start vertex into pq, update distanceTo and parents
        fringe.insert(start, 0);
        distanceTo.put(start, 0.0);
        parents.put(start, null);

        // start the search
        while (fringe.size() > 0) {
            numStatesExplored++;
            Vertex cur = fringe.peek();
            if (cur.equals(end)) {
                // a solution was found
                // create the solution from parents
                Vertex solCur = end;
                while (solCur != null) {
                    solution.addFirst(solCur);
                    solCur = parents.get(solCur);
                }

                solutionWeight = distanceTo.get(end);
                outcome = SolverOutcome.SOLVED;
                explorationTime = sw.elapsedTime();
                break;
            } else {
                cur = fringe.poll();
                if (sw.elapsedTime() >= timeout) {
                    // a solution was not found in time
                    solutionWeight = 0;
                    outcome = SolverOutcome.TIMEOUT;
                    explorationTime = sw.elapsedTime();
                    break;
                } else {
                    // pseudo-code found in spec
                    for (WeightedEdge<Vertex> e : G.neighbors(cur)) {
                        Vertex q = e.to();
                        double curDist = distanceTo.get(cur) + e.weight();
                        if (!distanceTo.containsKey(q) || Double.compare(curDist, distanceTo.get(q)) < 0) {

                            distanceTo.put(q, curDist);
                            parents.put(q, cur);

                            if (fringe.contains(q)) {
                                fringe.changePriority(q, curDist + G.estimatedDistanceToGoal(q, end));
                            } else {
                                fringe.insert(q, curDist + G.estimatedDistanceToGoal(q, end));
                            }
                        }
                    }
                }
            }
        }
        if (outcome == SolverOutcome.UNSOLVABLE) {
            // unsolvable
            solutionWeight = 0;
            explorationTime = sw.elapsedTime();
        }
    }

    @Override
    public SolverOutcome outcome() {
        return outcome;
    }

    @Override
    public List<Vertex> solution() {
        return solution;
    }

    @Override
    public double solutionWeight() {
        return solutionWeight;
    }

    @Override
    public int numStatesExplored() {
        return numStatesExplored;
    }

    @Override
    public double explorationTime() {
        return explorationTime;
    }
}
