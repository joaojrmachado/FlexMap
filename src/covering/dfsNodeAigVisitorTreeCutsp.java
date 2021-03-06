package covering;

/**
 * Método que aplica a restrição s p através do caminhamento dfs na árvore em questão
 * @author Julio Saraçol
 */

import FlexMap.*;
import aig.*;
import tree.*;
import java.util.*;

public class dfsNodeAigVisitorTreeCutsp extends dfsNodeAigVisitor
{
    protected int s;
    protected int p;
    protected Trees         trees;
    protected Tree          tree;
    protected Set<Tree>     newTrees;
    protected TreeMap<String,Integer>    coveringS    = new TreeMap<String,Integer>();
    protected TreeMap<String,Integer>    coveringP    = new TreeMap<String,Integer>(); 
    protected TreeMap<String,Integer>    level        = new TreeMap<String,Integer>(); 


    public dfsNodeAigVisitorTreeCutsp(Trees trees, Tree tree, int s, int p)
    {
        super();
        this.s = s;
        this.p = p;
        this.trees    = trees;
        this.tree     = tree;
        this.newTrees = new HashSet<Tree>();
        coveringS.clear();
        coveringP.clear();
        level.clear();
    }

    @Override
    public void function(NodeAig nodeAigActual) 
    {
        if(nodeAigActual.isInput())
        {
            coveringS.put(nodeAigActual.getName(), 1);
            coveringP.put(nodeAigActual.getName(), 1);
            level.put(nodeAigActual.getName(),0);
            System.out.println(nodeAigActual.getName()+" nivel: "+level.get(nodeAigActual.getName()) +" custo s p: 1-1");
            return;
        }
        if(!sumBestCost(nodeAigActual))
        {
          System.out.println("CutTree nodo: "+nodeAigActual.getName());
          cutTree(nodeAigActual);
          function(nodeAigActual);
        }
    }
    
   /**Método que aplica o cálculo dos custos do nodo onde caso o custo ultrapasse s p retorna false
    * @param nodeCurrent (node actual)
    * @return true (cost OK) false (cost > sp)*/
    private boolean sumBestCost(NodeAig nodeAigActual) 
    {
       if(coveringP.containsKey(nodeAigActual.getName())&&coveringS.containsKey(nodeAigActual.getName()))
           return true;
       int costS=0,costP=0;
       if(nodeAigActual.isOR()) //caso OR
       {
           for(NodeAig father: nodeAigActual.getParents())
           {
              if((!coveringP.containsKey(father.getName()))||(!coveringS.containsKey(father.getName())))
                      function(father);
              if(coveringS.get(father.getName()) > costS)
                costS = coveringS.get(father.getName());
              costP += coveringP.get(father.getName());
           }
       }
       else
       {
           for(NodeAig father: nodeAigActual.getParents())
           {
              if((!coveringP.containsKey(father.getName()))||(!coveringS.containsKey(father.getName())))
                      function(father);
              if(coveringP.get(father.getName()) > costP)
               costP = coveringP.get(father.getName());
              costS += coveringS.get(father.getName());
           }   
       }
       if((costP > p)||(costS > s))
           return false;
       coveringS.put(nodeAigActual.getName(), costS);
       coveringP.put(nodeAigActual.getName(), costP);
       level.put(nodeAigActual.getName(), (getBiggerLevel(nodeAigActual)+1));
       System.out.println("Custo do nodo: "+nodeAigActual.getName()+" nivel: "+level.get(nodeAigActual.getName())+
               " é s:"+costS+" p:"+costP);
       return true;
   }

    private int getBiggerLevel(NodeAig nodeAigActual) 
    {
        int maxLevel = 0;
        for(NodeAig father: nodeAigActual.getParents()){
            if(this.level.get(father.getName()) > maxLevel)
                maxLevel = this.level.get(father.getName());
        }
        return maxLevel;
    }

    private void cutTree(NodeAig nodeAigActual) 
    {
        ArrayList<ArrayList<Integer>> cost      = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<NodeAig>> choices   = new ArrayList<ArrayList<NodeAig>>();
        boolean solution=false;
        int selected = choiceDinamic(nodeAigActual,choices,cost); //escolhe melhores nodos pra cortar
        for(int i=0;i<choices.get(selected).size();i++)
        {
            if((coveringP.get(choices.get(selected).get(i).getName()) > 1)||
                    (coveringS.get(choices.get(selected).get(i).getName()) > 1))
            {   solution = false; break; }            
        }
        if(solution == false) //caso de corte na árvore //neste caso não pode passa o nodo so os filhos dele
           copyAndCutTree(nodeAigActual,choices,selected,cost,solution);
        else //nodos com custo 1,1
           copyAndCutTree(nodeAigActual,choices,selected,cost,solution);           
    }
    
    /** Método que faz todas a combinações de corte e seleciona a melhor alternativa do conjunto de nodos disponiveis
     * @param nodeAigActual (node em questão)
     * @param choices (indice das combinações de nodos pai)
     * @param cost  (custo do corte dessa combinação)
     * @return (indice do choices a ser utilizado para cortar)
     */
    private int choiceDinamic(NodeAig nodeAigActual, ArrayList<ArrayList<NodeAig>> choices, ArrayList<ArrayList<Integer>> cost)
    {
        int costS,costP,indexBest=0,levelMax=0,combination;
        ArrayList<Integer> levelCombination = new ArrayList<Integer>();
        if(nodeAigActual.isOR())
            combination = p;
        else
            combination = s;           
            
        for(int i=1;i<=combination;i++)
        {
          //gera todas as combinações dos filhos
          ArrayList<ArrayList<Integer>> indexCombinations  = 
                  CombinationGenerator.getCombinations(nodeAigActual.getParents().size(),i);
          for(int j=0;j<indexCombinations.size();j++) 
          {
              choices.add(new ArrayList<NodeAig>()); //separa combinacao
              cost.add(new ArrayList<Integer>());    //calcula o custo da combinacao
              costS =0;
              costP =0;
              levelMax=0;
              for(int w=0;w<indexCombinations.get(j).size();w++)
              {
                  choices.get(choices.size()-1).add(nodeAigActual.getParents().get(indexCombinations.get(j).get(w)));
                  if(nodeAigActual.isOR())
                  {
                     if(costS < coveringS.get(nodeAigActual.getParents().get(indexCombinations.get(j).get(w)).getName()))
                         costS = coveringS.get(nodeAigActual.getParents().get(indexCombinations.get(j).get(w)).getName());
                     costP += coveringP.get(nodeAigActual.getParents().get(indexCombinations.get(j).get(w)).getName());                  
                     levelMax +=level.get(nodeAigActual.getParents().get(indexCombinations.get(j).get(w)).getName());
                  }    
                  else
                  {
                     costS += coveringS.get(nodeAigActual.getParents().get(indexCombinations.get(j).get(w)).getName());
                     if(costP < coveringP.get(nodeAigActual.getParents().get(indexCombinations.get(j).get(w)).getName()))
                        costP = coveringP.get(nodeAigActual.getParents().get(indexCombinations.get(j).get(w)).getName());                     
                     levelMax +=level.get(nodeAigActual.getParents().get(indexCombinations.get(j).get(w)).getName());
                  }
              }
              levelCombination.add(levelMax);
              if(nodeAigActual.isOR())
              {
                  cost.get(cost.size()-1).add(costP);
                  cost.get(cost.size()-1).add(costS);
                  if((cost.get(indexBest).get(0) <= costP)&&(costP <= p))
                    if((cost.get(indexBest).get(1) <= costS)&&(costS <= s))
                        if((cost.get(indexBest).get(0) < costP)||(cost.get(indexBest).get(1) < costS))
                            indexBest = cost.size()-1;                        
                        else
                            if(levelCombination.get(levelCombination.size()-1) <= levelCombination.get(indexBest))
                                indexBest = cost.size()-1;
              }
              else
              {
                  cost.get(cost.size()-1).add(costS);
                  cost.get(cost.size()-1).add(costP);
                  if((cost.get(indexBest).get(0) <= costS)&&(costS <= s))
                    if((cost.get(indexBest).get(1) <= costP)&&(costP <= p))
                        if((cost.get(indexBest).get(0) < costS)||(cost.get(indexBest).get(1) < costP))
                             indexBest = cost.size()-1;
                        else
                            if(levelCombination.get(levelCombination.size()-1) <= levelCombination.get(indexBest))
                             indexBest = cost.size()-1;
              }
           } 
        }
        return indexBest;
    }
    /**Método responsável por aplicar a cópia do nodo a ser cortado e caso necessário copiar a subárvore dele assim como criar o nodo fake
     * @param nodeAigActual
     * @param choices
     * @param selected
     * @param cost 
     * @param solution (avisa se os nodos filhos são somente entradas primarias ou existe subarvores)
     */
    private void copyAndCutTree(NodeAig nodeAigActual, ArrayList<ArrayList<NodeAig>> choices, int selected, 
            ArrayList<ArrayList<Integer>> cost, boolean solution) 
    {
        if(solution == false)
        {
            
        }
        else //SOMENTE ENTRADAS 1,1 CUSTO
        {
          ArrayList<NodeAig> newFathers = new ArrayList<NodeAig>();
          Integer index;
          if(nodeAigActual.isOR())
              index = p;
          else
              index = s;             
          while(solution != false)
          {                       
            int newNodesCut = nodeAigActual.getParents().size()/index; //quantidade de vezes que vai aplica o corte de entradas 1,1 
            while(newNodesCut > 0)
            {
               NodeAig newInput = new NodeAigInput(tree.getEdgesCount(),createName(nodeAigActual.getName(),"X"));
               ///AKIIIIII  deleteFather(myAigTree, nodeCurrent,choices.get(selected),treesNews,levelParents,"X");
               coveringS.put(newInput.getName(),1);
               coveringP.put(newInput.getName(),1);                
               level.put(newInput.getName(),(getBiggerLevel(nodeAigActual)+1));
               newFathers.add(newInput);                   
               System.out.println("Novo corte na arvore novo Nodo:"+newInput.getName());
               newNodesCut--;
               if(newNodesCut > 0)
               {
                cost.clear();
                choices.clear();
                selected = choiceDinamic(nodeAigActual,choices,cost);
               }
             }
             for(int j=0;j<newFathers.size();j++)
               myAigTree.addEdge(n, newFathers.get(j),false);
             newFathers.clear();                    
             if(nodeAigActual.getParents().size() >= index ) //caso nao necessite cortar mais as entradas
              solution = false; 
          }
        }
        
        
        
        //AKI QUANDO NAO CRIA NODO FAKE 
        NodeAig root = null, inputFake = null;
        if(nodeAigActual.isOR())
            root    = new NodeAigGateOr(nodeAigActual.getId(), nodeAigActual.getName()+"X"); 
        else
            root    = new NodeAigGate(nodeAigActual.getId(), nodeAigActual.getName()+"X"); 
        inputFake   = new NodeAigInput(nodeAigActual.getId(), nodeAigActual.getName()+"F"); 
        Tree newTree = new Tree(root);       
        if(solution == true)// somente entradas nodos com custo 1,1
        {      
           for(NodeAig father : choices.get(selected))
           {
               newTree.addEdge(root, father, Algorithms.isInverter(nodeAigActual, father));
               newTree.add(father);
           }
        }
        else
        {
           bfsNodeTreeVisitorCopy bfsCopy =  new bfsNodeTreeVisitorCopy(new ArrayList<String>(), newTree);
           nodeAigActual.accept(bfsCopy);
           coveringP.put(nodeAigActual.getName(),1);
           coveringS.put(nodeAigActual.getName(),1);
           level.put(nodeAigActual.getName(), 0);
        }
        this.newTrees.add(newTree);
        for(NodeAig deleted: newTree.getTree())
            if(nodeAigActual.getId() != deleted.getId())
               tree.removeVertex(deleted.getId());
        tree.addEdge(nodeAigActual, inputFake, false);
        tree.add(inputFake);        
    }
    
    /**Método que cria nomes para nodos fake sem redundância*/
    private String createName(String nodeCurrentName, String type)
    {
        String name="";
        name = nodeCurrentName+type;
        for(NodeAig node: this.tree.getTree())
            if(node.getName().equals(name))
              name = createName(name, type);
        return name;
    }
}
