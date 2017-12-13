import java.util.*;
import java.io.*;

public class Board implements Cloneable{
	private Position[][] map;
	private ArrayList<Entry> entries;
	private char[] unusedLetters;
	
	//costs for letters, the harder it is to make a word with that letter, the higher the cost.
	private static final HashMap<Character, Integer> charCosts ;
	static{
		charCosts = new HashMap<Character,Integer>(){{
			put('a', 21);put('b', 21);put('c', 21); 
		}};
	}
	
	//construct empty board 
	public Board(char[] letters){
		map = new Position[20][20];
		entries = new ArrayList<Entry>();
		unusedLetters = letters;		
	}
	
	//clone board and underlying objects
	public Board clone(){
		Board copy = new Board(this.unusedLetters);
		for(int i = 0; i < copy.map.length ; i ++){
			for(int j = 0; j < copy.map[i].length ; i ++){
				copy.map[i][j] = this.map[i][j].clone();
			}
		}
		for(int i = 0 ; i < this.entries.size() ; i ++){
			Entry e = this.entries.get(i).clone();
			copy.entries.add(e);
		}
		return copy;
	}
	
	//construct board with only one word, under assumption that "this" is an empty board.
	public Board buildOneWordBoard(String letters){
		Board child = new Board(this.unusedLetters);
		ArrayList<String> listOfWords = Functions.simulatedAnnealingGetWords(letters.toCharArray());
		String firstWord = listOfWords.get(0);
		child.deleteChars(firstWord);
		int x = map.length / 2;
		int y = (map[x].length - firstWord.length()) / 2;
		child.addEntry(x, y, firstWord, false);
		child.fillBoard(firstWord, x, y, false);
		return child;
	}
	
	//check if the board is valid.
	public boolean validateBoard(){
		//check all horizontal entries
		for(int i = 0 ; i < map.length; i++){
			StringBuilder str = new StringBuilder();
			boolean c = false;
			for( int j = 0 ; j < map[i].length; j ++){
				if( !c && !map[i][j].isFull()){
					continue;
				}
				if( c && !map[i][j].isFull()){
					if(Functions.isAWord(str.toString())){
						str.delete(0, str.length());
						c = false;
					}
					else return false;
				}
				else if (map[i][j].isFull()){
					str.append(map[i][j].getLetter());
					c = true;
				}
			}
			if (str.length() > 0 && !Functions.isAWord(str.toString())) return false;
		}

		//check all vertical entries
		for(int i = 0 ; i < map[1].length; i++){
			StringBuilder str = new StringBuilder();
			boolean c = false;
			for( int j = 0 ; j < map.length; j ++){
				if( !c && !map[j][i].isFull()){
					continue;
				}
				if( c && !map[j][i].isFull()){
					if(Functions.isAWord(str.toString())){
						str.delete(0, str.length());
						c = false;
					}
					else return false;
				}
				else if (map[j][i].isFull()){
					str.append(map[j][i].getLetter());
					c = true;
				}
			}
			if (str.length() > 0 && !Functions.isAWord(str.toString())) return false;
		}
		return true;
	}
	
	//check if the positions to be filled are empty, if full return false, else fill the positions with str
	public boolean fillBoard(String str, int x, int y, boolean vertical){
		if(str == null || str.length() == 0) return true;
		if(vertical){
			for( int i = x; i < str.length()+ x; i++){
				if (map[i][y].isFull()) return false;
				//if (getSurrounding(i,y) > 0) return false;
			}
			for( int i = x; i < str.length() + x; i++){
				if (map[i][y].fillPosition( str.charAt(i-x))<=0 ) return false;
			}
		}
		else{
			for( int i = y; i < str.length() + y; i++){
				if(map[x][i].isFull()) return false;
				//if (getSurrounding(i,y) > 1) return false;
			}
			for( int i = y; i < str.length() + y; i++){
				if (map[x][i].fillPosition( str.charAt(i-y)) <= 0 ) return false;
			}
		}
		return true;
	}
	
	//clear positions on board
	public void clearBoard(int x, int y, int length, boolean vertical){
		if(length == 0) return;
		if(vertical){
			for( int i = x; i < length + x; i++){
				map[i][y].clearPosition();
			}
		}
		else{			
			for( int i = y; i < length + y; i++){
				map[x][i].clearPosition();
			}
		}
	}
	
	//method that the game class calls to get a valid board.
	public Board build(char[] additionalLetters){
		if(additionalLetters.length > 0 ){
			unusedLetters = (unusedLetters.toString() + additionalLetters[0]).toCharArray();
		}
		if(unusedLetters.length == 1) {
			return addOneLetter();
		}
		else return addWord();
	}
	
	//
	public Board addWord(){
		Board child = this.clone();
		for(int i = 0; i < entries.size(); i++){
			Entry e = entries.get(i);
			String w = e.getWord();
			for(int j = 0 ; j < w.length() ; j ++){
				String s = new String(this.unusedLetters) + w.substring(j, j+ 1);
				ArrayList<String> listOfWords = Functions.simulatedAnnealingGetWords(this.unusedLetters);
				for( int k = 0 ; k < 5 ; k ++){
					String word = listOfWords.get(k);
					int index  = word.indexOf(w.substring(j,j+1));
					if (e.isVertical()){
						if (!child.fillBoard(word.substring(0, index), e.getX() + j ,e.getY() - index, false)) continue;
						if (!child.fillBoard(word.substring(index+1), e.getX() + j, e.getY()+ i, false)){
							child.clearBoard(index, e.getX() + j, e.getY() - index, false);
							continue;
						}
						if(child.validateBoard()){
							child.addEntry(e.getX() + j , e.getY() - index, word, false);
							return child;
						}
						else{
							child.clearBoard(e.getX() + j ,e.getY() - index, index , false);
							child.clearBoard(e.getX() + j, e.getY()+ i, word.length() - index - 1 , false);
						}
					}
					else{
						if (!child.fillBoard(word.substring(0, index), e.getX() - index ,e.getY() + j, true)) continue;
						if (!child.fillBoard(word.substring(index+1), e.getX() - index ,e.getY() + j, true)){
							child.clearBoard(index, e.getX() - index, e.getY() + j, true);
							continue;
						}
						if(child.validateBoard()){
							child.addEntry(e.getX() - index , e.getY() + j, word, true);						
							return child;
						}
						else{
							child.clearBoard(e.getX() - index ,e.getY() + j, index, true);
							child.clearBoard(e.getX() - index ,e.getY() + j,word.length() - index - 1, true);
						}
					}
					
				}
			}
		}		
		return null;
	}
	
	public Board addOneLetter(){
		char letter = unusedLetters[0];
		Board next = this.clone();
		for (int i = 0; i < entries.size() ; i++){
			Entry e = next.entries.get(i);
			String w = e.getWord();
			String front = letter + w ;
			String end = w + letter;
			
			//appending and prepending to existing words on board
			if( Functions.isAWord(front)){
				if (next.prependToEntry(e, letter)){
					next.entries.remove(i);
					if(e.isVertical()) next.addEntry(e.getX()-1,e.getY(),front, e.isVertical());
					else next.addEntry(e.getX(),e.getY()-1,front, e.isVertical());					
					return next;
				}
			}
			
			if( Functions.isAWord(end)){
				if ( next.appendToEntry(e, letter)){
					next.entries.remove(i);
					next.addEntry(e.getX(),e.getY(),end, e.isVertical());
					return next;
				}
			}
			
			//sidepending to existing words on board
			for( int j = 0 ; j < w.length() ; j++){
				String s = letter + w.substring(j,j+1); 
				String t = w.substring(j , j+1) + letter;
				
				if(Functions.isAWord(s)){
					if( e.isVertical()){
						if (next.fillBoard(Character.toString(letter), e.getX() + j ,e.getY()- 1, false)){
							if (next.validateBoard()){
								next.addEntry(e.getX()  + j , e.getY() - 1 , s , e.isVertical());
								return next;
							}
							else next.clearBoard(e.getX()  + j  , e.getY() -1, 1, false);
						}
					}
					else{
						if (next.fillBoard(Character.toString(letter), e.getX() - 1 , e.getY()+ j, true)){
							if (next.validateBoard()){
								next.addEntry(e.getX()  -1 , e.getY() +j , s , e.isVertical());
								return next;
							}
							else next.clearBoard(e.getX() -1 , e.getY() + j, 1, true);
						}
					}
				}
				if(Functions.isAWord(t)){
					if( e.isVertical()){
						if (next.fillBoard(Character.toString(letter), e.getX() + j ,e.getY() + 1, false)){
							if(next.validateBoard()){
								next.addEntry(e.getX()  + j , e.getY() + 1 , t , e.isVertical());
								return next;
							}
							else next.clearBoard(e.getX() + j ,e.getY() + 1 ,1, false);
						}
					}
					else{
						if (next.fillBoard(Character.toString(letter), e.getX() + 1, e.getY() + j, true)){
							if(next.validateBoard()){
								next.addEntry(e.getX()  + 1 , e.getY() + j , t , e.isVertical());
								return next;
							}
							else next.clearBoard(e.getX() + 1, e.getY() + j, 1 ,true);
						}
					}
				}
			}
		}
		return null;
	}
	
	public boolean addEntry(int x, int y, String word, boolean vertical){
		int score = 0;
		for(int i = 0 ; i < word.length() ; i ++){
			score -= charCosts.get(word.charAt(i));
			score += getSurrounding(x,y) * 3;
		}
		Entry e = new Entry(x,y,word, score, vertical);
		this.entries.add(e);
		Collections.sort(entries);
		return true;
	}
	
	
	//prepend one letter to entry e
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
			if ( n == 1){
				this.fillBoard(Character.toString(c),e.getX()  , e.getY() - 1, true);
				return true;
			}
		}
		return false;
	}
	
	//append one letter to  entry e
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
	public void deleteChars(String chars){
		for(int i = 0 ; i < chars.length() ; i++ ){
			if (unusedLetters.toString().contains(chars.substring(i,i+1))){
				unusedLetters.toString().replaceFirst(chars.substring(i,i+1), "");
			}
		}
	}
	
	public char[] getLetters(){
		return unusedLetters;
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
		
		//fill the position with char c
		//returns -1 if there is already an entry, 0 if there is a letter, 1 if successful.
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
		
		public void clearPosition(){
			full = false;
			isEntry = false;
			head = null;
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
			
	private class Entry implements Comparable{
		//0,0 is bottom left of the board
		private int x;
		private int y;
		private int score;
		private int horizontal;
		private int vertical;
		private String word;
		
		@Override
		public int compareTo(Object o) {
			Entry e = (Entry) o;
			if(this.score < e.score) return -1;
			if(this.score == e.score) return 0;
			else return 1;
		}

		public Entry(int i, int j, String str, int s, boolean v){
			x = i;
			y = j;
			score = s;
			if(!v){
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
				Entry copy = new Entry(this.x, this.y, this.word, this.score ,true);
				return copy;
			}
			if(this.vertical> 0){
				Entry copy = new Entry(this.x, this.y, this.word,  this.score ,false);
				return copy;
			}
			return null;
			
		}
		
		public int getScore(){
			return score;
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
