/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.memento;

import static org.locationtech.udig.project.memento.Tokens.__null__;
import static org.locationtech.udig.project.memento.Tokens._children_;
import static org.locationtech.udig.project.memento.Tokens._data_;
import static org.locationtech.udig.project.memento.Tokens._memento_;
import static org.locationtech.udig.project.memento.Tokens._text_;

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
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.ui.IMemento;

/**
 * Memento that uses a JSON like format to persist the data
 *
 * @author jesse
 */
public class UdigMemento implements IMemento {
    private final Map<String, List<IMemento>> mementoChildren = new HashMap<>();

    private Map<String, String> mementoData = new HashMap<>();

    private String mementoText;

    private String mementoType;

    @Override
    public UdigMemento createChild(String type) {
        UdigMemento child = new UdigMemento();
        child.mementoType = type;
        List<IMemento> list = getTypeList(type);
        list.add(child);
        return child;
    }

    private List<IMemento> getTypeList(String type) {
        List<IMemento> list = mementoChildren.get(type);
        if (list == null) {
            list = new ArrayList<>();
            mementoChildren.put(type, list);
        }
        return list;
    }

    @Override
    public IMemento createChild(String type, String id) {
        IMemento found = findChild(type, id);
        if (found == null) {
            found = createChild(type);
            found.putString(TAG_ID, id);
        }
        return found;
    }

    @Override
    public IMemento[] getChildren() {
        return new IMemento[0];
    }

    public IMemento findChild(String type, String id) {
        IMemento[] children = getChildren(type);
        IMemento found = null;
        for (IMemento memento : children) {
            if (id == null) {
                if (memento.getID() == null) {
                    found = memento;
                    break;
                }
            } else if (id.equals(memento.getID())) {
                found = memento;
                break;
            }
        }
        return found;
    }

    @Override
    public IMemento getChild(String type) {
        return getTypeList(type).get(0);
    }

    @Override
    public IMemento[] getChildren(String type) {
        return getTypeList(type).toArray(new IMemento[0]);
    }

    @Override
    public Float getFloat(String key) {
        try {
            return Float.valueOf(getString(key));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getID() {
        return getString(TAG_ID);
    }

    @Override
    public Integer getInteger(String key) {
        try {
            return Integer.valueOf(getString(key));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getString(String key) {
        return mementoData.get(key);
    }

    @Override
    public String getTextData() {
        return mementoText;
    }

    @Override
    public void putFloat(String key, float value) {
        mementoData.put(key, String.valueOf(value));
    }

    @Override
    public void putInteger(String key, int value) {
        mementoData.put(key, String.valueOf(value));
    }

    @Override
    public void putMemento(IMemento memento) {
        if (!(memento instanceof UdigMemento)) {
            throw new IllegalArgumentException(
                    memento.getClass() + " cannot be put into a UdigMemento"); //$NON-NLS-1$
        }

        UdigMemento other = (UdigMemento) memento;
        mementoChildren.putAll(other.mementoChildren);
        mementoData.putAll(other.mementoData);
    }

    @Override
    public void putString(String key, String value) {
        mementoData.put(key, value);
    }

    @Override
    public void putTextData(String data) {
        mementoText = data;
    }

    public void write(OutputStream outputStream, int indent) {
        PrintStream out = new PrintStream(outputStream);
        println(out, indent, _memento_ + "{"); //$NON-NLS-1$

        if (mementoText != null) {
            writeText(indent + 1, out);
        }
        if (!mementoData.isEmpty()) {
            writeData(indent + 1, out);
        }
        if (!mementoChildren.isEmpty()) {
            writeChildren(indent + 1, out);
        }

        println(out, indent, "}"); //$NON-NLS-1$
    }

    private void writeText(int indent, PrintStream out) {
        println(out, indent, _text_ + "{"); //$NON-NLS-1$
        println(out, 0, mementoText);
        println(out, indent, "}"); //$NON-NLS-1$
    }

    private void writeData(int indent, PrintStream out) {
        println(out, indent, _data_ + "{"); //$NON-NLS-1$

        Set<Entry<String, String>> dataItems = mementoData.entrySet();
        for (Entry<String, String> entry : dataItems) {
            final String value = toNullToken(entry.getValue());
            final String key = toNullToken(entry.getKey());
            println(out, indent + 1, "|" + key + "|{"); //$NON-NLS-1$ //$NON-NLS-2$
            println(out, 0, value);
            println(out, indent + 1, "}"); //$NON-NLS-1$
        }

        println(out, indent, "}"); //$NON-NLS-1$
    }

    private String toNullToken(String value) {
        if (value == null) {
            value = __null__.name();
        }
        return value;
    }

    private void writeChildren(int indent, PrintStream out) {
        println(out, indent, _children_ + "{"); //$NON-NLS-1$

        Set<Entry<String, List<IMemento>>> childEntries = mementoChildren.entrySet();
        for (Entry<String, List<IMemento>> entry2 : childEntries) {
            String typeName = toNullToken(entry2.getKey());
            println(out, indent + 1, typeName + "{"); //$NON-NLS-1$

            boolean isFirst = true;
            List<IMemento> mementos = entry2.getValue();
            for (IMemento memento : mementos) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    println(out, indent + 2, ","); //$NON-NLS-1$
                }
                ((UdigMemento) memento).write(out, indent + 2);
            }
            println(out, indent + 1, "}"); //$NON-NLS-1$
        }

        println(out, indent, "}"); //$NON-NLS-1$
    }

    private void println(PrintStream out, int indent, String string) {
        indent(out, indent);
        out.println(string);
    }

    private void indent(PrintStream out, int indent) {
        for (int i = 0; i < indent; i++) {
            out.print("  "); //$NON-NLS-1$
        }
    }

    public static UdigMemento read(InputStream inputStream) throws IOException {
        UdigMemento memento = new UdigMemento();

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        ParserState.OUT.parse(memento, in, null);

        return memento;
    }

    @Override
    public String toString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(out, 0);
        return new String(out.toByteArray());
    }

    /**
     * Reads the string obtained from toString and populates this memento.
     */
    public static UdigMemento readString(String mementoString) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(mementoString.getBytes());
        return read(in);
    }

    @Override
    public String[] getAttributeKeys() {
        return mementoData.keySet().toArray(new String[0]);
    }

    @Override
    public Boolean getBoolean(String key) {
        if (mementoData.containsKey(key)) {
            try {
                return Boolean.parseBoolean(mementoData.get(key));
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public String getType() {
        return mementoType;
    }

    @Override
    public void putBoolean(String key, boolean value) {
        mementoData.put(key, String.valueOf(value));
    }

}
