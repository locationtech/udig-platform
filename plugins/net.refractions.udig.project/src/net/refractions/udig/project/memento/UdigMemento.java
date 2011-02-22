/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.memento;

import static net.refractions.udig.project.memento.Tokens.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.ui.IMemento;

/**
 * Memento that uses a JSon like format to persist the data
 *
 * @author jesse
 */
public class UdigMemento implements IMemento {
    private final Map<String, List<IMemento>> m_children = new HashMap<String, List<IMemento>>();
    private Map<String, String> m_data = new HashMap<String, String>();
    private String m_text;

    public UdigMemento createChild( String type ) {
        UdigMemento child = new UdigMemento();
        List<IMemento> list = getTypeList(type);
        list.add(child);
        return child;
    }

    private List<IMemento> getTypeList( String type ) {
        List<IMemento> list = m_children.get(type);
        if (list == null) {
            list = new ArrayList<IMemento>();
            m_children.put(type, list);
        }
        return list;
    }

    public IMemento createChild( String type, String id ) {
        IMemento found = findChild(type, id);
        if (found == null) {
            found = createChild(type);
            found.putString(TAG_ID, id);
        }
        return found;
    }

    public IMemento findChild( String type, String id ) {
        IMemento[] children = getChildren(type);
        IMemento found = null;
        for( IMemento memento : children ) {
            if( id==null){
                if(memento.getID()==null){
                    found = memento;
                    break;
                }
            }else if (id.equals(memento.getID())) {
                found = memento;
                break;
            }
        }
        return found;
    }

    public IMemento getChild( String type ) {
        return getTypeList(type).get(0);
    }

    public IMemento[] getChildren( String type ) {
        return getTypeList(type).toArray(new IMemento[0]);
    }

    public Float getFloat( String key ) {
        try {
            return Float.valueOf(getString(key));
        } catch (Exception e) {
            return null;
        }
    }

    public String getID() {
        return getString(TAG_ID);
    }

    public Integer getInteger( String key ) {
        try {
            return Integer.valueOf(getString(key));
        } catch (Exception e) {
            return null;
        }
    }

    public String getString( String key ) {
        return m_data.get(key);
    }

    public String getTextData() {
        return m_text;
    }

    public void putFloat( String key, float value ) {
        m_data.put(key, String.valueOf(value));
    }

    public void putInteger( String key, int value ) {
        m_data.put(key, String.valueOf(value));
    }

    public void putMemento( IMemento memento ) {
        if( !(memento instanceof UdigMemento) ){
            throw new IllegalArgumentException(memento.getClass()+" cannot be put into a UdigMemento");
        }

        UdigMemento other = (UdigMemento) memento;
        m_children.putAll(other.m_children);
        m_data.putAll(other.m_data);
    }

    public void putString( String key, String value ) {
        m_data.put(key, value);
    }

    public void putTextData( String data ) {
        m_text = data;
    }

    public void write( OutputStream outputStream, int indent ) {
        PrintStream out = new PrintStream(outputStream);
        println(out, indent, _memento_ +"{");

        if( m_text!=null){
            writeText(indent+1,out);
        }
        if( m_data.size()>0){
            writeData(indent+1, out);
        }
        if( m_children.size()>0){
            writeChildren(indent+1, out);
        }

        println(out, indent, "}");
    }

    private void writeText( int indent, PrintStream out ) {
        println(out,indent,_text_+"{");
        println(out,0,m_text);
        println(out,indent,"}");
    }

    private void writeData( int indent, PrintStream out ) {
        println(out, indent, _data_ +"{");

        Set<Entry<String, String>> dataItems = m_data.entrySet();
        for( Entry<String, String> entry : dataItems ) {
            final String value = toNullToken(entry.getValue());
            final String key = toNullToken(entry.getKey());
            println(out, indent + 1, "|"+key + "|{");
            println(out,0,value);
            println(out,indent+1,"}");
        }

        println(out, indent, "}");
    }

    private String toNullToken( String value ) {
        if(value==null){
            value=__null__.name();
        }
        return value;
    }

    private void writeChildren( int indent, PrintStream out ) {
        println(out, indent, _children_+"{");

        Set<Entry<String, List<IMemento>>> childEntries = m_children.entrySet();
        for( Entry<String, List<IMemento>> entry2 : childEntries ) {
            String typeName = toNullToken(entry2.getKey());
            println(out, indent + 1, typeName + "{");

            boolean isFirst = true;
            List<IMemento> mementos = entry2.getValue();
            for( IMemento memento : mementos ) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    println(out, indent + 2, ",");
                }
                ((UdigMemento) memento).write(out, indent + 2);
            }
            println(out, indent+1, "}");
        }

        println(out, indent, "}");
    }

    private void println( PrintStream out, int indent, String string ) {
        indent(out, indent);
        out.println(string);
    }

    private void indent( PrintStream out, int indent ) {
        for( int i = 0; i < indent; i++ ) {
            out.print("  ");
        }
    }

    public static UdigMemento read( InputStream inputStream ) throws IOException {
        UdigMemento memento = new UdigMemento();

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        ParserState.OUT.parse(memento, in, null);

        return memento;
    }

    @Override
    public String toString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(out,0);
        return new String(out.toByteArray());
    }

    /**
     * Reads the string obtained from toString and populates this memento.
     */
    public static UdigMemento readString(String mementoString) throws IOException{
        ByteArrayInputStream in = new ByteArrayInputStream(mementoString.getBytes());
        return read(in);
    }


}
