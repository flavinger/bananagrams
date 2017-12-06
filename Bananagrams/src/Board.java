import java.util.*;
import java.io.*;

public class Board implements Cloneable{
	private Position[][] map;
	private ArrayList<Entry> entries;
	private HashMap<String, Entry> stringEntryMap;
	private ArrayList<Character> unusedWords;
	private int numWords;
	private int score;
	
	public Board(ArrayList<Character> letters){
		map = new Position[10][10];
		entries = new ArrayList<Entry>(5);
		numWords = 0;
		score = 0;
		unusedWords = letters;
		
	}
	public Board clone(){
		Board copy = new Board(this.unusedWords);
		for(int i = 0; i < copy.map.length ; i ++){
			for(int j = 0; j < copy.map[i].length ; i ++){
				copy.map[i][j] = this.map[i][j].clone();
				if(this.map[i][j].isEntry()){
					Entry e= copy.map[i][j].getEntry();
					copy.entries.add(e);
					copy.stringEntryMap.put(e.getWord(), e);
				}
			}
		}
		copy.numWords = this.getNumWords();
		copy.score = this.getScore();
		return copy;
	}
	
	public Board buildOneWordBoard(String letters){
		Board child = new Board(this.unusedWords);
		ArrayList<String> listOfWords = Functions.simulatedAnnealingGetWords(letters.toCharArray());
		String firstWord = listOfWords.get(0);
		int x = map.length / 2;
		int y = (map[x].length - firstWord.length()) / 2;
		child.fillBoard(firstWord, x, y, false);
		return child;
	}
	
	public ArrayList<String> getExistingWords(){
		ArrayList<String> ls = new ArrayList<>();
		ls.addAll(stringEntryMap.keySet());
		return ls;
	}
	
	public boolean fillBoard(String str, int x, int y, boolean vertical){
		if(vertical){
			for( int i = x; i <= str.length(); i++){
				if (map[i][y].fillPosition( str.charAt(i-x))== -1) return false;
			}
		}
		else{
			for( int i = y; i <= str.length(); i++){
				if (map[x][i].fillPosition( str.charAt(i-y)) == -1) return false;
			}
		}
		return true;
	}
	
	public Board addOneLetter(char letter){
		Board next = this.clone();
		for (int i = 0; i < entries.size() ; i++){
			Entry e = entries.get(i);
			String w = e.getWord();
			String front = letter + w ;
			String end = w + letter;
			if( Functions.isAWord(front)){
				if (next.prependToEntry(e, letter)) return next;
			}
			if( Functions.isAWord(end)){
				if ( next.appendToEntry(e, letter)) return next;
			}	
			for( int j = 0 ; j < w.length() ; j++){
				String s = letter + w.substring(j,j+1); 
				String t = w.substring(j , j+1) + letter;
				if(Functions.isAWord(s)){
					if( e.isVertical()){
						if (next.fillBoard(Character.toString(letter), e.getX()  ,e.getY()- 1, false)) return next;
					}
					else{
						if (next.fillBoard(Character.toString(letter), e.getX() - 1, e.getY(), true)) return next;
					}
				}
				if(Functions.isAWord(t)){
					if( e.isVertical()){
						if (next.fillBoard(Character.toString(letter), e.getX()  ,e.getY() + 1, false)) return next;
					}
					else{
						if (next.fillBoard(Character.toString(letter), e.getX() + 1, e.getY(), true)) return next;
					}
				}
			}
		}
		return null;
	}
	
	
	public boolean prependToEntry(Entry e, char c){
		if(e.isVertical()){
			int n = getSurrounding(e.getX() - 1, e.getY());
			if ( n == 1){
				this.fillBoard(Character.toString(c),e.getX() - 1 , e.getY(), true);
				return true;
			}
		}
		else{
			int n = getSurrounding(e.getX() , e.getY() - 1);
			if( n == 1){
				this.fillBoard(Character.toString(c),e.getX()  , e.getY() - 1, true);
				return true;
			}
		}
		return false;
	}
	
	public boolean appendToEntry(Entry e, char c){
		if(e.isVertical()){
			int n = getSurrounding(e.getX() - 1, e.getY());
			if ( n == 1){
				this.fillBoard(Character.toString(c),e.getX() + 1 , e.getY(), true);
				return true;
			}
		}
		else{
			int n = getSurrounding(e.getX() , e.getY() - 1);
			if( n == 1){
				this.fillBoard(Character.toString(c),e.getX()  , e.getY() + 1, true);
				return true;
			}
		}
		return false;
	}
	public boolean isValidBoard(){
		
	}
	
	public int getSurrounding(int x, int y){
		int counter = 0;
		if( x < map.length - 1){
			if(map[x+1][y].isFull()) counter++;
		}
		if ( x > 1){
			if(map[x-1][y].isFull()) counter++;
		}
		if( y < map[x].length - 1 && y > 1){
			if(map[x][y+1].isFull()) counter++;
		}
		if( y > 1){
			if(map[x][y-1].isFull()) counter++;
		}
		return counter;
	}
	public int getNumWords(){
		return numWords;
	}
	
	public int getScore(){
		return score;
	}

	
			
	private class Position{
		private int x, y;
		private boolean full;
		private boolean isEntry;
		private char letter;
		private Entry head;
		public Position(int i, int j){
			x = i;
			y = j;
			full = false;
			isEntry = false;
		}
		public Position clone(){
			Position copy = new Position(this.x, this.y);
			copy.full = this.isFull();
			copy.isEntry = this.isEntry();
			copy.letter = this.getLetter();
			copy.head = this.getEntry().clone();
			return copy;
		}
		public int fillPosition(char c){
			if(full && isEntry) return -1;
			if(full) return 0;
			letter = c;
			full = true;
			return 1;
		}
		
		public int fillPosition(Entry e){
			if(full && isEntry) return -1;
			if(full) return 0;
			letter = e.getWord().charAt(0);
			full = true;
			head = e;
			return 1;
		}
		
		public int getX(){
			return x;
		}
		
		public int getY(){
			return y;
		}
		public boolean isFull(){
			return full;
		}
		
		public boolean isEntry(){
			return isEntry();
		}
		
		public Entry getEntry(){
			return head;
		}
		
		public char getLetter(){
			return letter;
		}
	}
			
	private class Entry{
		//0,0 is bottom left of the board
		private int x;
		private int y;
		private int horizontal;
		private int vertical;
		private boolean empty;
		private String word;
		
		public Entry(int i, int j){
			x = i;
			y = j;
			empty = true;
		}
			
		public Entry(int i, int j, String str, boolean h){
			x = i;
			y = j;
			empty = false;
			if(h){
				horizontal =str.length();
				word = str;
			}
			else{
				vertical  = str.length();
				word = str;
			}			
		}
		
		public Entry clone(){
			if(this.horizontal>0){
				Entry copy = new Entry(this.x, this.y, this.word, true);
				return copy;
			}
			if(this.vertical> 0){
				Entry copy = new Entry(this.x, this.y, this.word, false);
				return copy;
			}
			return new Entry(this.x, this.y );
			
		}
		public boolean fill(String str, boolean h){
			if(!isEmpty()) return false;
			empty = false;
			if( h ){
				horizontal = str.length();
				word = str;
				return true;
			}
			vertical = str.length();
			word = str;
			return true;
		}
		
		public boolean isEmpty(){
			return empty;
		}
		
		public int getX(){
			return x;
		}
		
		public int getY(){
			return y;
		}
		
		public boolean isVertical(){
			return (vertical > 0);
		}
		
		public int length(){
			return horizontal > 0? horizontal: vertical;
		}
		
		public String getWord(){
			return word;
		}
	}
}
