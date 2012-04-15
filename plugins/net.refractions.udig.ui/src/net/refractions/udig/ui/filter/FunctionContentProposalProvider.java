package net.refractions.udig.ui.filter;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.geotools.filter.FunctionFinder;
import org.opengis.filter.capability.FunctionName;

/**
 * SimpleContentProposalProvider is a class designed to map a static list of Strings to content
 * proposals.
 * 
 * @see IContentProposalProvider
 * @since 3.2
 */
@SuppressWarnings("deprecation")
class FunctionContentProposalProvider implements IContentProposalProvider {

    public static Set<String> proposals;
    static {
        proposals = new TreeSet<String>();
        FunctionFinder functionFinder = new FunctionFinder(null);

        for (FunctionName function : functionFinder.getAllFunctionDescriptions()) {
            proposals.add(function.getName().toLowerCase());
        }
    }

    /*
     * The proposals mapped to IContentProposal. Cached for speed in the case where filtering is not
     * used.
     */
    private IContentProposal[] contentProposals;

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
                        && extra.substring(0, word.length()).equalsIgnoreCase(word)) {
                    list.add(makeContentProposal(extra, prefixLength));
                }
            }
        }
        for (String proposal : proposals) {
            if (proposal.length() >= word.length()
                    && proposal.substring(0, word.length()).equalsIgnoreCase(word)) {
                list.add(makeContentProposal(proposal, prefixLength));
            }
        }
        return (IContentProposal[]) list.toArray(new IContentProposal[list.size()]);
    }

    /*
     * Make an IContentProposal for showing the specified String.
     */
    private IContentProposal makeContentProposal(final String proposal,final int prefixLength) {
        return new IContentProposal() {
            public String getContent() {
                if( prefixLength < proposal.length() ){
                    return proposal.substring(prefixLength);
                }
                else {
                    return proposal;
                }
            }

            public String getDescription() {
                return null;
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
        this.contentProposals = null;
    }
}