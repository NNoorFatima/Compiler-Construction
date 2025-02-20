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

    public static NFA createSimpleNFA(char symbol,String type) {
        State start = new State(stateCounter++,type);
        State end = new State(stateCounter++,type);
        start.addTransition(symbol, end);
        end.isFinal = true;
        return new NFA(start, end);
    }

    public static NFA union(NFA... nfas) {
        if (nfas.length == 0) 
        	return null; 

        State newStart = new State(stateCounter++);
        State newEnd = new State(stateCounter++);

        for (NFA nfa : nfas) {
            newStart.addTransition('ε', nfa.startState);
            nfa.finalState.addTransition('ε', newEnd);
            nfa.finalState.isFinal = false;
        }
        
        newEnd.isFinal= true;
        NFA result = new NFA(newStart, newEnd);
        result.finalState = newEnd;
        return result;
    }

    public static NFA concatenate(NFA nfa1, NFA nfa2) {
        nfa1.finalState.addTransition('ε', nfa2.startState);
        nfa1.finalState.isFinal = false; 
        NFA result = new NFA(nfa1.startState, nfa2.finalState);
        result.finalState = nfa2.finalState;
        return result;
    }

    public static NFA kleeneStar(NFA nfa) {
        State newStart = new State(stateCounter++);
        State newEnd = new State(stateCounter++);

        newStart.addTransition('ε', nfa.startState);
        newStart.addTransition('ε', newEnd);
        nfa.finalState.addTransition('ε', nfa.startState);
        nfa.finalState.addTransition('ε', newEnd);
        nfa.finalState.isFinal = false;
        newEnd.isFinal = true;
        return new NFA(newStart, newEnd);
    }

    public static NFA kleenePlus(NFA nfa) {
        State newStart = new State(stateCounter++);
        State newEnd = new State(stateCounter++);

        newStart.addTransition('ε', nfa.startState);  
        nfa.finalState.addTransition('ε', newEnd);  
        nfa.finalState.addTransition('ε', nfa.startState);  
        nfa.finalState.isFinal = false;
        newEnd.isFinal = true;
        return new NFA(newStart, newEnd);
    }

    public static NFA optional(NFA nfa) {
        State newStart = new State(stateCounter++);
        State newEnd = new State(stateCounter++);
        
        newStart.addTransition('ε', nfa.startState); 
        newStart.addTransition('ε', newEnd);  
        
        nfa.finalState.addTransition('ε', newEnd);  
        nfa.finalState.isFinal = false;
        newEnd.isFinal = true;
        return new NFA(newStart, newEnd);
    }

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

        allStates.sort(Comparator.comparingInt(s -> s.id));

        for (State state : allStates) {
            state.display();
        }
    }
}
