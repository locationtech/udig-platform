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

        for( FunctionName function : functionFinder.getAllFunctionDescriptions() ) {
            proposals.add(function.getName().toLowerCase());
        }
    }

    /*
     * The proposals mapped to IContentProposal. Cached for speed in the case where filtering is not
     * used.
     */
    private IContentProposal[] contentProposals;

    /*
     * Boolean that tracks whether filtering is used.
     */
    private boolean filterProposals = false;

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
    public IContentProposal[] getProposals( String contents, int position ) {
        String word = contents.substring(0, position);
        int start = contents.lastIndexOf(" ", position);
        if (start != -1) {
            word = contents.substring(start, position);
        }
        word = word.trim();
        if (word.length() == 0) {
            return new IContentProposal[0];
        }

        if (filterProposals) {
            ArrayList<IContentProposal> list = new ArrayList<IContentProposal>();
            if (extras != null) {
                for( String extra : extras ) {
                    if (extra.length() >= word.length()
                            && extra.substring(0, word.length()).equalsIgnoreCase(word)) {
                        list.add(makeContentProposal(extra));
                    }
                }
            }
            for( String proposal : proposals ) {
                if (proposal.length() >= word.length()
                        && proposal.substring(0, word.length()).equalsIgnoreCase(word)) {
                    list.add(makeContentProposal(proposal));
                }
            }
            return (IContentProposal[]) list.toArray(new IContentProposal[list.size()]);
        } else {
            if (contentProposals == null) {
                final int LENGTH = proposals.size() + (extras == null ? 0 : extras.size());

                contentProposals = new IContentProposal[LENGTH];
                int i = 0;
                if (extras != null && !extras.isEmpty()) {
                    for( String extra : extras ) {
                        contentProposals[i] = makeContentProposal(extra);
                        i++;
                    }
                }
                for( String proposal : proposals ) {
                    contentProposals[i] = makeContentProposal(proposal);
                    i++;
                }
            }
            return contentProposals;
        }
    }
    /**
     * Set the boolean that controls whether proposals are filtered according to the current field
     * content.
     * 
     * @param filterProposals <code>true</code> if the proposals should be filtered to show only
     *        those that match the current contents of the field, and <code>false</code> if the
     *        proposals should remain the same, ignoring the field content.
     * @since 3.3
     */
    public void setFiltering( boolean filterProposals ) {
        this.filterProposals = filterProposals;
        // Clear any cached proposals.
        contentProposals = null;
    }

    /*
     * Make an IContentProposal for showing the specified String.
     */
    private IContentProposal makeContentProposal( final String proposal ) {
        return new IContentProposal(){
            public String getContent() {
                return proposal;
            }

            public String getDescription() {
                return null;
            }

            public String getLabel() {
                return null;
            }

            public int getCursorPosition() {
                return proposal.length();
            }
        };
    }

    public void setExtra( Set<String> names ) {
        this.extras = names;
        this.contentProposals = null;
    }
}