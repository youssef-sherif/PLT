import java.util.ArrayList;
import java.util.Map;

public class DFAState {
    private boolean finalstate;
    private Map<Character, DFAState> transitions;
    private ArrayList<NFAState> collectionStates;
    private String id;
    private boolean marked = false;
    
     public DFAState(){
     //    this.transitions = new Map<Character, DFAState>();
         this.collectionStates = new ArrayList<NFAState>();
     }
     
     public void addCollectionState(NFAState inputState){
        // for(NFAState state: inputState){
            this.collectionStates.add(inputState);
         //}
     }
     
     public ArrayList<NFAState> getCollectionStates(){
         return this.collectionStates;
     }
     
     public boolean isCollectionState(NFAState inputState){
         if(this.collectionStates.contains(inputState)){
             return true;
         } else{
             return false;
         }
     }
     
     public void mark(){
         this.marked = true;
     }
     
     public boolean isMarked(){
         return this.marked;
     }
}
