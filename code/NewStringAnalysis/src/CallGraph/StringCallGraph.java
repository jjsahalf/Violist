package CallGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import edu.usc.sql.graphs.cfg.CFGInterface;
import edu.usc.sql.graphs.cfg.SootCFG;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Sources;
class Counter{
	private int cnt=0;
	public void inCreaseCounter()
	{
		this.cnt++;
	}
	public int getCnt(){
		return cnt;
	}
}
public class StringCallGraph {
	private Set<Node> nodes;
	private Set<Node> heads;
	private Set<Node> nodesinscc;
	private List<Node> RTOlist;
	/*public List<Node> reverseTopologySort(){
		
	}*/
	private void Visit(Node n, List<Node> list, Set<Node> visited)
	{
		if(visited.contains(n))
			return;
		visited.add(n);

		for(Node w:n.getChildren())
		{
			Visit(w,list,visited);
		}
		list.add(n);
	}
	private void RTOsorting(){
		Set<Node> visited=new HashSet<Node> ();
		 for(Node n:heads)
		 {
			 if(n.getOurdgree()>0)
				 Visit(n,RTOlist,visited);
		 }
		 for(Node n:heads)
		 {
			 if(n.getOurdgree()==0)
				 RTOlist.add(n);

		 }
	}
	public Set<String> getParents(String s)
	{
		Set<String> r=new HashSet<String>();
		for(Node n:nodes)
		{
			SootMethod sm=n.getMethod();
			if(s.equals(sm.getSignature())){
				Set<Node> pset=n.getParent();
				for(Node p:pset){
					r.add(p.getMethod().getSignature());
				}
			}
		}
		return r;
	}
	public List<Node> getRTOdering(){
		
		return RTOlist;
	}
	public List<CFGInterface> getRTOInterface(){
		List<CFGInterface> r=new LinkedList<CFGInterface>();
		for(Node n:RTOlist)
		{
			if(n.getMethod().isConcrete())
				r.add(new SootCFG(n.getMethod().getSignature(),n.getMethod()));
		}
		return r;
	}
	private void PathBased(Node n, Counter c, Stack<Node> S,Stack<Node> P)
	{
		n.SetOrder(c.getCnt());
		c.inCreaseCounter();
		S.push(n);
		P.push(n);
		for(Node w:n.getChildren())
		{
			if(!w.OrderAssigned())
			{
				PathBased(w,c,S,P);
			}
			else if(!nodesinscc.contains(w)){
				//Node top=P.pop();
				System.out.println("Stringly connected detected");
				System.out.println(w+" "+w.GetOrder());

					while(P.peek().GetOrder()>w.GetOrder())
					{
						System.out.println(P.peek()+" "+P.peek().GetOrder());
						P.pop();
					}
				
				
			}
		}
		Node Ptop=P.peek();
		if(Ptop.equals(n))
		{
			int cnt=0;
			while(!S.peek().equals(n))
			{	cnt++;
				Node fn=S.pop();

				nodesinscc.add(fn);
			}
			if(S.peek().equals(n))
			{
				if(cnt>0)
				{
					nodesinscc.add(S.pop());
				}
				else{
					S.pop();
				}
			}
			P.pop();
			
		}
		
		// there is one step of path-based SCC detection not implemented, it will not be used at this time, but we may want to implement 
		// it some times in the future
	}
	public void SCCdetection(){
		Counter c=new Counter();
		 Stack<Node> S=new Stack<Node>();
		 Stack<Node> P=new Stack<Node>();
		 for(Node n:heads)
		 {
			 if(n.getOurdgree()>0)
				 PathBased(n,c,S,P);
		 }
	}
	public StringCallGraph(CallGraph cg, Set<SootMethod> allmethods){
		nodes=new HashSet<Node>();
		heads=new HashSet<Node>();
		nodesinscc= new HashSet<Node>();
		RTOlist= new LinkedList<Node>();
		HashMap<SootMethod,Node> methodToNodeMap=new HashMap<SootMethod,Node>();
		for(SootMethod sm:allmethods)
		{
			//System.out.println(sm.getSignature());
			Node n=new Node(sm);
			nodes.add(n);
			methodToNodeMap.put(sm, n);
		}
		for(Node n:nodes)
		{
			SootMethod sm=n.getMethod();
			Iterator sources = new Sources(cg.edgesInto(sm));
			while (sources.hasNext()) {
				SootMethod src = (SootMethod)sources.next();
				if(allmethods.contains(src))
				{
					Node p=methodToNodeMap.get(src);
					p.addChild(n);
					n.addParent(p);
				}
			}
		}
		for(Node n:nodes)
		{
			//System.out.println(n.getMethod().getSignature()+" "+n.getIndgree());

			if(n.getIndgree()==0)
			{
				heads.add(n);
			}
		}
		//SCCdetection();
		RTOsorting();
		//System.out.println(RTOlist);
	}
	public void display()
	{
		//System.out.println(heads);
		System.out.println("=================================");
		for(Node n:nodes)
		{
			String sig=n.getMethod().getSignature();
			for(Node c: n.getChildren())
			{
				String log=sig+"->"+c.getMethod().getSignature();
				System.out.println(log);
			}
		}
		System.out.println("=================================");
		System.out.println(nodesinscc);


	}
}