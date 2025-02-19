package work;

import java.util.*;

class NFA {
    State startState;
    State finalState;
    static int stateCounter = 0; 

    public NFA(State start, State end) {
        this.startState = start;
        this.finalState = end;
    }

    public static NFA createSimpleNFA(char symbol) {
        State start = new State(stateCounter++);
        State end = new State(stateCounter++);
        start.addTransition(symbol, end);
        end.isFinal = true;
        return new NFA(start, end);
    }

//    public static NFA union(NFA nfa1, NFA nfa2) {
//        State newStart = new State(stateCounter++);
//        State newEnd = new State(stateCounter++);
//        
//        newStart.addTransition('ε', nfa1.startState);
//        newStart.addTransition('ε', nfa2.startState);
//        
//        nfa1.finalState.addTransition('ε', newEnd);
//        nfa2.finalState.addTransition('ε', newEnd);
//        
//        newEnd.isFinal = true;
//        return new NFA(newStart, newEnd);
//    }
    public static NFA union(NFA... nfas) {
        if (nfas.length == 0) return null; // No NFAs to merge

        State newStart = new State(stateCounter++);
        State newEnd = new State(stateCounter++);

        for (NFA nfa : nfas) {
            newStart.addTransition('ε', nfa.startState);
            nfa.finalState.addTransition('ε', newEnd);
        }

        newEnd.isFinal = true;
        return new NFA(newStart, newEnd);
    }


    public static NFA concatenate(NFA nfa1, NFA nfa2) {
        nfa1.finalState.addTransition('ε', nfa2.startState);
        nfa1.finalState.isFinal = false; // Make intermediate state non-final
        return new NFA(nfa1.startState, nfa2.finalState);
    }

    public static NFA kleeneStar(NFA nfa) {
        State newStart = new State(stateCounter++);
        State newEnd = new State(stateCounter++);

        newStart.addTransition('ε', nfa.startState);
        newStart.addTransition('ε', newEnd);
        nfa.finalState.addTransition('ε', nfa.startState);
        nfa.finalState.addTransition('ε', newEnd);
        
        newEnd.isFinal = true;
        return new NFA(newStart, newEnd);
    }

    // ✅ **NEW: kleenePlus() (Matches at least once, then repeats)**
    public static NFA kleenePlus(NFA nfa) {
        State newStart = new State(stateCounter++);
        State newEnd = new State(stateCounter++);

        newStart.addTransition('ε', nfa.startState);  // Must match at least once
        nfa.finalState.addTransition('ε', newEnd);    // Accepting transition
        nfa.finalState.addTransition('ε', nfa.startState);  // Loop back for repetition
        
        newEnd.isFinal = true;
        return new NFA(newStart, newEnd);
    }

    // ✅ **NEW: optional() (Matches once or skips)**
    public static NFA optional(NFA nfa) {
        State newStart = new State(stateCounter++);
        State newEnd = new State(stateCounter++);
        
        newStart.addTransition('ε', nfa.startState);  // Go to NFA
        newStart.addTransition('ε', newEnd);  // OR directly to accepting state (skip)
        
        nfa.finalState.addTransition('ε', newEnd);  // NFA final state connects to new end
        
        newEnd.isFinal = true;
        return new NFA(newStart, newEnd);
    }

    // ✅ **Improved Display (Sorted Output)**
    public void displayNFA() {
        System.out.println("\n=== NFA Transitions (Sorted) ===");
        Set<State> visited = new HashSet<>();
        Queue<State> queue = new LinkedList<>();
        
        queue.add(startState);
        visited.add(startState);

        List<State> allStates = new ArrayList<>();

        while (!queue.isEmpty()) {
            State current = queue.poll();
            allStates.add(current);

            for (List<State> stateList : current.transitions.values()) { 
                for (State nextState : stateList) {
                    if (!visited.contains(nextState)) {
                        visited.add(nextState);
                        queue.add(nextState);
                    }
                }
            }
        }

        // ✅ Sort states before printing their transitions
        allStates.sort(Comparator.comparingInt(s -> s.id));

        for (State state : allStates) {
            state.display();
        }
    }
}
