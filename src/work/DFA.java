package work;

import java.util.*;

class DFA {
    static class DFAState {
        int id;
        Map<Character, DFAState> transitions = new HashMap<>();
        boolean isFinal;
        String tokenType;

        DFAState(int id, boolean isFinal, String tokenType) {
            this.id = id;
            this.isFinal = isFinal;
            this.tokenType = tokenType;
        }
    }
    private DFAState startState;
    private final Map<Set<State>, DFAState> dfaStates = new HashMap<>();
    private int stateCounter = 0;

    public DFA(NFA nfa) {
        convertNFAtoDFA(nfa);
    }

    private void convertNFAtoDFA(NFA nfa) {
        Queue<Set<State>> queue = new LinkedList<>();
        Set<State> startSet = epsilonClosure(Set.of(nfa.startState));

        DFAState startDFAState = new DFAState(stateCounter++, isFinalState(startSet), getTokenType(startSet));
        dfaStates.put(startSet, startDFAState);
        queue.add(startSet);

        while (!queue.isEmpty()) {
            Set<State> currentSet = queue.poll();
            DFAState dfaState = dfaStates.get(currentSet);

            Map<Character, Set<State>> transitionMap = new HashMap<>();

            for (State nfaState : currentSet) {
                for (Map.Entry<Character, List<State>> entry : nfaState.transitions.entrySet()) {
                    if (entry.getKey() == 'ε') continue;
                    transitionMap.putIfAbsent(entry.getKey(), new HashSet<>());
                    transitionMap.get(entry.getKey()).addAll(epsilonClosure(new HashSet<>(entry.getValue())));
                }
            }

            for (Map.Entry<Character, Set<State>> entry : transitionMap.entrySet()) {
                if (!dfaStates.containsKey(entry.getValue())) {
                    DFAState newDFAState = new DFAState(stateCounter++, isFinalState(entry.getValue()), getTokenType(entry.getValue()));
                    dfaStates.put(entry.getValue(), newDFAState);
                    queue.add(entry.getValue());
                }
                dfaState.transitions.put(entry.getKey(), dfaStates.get(entry.getValue()));
            }
        }
        startState = dfaStates.get(startSet);
    }

    private Set<State> epsilonClosure(Set<State> states) {
        Stack<State> stack = new Stack<>();
        Set<State> closure = new HashSet<>(states);
        stack.addAll(states);

        while (!stack.isEmpty()) {
            State state = stack.pop();
            if (state.transitions.containsKey('ε')) {
                for (State nextState : state.transitions.get('ε')) {
                    if (!closure.contains(nextState)) {
                        closure.add(nextState);
                        stack.push(nextState);
                    }
                }
            }
        }
        return closure;
    }

    private boolean isFinalState(Set<State> states) {
        for (State state : states) {
            if (state.isFinal) return true;
        }
        return false;
    }

    private String getTokenType(Set<State> states) {
        for (State state : states) {
            if (state.isFinal) return state.tokenType;
        }
        return null;
    }

    public void printDFA() {
        System.out.println("\n DFA Transition Table (Sorted):");

        List<String> transitions = new ArrayList<>();
        for (DFAState state : dfaStates.values()) {
            for (Map.Entry<Character, DFAState> entry : state.transitions.entrySet()) {
                String transitionInfo = String.format(
                    "s%d  -- '%c' --> s%d%s", 
                    state.id, 
                    entry.getKey(), 
                    entry.getValue().id,
                    entry.getValue().isFinal ? " [Final State]":""
                );
                transitions.add(transitionInfo);
            }
        }
        for (DFAState state : dfaStates.values()) {
        	if (state.isFinal && state.transitions.isEmpty()) {
                transitions.add(String.format("s%d [Final]", state.id));
            }
        }

        Collections.sort(transitions, (a, b) -> {
            int stateA = Integer.parseInt(a.split(" ")[0].substring(1)); 
            int stateB = Integer.parseInt(b.split(" ")[0].substring(1));
            return Integer.compare(stateA, stateB);
        });

        for (String transition : transitions) {
            System.out.println(transition);
        }
    }

}