// Marco Cristo, 2015, 2019
// Based on the book of Mark Allen Weiss on Data Structures
// TODO: has_edge, reverse

import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

// Used to signal violations of preconditions for
// various shortest path algorithms.
class GraphException extends RuntimeException
{
    public GraphException( String name )
    {
        super( name );
    }
} 

// Represents an edge in the graph.
class Edge implements Comparable<Edge>
{
    public Vertex     dest;   // Second vertex in Edge
	public Object     info;	  // Info related to edge			
    
    public Edge( Vertex d, Object i )
    {
        dest = d;
        info = i;
    }

	@Override
	public int compareTo(Edge e) {
		return dest.name.compareTo(e.dest.name);
	}
}

// Represents a vertex in the graph.
class Vertex implements Comparable<Vertex> 
{
    public String        name;   // Vertex name
    public TreeSet<Edge> adj;    // Adjacent vertices (successors)
    public TreeSet<Edge> padj;   // Adjacent vertices (predecessors)
	public Object        info;   // Info related to vertex			

    public Vertex( String nm, Object i )
    { 
		name = nm; 
	    adj = new TreeSet<Edge>( ); 
	    padj = new TreeSet<Edge>( ); 
		info = i; 
	}

    // reverse a vertex -- make the successors the predecessors and vice versa
    public void reverse()
    {
        TreeSet<Edge> temp;
        temp = adj;
        adj = padj;
        padj = temp;
    }

    public String toString()
    {
        return name;
    }

	@Override
	public int compareTo(Vertex v) {
		return name.compareTo(v.name);
	}
}

// Implements a directed graph --
// That is, Graph support directed edges and only one edge among two nodes
public class Graph implements Cloneable
{
    // maps string names to vertices
    private Map vertexMap; 

    public Graph() {
        init();
    }
	
    public void init() {
        this.vertexMap = new LinkedHashMap( );
    }
	
    /**
     * Add a new edge to the graph. Edges are given as vertices
     */
    public void addEdge( Vertex v, Vertex w, Object info )
    {
		if (v != null && w != null) {
			v.adj.add( new Edge( w, info ) );
            w.padj.add( new Edge( v, info ) );
        }
    }

    /**
     * Add a new edge to the graph. Edges are given as pairs of vertex names
     */
    public void addEdge( String sourceName, String destName, Object info )
    {
        Vertex v = getVertex( sourceName );
        Vertex w = getVertex( destName );
        addEdge( v, w, info );
    }

     /**
     * If vertexName is not present, add it to vertexMap.
     * In either case, return the Vertex.
     */
    public Vertex addVertex( String vertexName, Object info )
    {
        Vertex v = (Vertex) vertexMap.get( vertexName );
        if( v == null )
        {
            v = new Vertex( vertexName, info );
            vertexMap.put( vertexName, v );
        }
        return v;
    }

     /**
     * Delete vertex from graph.
     * If preservePaths == False, vertex edges are also removed
     * If preservePaths == True, bypassing edges are created
     */
    public void delVertex( String vertexName, boolean preservePaths )
    {
        // remove vertex
        Vertex v = (Vertex)vertexMap.remove( vertexName );
        if( v != null )
        {
            if (preservePaths) {
                // v's incoming edges are connected to successors
                // v's outgoing edges are connected to predecessors
                LinkedList<Vertex> succs = getSuccessors(v);
                for( Iterator itr = v.padj.iterator( ); itr.hasNext( ); )
                {
                    Edge e = (Edge) itr.next( );
                    Vertex vin = (Vertex)e.dest;
                    for (Vertex vout: succs)
                        // info of incoming edge is copied to new edge
                        addEdge( vin, vout, e.info );
                }
            }
            // remove edges
            LinkedList<Vertex> preds = getPredecessors(v);
            for (Vertex p: preds) 
                for( Iterator itr = p.adj.iterator( ); itr.hasNext( ); )
                {
                    Edge e = (Edge) itr.next( );
                    if (e.dest == v)
                        itr.remove();
                }
            LinkedList<Vertex> succs = getSuccessors(v);
            for (Vertex s: succs) 
                for( Iterator itr = s.padj.iterator( ); itr.hasNext( ); )
                {
                    Edge e = (Edge) itr.next( );
                    if (e.dest == v)
                        itr.remove();
                }
        }
    }

     /**
     * Return the Vertex given by its name.
     */
    public Vertex getVertex( String vertexName )
    {
        Vertex v = (Vertex) vertexMap.get( vertexName );
        if( v == null )
        {
            v = new Vertex( vertexName, null );
            vertexMap.put( vertexName, v );
        }
        return v;
    }

    /**
     * Check if Graph already has the Vertex.
     */
    public boolean hasVertex( Vertex v )
    {
        v = (Vertex) vertexMap.get( v.name );
        return v != null;
    }

    /**
     * Check if Graph already has the Edge.
     */
    public boolean hasEdge( Vertex v, Vertex w )
    {
        if (hasVertex(v) && hasVertex(w)) 
            return v.adj.contains(new Edge(w, null)); // compare only minds about w.dest
        else
            return false;
    }

     /**
     * Return an iterator over all vertices.
     */
    public Iterable<Vertex> getVertices() {
    	return vertexMap.values();
    }

     /**
     * Return an iterator over the neighbors of v.
     */
    public Iterable<Edge> getEdges(String vname) {
    	if (! vertexMap.containsKey(vname))
    		return Collections.EMPTY_LIST;
    	return ((Vertex)vertexMap.get(vname)).adj;
    }

     /**
     * Return a list of edges over the neighbors of v.
     */
    public LinkedList<Vertex> getOutNeighbors(Vertex v) {
        LinkedList<Vertex> edgeList = new LinkedList<Vertex>();
        for( Iterator itr = v.adj.iterator( ); itr.hasNext( ); )
        {
            Edge e = (Edge) itr.next( );
            edgeList.add((Vertex)e.dest);
        }
        return edgeList;
    }

     /**
     * Return list of edges over the neighbors of v (successors of v).
     * The successors of v are the nodes which v points to
     */
    public LinkedList<Vertex> getSuccessors(Vertex v) {
        return getOutNeighbors(v);
    }

     /**
     * Return list of edges over the neighbors of v (predecessors of v).
     * The predecessors of v are the nodes which v is pointed by
     */
    public LinkedList<Vertex> getPredecessors(Vertex v) {
        LinkedList<Vertex> edgeList = new LinkedList<Vertex>();
        for( Iterator itr = v.padj.iterator( ); itr.hasNext( ); )
        {
            Edge e = (Edge) itr.next( );
            edgeList.add((Vertex)e.dest);
        }
        return edgeList;
    }

     /**
     * Make a clone of a graph
     */
    public Object clone() throws CloneNotSupportedException 
    {
        Graph g = (Graph)super.clone();
        g.init();
		
        Vertex v;
        for( Iterator iv = vertexMap.values( ).iterator( ); iv.hasNext( ); ) {
            v = (Vertex)iv.next( );
            Vertex nv = g.addVertex(v.name, v.info);
			for( Iterator itr = v.adj.iterator( ); itr.hasNext( ); )
            {
                Edge e = (Edge) itr.next( );
                Vertex nw = g.addVertex(e.dest.name, e.dest.info);
                g.addEdge( nv, nw, e.info );
            }
		}

        return g;
    }
    
    /**
     * Reverse a directed graph, i.e, reverse all nodes of graph
     * To create a reverse clone of a graph, use:
     *    newg = g.clone()
     *    newg.reverse()
     */
    public void reverse ()
    {
        for (Vertex v: getVertices())
            v.reverse(); 
    }
    
    /**
     * display graph -- debug purpose only
     */
    public void show( )
    {
		Vertex v;
        for( Iterator iv = vertexMap.values( ).iterator( ); iv.hasNext( ); ) {
            v = (Vertex)iv.next( );
			System.out.print(v.name + ": ");
			for( Iterator itr = v.adj.iterator( ); itr.hasNext( ); )
            {
                Edge e = (Edge) itr.next( );
                Vertex w = e.dest;
				System.out.print(w.name + " ");
            }
			System.out.print("\n");
		}
    }

    /*  Generate all simple paths in the graph G from source to target.
        A simple path is a path with no repeated nodes.
        Ported from Python NetworkX, Marco 2019

        Parameters      source : Starting node for path
                        target : Ending node for path
        Returns         paths: list of all possible paths

        Simple Test Case:
            Complete graph for nodes 0,1,2,3 should lead to the 
            following 5 paths from 0 to 3: [[0, 1, 2, 3], [0, 1, 3],
            [0, 2, 1, 3], [0, 2, 3], [0, 3]]

        Notes
        This algorithm uses a modified depth-first search to generate the
        paths [1].  A single path can be found in O(V+E) time but the
        number of simple paths in a graph can be very large, e.g. O(n!) in
        the complete graph of order n.

        References
        [1] R. Sedgewick, "Algorithms in C, Part 5: Graph Algorithms",
        Addison Wesley Professional, 3rd ed., 2001.
    */
    public LinkedList<ArrayList<Vertex>> allSimplePaths(Vertex source, Vertex target)
    {
        LinkedList<ArrayList<Vertex>> paths = new LinkedList<ArrayList<Vertex>>();
        Stack<Vertex> visited = new Stack<Vertex>(); 
        Stack<LinkedList<Vertex>> stack = new Stack<LinkedList<Vertex>>(); 

        if (! this.hasVertex(source))
            throw new GraphException("Source node not in graph: " + source.name);
        if (! this.hasVertex(target))
            throw new GraphException("Target node not in graph: " + target.name);
        if (source == target)
            return paths;

        int cutoff = vertexMap.size() - 1;
        visited.push(source);
        stack.push(getOutNeighbors(source)); 

        LinkedList<Vertex> children;
        Vertex child;
        while (! stack.empty()) {
            children = stack.peek();
            if (children.size() == 0)
                child = null;
            else 
                child = children.pollLast();
            if (child == null) {
                stack.pop();
                visited.pop();
            } else if (visited.size() < cutoff) {
                if (visited.search(child) > 0)
                    continue;
                if (child == target) {
                    ArrayList<Vertex> temp = new ArrayList<Vertex>(visited);
                    temp.add(child);
                    paths.add(temp); 
                }
                visited.push(child);
                if (visited.search(target) == -1) // target not in visited
                    stack.push(getOutNeighbors(child));
                else
                    visited.pop();
            } else {
                if (visited.search(target) == -1) { // target not in visited
                    int count = child == target? 1: 0;
                    for (Vertex v: children)
                        if (v == target)
                            count ++;
                    for (int i = 0; i < count; i ++) {
                        ArrayList<Vertex> temp = new ArrayList<Vertex>(visited);
                        temp.add(target);
                        paths.add(temp); 
                    }
                }                 
                stack.pop();
                visited.pop();
            }
        }
    
        return paths;
    }

    /*
        Checks if there is a path from source to target
        in a bidirectional graph
        Ported from Python NetworkX, Marco 2019
    */
    public boolean hasPath(Vertex source, Vertex target)
    {
        if (target == source)
            return true;   

        // initialize fringes, start with forward
        ArrayList<Vertex> forward_fringe = new ArrayList<Vertex>(); 
        forward_fringe.add(source);
        ArrayList<Vertex> reverse_fringe = new ArrayList<Vertex>(); 
        reverse_fringe.add(target);

        TreeSet<Vertex> pred = new TreeSet<Vertex>();    
        pred.add(source);
        TreeSet<Vertex> succ = new TreeSet<Vertex>();   
        succ.add(target);

        ArrayList<Vertex> this_level;
        while (forward_fringe.size() > 0 && reverse_fringe.size() > 0) {
            if (forward_fringe.size() <= reverse_fringe.size()) {
                this_level = forward_fringe;
                forward_fringe = new ArrayList<Vertex>();
                for (Vertex v: this_level) {
                    for (Vertex w: getSuccessors(v)) {
                        if (! pred.contains(w)) {
                            forward_fringe.add(w);
                            pred.add(w);
                        }
                        if (succ.contains(w))  // path found
                            return true;
                    }
                }
            } else {
                this_level = reverse_fringe;
                reverse_fringe = new ArrayList<Vertex>();
                for (Vertex v: this_level) {
                    for (Vertex w: getPredecessors(v)) {
                        if (! succ.contains(w)) {
                            reverse_fringe.add(w);
                            succ.add(w);
                        }
                        if (pred.contains(w))  // path found
                            return true;
                    }
                }
            }
        }
        return false;
    }

	/**
        simple tests using graph corresponding to program:
            void main()
            {
        01:    int Y;
        02:    Y = init();
        03:    if (condX()) {
        04:        if (condZ()) 
        05:            return; 
        06:    } else W();
        07:    f(Y); 
            }
     */
    public static void main( String [ ] args ) throws CloneNotSupportedException
    {
        Graph g = new Graph( );

		Vertex v0 = g.addVertex("0. start", null);
		Vertex v1 = g.addVertex("1. int y", null);
		Vertex v2 = g.addVertex("2. y = init()", null);
		Vertex v3 = g.addVertex("3. if (condX())", null);
		Vertex v4 = g.addVertex("4. if (condZ())", null);
		Vertex v5 = g.addVertex("5. return", null);
		Vertex v6 = g.addVertex("6. w()", null);
		Vertex v7 = g.addVertex("7. f(y)", null);
		Vertex v8 = g.addVertex("8. stop", null);
		
        g.addEdge( v0, v1, null ); 
        g.addEdge( v1, v2, null );
        g.addEdge( v2, v3, null );
        g.addEdge( v3, v4, null ); // if T
        g.addEdge( v3, v6, null ); // if F
        g.addEdge( v4, v5, null ); // if T
        g.addEdge( v4, v7, null ); // if F
        g.addEdge( v5, v8, null );
        g.addEdge( v6, v7, null );
        g.addEdge( v7, v8, null );

        System.out.println("Original graph g:");
		g.show();

        // check edge 0-1
        System.out.println("\nEdge v0 -> v1?");
        if (g.hasEdge(v0, v1))
            System.out.println("yes");
        else
            System.out.println("no");
        System.out.println("\nEdge v1 -> v0?");
        if (g.hasEdge(v1, v0))
            System.out.println("yes");
        else
            System.out.println("no");

        // all paths
        System.out.println("\nAll paths between v2 and v7 in g:");
        LinkedList<ArrayList<Vertex>> paths = g.allSimplePaths(v2, v7);
        System.out.println(paths);

        // check path 2-7
        System.out.println("\nPath v2 -> v7?");
        if (g.hasPath(v2, v7))
            System.out.println("yes");
        else
            System.out.println("no");

        System.out.println("\nPath v7 -> v2?");
        if (g.hasPath(v7, v2))
            System.out.println("yes");
        else
            System.out.println("no");

        // reverse clone
        System.out.println("\nReversed graph:");
        Graph newg = (Graph)g.clone();
        newg.reverse();
        newg.show();
        System.out.println("\nOriginal graph:");
        g.show();

        // get new references for the copied vertices
        v0 = newg.getVertex("0. start");
		v1 = newg.getVertex("1. int y");
		v2 = newg.getVertex("2. y = init()");
		v3 = newg.getVertex("3. if (condX())");
		v4 = newg.getVertex("4. if (condZ())");
		v5 = newg.getVertex("5. return");
		v6 = newg.getVertex("6. w()");
		v7 = newg.getVertex("7. f(y)");
		v8 = newg.getVertex("8. stop");

        // all paths
        System.out.println("\nAll paths between v7 and v2 in reversed g:");
        paths = newg.allSimplePaths(v7, v2);
        System.out.println(paths);

        // check edge 0-1
        System.out.println("\nEdge v0 -> v1 in reversed graph?");
        if (newg.hasEdge(v0, v1))
            System.out.println("yes");
        else
            System.out.println("no");
        System.out.println("\nEdge v1 -> v0 in reversed graph?");
        if (newg.hasEdge(v1, v0))
            System.out.println("yes");
        else
            System.out.println("no");

        // check path 2-7
        System.out.println("\nPath v2 -> v7 in reversed graph?");
        if (newg.hasPath(v2, v7))
            System.out.println("yes");
        else
            System.out.println("no");

        System.out.println("\nPath v7 -> v2 in reversed graph?");
        if (newg.hasPath(v7, v2))
            System.out.println("yes");
        else
            System.out.println("no");

        // deletion test 
        System.out.println("\nOriginal graph:");

        g = new Graph( );
        /*
            A: B, C
            B: D
            C: D
            D: E, F, G
            E: H
            F: H
            G: H
        */

		v0 = g.addVertex("A", null);
		v1 = g.addVertex("B", null);
		v2 = g.addVertex("C", null);
		v3 = g.addVertex("D", null);
		v4 = g.addVertex("E", null);
		v5 = g.addVertex("F", null);
		v6 = g.addVertex("G", null);
		v7 = g.addVertex("H", null);
		
        g.addEdge( v0, v1, null ); 
        g.addEdge( v0, v2, null );
        g.addEdge( v1, v3, null );
        g.addEdge( v2, v3, null ); 
        g.addEdge( v3, v4, null ); 
        g.addEdge( v3, v5, null ); 
        g.addEdge( v3, v6, null ); 
        g.addEdge( v4, v7, null );
        g.addEdge( v5, v7, null );
        g.addEdge( v6, v7, null );

        g.show();

        System.out.println("\nafter deletion:");
        g.delVertex("D", true);
        g.show();

    }
}
