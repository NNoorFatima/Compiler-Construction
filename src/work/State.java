package work;
import java.util.*;

class State {
    int id;
    Map<Character, List<State>> transitions;
    boolean isFinal;
    String tokenType;  // ✅ Added tokenType field

    // ✅ Updated Constructor
    public State(int id) {
        this.id = id;
        this.transitions = new HashMap<>();
        this.isFinal = false;
        this.tokenType = null;  // Default to null
    }
    public State(int id, String Tk) {
        this.id = id;
        this.transitions = new HashMap<>();
        this.isFinal = false;
        this.tokenType = Tk;  // Default to null
    }

    // ✅ Overloaded Constructor to set `isFinal` and `tokenType`
    public State(int id, boolean isFinal, String tokenType) {
        this.id = id;
        this.transitions = new HashMap<>();
        this.isFinal = isFinal;
        this.tokenType = tokenType;
    }

    public void addTransition(char symbol, State state) {
        transitions.putIfAbsent(symbol, new ArrayList<>());
        transitions.get(symbol).add(state);
        
    }

    // ✅ Modified `display()` to show `tokenType`
//    public void display() {
//        for (Map.Entry<Character, List<State>> entry : transitions.entrySet()) {
//            for (State state : entry.getValue()) {
//                System.out.println(
//                    "State " + id + 
//                    " -- " + (entry.getKey() == 'ε' ? "ε" : entry.getKey()) + 
//                    " --> State " + state.id +
//                    (state.isFinal ? " [Final: " + (state.tokenType != null ? state.tokenType : "UNKNOWN") + "]" : "")
//                );
//            }
//        }
//}
//    public void display() {
//        List<String> transitionsList = new ArrayList<>();
//
//        for (Map.Entry<Character, List<State>> entry : transitions.entrySet()) {
//            char transitionChar = entry.getKey();
//            List<State> states = entry.getValue();
//
//            // ✅ Sort destination states by ID
//            states.sort(Comparator.comparingInt(s -> s.id));
//
//            for (State state : states) {
//                transitionsList.add(
//                    "State " + id + 
//                    " -- " + (transitionChar == 'ε' ? "ε" : transitionChar) + 
//                    " --> State " + state.id +
//                    (state.isFinal ? " [Final: " + (state.tokenType != null ? state.tokenType : "UNKNOWN") + "]" : "")
//                );
//            }
//        }
//
//        // ✅ Sort transitions safely
////        transitionsList.sort((s1, s2) -> {
////            try {
////                // ✅ Extract numbers from "State X -- 'a' --> State Y"
////                String[] parts1 = s1.replace("State ", "").split(" -- ");
////                String[] parts2 = s2.replace("State ", "").split(" -- ");
////
////                if (parts1.length < 2 || parts2.length < 2) return 0; // ✅ Avoid crash if split fails
////
////                int source1 = Integer.parseInt(parts1[0].trim());  // ✅ Extract source state ID
////                int source2 = Integer.parseInt(parts2[0].trim());
////
////                String[] transitionParts1 = parts1[1].split(" --> State ");
////                String[] transitionParts2 = parts2[1].split(" --> State ");
////
////                if (transitionParts1.length < 2 || transitionParts2.length < 2) return 0; // ✅ Avoid crash
////
////                char transitionChar1 = transitionParts1[0].trim().charAt(1); // ✅ Extract transition character
////                char transitionChar2 = transitionParts2[0].trim().charAt(1);
////
////                int destination1 = Integer.parseInt(transitionParts1[1].trim());  // ✅ Extract destination state ID
////                int destination2 = Integer.parseInt(transitionParts2[1].trim());
////
////                // ✅ Sort based on Source ID → Destination ID → Transition Character
////                if (source1 != source2) return Integer.compare(source1, source2);
////                if (destination1 != destination2) return Integer.compare(destination1, destination2);
////                return Character.compare(transitionChar1, transitionChar2);
////
////            } catch (Exception e) {
////                return 0; // ✅ Catch and prevent any crashes
////            }
////        });
//        if (isFinal && transitions.isEmpty()) {
//            transitionsList.add("State " + id + " [Final: " + (tokenType != null ? tokenType : "UNKNOWN") + "]");
//        }
//
//        // ✅ Sort transitions safely
//        transitionsList.sort(Comparator.naturalOrder());
//        // ✅ Print sorted transitions
//        for (String transition : transitionsList) {
//            System.out.println(transition);
//        }
//    }
    
    public void display() {
        List<String> transitionsList = new ArrayList<>();

        for (Map.Entry<Character, List<State>> entry : transitions.entrySet()) {
            char transitionChar = entry.getKey();
            List<State> states = entry.getValue();

            // ✅ Sort destination states by ID
            states.sort(Comparator.comparingInt(s -> s.id));

            for (State state : states) {
                transitionsList.add(
                    "S " + id + 
                    " -- " + (transitionChar == 'ε' ? "ε" : transitionChar) + 
                    " --> S " + state.id +
                    (state.isFinal ? " [Final: " + (state.tokenType != null ? state.tokenType : "UNKNOWN") + "]" : "")
                );
            }
        }

        // ✅ Always print final states, even if they have outgoing transitions
        if (isFinal) { 
            transitionsList.add("State " + id + " [Final: " + (tokenType != null ? tokenType : "UNKNOWN") + "]");
        }

        // ✅ Sort transitions safely
        transitionsList.sort(Comparator.naturalOrder());

        // ✅ Print sorted transitions
        for (String transition : transitionsList) {
            System.out.println(transition);
        }
    }


}
