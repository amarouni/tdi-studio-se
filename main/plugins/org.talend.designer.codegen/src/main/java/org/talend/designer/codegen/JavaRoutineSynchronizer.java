// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Perl5Substitution;
import org.apache.oro.text.regex.Util;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.SystemException;
import org.talend.commons.utils.VersionUtils;
import org.talend.commons.utils.generation.JavaUtils;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.CorePlugin;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.IService;
import org.talend.core.model.general.ILibrariesService;
import org.talend.core.model.process.JobInfo;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.PigudfItem;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.properties.RoutineItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.utils.JavaResourcesHelper;
import org.talend.core.runtime.process.ITalendProcessJavaProject;
import org.talend.core.ui.ITestContainerProviderService;
import org.talend.core.ui.branding.IBrandingService;
import org.talend.designer.runprocess.IRunProcessService;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.RepositoryNodeUtilities;

/**
 * Routine synchronizer of java project.
 * 
 * yzhang class global comment. Detailled comment <br/>
 * 
 * $Id: JavaRoutineSynchronizer.java JavaRoutineSynchronizer 2007-2-2 下午03:29:12 +0000 (下午03:29:12, 2007-2-2 2007)
 * yzhang $
 * 
 */
public class JavaRoutineSynchronizer extends AbstractRoutineSynchronizer {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.codegen.IRoutineSynchronizer#syncAllRoutines()
     */
    @Override
    public void syncAllRoutines() throws SystemException {
        syncRoutineItems(getRoutines(), false);
    }

    @Override
    public void syncAllRoutinesForLogOn() throws SystemException {
        syncRoutineItems(getRoutines(), true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.codegen.AbstractRoutineSynchronizer#syncAllPigudf()
     */
    @Override
    public void syncAllPigudf() throws SystemException {
        syncRoutineItems(getAllPigudf(), false);
    }

    @Override
    public void syncAllPigudfForLogOn() throws SystemException {
        syncRoutineItems(getAllPigudf(), true);
    }

    private void syncRoutineItems(List<IRepositoryViewObject> routineObjects, boolean forceUpdate) throws SystemException {
        for (IRepositoryViewObject routine : routineObjects) {
            RoutineItem routineItem = (RoutineItem) routine.getProperty().getItem();
            syncRoutine(routineItem, true, forceUpdate);
        }

        try {
            ILibrariesService jms = CorePlugin.getDefault().getLibrariesService();
            List<URL> urls = jms.getTalendRoutinesFolder();

            for (URL systemModuleURL : urls) {
                if (systemModuleURL != null) {
                    String fileName = systemModuleURL.getPath();
                    if (fileName.startsWith("/")) { //$NON-NLS-1$
                        fileName = fileName.substring(1);
                    }
                    File f = new File(systemModuleURL.getPath());
                    if (f.isDirectory()) {
                        syncModule(f.listFiles());
                    }
                }
            }
        } catch (IOException e) {
            // e.printStackTrace();
            ExceptionHandler.process(e);
        }

    }

    @Override
    public void syncAllBeans() throws SystemException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.codegen.IRoutineSynchronizer#syncRoutine(org.talend .core.model.properties.RoutineItem)
     */
    @Override
    protected void doSyncRoutine(RoutineItem routineItem, boolean copyToTemp) throws SystemException {
        FileOutputStream fos = null;
        try {
            IFile file = getRoutineFile(routineItem);
            if (routineItem.getProperty().getModificationDate() != null) {
                long modificationItemDate = routineItem.getProperty().getModificationDate().getTime();
                long modificationFileDate = file.getModificationStamp();
                if (modificationItemDate <= modificationFileDate) {
                    return;
                }
            } else {
                routineItem.getProperty().setModificationDate(new Date());
            }

            if (copyToTemp) {
                String routineContent = new String(routineItem.getContent().getInnerContent());
                // see 14713
                String version = VersionUtils.getVersion();
                if (routineContent.contains("%GENERATED_LICENSE%")) { //$NON-NLS-1$
                    IService service = GlobalServiceRegister.getDefault().getService(IBrandingService.class);
                    // if (service instanceof AbstractBrandingService) {
                    String routineHeader = ((IBrandingService) service).getRoutineLicenseHeader(version);
                    routineContent = routineContent.replace("%GENERATED_LICENSE%", routineHeader); //$NON-NLS-1$
                    // }
                }// end
                String label = routineItem.getProperty().getLabel();
                if (!label.equals(ITalendSynchronizer.TEMPLATE) && routineContent != null) {
                    routineContent = routineContent.replaceAll(ITalendSynchronizer.TEMPLATE, label);
                    // routineContent = renameRoutinePackage(routineItem,
                    // routineContent);
                    File f = file.getLocation().toFile();
                    fos = new FileOutputStream(f);
                    fos.write(routineContent.getBytes());
                    fos.close();
                }
            }
            file.refreshLocal(1, null);
        } catch (CoreException e) {
            throw new SystemException(e);
        } catch (IOException e) {
            throw new SystemException(e);
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                // ignore me even if i'm null
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.codegen.IRoutineSynchronizer#syncRoutine(org.talend .core.model.properties.RoutineItem)
     */
    @Override
    protected void doSyncBean(Item beanItem, boolean copyToTemp) throws SystemException {

    }

    private IFile getRoutineFile(RoutineItem routineItem) throws SystemException {
        IRunProcessService service = CodeGeneratorActivator.getDefault().getRunProcessService();
        ITalendProcessJavaProject talendProcessJavaProject = service.getTalendProcessJavaProject();
        if (talendProcessJavaProject == null) {
            return null;
        }
        IFolder routineFolder = talendProcessJavaProject.getSrcSubFolder(null, routineItem.getPackageType());
        IFile file = routineFolder.getFile(routineItem.getProperty().getLabel() + JavaUtils.JAVA_EXTENSION);
        return file;
    }

    @Override
    public IFile getProcessFile(JobInfo jobInfo) throws SystemException {
        String projectFolderName = jobInfo.getProjectFolderName();
        String jobName = jobInfo.getJobName();
        String folderName = JavaResourcesHelper.getJobFolderName(jobName, jobInfo.getJobVersion());
        return getProcessFile(projectFolderName, folderName, jobName);
    }

    private IFile getProcessFile(ProcessItem item) throws SystemException {
        String projectFolderName = JavaResourcesHelper.getProjectFolderName(item);
        String jobName = item.getProperty().getLabel();
        String folderName = JavaResourcesHelper.getJobFolderName(jobName, item.getProperty().getVersion());
        return getProcessFile(projectFolderName, folderName, jobName);
    }

    private IFile getTestContainerFile(ProcessItem item) throws SystemException {
        String projectFolderName = JavaResourcesHelper.getProjectFolderName(item);
        String jobName = item.getProperty().getLabel();
        String folderName = JavaResourcesHelper.getJobFolderName(jobName, item.getProperty().getVersion());
        return getTestContainerFile(item, projectFolderName, folderName, jobName);
    }

    private IFile getProcessFile(String projectFolderName, String folderName, String jobName) {
        IRunProcessService service = CodeGeneratorActivator.getDefault().getRunProcessService();
        ITalendProcessJavaProject talendProcessJavaProject = service.getTalendProcessJavaProject();
        if (talendProcessJavaProject == null) {
            return null;
        }
        IFolder srcFolder = talendProcessJavaProject.getSrcFolder();
        IFile file = srcFolder.getFile(projectFolderName + '/' + folderName + '/' + jobName + JavaUtils.JAVA_EXTENSION);
        return file;
    }

    private IFile getTestContainerFile(ProcessItem item, String projectFolderName, String folderName, String jobName) {
        IRunProcessService service = CodeGeneratorActivator.getDefault().getRunProcessService();
        ITalendProcessJavaProject talendProcessJavaProject = service.getTalendProcessJavaProject();
        if (talendProcessJavaProject == null) {
            return null;
        }
        IFolder srcFolder = talendProcessJavaProject.getTestSrcFolder();
        String packageName = JavaResourcesHelper.getJobClassPackageFolder(item, true);
        IFile file = srcFolder.getFile(packageName + '/' + jobName + "Test" + JavaUtils.JAVA_EXTENSION);
        return file;
    }

    public void copyFile(File in, IFile out) throws Exception {
        if (out.exists()) {
            out.delete(true, null);
        }
        FileInputStream fis = new FileInputStream(in);
        if (!out.exists()) {
            out.create(fis, true, null);
        }
        fis.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.codegen.IRoutineSynchronizer#syncRoutine(org.talend .core.model.properties.RoutineItem)
     */
    public IFile syncModule(File[] modules) throws SystemException {
        return syncModules(modules, "");
    }

    private IFile syncModules(File[] modules, String directory) throws SystemException {
        try {
            IRunProcessService service = CodeGeneratorActivator.getDefault().getRunProcessService();
            ITalendProcessJavaProject talendProcessJavaProject = service.getTalendProcessJavaProject();
            if (talendProcessJavaProject == null) {
                return null;
            }
            IFolder systemFolder = talendProcessJavaProject.getSrcSubFolder(null, JavaUtils.JAVA_ROUTINES_DIRECTORY + '/'
                    + JavaUtils.JAVA_SYSTEM_DIRECTORY + '/' + directory);

            for (File module : modules) {
                if (!module.isDirectory()) {
                    IFile file = systemFolder.getFile(module.getName());

                    copyFile(module, file);
                } else if (!module.getName().startsWith(".") && !FilesUtils.isSVNFolder(module.getName())) {
                    syncModules(module.listFiles(), directory + module.getName() + '/');
                }
            }
        } catch (CoreException e) {
            throw new SystemException(e);
        } catch (FileNotFoundException e) {
            throw new SystemException(e);
        } catch (IOException e) {
            throw new SystemException(e);
        } catch (Exception e) {
            throw new SystemException(e);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.codegen.ITalendSynchronizer#getFile(org.talend.core .model.properties.Item)
     */
    @Override
    public IFile getFile(Item item) throws SystemException {
        boolean isTestContainer = false;
        if (GlobalServiceRegister.getDefault().isServiceRegistered(ITestContainerProviderService.class)) {
            ITestContainerProviderService testContainerService = (ITestContainerProviderService) GlobalServiceRegister
                    .getDefault().getService(ITestContainerProviderService.class);
            if (testContainerService != null) {
                isTestContainer = testContainerService.isTestContainerItem(item);
            }
        }
        if (isTestContainer) {
            return getTestContainerFile((ProcessItem) item);
        } else if (item instanceof RoutineItem) {
            return getRoutineFile((RoutineItem) item);
        } else if (item instanceof ProcessItem) {
            return getProcessFile((ProcessItem) item);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * qli modified to fix the bug 5400 and 6185.
     * 
     * @seeorg.talend.designer.codegen.AbstractRoutineSynchronizer#renameRoutineClass(org.talend.core.model.properties.
     * RoutineItem, java.lang.String)
     */
    @Override
    public void renameRoutineClass(RoutineItem routineItem) {
        if (routineItem == null) {
            return;
        }
        String routineContent = new String(routineItem.getContent().getInnerContent());
        String label = routineItem.getProperty().getLabel();
        //
        String regexp = "public(\\s)+class(\\s)+\\w+(\\s)+\\{";//$NON-NLS-1$
        routineContent = routineContent.replaceFirst(regexp, "public class " + label + " {");//$NON-NLS-1$//$NON-NLS-2$
        routineItem.getContent().setInnerContent(routineContent.getBytes());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.codegen.AbstractRoutineSynchronizer#renamePigudfClass(org.talend.core.model.properties.
     * RoutineItem)
     */
    @Override
    public void renamePigudfClass(PigudfItem pigudfItem, String oldLabel) {
        if (pigudfItem == null) {
            return;
        }
        String routineContent = new String(pigudfItem.getContent().getInnerContent());
        String label = pigudfItem.getProperty().getLabel();
        //
        Perl5Matcher matcher = new Perl5Matcher();

        Perl5Compiler compiler = new Perl5Compiler();
        PatternMatcherInput patternMatcherInput = new PatternMatcherInput(routineContent);
        String regx = "public(\\s)+class(\\s)+" + oldLabel + "(\\s)(.+)\\{";//$NON-NLS-1$//$NON-NLS-2$
        String extendsText = "";
        try {
            org.apache.oro.text.regex.Pattern pattern = compiler.compile(regx);
            boolean contains = matcher.contains(patternMatcherInput, pattern);
            if (contains) {
                org.apache.oro.text.regex.MatchResult matchResult = matcher.getMatch();
                extendsText = matchResult.group(matchResult.groups() - 1);

            }

            String regexp = "public(\\s)+class(\\s)+\\w+(\\s)\\{";//$NON-NLS-1$
            if (extendsText != null) {
                extendsText = extendsText.trim();
                regexp = "public(\\s)+class(\\s)+\\w+(\\s)+" + extendsText + "(\\s)*\\{";//$NON-NLS-1$//$NON-NLS-2$
            }

            // rename class name
            routineContent = routineContent.replaceFirst(regexp, "public class " + label + " " + extendsText + " {");//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

            // rename constructor
            String constructorRegx = "(\\s+)" + oldLabel + "(\\((.*)\\))";
            String toReplace = "$1" + label + "$1$2";
            pattern = compiler.compile(constructorRegx);
            Perl5Substitution substitution = new Perl5Substitution(toReplace, Perl5Substitution.INTERPOLATE_ALL);
            routineContent = Util.substitute(matcher, pattern, substitution, routineContent, Util.SUBSTITUTE_ALL);
        } catch (MalformedPatternException e) {
            ExceptionHandler.process(new Exception("Rename pigudf failed"));
        }

        pigudfItem.getContent().setInnerContent(routineContent.getBytes());

    }

    /*
     * (non-Javadoc)
     * 
     * qli modified to fix the bug 5400 and 6185.
     * 
     * @seeorg.talend.designer.codegen.AbstractRoutineSynchronizer#renameRoutineClass(org.talend.core.model.properties.
     * RoutineItem, java.lang.String)
     */
    @Override
    public void renameBeanClass(Item beanItem) {

    }

    @Override
    public void deleteRoutinefile(IRepositoryViewObject objToDelete) {
        Item item = objToDelete.getProperty().getItem();
        try {
            IRunProcessService service = CodeGeneratorActivator.getDefault().getRunProcessService();
            ITalendProcessJavaProject talendProcessJavaProject = service.getTalendProcessJavaProject();
            if (talendProcessJavaProject == null) {
                return;
            }
            IFolder srcFolder = talendProcessJavaProject.getSrcFolder();
            IFile file = srcFolder.getFile(((RoutineItem) item).getPackageType() + '/' + objToDelete.getLabel()
                    + JavaUtils.JAVA_EXTENSION);
            file.delete(true, null);
        } catch (CoreException e) {
            ExceptionHandler.process(e);
        }
    }

    @Override
    public void deleteBeanfile(IRepositoryViewObject objToDelete) {
        try {
            IRunProcessService service = CodeGeneratorActivator.getDefault().getRunProcessService();
            ITalendProcessJavaProject talendProcessJavaProject = service.getTalendProcessJavaProject();
            if (talendProcessJavaProject == null) {
                return;
            }
            IFolder srcFolder = talendProcessJavaProject.getSrcFolder();
            IFile file = srcFolder.getFile(JavaUtils.JAVA_BEANS_DIRECTORY + '/' + objToDelete.getLabel()
                    + JavaUtils.JAVA_EXTENSION);
            file.delete(true, null);
        } catch (CoreException e) {
            org.talend.commons.exception.ExceptionHandler.process(e);
        }
    }

    @Override
    public IFile getRoutinesFile(Item item) throws SystemException {
        try {
            if (item instanceof RoutineItem) {
                RoutineItem routineItem = (RoutineItem) item;
                ProjectManager projectManager = ProjectManager.getInstance();
                org.talend.core.model.properties.Project project = projectManager.getProject(routineItem);
                IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getTechnicalLabel());
                String repositoryPath = ERepositoryObjectType.getFolderName(ERepositoryObjectType.ROUTINES);
                if (item instanceof PigudfItem) {
                    repositoryPath = ERepositoryObjectType.getFolderName(ERepositoryObjectType.PIG_UDF);
                }
                String folderPath = RepositoryNodeUtilities.getPath(routineItem.getProperty().getId()).toString();
                String fileName = routineItem.getProperty().getLabel() + "_" + routineItem.getProperty().getVersion()
                        + JavaUtils.ITEM_EXTENSION;
                String path = null;
                if (folderPath != null && !folderPath.trim().equals("")) {
                    path = repositoryPath + "/" + folderPath + "/" + fileName;
                } else {
                    path = repositoryPath + "/" + fileName;
                }

                IFile file = iProject.getFile(path);
                return file;
            }
        } catch (Exception e) {
            throw new SystemException(e);
        }
        return null;
    }
}
