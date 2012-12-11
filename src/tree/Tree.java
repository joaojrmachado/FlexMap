package tree;

import graph.*;
import aig.*;
import java.util.*;

/**
 * Classe que re-implementa o SET de nodeAig para representar uma árvore
 * @author Julio Saraçol
 */
public class Tree extends Graph 
{
    protected NodeAig      root  = null;
    protected Set<NodeAig> tree  = new HashSet<NodeAig>();
    
    /**
     * Construtor
     */
    public Tree() {
    }

    /**
     * Construtor para indicando a raíz da árvore
     * @param node The rootNode for tree 
     */
    public Tree(NodeAig node) {
        super();
        this.root = node;
        this.add(node);
    }
    
    /**@override do Método de inserção de nodos no graph*/
    public void add(NodeAig node)
    {
         try 
        {
            this.vertices.put(this.verticesCount, node);
            this.verticesCount++;
            this.tree.add(node);
        }
        catch(Exception e) {
            System.out.print(e.toString());
        }
    }
    
    
    public int addEdge(NodeAig vertex1, NodeAig vertex2,boolean type) throws NullPointerException
    { 
        try {
            if((vertex1 == null || vertex2 == null)||(this.tree.contains(vertex1)||this.tree.contains(vertex2)))
                throw new NullPointerException("Vertices inconsistentes");// dispara a Exceção throws
            EdgeAig auxEdge = new EdgeAig(this.edgesCount,vertex1,vertex2);
            if(type == true) //caso seja um inversor faz o set da aresta
                auxEdge.createInverter();
            this.edges.put(auxEdge.getId(), auxEdge);
            vertex1.getAdjacencies().add(auxEdge);
            vertex2.getAdjacencies().add(auxEdge);
            this.edgesCount++;
            return auxEdge.getId();
        }
        catch(Exception e) {
            System.out.print("não foi possivel inserir a aresta" + e.toString());
        }
        return 0;
    }       
        
    
    /**Método para geração de logs*/
    public String getBytesForLog()
    {
       String treeString="";
       treeString +="[";
       int i=0;
       for(NodeAig node: this.tree)
       {
           if(i < this.tree.size()-1)
               treeString +=node.getName()+",";
           else
               treeString +=node.getName()+"]\n";
           i++;
       }
       return treeString;
    }
    
    //-------Basic Methods--------------
    public int size() {
        return tree.size();
    }

    public boolean isEmpty() {
        return tree.isEmpty();
    }

    @SuppressWarnings("element-type-mismatch")
    public boolean contains(Object o) {
        return tree.contains(o);
    }

    public Iterator<NodeAig> iterator() {
        return tree.iterator();
    }

    public Object[] toArray() {
        return tree.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return tree.toArray(a);
    }
    
    public boolean containsAll(Collection<?> c) {
        return tree.containsAll(c);
    }

    public void clear() {
        tree.clear();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tree)) {
            return false;
        }
        return tree.equals(((Tree) obj));
    }

    @Override
    public int hashCode() {
        return tree.hashCode();
    }

    @Override
    public String toString() {
        return tree.toString();
    }

    public Set<NodeAig> getTree() {
        return tree;
    }
    
}
