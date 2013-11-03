/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui.filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.geotools.filter.FunctionFinder;
import org.geotools.filter.text.generated.parsers.CQLParser;
import org.geotools.filter.text.generated.parsers.ECQLParser;
import org.opengis.filter.capability.FunctionName;
import org.opengis.parameter.Parameter;

import static org.geotools.data.Parameter.*;
/**
 * SimpleContentProposalProvider is a class designed to map a static list of Strings to content
 * proposals.
 * 
 * @see IContentProposalProvider
 * @since 3.2
 */
class FunctionContentProposalProvider implements IContentProposalProvider {

    protected static Set<String> proposals;

    protected static FunctionFinder functionFinder;
    static {
        proposals = new TreeSet<String>();
        functionFinder = new FunctionFinder(null);

        for (FunctionName function : functionFinder.getAllFunctionDescriptions()) {
            proposals.add(function.getName());
        }
    }

    /*
     * The proposals mapped to IContentProposal. Cached for speed in the case where filtering is not
     * used.
     */
    private Set<String> extras;

    private Set<String> grammer;
    /**
     * Construct a SimpleContentProposalProvider whose content proposals are always the specified
     * array of Objects.
     * 
     * @param proposals the array of Strings to be returned whenever proposals are requested.
     */
    public FunctionContentProposalProvider() {
        grammer = generateGrammer(true);
    }
    /**
     * Construct a SimpleContentProposalProvider whose content proposals are always the specified
     * array of Objects.
     * 
     * @param proposals the array of Strings to be returned whenever proposals are requested.
     */
    public FunctionContentProposalProvider(boolean includeFilterTokens) {
        grammer = generateGrammer(includeFilterTokens);
    }
    
    private Set<String> generateGrammer(boolean includeFilter) {
        Set<String> generate = new HashSet<String>();
        for( String tokenImage : CQLParser.tokenImage ){
            if( tokenImage.startsWith("\"")){
                String token = tokenImage.substring(1,tokenImage.length()-1);
                if( token.isEmpty() ) continue;
                if( Character.isLetter( token.charAt(0) ) ){
                    if( includeFilter ) {
                        generate.add(token.toUpperCase());
                    }
                }
                else {
                    generate.add(token);
                }
            }
        }
        return generate;
    }

    /**
     * Return an array of Objects representing the valid content proposals for a field.
     * 
     * @param contents the current contents of the field (only consulted if filtering is set to
     *        <code>true</code>)
     * @param position the current cursor position within the field used to select a word
     * @return the array of Objects that represent valid proposals for the field given its current
     *         content.
     */
    public IContentProposal[] getProposals(String contents, int position) {
        String word = contents.substring(0, position);
        int start = word.lastIndexOf(" ", position);
        if (start != -1) {
            word = contents.substring(start, position);
        }
        word = word.trim();
        int prefixLength = word.length();
        if (word.length() == 0) {
            return new IContentProposal[0];
        }

        ArrayList<IContentProposal> list = new ArrayList<IContentProposal>();
        if (extras != null) {
            for (String extra : extras) {
                if (extra.length() > word.length()
                        && extra.substring(0, word.length()).equals(word)) {
                    list.add(makeContentProposal(extra, prefixLength));
                }
            }
        }
        for (String proposal : proposals) {
            if (proposal.length() > word.length()
                    && proposal.substring(0, word.length()).equals(word)) {
                IContentProposal contentProposal = makeContentProposal(proposal, prefixLength);
                list.add(contentProposal);
            }
        }
        for(String token : grammer ){
            if (token.length() > word.length()
                    && token.substring(0, word.length()).equals(word)) {
                IContentProposal contentProposal = makeContentProposal(token, prefixLength);
                list.add(contentProposal);
            }
        }
        return (IContentProposal[]) list.toArray(new IContentProposal[list.size()]);
    }

    /*
     * Make an IContentProposal for showing the specified String.
     */
    private IContentProposal makeContentProposal(final String proposal, final int prefixLength) {
        return new IContentProposal() {
            public String getContent() {
                if (prefixLength < proposal.length()) {
                    return proposal.substring(prefixLength);
                } else {
                    return proposal;
                }
            }

            public String getDescription() {
                FunctionName description = functionFinder.findFunctionDescription(proposal);
                if (description != null) {
                    StringBuilder builder = new StringBuilder();

                    builder.append(description.getName());
                    String seperator = null;
                    if (description.getArguments() != null && !description.getArguments().isEmpty()) {
                        for (Parameter<?> param : description.getArguments()) {
                            if (seperator == null) {
                                builder.append("(");
                                seperator = ",";
                            } else {
                                builder.append(seperator);
                            }
                            builder.append(param.getName());
                        }

                        builder.append(")\nWhere:\n");
                        for (Parameter<?> param : description.getArguments()) {
                            builder.append("  ");
                            describeParameter(builder, param);
                            builder.append("\n");
                        }
                    }
                    if (description.getReturn() != null) {
                        builder.append("Result:\n");
                        Parameter<?> param = description.getReturn();
                        
                        builder.append(" ");
                        describeParameter(builder, param);
                        builder.append("\n");
                    }
                    return builder.toString();
                }
                return null;
            }

            private void describeParameter(StringBuilder builder, Parameter<?> param) {
                builder.append(param.getName());
                builder.append(" ");
                if( param.getType() != Object.class ){
                    builder.append( param.getType().getSimpleName() );
                    builder.append(" ");
                }
                
                builder.append(": ");
                if (param.isRequired()) {
                    builder.append("Required ");
                }
                if ( param.getMinOccurs() == 1 && param.getMaxOccurs() == 1){
                    // ignore
                }
                else if ( param.getMinOccurs() == 0 && param.getMaxOccurs() == 1){
                    builder.append("Optional ");
                }
                else {
                    builder.append("(");
                    builder.append(param.getMinOccurs());
                    builder.append("-");
                    if (param.getMaxOccurs() < 0 || param.getMaxOccurs() == Integer.MAX_VALUE ) {
                        builder.append("unbound");
                    } else {
                        builder.append(param.getMaxOccurs());
                    }
                    builder.append(") ");
                }
                if (param.getDescription() != null) {
                    builder.append(param.getDescription());
                    builder.append(" ");
                }
                
                if( param instanceof org.geotools.data.Parameter){
                    // advanced tips!
                    org.geotools.data.Parameter<?> parameter = (org.geotools.data.Parameter<?>) param;
                    if( parameter.metadata.containsKey( OPTIONS )){
                        builder.append( " Options: ");
                        builder.append( parameter.metadata.get( OPTIONS ));
                        builder.append(" ");
                    }
                    if( parameter.metadata.containsKey( LENGTH )){
                        builder.append( " Length: ");
                        builder.append( parameter.metadata.get( LENGTH ));
                        builder.append(" ");
                    }
                    if( parameter.metadata.containsKey( MIN )){
                        builder.append( " Min: ");
                        builder.append( parameter.metadata.get( MIN ));
                        builder.append(" ");
                    }
                    if( parameter.metadata.containsKey( MAX )){
                        builder.append( " Max: ");
                        builder.append( parameter.metadata.get( MAX ));
                        builder.append(" ");
                    }
                }
            }

            public String getLabel() {
                return proposal;
            }

            public int getCursorPosition() {
                return proposal.length() - prefixLength;
            }
        };
    }

    public void setExtra(Set<String> names) {
        this.extras = names;
        // this.contentProposals = null;
    }
}
