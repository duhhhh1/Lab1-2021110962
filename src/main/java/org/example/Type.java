package org.example;

import java.util.HashMap;
import java.util.Map;

public class Type {
    String word;
    Map<Type, Integer> outgoingEdges;

    Type(String word) {
        this.word = word;
        outgoingEdges = new HashMap<>();
    }
}