package graph;
import java.util.Map;
import java.util.TreeMap;

/** 
* Classe que define a forma da estrutura basica de grafo
* @author vini
**/
public class Graph {

    protected TreeMap<Integer, Vertex> vertices;
    protected TreeMap<Integer, Edge> edges;
    protected int verticesCount;
    protected int edgesCount;

    /** Contrutor do grafo*/
    public Graph() {
        this.vertices = new TreeMap<Integer, Vertex>();
        this.edges = new TreeMap<Integer, Edge>();
        verticesCount = 1;
        edgesCount = 1;
    }

    /** Construtor para clonagen do grafo */
    public Graph(Graph graph) {
        this.vertices = new TreeMap<Integer, Vertex>();
        this.edges = new TreeMap<Integer, Edge>();
        verticesCount = graph.getVerticesCount();
        edgesCount = graph.getEdgesCount();

        this.copyGraph(graph); // Metodo que faz a copia profundo dos objetos
    }

    public Vertex getVertex(int id) {
        return this.vertices.get(id);
    }
    
    public int addVertex() {
        try {
            int id = verticesCount;
            Vertex temp = new Vertex(verticesCount);
            this.vertices.put(verticesCount, temp);
            verticesCount++;

            return id;
        }
        catch(Exception e) {
            System.out.print(e.toString());
        }

        return 0;
    }

    public void removeVertex(int id) {
        try {
            
            Vertex removedVertex = this.vertices.get(id);
            Vertex auxVertex;
            
            /* Remove arestas das listas de adjacencia dos vertices vizinhos */
            for(Edge edge: removedVertex.getAdjacencies()) {
                
                auxVertex = removedVertex.getNextVertex(edge);
                auxVertex.getAdjacencies().remove(edge);
                this.edges.remove(edge.getId());
            }

            /* Remove todas as arestas que estao conectadas ao vertice que esta sendo removido */
            removedVertex.getAdjacencies().clear();

            /* Remove o vertice do TreeMap */
            this.vertices.remove(id);

        }
        catch(Exception e) {
            System.out.print(e.toString());
        }
    }

    public Edge getEdge(int id) {
        return this.edges.get(id);
    }

    public int addEdge(int vertex1, int vertex2) throws NullPointerException{
        
        try {
            Vertex v1 = getVertex(vertex1);
            Vertex v2 = getVertex(vertex2);

            if(v1 == null || v2 == null) {
                throw new NullPointerException("Vertices não instanciados");// dispara a Exeção throws
            }

            Edge auxEdge = new Edge(edgesCount, v1, v2);
            this.edges.put(auxEdge.getId(), auxEdge);

            v1.getAdjacencies().add(auxEdge);
            v2.getAdjacencies().add(auxEdge);

            edgesCount++;

            return auxEdge.getId();

        }
        catch(Exception e) {
            System.out.print(e.toString());
        }

        return 0;
    }

    public void removeEdge(int id) {
        try {

            Edge removedEdge = this.edges.get(id);

            /* Remocao da aresta nas listas de adjacencia dos vertices queestao conectados a ela */
            removedEdge.getVertex1().getAdjacencies().remove(removedEdge);
            removedEdge.getVertex2().getAdjacencies().remove(removedEdge);

            /* Remove da lista de arestas do grafo */
            this.edges.remove(id);
        }
        catch(Exception e) {
            System.out.print(e.toString());
        }
    }

    /* Metodo para fazer uma copia profunda dos objetos */
    private void copyGraph(Graph graph) {

        int idVertex1, idVertex2, idEdge;
        Vertex vertex1;
        Vertex vertex2;
        Edge auxEdge;

        try {
            /* Copia o Map de vertices */
            for(Map.Entry<Integer, Vertex> it: graph.getVertices().entrySet()) {

                idVertex1 = it.getValue().getId();
                this.vertices.put(idVertex1, new Vertex(idVertex1));
            }

            /* Copia o Map de arestas e religa essas arestas aos novos vertices */
            for(Map.Entry<Integer, Edge> it: graph.getEdges().entrySet()) {

                /* Pega o id da aresta original para criar a nova aresta com o mesmo id */
                idEdge = it.getValue().getId();

                /* Pega o id dos vertices originais */
                idVertex1 = it.getValue().getVertex1().getId();
                idVertex2 = it.getValue().getVertex2().getId();

                /* Usa o id dos vertices originais para pegar uma referencia para os novos vertices no TreeMap de vertices */
                vertex1 = this.vertices.get(idVertex1);
                vertex2 = this.vertices.get(idVertex2);

                auxEdge = new Edge(idEdge, vertex1, vertex2);

                /* Coloca a nova aresta na lista de adjacencia dos vertices que estao conectados a ela */
                vertex1.getAdjacencies().add(auxEdge);
                vertex2.getAdjacencies().add(auxEdge);

                /* Coloca a nova aresta no TreeMap de arestas */
                this.edges.put(idEdge, auxEdge);
            }
        }
        catch(Exception e) {
            System.out.print(e.toString());
        }
    }


    /***************** GETER AND SETTER ****************/

    public TreeMap<Integer, Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(TreeMap<Integer, Vertex> vertices) {
        this.vertices = vertices;
    }

    public TreeMap<Integer, Edge> getEdges() {
        return edges;
    }

    public void setEdges(TreeMap<Integer, Edge> edges) {
        this.edges = edges;
    }

    public void inc_edgesCount() {
        edgesCount++;
    }

    public void dec_edgesCount() {
        edgesCount--;
    }

    public void inc_verticesCount() {
        verticesCount++;
    }

    public void dec_verticesCount() {
        verticesCount--;
    }

    public int getVerticesCount() {
        return verticesCount;
    }

    public int getEdgesCount() {
        return edgesCount;
    }
    
}
