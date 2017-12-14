import java.util.*;
public class Game {
	
	public static void main(String[] args){
		ArrayList<String> alphabet = new ArrayList<String>(Arrays.asList("n,e,y,t,s,i,b,e,n,s,e,t,u,m,i,x,e,s,l,d,n,e,z,r,s,k,r,e,j,s,x".split(",")));
		Random r = new Random();
		StringBuilder initLetters = new StringBuilder();
		for (int i = 0; i < 10; i++) {
		        initLetters.append(alphabet.remove(r.nextInt(alphabet.size())));
		}
		System.out.println("Initials letters:" + initLetters.toString());
		Node root = split(initLetters.toString());
		Node current = root;
		while(!alphabet.isEmpty()){
			while(!current.isComplete()){
				current.printBoard();
				System.out.println("Remaining pile: " + alphabet);
				System.out.println();
				Board b = current.getBoard();			
				if( current == root){				//if we are at the top of tree
					// dump if the number children exceeds getDumps()+1 * 5
					if(current.getNumOfChildren() > (current.getDumps() + 1)* 3){
						String newChars = alphabet.remove(r.nextInt(alphabet.size()))+
											(alphabet.isEmpty() ? "": alphabet.remove(r.nextInt(alphabet.size()))) +
											(alphabet.isEmpty() ? "": alphabet.remove(r.nextInt(alphabet.size())));
						System.out.println("Dumped: " + newChars);
						current = dump(current, newChars);
						continue;						
					}
					
					// otherwise, build new children
					System.out.println("building one word tree with: "+ current.getLetters());
					Board nextB = b.buildOneWordBoard(current.getLetters());
					if(nextB == null){
						String newChars = alphabet.remove(r.nextInt(alphabet.size()))+
								(alphabet.isEmpty() ? "": alphabet.remove(r.nextInt(alphabet.size()))) +
								(alphabet.isEmpty() ? "": alphabet.remove(r.nextInt(alphabet.size())));
						current = dump(current,newChars);
						continue;
					}
					current = new Node(current, nextB);
					continue;
				}				
				else{
					
					// if current node has already generated 5 children, give up and go back up to its parent
					if(current.getNumOfChildren() == 3){
						Node parent = current.getParent();
						if(current.getNewLetters()!= null) parent.addLetters(current.getNewLetters());
						current = parent;	
						continue;
					}
					
					// build children of current node
					System.out.println("building child tree");
					Board nextB = b.build();
					// no more child node can be generated, giveup and go back up to its parent
					if(nextB == null){
						System.out.println("No more child can be generated");
						Node parent = current.getParent();
						if(current.getNewLetters()!= null) parent.addLetters(current.getNewLetters());
						current = parent;	
						continue;
					}
					else{
						current = new Node(current, nextB);
					}					
				}
				
			}
			//if board is complete, peel
			if (alphabet.isEmpty()) break;
			System.out.println("Peel");
			peel(current,alphabet.remove(r.nextInt(alphabet.size())).charAt(0));
		}
		Bananagrams();
		current.printBoard();
		
		
	}
	public static Node split(String letters){

		Board empty = new Board(letters);
		Node n = new Node(null, empty);
		return n;
	}
	public static Node peel( Node current, char newLetter){
		current.addLetters(Character.toString(newLetter));
		return current;
	}
	public static Node dump(Node current, String newLetters){
		current.addLetters(newLetters);
		current.addDump();
		return current;
	}
	public static void Bananagrams(){
		System.out.println("BANANAGRAMS");
	}
	
	
	private static class Node{
		private Node parent;
		private int children;
		private Board board;
		private String newLetters;
		private int dumps;
		private int level;
		public Node(Node p, Board b){
			parent = p;
			if(p != null){
				p.addChild();
				level = p.getLevel()+1; 
			}
			else level = 0;
			board = b;
			children = 0;		
		}
		public int getLevel(){
			return level;
		}
		public int getDumps(){
			return dumps;
		}
		public void addDump(){
			dumps++;
		}
		public Node getParent(){
			return parent;
		}
		public Board getBoard(){
			return board;
		}
		public int getNumOfChildren(){
			return children;
		}
		public void addChild(){
			this.children ++;
		}
		public String getLetters(){
			if(newLetters != null){
				return board.getLetters() + newLetters;
			}
			return board.getLetters();
		}
		public void addLetters(String str){
			if(str == null) return;
			if(newLetters == null) newLetters = str;
			else{
				newLetters = newLetters + str;
			}
			this.getBoard().addLetters(str);;
		}
		public String getNewLetters(){
			if(newLetters == null) return "";
			return newLetters;
		}
		public int getNumLeft(){
			return board.getLetters().length();
		}
		public boolean isComplete(){
			if (board.getLetters().length() == 0) return true;
			return false;
		}
		public void printBoard(){
			System.out.println("Node level: " + this.getLevel() + ", numChildren: " + this.getNumOfChildren() +", numDump: " + this.getDumps());
			board.print();
		}
	}
}
