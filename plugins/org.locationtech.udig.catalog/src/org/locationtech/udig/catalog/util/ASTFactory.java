/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2005, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.util;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

/**
 * Used by catalog to implement search.
 *
 * @author David Zwiers, Refractions Research
 */
public class ASTFactory {
    private ASTFactory() {/* not used */
    }

    /**
     * Creates an AST for the pattern The pattern uses the following conventions: use " " to
     * surround a phase use + to represent 'AND' use - to represent 'OR' use ! to represent 'NOT'
     * use ( ) to designate scope
     *
     * @param pattern Search pattern
     * @return AST
     */
    public static AST parse(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        List<String> tokens = tokenize(str);
        Stack<AST> s = new Stack<>();
        ListIterator<String> li = tokens.listIterator();

        // create the ast
        while (li.hasNext())
            node(s, li);
        if (s.size() == 1) {
            return s.pop();
        }
        if (s.size() > 1) {
            // assume they are anded ... TODO balance the tree
            while (s.size() > 1) {
                AST p1 = s.pop();
                AST p2 = s.pop();
                s.push(new And(p1, p2));
            }
            return s.pop();
        }
        System.err.println("An internal error creating an AST may have occured"); //$NON-NLS-1$
        return null;
    }

    protected static List<String> tokenize(String pattern) {
        List<String> l = new LinkedList<>();
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            switch (c) {
            case '(':
                l.add("("); //$NON-NLS-1$
                break;
            case ')':
                l.add(")"); //$NON-NLS-1$
                break;
            case '+':
                l.add("+"); //$NON-NLS-1$
                break;
            case '-':
                l.add("-"); //$NON-NLS-1$
                break;
            case '!':
                l.add("!"); //$NON-NLS-1$
                break;
            case '"':
                // greedy grab
                int j = pattern.indexOf('"', i + 1);
                l.add(pattern.substring(i + 1, j));
                i = j;
                break;
            case ' ':
            case '\t':
            case '\n':
                // skip
                break;
            case 'A':// ND
                if (pattern.charAt(i + 1) == 'N' && pattern.charAt(i + 2) == 'D') {
                    // it's a +
                    l.add("+"); //$NON-NLS-1$
                    i += 2;
                }
                break;
            case 'O':// R
                if (pattern.charAt(i + 1) == 'R') {
                    // it's a +
                    l.add("-"); //$NON-NLS-1$
                    i += 1;
                }
                break;
            case 'N':// OT
                if (pattern.charAt(i + 1) == 'O' && pattern.charAt(i + 2) == 'T') {
                    // it's a +
                    l.add("!"); //$NON-NLS-1$
                    i += 2;
                }
                break;
            default:
                // greedy grab
                j = i + 1;
                while (j < pattern.length() && pattern.charAt(j) != '"' && pattern.charAt(j) != '+'
                        && pattern.charAt(j) != '-' && pattern.charAt(j) != '!'
                        && pattern.charAt(j) != '(' && pattern.charAt(j) != ')'
                        && pattern.charAt(j) != ' ' && pattern.charAt(j) != '\t'
                        && pattern.charAt(j) != '\n')
                    j++;
                l.add(pattern.substring(i, j));
                i = (i == j ? j : j - 1);
                break;
            }
        }
        return l;
    }

    private static void node(Stack<AST> s, ListIterator<String> li) {
        if (li.hasNext()) {
            String token = li.next();

            char c = token.charAt(0);
            switch (c) {
            case '(':
                // child is what we want
                node(s, li);
                break;
            case ')':
                // ignore this
                break;
            case '+':
                AST prev = s.pop();
                node(s, li);
                AST next = s.pop();
                s.push(new And(prev, next));
                break;
            case '-':
                prev = s.pop();
                node(s, li);
                next = s.pop();
                s.push(new Or(prev, next));
                break;
            case '!':
                node(s, li);
                next = s.pop();
                s.push(new Not(next));
                break;
            default:
                s.push(new Literal(token));
                break;
            }
        }
    }

    private static class And implements AST {
        private AST left, right;

        @SuppressWarnings("unused")
        private And() {/* should not be used */
        }

        public And(AST left, AST right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean accept(String datum) {
            if (datum == null)
                return false;
            return left != null && right != null && left.accept(datum) && right.accept(datum);
        }

        @Override
        public int type() {
            return AND;
        }

        @Override
        public AST getLeft() {
            return left;
        }

        @Override
        public AST getRight() {
            return right;
        }
    }

    private static class Or implements AST {
        private AST left, right;

        @SuppressWarnings("unused")
        private Or() {/* should not be used */
        }

        public Or(AST left, AST right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean accept(String datum) {
            if (datum == null)
                return false;
            return (right != null && right.accept(datum)) || (left != null && left.accept(datum));
        }

        @Override
        public int type() {
            return OR;
        }

        @Override
        public AST getLeft() {
            return left;
        }

        @Override
        public AST getRight() {
            return right;
        }
    }

    private static class Not implements AST {
        private AST child;

        @SuppressWarnings("unused")
        private Not() {/* should not be used */
        }

        public Not(AST child) {
            this.child = child;
        }

        @Override
        public boolean accept(String datum) {
            if (datum == null)
                return false;
            return !(child != null && child.accept(datum));
        }

        @Override
        public int type() {
            return NOT;
        }

        @Override
        public AST getLeft() {
            return child;
        }

        @Override
        public AST getRight() {
            return null;
        }
    }

    private static class Literal implements AST {
        private String value;

        @SuppressWarnings("unused")
        private Literal() {/* should not be used */
        }

        public Literal(String value) {
            this.value = value;
        }

        @Override
        public boolean accept(String datum) {
            return value != null && datum != null
                    && datum.toUpperCase().indexOf(value.toUpperCase()) > -1;
        }

        @Override
        public int type() {
            return LITERAL;
        }

        @Override
        public AST getLeft() {
            return null;
        }

        @Override
        public AST getRight() {
            return null;
        }

        @Override
        public String toString() {
            return value;
        }
    }

}
