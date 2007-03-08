// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006 Talend - www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.repository.ui.views;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.image.ImageProvider;
import org.talend.core.model.properties.DocumentationItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryObject;
import org.talend.core.ui.images.CoreImageProvider;
import org.talend.core.ui.images.ECoreImage;
import org.talend.core.ui.images.OverlayImageProvider;
import org.talend.repository.model.ERepositoryStatus;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.ProxyRepositoryFactory;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.RepositoryNode.ENodeType;

/**
 * Label provider for the repository view. <code>DEBUG</code> boolean field specify if details (such as objects ids)
 * must appears on display or not.<br/>
 * 
 * $Id$
 * 
 */
public class RepositoryLabelProvider extends LabelProvider implements IColorProvider, IFontProvider {

    private static final Color ACTIVE_REPOSITORY_ENTRY = new Color(null, 100, 100, 100);

    private static final Color INACTIVE_REPOSITORY_ENTRY = new Color(null, 200, 200, 200);

    private static final Color LOCKED_REPOSITORY_ENTRY = new Color(null, 200, 0, 0);

    private IRepositoryView view;

    public RepositoryLabelProvider(IRepositoryView view) {
        super();
        this.view = view;
    }

    public String getText(Object obj) {
        RepositoryNode node = (RepositoryNode) obj;

        if (node.getType() == ENodeType.REPOSITORY_ELEMENT || node.getType() == ENodeType.SIMPLE_FOLDER) {
            IRepositoryObject object = node.getObject();
            if (object == null) {
                return node.getLabel();
            }
            StringBuffer string = new StringBuffer();
            string.append(object.getLabel());
            // PTODO SML [FOLDERS++] temp code
            if (object.getType() != ERepositoryObjectType.FOLDER) {
                string.append(" " + object.getVersion()); //$NON-NLS-1$
            }

            IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
            try {
                if (factory.getStatus(object) == ERepositoryStatus.DELETED) {
                    string.append(" (" + factory.getOldPath(object) + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                }
            } catch (PersistenceException e) {
                ExceptionHandler.process(e);
            }

            // PTODO SML [FOLDERS++] temp code
            // if (object.getType() != ERepositoryObjectType.FOLDER) {
            // string.append(" [" + factory.getStatus(object.getProperty().getItem()) + "]");
            // }
            return string.toString();
        } else {
            return node.getLabel();
        }
    }

    public Image getImage(Object obj) {
        RepositoryNode node = (RepositoryNode) obj;

        switch (node.getType()) {
        case STABLE_SYSTEM_FOLDER:
        case SYSTEM_FOLDER:
            return ImageProvider.getImage(node.getIcon());
        case SIMPLE_FOLDER:
            // FIXME SML Move in repository node
            ECoreImage image = (view.getExpandedState(obj) ? ECoreImage.FOLDER_OPEN_ICON : ECoreImage.FOLDER_CLOSE_ICON);
            return ImageProvider.getImage(image);
        default:
            if (node.getObject() == null) {
                return ImageProvider.getImage(node.getIcon());
            }
            Image img = CoreImageProvider.getImage(node.getObject().getType());

            // Manage doc extensions:
            if (node.getObject().getType() == ERepositoryObjectType.DOCUMENTATION) {
                DocumentationItem item = (DocumentationItem) node.getObject().getProperty().getItem();
                img = OverlayImageProvider.getImageWithDocExt(item.getExtension());
            }

            // Manage master job case:
            if (node.getObject().getType() == ERepositoryObjectType.PROCESS && node.getObject().getLabel().equals("Tagada")) {
                img = OverlayImageProvider.getImageWithSpecial(img).createImage();
            }

            IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
            ERepositoryStatus repositoryStatus = factory.getStatus(node.getObject());

            return OverlayImageProvider.getImageWithStatus(img, repositoryStatus).createImage();

            // if (node.getObject() != null && node.getObject().getType() == ERepositoryObjectType.METADATA_CON_TABLE) {
            //
            // if (node.getObject() instanceof MetadataTable) {
            // System.out.println("node instanceof MetadataTable.");
            // }
            // MetadataTable metadataTable = (MetadataTable) node.getObject();
            // String tableType = metadataTable.getType()();
            // if (tableType!=null && tableType.equalsIgnoreCase("VIEW")) {
            // return CoreImageProvider.getImage(ERepositoryObjectType.METADATA_CON_VIEW);
            // }
            // if (tableType!=null && tableType.equalsIgnoreCase("TABLE")) {
            // return CoreImageProvider.getImage(ERepositoryObjectType.METADATA_CON_TABLE);
            // }
            // }

            // Gets different icons for corresponding table type(view, table, synonym),added by ftang.
            // If want to display different icons for different table type, uncomment code below.
            // if (node.getObject() instanceof MetadataTableRepositoryObject) {
            // String tableType = ((MetadataTableRepositoryObject) node.getObject()).getTableType();
            // if(tableType == null)
            // {
            // return CoreImageProvider.getImage(node.getObject().getType());
            // }
            // ERepositoryObjectType tableImage;
            // if (tableType.equals("VIEW")) {
            // tableImage = ERepositoryObjectType.METADATA_CON_VIEW;
            // } else if (tableType.equals("TABLE")) {
            // tableImage = ERepositoryObjectType.METADATA_CON_TABLE;
            //
            // } else if (tableType.equals("SYNONYM")) {
            // tableImage = ERepositoryObjectType.METADATA_CON_SYNONYM;
            //
            // } else {
            // tableImage = ERepositoryObjectType.METADATA_CON_TABLE;
            // }
            // return CoreImageProvider.getImage(tableImage);
            // }
            // Ends

            // return BusinessImageProvider.getImage(node.getObject().getType(), repositoryStatus);
        }
    }

    public Color getBackground(Object element) {
        return null;
    }

    public Color getForeground(Object element) {
        RepositoryNode node = (RepositoryNode) element;
        switch (node.getType()) {
        case STABLE_SYSTEM_FOLDER:
            if (node.getLabel().equals(ERepositoryObjectType.SNIPPETS.toString())) {
                return INACTIVE_REPOSITORY_ENTRY;
            }
        case SYSTEM_FOLDER:
            return ACTIVE_REPOSITORY_ENTRY;
        default:
            IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
            ERepositoryStatus repositoryStatus = factory.getStatus(node.getObject());
            if (repositoryStatus == ERepositoryStatus.LOCK_BY_OTHER) {
                return LOCKED_REPOSITORY_ENTRY;
            } else {
                return null;
            }
        }
    }

    public Font getFont(Object element) {
        RepositoryNode node = (RepositoryNode) element;
        switch (node.getType()) {
        case STABLE_SYSTEM_FOLDER:
            if (node.getLabel().equals(ERepositoryObjectType.SNIPPETS.toString())) {
                return JFaceResources.getFontRegistry().defaultFont();
            }
        case SYSTEM_FOLDER:
            return JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
        default:
            return JFaceResources.getFontRegistry().defaultFont();
        }
    }
}
