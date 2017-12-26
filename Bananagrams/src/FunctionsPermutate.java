import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;


public class FunctionsPermutate implements KeyListener {

	static int dictCall = 0;
	static boolean spaceKeyPressed = false;
	static int counter = 0;
	static int numValidWords = 0;
	static int numInvalidWords = 0;
	static int numDupes = 0;
    
    // checks dictionary API
    /*
    public static boolean isAWord(String s) {
    	dictCall++;
    	return true;
    }
    */
    private static final HashSet<String> dict;
    static{
    	dict = new HashSet<String>(){{
    		add("sex");add("jerks");add("mix");add("mutes");add("elders");add("zen");add("bent");add("sty");add("yen");
    		add("jerk");add("mute");add("elder");add("but");add("bust");add("bus");add("yes");add("meld");add("melt");
    		add("rim");add("miss");add("stem");add("bin");add("risk");add("kin");add("jet");add("jets");
    		add("jesus");add("jest");add("just");
    		
    	}};
    }
    
    public static boolean isAWord(String s){
    	dictCall++;
    	return dict.contains(s);
    }
    
    public static int randomWithRange(int min, int max)
    {
       int range = (max - min) + 1;     
       return (int)(Math.random() * range) + min;
    }
    
    public static int choose(int charsLen, int wordLen) {
    	if (wordLen == 1)
    		return 1;
    	int product = charsLen;
    	int multiplier = charsLen-1;
    	for (int i = 0; i < wordLen-1; i++) {
    		product *= multiplier;
    		multiplier -= 1;
    		System.out.println("hi "+product);
    	}
    	System.out.println("product for wordLen "+wordLen+" is "+product);
    	return product;
    }
    /*
    public static ArrayList permutate (char[] chars){
    	ArrayList<String> ls = new ArrayList<String>();
    	String str = new String(chars);
    	for(String s : dict){
    		if(s.equals("joined")) ls.add(0,s);
    		else ls.add(s);
    		for(int i = 0 ; i < s.length(); i++){
    			if(!str.contains(s.substring(i, i+1))){
    				ls.remove(s);
    				continue;
    			}
    		}
    	}
    	Collections.sort(ls, new SampleComparator());
    	Collections.reverse(ls);
    	return ls;
    }*/
    @Override
    public void keyReleased(KeyEvent evt) {
    	spaceKeyPressed = evt.getKeyCode() == KeyEvent.VK_SPACE;
    }
    
    @Override
    public void keyTyped(KeyEvent evt) {
    	
    }
    
    @Override
    public void keyPressed(KeyEvent evt) {
    	spaceKeyPressed = evt.getKeyCode() == KeyEvent.VK_SPACE;
    }
    
    public static ArrayList permutate (char[] chars) {
    	int randomInt = 0;
    	int length = 2;
    	char[] temp = chars;
    	int count = 0;
    	int randomizations = 5;
    	ArrayList<String> words = new ArrayList();
    	// case for length = 1:
    	for (char c : chars) {
    		if (isAWord(c+""))
    			words.add(c+"");
    	}
    	// all other lengths
    	for (int i = length; i <= chars.length; i++) {
    		System.out.println("i = "+i);
    		String word = "";
    		int choice = choose(chars.length, i);
    		System.out.println("choice = "+choice);
    		for (int k = 0; k < choice*randomizations && !spaceKeyPressed; k++) {
    			System.out.println("k = "+k);
    			word = "";
    			temp = chars.clone();
    			for (int j = 0; j < i; j++) {
        			randomInt = randomWithRange(0, chars.length-1);
        			while (temp[randomInt] == '?')
        				randomInt = randomWithRange(0, chars.length-1);
        			word += temp[randomInt];
        			temp[randomInt] = '?';
        		}	
        		if (!words.contains(word)) {
        			if (isAWord(word)) {
        				words.add(word);
        				numValidWords++;
        			}
        			else {
        				System.out.println("REJECT BECAUSE NOT VALID WORD IN DICTIONARY");
        				numInvalidWords++;
        			}
        		
        		}
        		else {
        			System.out.println("REJECT BECAUSE DUPLICATE");
        			numDupes++;
        		}
        		count++;
        		System.out.println("ran this amount of times: "+count);
        		System.out.println(word);
    		}
    	}
    	counter = count;
    	Collections.sort(words, new SampleComparator());
    	Collections.reverse(words);
    	return words;
    }
	public static void main(String[] args) {
		String input = "riskjmte";
		JLabel label = new JLabel("MESSAGE");
		label.setFont(new Font("Arial", Font.BOLD, 18));
		UIManager.put("OptionPane.minimumSize",new Dimension(500,500)); 
		Font font = UIManager.getFont("OptionPane.font");
		UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Verdana", Font.BOLD, 36) ) );
		UIManager.put("TextField.font", new FontUIResource(new Font("Verdana", Font.BOLD, 36) ) );
		UIManager.put("TextField.font", new FontUIResource(new Font("Verdana", Font.BOLD, 36) ) );
		UIManager.put("OptionPane.font", new Font(font.getName(), font.getStyle(), 72));
		font = UIManager.getFont("OptionPane.font");
		input = JOptionPane.showInputDialog("Which letters has the AI drawn?", "riskjmte");
		char[] chars = input.toCharArray();
//		ArrayList<String> anagrams = getAnagrams(chars, input);
		ArrayList<String> anagrams = permutate(chars);
		System.out.println("number of letters: "+chars.length);
		System.out.println("input letters: "+chars.toString());
		System.out.println("# of words: "+anagrams.size());
		System.out.println("permutated words: "+anagrams.toString());
		System.out.println("number of permutations: "+counter);
    	System.out.println("dictionary calls: "+ dictCall);
    	JOptionPane.showMessageDialog(null, "number of letters: "+chars.length+"\n"+"input letters: "+input+"\n"+
    	"# of words: "+anagrams.size()+"\n"+"permutated words: "+anagrams.toString()+"\n"+
    			"number of permutations: "+counter+"\n"+"dictionary calls: "+dictCall+"\n"+
    	"number of valid words: "+numValidWords+"\n"+"number of invalid words: "+numInvalidWords+"\n"
    	+ "number of duplicate words: "+numDupes);
//        int len = chars.length;
//        for (int i = len; i > 0; i--) {
//        	iterate(chars, i, new char[i], 0);
//        }
	}
	public static class SampleComparator implements Comparator<String> {
	    @Override
	    public int compare(String o1, String o2) {
	        return new Integer(o1.length()).compareTo(o2.length());
	   }
	}
}