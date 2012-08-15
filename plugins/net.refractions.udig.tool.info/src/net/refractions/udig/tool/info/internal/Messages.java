/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.tool.info.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    
    private static final String BUNDLE_NAME = "net.refractions.udig.tool.info.internal.messages"; //$NON-NLS-1$

    public static String AbstractAttachmentDocument_errSaveAsNotSupported;

    public static String DistanceTool_distance;

    public static String DistanceTool_error;

    public static String DocumentPropertyPage_description;

    public static String DocumentPropertyPage_title;

    public static String DocumentPropertyPage_Action;

    public static String Document_Action_Column;

    public static String Document_Add;

    public static String Document_Attachment_Enable;

    public static String Document_Attachment_Section;

    public static String Document_Attribute_Column;

    public static String DocumentPropertyPage_Attribute;

    public static String Document_Attributes;

    public static String Document_Edit;

    public static String DocumentPropertyPage_Hotlink;

    public static String Document_Hotlink_Column;

    public static String Document_Hotlink_Enable;

    public static String Document_Hotlink_Section;

    public static String DocumentPropertyPage_Label;

    public static String Document_Label_Column;

    public static String DocumentPropertyPage_Open;

    public static String DocumentPropertyPage_header;

    public static String Document_Remove;

    public static String DocumentDialog_actionLabel;

    public static String DocumentDialog_addAttachHeader;

    public static String DocumentDialog_attachSubHeader;

    public static String DocumentDialog_attachSubHeaderFeature;

    public static String DocumentDialog_attachSubHeaderShapefile;

    public static String DocumentDialog_attributeLabel;

    public static String DocumentDialog_createFileFromTemplateError;

    public static String DocumentDialog_createFileFromTemplateSuccess;

    public static String DocumentDialog_createFileFromTemplateTitle;

    public static String DocumentDialog_descriptionLabel;

    public static String DocumentDialog_documentLabel;

    public static String DocumentDialog_editAttachHeader;

    public static String DocumentDialog_enterFilenameErr;

    public static String DocumentDialog_enterFilenameMsg;

    public static String DocumentDialog_enterFilenameTitle;

    public static String DocumentDialog_errValidFile;

    public static String DocumentDialog_errValidURL;

    public static String DocumentDialog_fileBtn;

    public static String DocumentDialog_fileLabel;

    public static String DocumentDialog_goBtn;

    public static String DocumentDialog_hotlinkHeader;

    public static String DocumentDialog_labelLabel;

    public static String DocumentDialog_newBtn;

    public static String DocumentDialog_openBtn;

    public static String DocumentDialog_selectTemplateMsg;

    public static String DocumentDialog_selectTemplateTitle;

    public static String DocumentDialog_templateLabel;

    public static String DocumentDialog_typeLabel;

    public static String DocumentDialog_urlLabel;

    public static String DocumentDialog_valueLabel;

    public static String DocumentPropertyPage_errActionIsBlank;

    public static String DocumentPropertyPage_errExists;

    public static String DocumentView_openActionDialogMessage;

    public static String DocumentView_openActionDialogTitle;

    public static String DocumentView_saveAsDialogTitle;

    public static String DocumentView_saveAsErrorDialogMsg;

    public static String DocumentView_saveAsErrorDialogTitle;

    public static String DocumentView_saveAsSuccessDialogMsg;

    public static String DocumentView_saveAsSuccessDialogTitle;

    public static String InfoView_instructions_text;

    public static String InfoView2_information_request;

    public static String LayerPointInfo_toString;

    public static String docView_add;

    public static String docView_attachFile;

    public static String docView_attachFiles;

    public static String docView_delete;

    public static String docView_deleteAttachConfirmMsg;

    public static String docView_deleteAttachConfirmTitle;

    public static String docView_descriptionColumn;

    public static String docView_typeColumn;

    public static String docView_errEmpty;

    public static String docView_errFileExistMulti;

    public static String docView_errFileExistSingle;

    public static String docView_errInvalidURL;

    public static String docView_errURLExist;

    public static String docView_edit;

    public static String docView_linkDialogHeader;

    public static String docView_linkDialogTitle;

    public static String docView_linkURL;

    public static String docView_name;

    public static String docView_documentColumn;

    public static String docView_open;

    public static String docView_openDialogTitle;

    public static String docView_remove;

    public static String docView_saveAs;

    public static String InfoPropertyPage_labelExpression;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
