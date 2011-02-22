/*
 * Created on 8-Jan-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.refractions.udig.catalog.util;

/**
 * Use the visitor pattern to traverse the AST
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public interface AST {
    public boolean accept( String datum );
    public int type();

    public static final int AND = 1;
    public static final int OR = 2;
    public static final int NOT = 4;
    public static final int LITERAL = 0;

    /** may be null */
    public AST getLeft();

    /** may be null */
    public AST getRight();
}
