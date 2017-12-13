import java.util.*;
public class Game {
	
	public static void main(String[] args){
		ArrayList<String> alphabet = new ArrayList<String>(Arrays.asList("q,w,e,a,s,d,f,w,r,e,a,s,d,f,c,x,z,v,z,x,c".split(",")));
		int len = alphabet.size();
		Random r = new Random();
		char[] initLetters = new char[10];
		for (int i = 0; i < 10; i++) {
		        initLetters[i] = alphabet.remove(r.nextInt(len)).charAt(0);
		}
		Node root = split(initLetters);
		Node current = root;
		while(!alphabet.isEmpty()){
			while(!current.isComplete()){
				Board b = current.getBoard();				
				if( current == root){
					if(current.getNumOfChildren() > current.getDumps() * 5){
						char[] newChars = new char[] {alphabet.remove(r.nextInt(len)).charAt(0), alphabet.remove(r.nextInt(len)).charAt(0),alphabet.remove(r.nextInt(len)).charAt(0)};
						current = dump(current, newChars.toString());
						continue;						
					}
					Board nextB = b.buildOneWordBoard(current.getLetters().toString());
					current = new Node(current, nextB);
					continue;
				}				
				else{
					if(current.getNumOfChildren() == 5){
						Node parent = current.getParent();
						parent.addLetters(current.getNewLetters());
						current = parent;	
						continue;
					}
					Board nextB = b.build(new char[0]);
					if(nextB == null){
						Node parent = current.getParent();
						parent.addLetters(current.getNewLetters());
						current = parent;	
						continue;
					}
					else{
						current = new Node(current, nextB);
					}					
				}
			}
			peel(current,alphabet.remove(r.nextInt(len)).charAt(0));
		}
		Bananagrams();
		
		
	}
	public static Node split(char[] letters){

		Board empty = new Board(letters);
		Node n = new Node(null, empty);
		return n;
	}
	public static Node peel( Node current, char newLetter){
		
		return null;
	}
	public static Node dump(Node current, String newLetters){
		current.addLetters(newLetters);
		current.addDump();
		return null;
	}
	public static void Bananagrams(){
		System.out.println("BANANAGRAMS");
	}
	
	
	private static class Node{
		private Node parent;
		private ArrayList<Node> children;
		private Board board;
		private String newLetters;
		private int dumps;
		
		public Node(Node p, Board b){
			parent = p;
			board = b;
			children = new ArrayList<Node>();		
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
		public ArrayList<Node> getChildren(){
			return children;
		}
		public int getNumOfChildren(){
			return children.size();
		}
		public  void addToChildren(Node child){
			children.add(child);
		}
		public char[] getLetters(){
			return (board.getLetters().toString() + newLetters).toCharArray();
		}
		public void addLetters(String str){
			newLetters = newLetters + str;
		}
		public String getNewLetters(){
			return newLetters;
		}
		public int getNumLeft(){
			return board.getLetters().length;
		}
		public boolean isLeaf(){
			if (children.size() == 0) return true;
			return false;
		}
		public boolean isComplete(){
			if (board.getLetters().length == 0) return true;
			return false;
		}
		
	}
}
