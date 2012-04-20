package net.refractions.udig.ui.filter;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.geotools.filter.FunctionFinder;
import org.opengis.filter.capability.FunctionName;
import org.opengis.parameter.Parameter;

/**
 * SimpleContentProposalProvider is a class designed to map a static list of Strings to content
 * proposals.
 * 
 * @see IContentProposalProvider
 * @since 3.2
 */
@SuppressWarnings("deprecation")
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
    // private IContentProposal[] contentProposals;

    private Set<String> extras;

    /**
     * Construct a SimpleContentProposalProvider whose content proposals are always the specified
     * array of Objects.
     * 
     * @param proposals the array of Strings to be returned whenever proposals are requested.
     */
    public FunctionContentProposalProvider() {
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
                if (extra.length() >= word.length()
                        && extra.substring(0, word.length()).equals(word)) {
                    list.add(makeContentProposal(extra, prefixLength));
                }
            }
        }
        for (String proposal : proposals) {
            if (proposal.length() >= word.length()
                    && proposal.substring(0, word.length()).equals(word)) {
                IContentProposal contentProposal = makeContentProposal(proposal, prefixLength);
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
                    if( parameter.metadata.containsKey( parameter.OPTIONS )){
                        builder.append( " Options: ");
                        builder.append( parameter.metadata.get( parameter.OPTIONS ));
                        builder.append(" ");
                    }
                    if( parameter.metadata.containsKey( parameter.LENGTH )){
                        builder.append( " Length: ");
                        builder.append( parameter.metadata.get( parameter.LENGTH ));
                        builder.append(" ");
                    }
                    if( parameter.metadata.containsKey( parameter.MIN )){
                        builder.append( " Min: ");
                        builder.append( parameter.metadata.get( parameter.MIN ));
                        builder.append(" ");
                    }
                    if( parameter.metadata.containsKey( parameter.MAX )){
                        builder.append( " Max: ");
                        builder.append( parameter.metadata.get( parameter.MAX ));
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