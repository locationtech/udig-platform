/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues;

import java.util.Iterator;
import java.util.List;

import org.locationtech.udig.core.IFixer;
import org.locationtech.udig.core.enums.Resolution;
import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.issues.internal.IssuesActivator;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.XMLMemento;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * Base implementation of IIssue which persists a fixerMemento (for use with an IssueFixer).
 * <p>
 * </p>
 *
 * @author chorner
 * @since 1.1.0
 */
public abstract class AbstractFixableIssue extends AbstractIssue {

    public static final String ATT_ID = "id"; //$NON-NLS-1$
    public static final String ATT_CLASS = "class"; //$NON-NLS-1$
    public static final String ATT_TARGET = "targetClass"; //$NON-NLS-1$
    public static final String ATT_KEY = "key"; //$NON-NLS-1$
    public static final String ATT_REQKEY = "requiredKey"; //$NON-NLS-1$

    public static final String KEY_FIXERMEMENTO = "fixerMemento"; //$NON-NLS-1$
    public static final String XPID_ISSUEFIXER = "org.locationtech.udig.issues.issueFixer"; //$NON-NLS-1$

    IMemento fixerMemento = null;

    @Override
    public void fixIssue( IViewPart part, IEditorPart editor ) {
        IFixer fixer = findIssueFixer(fixerMemento);
        if (fixer == null) {
            return;
        }
        setResolution(Resolution.IN_PROGRESS);
        fixer.fix(this, fixerMemento);
    }

    /**
     * @param fixerMemento
     * @return IssueFixer
     */
    protected IFixer findIssueFixer( IMemento fixerMemento ) {
        List<IConfigurationElement> extensionPointList = ExtensionPointList
                .getExtensionPointList(XPID_ISSUEFIXER);
        Iterator<IConfigurationElement> it = extensionPointList.iterator();
        while( it.hasNext() ) {
            IConfigurationElement element = it.next();
            boolean isValid = true;
            //check the required keys
            IConfigurationElement[] requiredKeys = element.getChildren(ATT_REQKEY);
            for (IConfigurationElement key : requiredKeys) {
                String expectedKey = key.getAttribute(ATT_KEY);
                IMemento value = fixerMemento.getChild(expectedKey);
                String value2 = fixerMemento.getString(expectedKey);
                Integer value3 = fixerMemento.getInteger(expectedKey);
                if (value == null && value2 == null && value3 == null) { //failure!
                    isValid = false;
                    break;
                }
            }
            if (isValid) { //check the target class
                String targetClass = element.getAttribute(ATT_TARGET);
                //first ensure that this class name and target name are not identical
                if (targetClass != null && this.getClass().getCanonicalName() != targetClass) {
                    //next instantiate the class and do an instanceof check
                    try {
                        Class clazz = Class.forName(targetClass);
                        if (!this.getClass().isAssignableFrom(clazz)) {
                            isValid = false;
                        }
                    } catch (ClassNotFoundException e) {
                        //can't instantiate
                        isValid = false;
                        LoggingSupport.log(IssuesActivator.getDefault(), "couldn't create class " + targetClass, e); //$NON-NLS-1$
                    }
                }
            }
            if (isValid) {
                //extension point
                IFixer fixer = null;
                try {
                    fixer = (IFixer) element.createExecutableExtension(ATT_CLASS);
                } catch (Exception e) {
                    LoggingSupport.log(IssuesActivator.getDefault(), "Could not instantiate IssueFixer extension", e); //$NON-NLS-1$
                }
                if (fixer != null && fixer.canFix(this, fixerMemento)) {
                    return fixer;
                }
            }
        }
        return null;
    }

    @Override
    public void init( IMemento memento, IMemento viewMemento, String issueId, String groupId,
            ReferencedEnvelope bounds ) {
        setViewMemento(viewMemento);
        setId(issueId);
        setGroupId(groupId);
        setBounds(bounds);
        if (memento == null) {
            fixerMemento = XMLMemento.createWriteRoot(KEY_FIXERMEMENTO);
        } else {
            fixerMemento = memento.getChild(KEY_FIXERMEMENTO);
        }
    }

    /**
     * Subclasses should override and call super.save().
     */
    @Override
    public void save( IMemento memento ) {
        memento.putMemento(fixerMemento);
    }

    /**
     * Obtains the fixer memento, which contains issue state and initialization data for the
     * IssueFixer.
     *
     * @return fixerMemento
     */
    public IMemento getFixerMemento() {
        return fixerMemento;
    }

    /**
     * Overwrites the current fixerMemento with new issue state data.
     *
     * @param fixerMemento
     */
    public void setFixerMemento(IMemento fixerMemento) {
        this.fixerMemento = fixerMemento;
    }

}
