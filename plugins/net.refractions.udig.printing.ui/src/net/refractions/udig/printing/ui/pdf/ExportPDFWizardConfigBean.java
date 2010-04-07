package net.refractions.udig.printing.ui.pdf;

/**
 * 
 * This bean is used to pass configuration information to the ExportPDFWizard. 
 * <p>
 *
 * </p>
 * @author brocka
 * @since 1.1.0
 */
public class ExportPDFWizardConfigBean {

    public static final String BLACKBOARD_KEY = "ExportPDFWizardConfiBean.key"; //$NON-NLS-1$
    
    private String defaultFilename;

    public ExportPDFWizardConfigBean() {
        this.defaultFilename = null;
    }
    
    public String getDefaultFilename() {
        return defaultFilename;
    }

    public void setDefaultFilename( String defaultFilename ) {
        this.defaultFilename = defaultFilename;
    }
    
    
}
