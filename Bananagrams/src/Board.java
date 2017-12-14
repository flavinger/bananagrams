import java.util.*;
import java.io.*;

public class Board implements Cloneable{
	private Position[][] map;
	private ArrayList<Entry> entries;
	private String unusedLetters;
	private HashSet<String> checkedWords;
	private static final int SIZE = 30;
	
	//costs for letters, the harder it is to make a word with that letter, the higher the cost.
	private static final HashMap<Character, Integer> charCosts ;
	static{
		charCosts = new HashMap<Character,Integer>(){{
			put('a', 1);put('b', 3);put('c', 3); put('d', 2); put('e', 1); put('f',4); put('g',2);
			put('h',4); put('i',1);put('j',8);  put('k', 5); put('l',1); put('m', 3); put('n',1);
			put('o',1) ; put('p',3); put('q',10); put('r',1); put('s',1); put('t',1); put('u',1);
			put('v',4); put('w', 4); put('x', 8); put('y',4) ; put('z',10);
		}};
	}
	
	//construct empty board 
	public Board(String letters){
		map = new Position[SIZE][SIZE];
		for( int i = 0 ; i < SIZE; i ++){
			for(int j = 0 ; j < SIZE; j++){
				map[i][j] = new Position(i,j);
			}
		}
		entries = new ArrayList<Entry>();
		unusedLetters = letters;	
		checkedWords = new HashSet<String>();
		//System.out.println("New board created with: " + unusedLetters);
	}
	
	//clone board and underlying objects
	public Board clone(){
		Board copy = new Board(this.unusedLetters);
		for(int i = 0; i < copy.map.length ; i ++){
			for(int j = 0; j < copy.map[i].length ; j ++){
				copy.map[i][j] = this.map[i][j].clone();
			}
		}
		for(int i = 0 ; i < this.entries.size() ; i ++){
			Entry e = this.entries.get(i).clone();
			copy.entries.add(e);
		}
		return copy;
	}
	public void addLetters(String str){
		this.unusedLetters = this.unusedLetters + str;
	}
	//construct board with only one word, under assumption that "this" is an empty board.
	public Board buildOneWordBoard(String letters){
		Board child = new Board(this.unusedLetters);
		ArrayList<String> listOfWords = Functions.simulatedAnnealingGetWords(letters.toCharArray());
		listOfWords = this.removeChecked(listOfWords);
		if(listOfWords.isEmpty()) return null;
    	System.out.print("Candidate words for empty board: ");
    	for(int z = 0 ; z < listOfWords.size(); z++){
    		System.out.print(listOfWords.get(z) + " ");
    	}
    	System.out.println();
		String firstWord = listOfWords.get(0);
		int x = map.length / 2;
		int y = (map[x].length - firstWord.length()) / 2 - 5;
		this.checkedWords.add(firstWord);
		child.deleteChars(firstWord);
		child.addEntry(x, y, firstWord, false);
		child.fillBoard(firstWord, x, y, false);
		return child;
	}
	
	//method that the game class calls to get a valid board.
	public Board build(){
		if(unusedLetters.length() == 1) {
			return addOneLetter();
		}
		else return addWord();
	}
	
	//adds a single word to the clone of current board using a subset of unusedLetters.
	//Returns the new board.
	public Board addWord(){
		Board child = this.clone();
		System.out.println("addWord() with: " + child.unusedLetters);
		for(int i = 0; i < entries.size(); i++){
			Entry e = entries.get(i);
			String w = e.getWord();
			System.out.println("Entry checked: " + w);
			for(int j = 0 ; j < w.length() ; j ++){
				String s = this.unusedLetters + w.substring(j, j+ 1);
				ArrayList<String> listOfWords = Functions.simulatedAnnealingGetWords(s.toCharArray());
				listOfWords = this.removeChecked(listOfWords);
		    	System.out.print("Candidate words for "+ w.substring(j,j+1) + " : ");
		    	for(int z = 0 ; z < listOfWords.size(); z++){
		    		System.out.print(listOfWords.get(z) + " ");
		    	}
		    	System.out.println();
				for( int k = 0 ; k < Integer.min(5, listOfWords.size()) ; k ++){
					String word = listOfWords.get(k);
					System.out.println("Word chosen: " + word);
					int index  = word.indexOf(w.substring(j,j+1));
					if(index == -1) continue;
					if (e.isVertical()){
						if (!child.fillBoard(word.substring(0, index), e.getX() + j ,e.getY() - index, false)) continue;
						if (!child.fillBoard(word.substring(index+1), e.getX() + j, e.getY()+ 1, false)){
							child.clearBoard(e.getX() + j, e.getY() - index, index,  false);
							continue;
						}
						
						if(child.validateBoard()){
							
							child.addEntry(e.getX() + j , e.getY() - index, word, false);
							this.checkedWords.add(word);
							child.deleteChars(word.substring(0,index));
							child.deleteChars(word.substring(index + 1));
							return child;
						}
						else{
							System.out.println("Board not valid");
							child.print();
							child.clearBoard(e.getX() + j ,e.getY() - index, index , false);
							child.clearBoard(e.getX() + j, e.getY()+ i, word.length() - index - 1 , false);
						}
					}
					else{
						if (!child.fillBoard(word.substring(0, index), e.getX() - index ,e.getY() + j, true)) continue;
						if (!child.fillBoard(word.substring(index+1), e.getX() + 1 ,e.getY() + j, true)){
							child.clearBoard(e.getX() - index, e.getY() + j, index, true);
							continue;
						}
						System.out.println("finished constrcution of new board");
						if(child.validateBoard()){
							
							child.addEntry(e.getX() - index , e.getY() + j, word, true);
							this.checkedWords.add(word);
							child.deleteChars(word.substring(0,index));
							child.deleteChars(word.substring(index + 1));
							return child;
						}
						else{
							System.out.println("Board not valid");
							child.clearBoard(e.getX() - index ,e.getY() + j, index, true);
							child.clearBoard(e.getX() + 1 ,e.getY() + j,word.length() - index - 1, true);
						}
					}
					
				}
			}
		}		
		return null;
	}
	
	public Board addOneLetter(){
		char letter = unusedLetters.charAt(0);
		Board child = this.clone();
		System.out.println("addOneLetter() with: " + letter);
		for (int i = 0; i < entries.size() ; i++){
			Entry e = child.entries.get(i);
			String w = e.getWord();
			String front = letter + w ;
			String end = w + letter;
			
			//appending and prepending to existing words on board
			if( Functions.isAWord(front)){
				if (child.prependToEntry(e, letter)){
					child.entries.remove(i);
					child.deleteChars(Character.toString(letter));
					if(e.isVertical()) child.addEntry(e.getX()-1,e.getY(),front, e.isVertical());
					else child.addEntry(e.getX(),e.getY()-1,front, e.isVertical());	
					this.checkedWords.add(front);
					return child;
				}
			}
			
			if( Functions.isAWord(end)){
				if ( child.appendToEntry(e, letter)){
					child.entries.remove(i);
					child.deleteChars(Character.toString(letter));
					child.addEntry(e.getX(),e.getY(),end, e.isVertical());
					this.checkedWords.add(end);
					return child;
				}
			}
			
			//sidepending to existing words on board
			for( int j = 0 ; j < w.length() ; j++){
				String s = letter + w.substring(j,j+1); 
				String t = w.substring(j , j+1) + letter;
				
				if(Functions.isAWord(s)){
					if( e.isVertical()){
						if (child.fillBoard(Character.toString(letter), e.getX() + j ,e.getY()- 1, false)){
							if (child.validateBoard()){
								child.addEntry(e.getX()  + j , e.getY() - 1 , s , !e.isVertical());
								child.deleteChars(Character.toString(letter));
								this.checkedWords.add(s);
								return child;
							}
							else child.clearBoard(e.getX()  + j  , e.getY() -1, 1, false);
						}
					}
					else{
						if (child.fillBoard(Character.toString(letter), e.getX() - 1 , e.getY()+ j, true)){
							if (child.validateBoard()){
								child.addEntry(e.getX()  -1 , e.getY() +j , s , !e.isVertical());
								child.deleteChars(Character.toString(letter));
								this.checkedWords.add(s);
								return child;
							}
							else child.clearBoard(e.getX() -1 , e.getY() + j, 1, true);
						}
					}
				}
				if(Functions.isAWord(t)){
					if( e.isVertical()){
						if (child.fillBoard(Character.toString(letter), e.getX() + j ,e.getY() + 1, false)){
							if(child.validateBoard()){
								child.addEntry(e.getX()  + j , e.getY(), t , !e.isVertical());
								child.deleteChars(Character.toString(letter));
								return child;
							}
							else child.clearBoard(e.getX() + j ,e.getY() + 1 ,1, false);
						}
					}
					else{
						if (child.fillBoard(Character.toString(letter), e.getX() + 1, e.getY() + j, true)){
							if(child.validateBoard()){
								child.addEntry(e.getX()  , e.getY() + j , t , !e.isVertical());
								child.deleteChars(Character.toString(letter));
								this.checkedWords.add(t);
								return child;
							}
							else child.clearBoard(e.getX() + 1, e.getY() + j, 1 ,true);
						}
					}
				}
			}
		}
		return null;
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
				if (map[i][j].isFull()){
					str.append(map[i][j].getLetter());
					c = true;
				}
				else if( c && !map[i][j].isFull()){
					//System.out.println("validating: " + str.toString());
					if(str.length() > 1 && !Functions.isAWord(str.toString())){
						return false;
					}
					else{
						str = new StringBuilder();
						c = false;
					}
				}

			}
			if (str.length() > 1 && !Functions.isAWord(str.toString())) return false;
		}

		//check all vertical entries
		for(int i = 0 ; i < map[1].length; i++){
			StringBuilder str = new StringBuilder();
			boolean c = false;
			for( int j = 0 ; j < map.length; j ++){
				if( !c && !map[j][i].isFull()){
					continue;
				}
				if (map[j][i].isFull()){
					str.append(map[j][i].getLetter());
					c = true;
				}
				else if( c && !map[j][i].isFull()){
					//System.out.println("validating: " + str.toString());
					if(str.length() > 1 && !Functions.isAWord(str.toString())){
						return false;
					}
					else{
						str = new StringBuilder();
						c = false;
					}
				}

			}
			if (str.length() > 0 && !Functions.isAWord(str.toString())) return false;
		}
		return true;
	}
	
	//check if the positions to be filled are empty, if full return false, else fill the positions with str
	public boolean fillBoard(String str, int x, int y, boolean vertical){
		System.out.println("Filling "+ str + " at " + x +" "+ y + "; vertical: " + vertical);
		if(str == null || str.length() == 0) return true;
		if(vertical){
			for( int i = x; i < str.length()+ x; i++){
				if (map[i][y].isFull()){
					System.out.println("Filling at " + i + " " + y + "failed with " + str.charAt(i-x));
					return false;
				}
				//if (getSurrounding(i,y) > 0) return false;
			}
			for( int i = x; i < str.length() + x; i++){
				if (!map[i][y].fillPosition( str.charAt(i-x)) ){
					System.out.println("Filling at " + i + " " + y + "failed with " + str.charAt(i-x));
					return false;
				}
			}
		}
		else{
			for( int i = y; i < str.length() + y; i++){
				if(map[x][i].isFull()) return false;
				//if (getSurrounding(i,y) > 1) return false;
			}
			for( int i = y; i < str.length() + y; i++){
				if (!map[x][i].fillPosition( str.charAt(i-y))) return false;
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
	
	//remove words that have been used already from the new candidate list
	public ArrayList<String> removeChecked(ArrayList<String> ls){
		for( int i = 0 ; i < ls.size() ; i ++){
			if(this.checkedWords.contains(ls.get(i))) ls.remove(i);
		}
		return ls;
	}
	
	
	
	public boolean addEntry(int x, int y, String word, boolean vertical){
		int score = 0;
		for(int i = 0 ; i < word.length() ; i ++){
			if(vertical){
				score += (10 - charCosts.get(word.charAt(i))) * getSurrounding(x + i,y) ;
			}
			else score += (10 - charCosts.get(word.charAt(i))) * getSurrounding(x ,y + i) ;
			
		}
		Entry e = new Entry(x,y,word, score, vertical);
		if (this.entries.isEmpty()){
			this.entries.add(e);
			return true;
		}
		else{
			for( int i = 0 ; i < this.entries.size(); i++){
				if( score < this.entries.get(i).getScore() ){
					this.entries.add(i,e);
					return true;
				}
				if( score == this.entries.get(i).getScore()){
					if(e.length() > this.entries.get(i).length()){
						this.entries.add(i,e);
						return true;
					}
				}
			}
		}
		this.entries.add(e);
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
			int n = getSurrounding(e.getX() + e.length()  , e.getY());
			if ( n == 1){
				this.fillBoard(Character.toString(c),e.getX() + e.length(), e.getY(), true);
				return true;
			}
		}
		else{
			int n = getSurrounding(e.getX() , e.getY()+ e.length());
			if( n == 1){
				this.fillBoard(Character.toString(c),e.getX()  , e.getY() + e.length(), true);
				return true;
			}
		}
		return false;
	}	

	public int getSurrounding(int x, int y){
		int counter = 0;
		if( x < map.length - 1){
			if(map[x+1][y] == null ||map[x+1][y].isFull()) counter++;
		}
		if ( x > 1){
			if(map[x-1][y] == null || map[x-1][y].isFull()) counter++;
		}
		if( y < map[x].length - 1 && y > 1){
			if(map[x][y+1] == null || map[x][y+1].isFull()) counter++;
		}
		if( y > 1){
			if(map[x][y - 1] == null ||map[x][y-1].isFull()) counter++;
		}
		return counter;
	}
	
	public void deleteChars(String chars){
		for(int i = 0 ; i < chars.length() ; i++ ){
			if (this.unusedLetters.contains((chars.substring(i, i+1)))){
				this.unusedLetters = this.unusedLetters.substring(0,this.unusedLetters.indexOf(chars.charAt(i))) + this.unusedLetters.substring(this.unusedLetters.indexOf(chars.charAt(i))+ 1);
			}
		}
	}
	
	public String getLetters(){
		return unusedLetters;
	}

	public void print(){
		for(int i = 0 ; i < map.length ; i++){
			for(int j = 0 ; j < map[i].length; j++){
				if(map[i][j].isFull()){
					System.out.print( map[i][j].getLetter() );
				}
				else System.out.print(map[i][j].isFull() ? "1":"-");
			}
			System.out.println();
		}
		System.out.print("Unused letters: " );
		for(int i = 0 ; i < unusedLetters.length() ; i++){
			System.out.print(unusedLetters.substring(i,i+1));
		}
		System.out.println();
		System.out.print("Checked words: ");
		for(String s :checkedWords){
			System.out.print(s + " ");
		}
		System.out.println();
	}
	
			
	private class Position{
		private int x, y;
		private boolean full;
		private char letter;
		public Position(int i, int j){
			x = i;
			y = j;
			full = false;
		}
		public Position clone(){
			Position copy = new Position(this.x, this.y);
			copy.full = this.isFull();
			copy.letter = this.getLetter();
			return copy;
		}
		
		//fill the position with char c
		//returns -1 if there is already an entry, 0 if there is a letter, 1 if successful.
		public boolean fillPosition(char c){
			if(full) return false;
			letter = c;
			full = true;
			return true;
		}
		/*
		public int fillPosition(Entry e){
			if(full) return 0;
			letter = e.getWord().charAt(0);
			full = true;
			return 1;
		}
		*/
		
		public void clearPosition(){
			full = false;
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
		
		public char getLetter(){
			return letter;
		}
	}
			
	private class Entry{
		//0,0 is bottom left of the board
		private int x;
		private int y;
		private int score;
		private int horizontal;
		private int vertical;
		private String word;

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
				Entry copy = new Entry(this.x, this.y, this.word, this.score ,false);
				return copy;
			}
			if(this.vertical> 0){
				Entry copy = new Entry(this.x, this.y, this.word,  this.score ,true);
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
