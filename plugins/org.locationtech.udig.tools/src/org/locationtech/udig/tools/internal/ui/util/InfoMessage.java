/*******************************************************************************
 * Copyright (c) 2006,2012,2013 County Council of Gipuzkoa, Department of Environment
 *                              and Planning and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Aritz Davila (Axios) - initial API, implementation, and documentation
 *    Mauricio Pazos (Axios) - initial API, implementation, and documentation
 *******************************************************************************/
package org.locationtech.udig.tools.internal.ui.util;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;

/**
 * Human Message
 * <p>
 * This class maintain the user text attributes, used by widgets to show information to user
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.1.0
 */
public final class InfoMessage {

    /**
     * Type of Message
     * <p>
     * The following sentences define a criteria to use this type:
     * <ul>
     * <li>INFORMATION, IMPORTANT_INFO : Message to give an indications to user</li>
     * <li>WARNING: The value of parameter could produce a no desired result, but the operation can
     * be executed</li>
     * <li>ERROR: The introduced value is not valid for this operation.</li>
     * <li>FAIL The operation can not be executed correctly (typically an exceptions occurred).</li>
     * <li>NULL: internal use</li>
     * </ul>
     * </p>
     */
    public enum Type {
        /** Message to give an indications to user */
        INFORMATION,
        /** Message to give an indications to user */
        IMPORTANT_INFO,
        /**
         * The value of parameter could produce a no desired result, but the operation can be
         * executed
         */
        WARNING,
        /** The introduced value is not valid for this operation */
        ERROR,
        /**
         * The operation can not be executed correctly (typically an exceptions occurred).
         */
        FAIL,
        /** internal use */
        NULL
    };

    public static final InfoMessage NULL = new InfoMessage("", Type.NULL); //$NON-NLS-1$

    private String text;

    private Type type;

    public InfoMessage() {

    }

    public InfoMessage(final String text, final Type type) {
        assert text != null;
        assert type != null;

        this.text = text;
        this.type = type;
    }

    public InfoMessage(final String text, final int type) {

        assert text != null;

        this.text = text;

        switch (type) {
        case IMessageProvider.INFORMATION:
            this.type = Type.INFORMATION;
            break;
        case IMessageProvider.ERROR:
            this.type = Type.ERROR;
            break;
        case IMessageProvider.NONE:
            this.type = Type.NULL;
            break;
        case IMessageProvider.WARNING:
            this.type = Type.WARNING;
            break;
        }

    }

    public void setText(final String text) {
        assert text != null;

        this.text = text;
    }

    public void setType(final Type type) {
        assert type != null;

        this.type = type;
    }

    @Override
    public String toString() {
        return this.text;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        InfoMessage other = (InfoMessage) obj;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    /**
     * @return Returns the text.
     */
    public final String getText() {
        return this.text;
    }

    /**
     * @return Returns the type.
     */
    public final Type getType() {
        return this.type;
    }

    /**
     * @return Returns the image
     */
    public final Image getImage() {

        Image image = null;

        switch (type) {
        case FAIL:
            image = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
            break;
        case INFORMATION:
        case IMPORTANT_INFO:
            image = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO);
            break;
        case WARNING:
            image = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);
            break;
        case ERROR:
            image = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
            break;

        case NULL:
            image = null;
            break;

        default:
            assert false; // impossible!
            break;
        }

        return image;
    }

}
