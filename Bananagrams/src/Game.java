import java.util.*;
public class Game {
	
	public static Node split(){
		return null;
	}
	public static Node peel(char newLetter, Node current){
		return null;
	}
	public static Node dump(char dump, String newLetters, Node Current){
		return null;
	}
	public static void Bananagrams(){
		System.out.println("BANANAGRAMS");
	}
	
	
	private static class Node{
		private Node parent;
		private ArrayList<Node> children;
		private Board board;
		private ArrayList<Integer> letters;
		
		public Node(Node p, Board b, ArrayList<Integer> l){
			parent = p;
			board = b;
			letters = l;
		}
		public Node getParent(){
			return parent;
		}
		public ArrayList<Node> getChildren(){
			return children;
		}
		public ArrayList<Integer> getLetters(){
			return letters;
		}
		public int getNumLeft(){
			return letters.size();
		}
		public boolean isLeaf(){
			if (children.size() == 0) return true;
			return false;
		}
		public boolean isComplete(){
			if (letters.size() == 0) return true;
			return false;
		}
	}
}
