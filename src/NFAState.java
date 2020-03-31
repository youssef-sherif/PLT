import java.util.*;

public class NFAState {
    boolean finalstate;
    ArrayList<NFAState> next=new ArrayList<NFAState>();
    ArrayList<String> edges=new ArrayList<String>();
}