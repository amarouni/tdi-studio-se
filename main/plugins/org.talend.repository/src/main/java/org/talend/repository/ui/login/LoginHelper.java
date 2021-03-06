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
package org.talend.repository.ui.login;

import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.CommonExceptionHandler;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.OperationCancelException;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.exception.SystemException;
import org.talend.commons.exception.WarningException;
import org.talend.commons.ui.gmf.util.DisplayUtils;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.commons.utils.PasswordHelper;
import org.talend.core.CorePlugin;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.PluginChecker;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.general.ConnectionBean;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.ExchangeUser;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.User;
import org.talend.core.model.repository.SVNConstant;
import org.talend.core.model.utils.TalendPropertiesUtil;
import org.talend.core.prefs.ITalendCorePrefConstants;
import org.talend.core.prefs.PreferenceManipulator;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.RepositoryFactoryProvider;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.core.service.IExchangeService;
import org.talend.core.services.ICoreTisService;
import org.talend.core.services.ISVNProviderService;
import org.talend.core.ui.branding.IBrandingService;
import org.talend.registration.wizards.register.TalendForgeDialog;
import org.talend.repository.ProjectManager;
import org.talend.repository.i18n.Messages;
import org.talend.repository.model.RepositoryConstants;
import org.talend.repository.ui.login.connections.ConnectionUserPerReader;

/**
 * created by cmeng on May 22, 2015 Detailled comment
 *
 */
public class LoginHelper {

    protected static final String PREFERENCE_TALEND_LOGON_STARTUP_TIMES = "PREFERENCE_TALEND_LOGON_STARTUP_TIMES"; //$NON-NLS-1$

    protected static final String PREFERENCE_TALEND_FORCE_SHOW_LOGON_DIALOG = "PREFERENCE_TALEND_FORCE_SHOW_LOGON_DIALOG"; //$NON-NLS-1$

    protected static LoginHelper instance;

    protected static final String LOCAL = "local"; //$NON-NLS-1$

    protected String lastWarnings;

    protected ConnectionUserPerReader perReader = null;

    protected List<ConnectionBean> storedConnections = null;

    protected String lastConnection = null;

    protected ConnectionBean firstConnBean;

    protected Shell usableShell;

    protected ISVNProviderService svnProviderService;

    protected PreferenceManipulator prefManipulator;

    public static boolean isRestart;

    public static LoginHelper getInstance() {
        if (instance == null) {
            instance = new LoginHelper();
        }
        return instance;
    }

    protected LoginHelper() {
        init();
    }

    /**
     * Since it is only used in login action usually, so needn't exist all the time, can destroy it after used
     */
    public static void destroy() {
        instance = null;
    }

    protected void init() {
        if (PluginChecker.isSVNProviderPluginLoaded()) {
            try {
                svnProviderService = (ISVNProviderService) GlobalServiceRegister.getDefault().getService(
                        ISVNProviderService.class);
            } catch (RuntimeException e) {
                // nothing to do
            }
        }
        prefManipulator = new PreferenceManipulator(CorePlugin.getDefault().getPreferenceStore());
        perReader = ConnectionUserPerReader.getInstance();
        readConnectionData();
        recordFirstConnection();
    }

    protected void readConnectionData() {
        if (perReader.isHaveUserPer()) {
            storedConnections = perReader.readConnections();
            lastConnection = perReader.readLastConncetion();
        } else {
            storedConnections = prefManipulator.readConnections();
            lastConnection = prefManipulator.getLastConnection();
        }
    }

    protected void recordFirstConnection() {
        if (storedConnections != null) {
            for (ConnectionBean connectionBean : storedConnections) {
                if (connectionBean.getName().equals(lastConnection)) {
                    firstConnBean = connectionBean;
                    break;
                }
            }
        }
    }

    public static boolean isRemoteConnection(ConnectionBean connectionBean) {
        if (connectionBean == null) {
            return false;
        }
        return RepositoryConstants.REPOSITORY_REMOTE_ID.equals(connectionBean.getRepositoryId());
    }

    public void saveConnections() {
        saveConnections(storedConnections);
    }

    public void saveConnections(List<ConnectionBean> connectionsBeans) {
        perReader.saveConnections(connectionsBeans);
    }

    public void saveLastConnectionBean(ConnectionBean connBean) {
        perReader.saveLastConnectionBean(connBean);
    }

    protected static ConnectionBean getConnection() {
        return LoginHelper.getInstance().getFirstConnBean();
    }

    protected static boolean needRestartForLocal(ConnectionBean curConnection) {
        if (curConnection != null && LoginHelper.getInstance().getFirstConnBean() != null) {
            // only switch from other connection to local.
            if (!LoginHelper.getInstance().getFirstConnBean().getRepositoryId().equals(LOCAL)
                    && curConnection.getRepositoryId().equals(LOCAL)) {
                return true;
            }
        }
        return false;
    }

    protected static void saveLastConnBean(ConnectionBean connectionBean) {
        LoginHelper.getInstance().saveLastConnectionBean(connectionBean);
    }

    /**
     * Store the current selected project&branch etc into context
     */
    public static void setRepositoryContextInContext(ConnectionBean connBean, User user, Project project, String branch) {
        Context ctx = CoreRuntimePlugin.getInstance().getContext();
        RepositoryContext repositoryContext = new RepositoryContext();
        ctx.putProperty(Context.REPOSITORY_CONTEXT_KEY, repositoryContext);

        repositoryContext.setUser(user);
        repositoryContext.setProject(project);
        String password = ""; //$NON-NLS-1$
        if (connBean != null) {
            repositoryContext.setFields(connBean.getDynamicFields());
            password = connBean.getPassword();
        }
        repositoryContext.setClearPassword(password);
        if (project != null) {
            ProjectManager.getInstance().setMainProjectBranch(project, branch);
        }
    }

    public static boolean isStudioNeedUpdate(ConnectionBean connBean) throws SystemException {
        ICoreTisService tisService = (ICoreTisService) GlobalServiceRegister.getDefault().getService(ICoreTisService.class);
        if (tisService != null) {
            return tisService.needUpdate(connBean.getUser(), connBean.getPassword(), getAdminURL(connBean));
        } else {
            return false;
        }
    }

    public static String getAdminURL(ConnectionBean currentBean) {
        String tacURL = null;
        if (currentBean != null && currentBean.getRepositoryId().equals(RepositoryConstants.REPOSITORY_REMOTE_ID)) {
            tacURL = currentBean.getDynamicFields().get(RepositoryConstants.REPOSITORY_URL);
        }
        return tacURL;
    }

    public static User getUser(ConnectionBean connBean) {
        User toReturn = PropertiesFactory.eINSTANCE.createUser();
        String user = ""; //$NON-NLS-1$
        String password = ""; //$NON-NLS-1$
        if (connBean != null) {
            user = connBean.getUser();
            password = connBean.getPassword();
        }
        toReturn.setLogin(user);
        // if (PluginChecker.isTIS()) {
        try {
            toReturn.setPassword(PasswordHelper.encryptPasswd(password));
        } catch (NoSuchAlgorithmException e) {
            // e.printStackTrace();
            CommonExceptionHandler.process(e);
        }
        // }
        return toReturn;
    }

    public boolean loginAuto() {
        ConnectionBean connBean = getConnection();
        User user = getUser(connBean);

        // must have this init, used to retreive projects
        setRepositoryContextInContext(connBean, user, null, null);

        boolean isRemoteConnection = LoginHelper.isRemoteConnection(connBean);
        if (isRemoteConnection) {
            boolean isStudioNeedUpdate = false;
            try {
                isStudioNeedUpdate = isStudioNeedUpdate(connBean);
            } catch (SystemException e) {
                isStudioNeedUpdate = false;
            }
            if (isStudioNeedUpdate) {
                return false;
            }
        }
        Project lastUsedProject = getLastUsedProject(getProjects(connBean));
        if (lastUsedProject == null) {
            return false;
        }
        String lastUsedBranch = prefManipulator.getLastSVNBranch();
        if (isRemoteConnection) {
            List<String> branches = getProjectBranches(lastUsedProject);
            if (branches == null || branches.isEmpty()) {
                return false;
            }
            if (!branches.contains(lastUsedBranch)) {
                MessageDialog.openError(getUsableShell(),
                        Messages.getString("LoginHelper.errorTitle"), Messages.getString("LoginHelper.branchChanged")); //$NON-NLS-1$ //$NON-NLS-2$
                return false;
            }
        } else {
            if (lastUsedBranch == null || lastUsedBranch.trim().isEmpty()) {
                lastUsedBranch = null;
            }
        }
        setRepositoryContextInContext(connBean, user, lastUsedProject, lastUsedBranch);
        return logIn(connBean, lastUsedProject);
    }

    public boolean logIn(ConnectionBean connBean, final Project project) {
        final ProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        final boolean needRestartForLocal = needRestartForLocal(connBean);
        if (connBean == null || project == null || project.getLabel() == null) {
            return false;
        }
        try {
            if (!project.getEmfProject().isLocal() && factory.isLocalConnectionProvider()) {
                List<IRepositoryFactory> rfList = RepositoryFactoryProvider.getAvailableRepositories();
                IRepositoryFactory remoteFactory = null;
                for (IRepositoryFactory rf : rfList) {
                    if (!rf.isLocalConnectionProvider()) {
                        remoteFactory = rf;
                        break;
                    }
                }
                if (remoteFactory != null) {
                    factory.setRepositoryFactoryFromProvider(remoteFactory);
                    factory.getRepositoryContext().setOffline(true);
                }
            }
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
        // Save last used parameters
        prefManipulator.setLastProject(project.getTechnicalLabel());
        saveLastConnBean(connBean);
        // check for Talendforge
        if (PluginChecker.isExchangeSystemLoaded() && !TalendPropertiesUtil.isHideExchange()) {
            IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
            boolean checkTisVersion = prefStore.getBoolean(ITalendCorePrefConstants.EXCHANGE_CHECK_TIS_VERSION);
            IBrandingService brandingService = (IBrandingService) GlobalServiceRegister.getDefault().getService(
                    IBrandingService.class);
            if (!checkTisVersion && brandingService.isPoweredbyTalend()) {
                int count = prefStore.getInt(TalendForgeDialog.LOGINCOUNT);
                if (count < 0) {
                    count = 1;
                }
                ExchangeUser exchangeUser = project.getExchangeUser();
                boolean isExchangeLogon = exchangeUser.getLogin() != null && !exchangeUser.getLogin().equals("");
                boolean isUserPassRight = true;
                if (isExchangeLogon) {
                    IExchangeService service = (IExchangeService) GlobalServiceRegister.getDefault().getService(
                            IExchangeService.class);
                    if (service.checkUserAndPass(exchangeUser.getUsername(), exchangeUser.getPassword()) != null) {
                        isUserPassRight = false;
                    }
                }

                if (!isExchangeLogon || !isUserPassRight) {
                    if ((count + 1) % 4 == 0) {
                        // if (Platform.getOS().equals(Platform.OS_LINUX)) {
                        // TalendForgeDialog tfDialog = new TalendForgeDialog(this.getShell(), project);
                        // tfDialog.open();
                        // } else {
                        Display.getDefault().asyncExec(new Runnable() {

                            @Override
                            public void run() {
                                String userEmail = null;
                                if (project.getAuthor() != null) {
                                    userEmail = project.getAuthor().getLogin();
                                }
                                TalendForgeDialog tfDialog = new TalendForgeDialog(getUsableShell(), userEmail);
                                tfDialog.setBlockOnOpen(true);
                                tfDialog.open();
                            }

                        });
                    }

                    prefStore.setValue(TalendForgeDialog.LOGINCOUNT, count + 1);
                }
            }
        }

        try {
            if (GlobalServiceRegister.getDefault().isServiceRegistered(ICoreTisService.class)) {
                final ICoreTisService service = (ICoreTisService) GlobalServiceRegister.getDefault().getService(
                        ICoreTisService.class);
                if (service != null) {// if in TIS then update the bundle status according to the project type
                    if (!service.validProject(project, needRestartForLocal)) {
                        isRestart = true;
                        return true;
                    }
                }// else not in TIS so ignor caus we may not have a licence so we do not know which bundles belong to
                 // DI, DQ or MDM
            }
        } catch (PersistenceException e) {
            CommonExceptionHandler.process(e);
            MessageDialog.openError(getUsableShell(), getUsableShell().getText(), e.getMessage());
            return false;
        }

        final Shell shell = getUsableShell();
        ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);

        IRunnableWithProgress runnable = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                // monitorWrap = new EventLoopProgressMonitor(monitor);
                try {

                    factory.logOnProject(project, monitor);
                } catch (LoginException e) {
                    throw new InvocationTargetException(e);
                } catch (PersistenceException e) {
                    throw new InvocationTargetException(e);
                } catch (OperationCanceledException e) {
                    throw new InterruptedException(e.getLocalizedMessage());
                }

                monitor.done();
            }
        };

        try {

            dialog.run(true, true, runnable);

        } catch (final InvocationTargetException e) {
            // if (PluginChecker.isSVNProviderPluginLoaded()) {
            if (e.getTargetException() instanceof OperationCancelException) {
                Display.getDefault().syncExec(new Runnable() {

                    @Override
                    public void run() {
                        MessageDialog.openError(Display.getDefault().getActiveShell(),
                                Messages.getString("LoginDialog.logonCanceled"), e.getTargetException().getLocalizedMessage());
                    }

                });
            } else {
                MessageBoxExceptionHandler.process(e.getTargetException(), getUsableShell());
            }
            // } else {
            // fillUIProjectList();
            // MessageBoxExceptionHandler.process(e.getTargetException(), getShell());
            // }
            return false;
        } catch (InterruptedException e) {
            // if (PluginChecker.isSVNProviderPluginLoaded()) {
            // loginComposite.populateProjectList();
            // } else {
            // loginComposite.populateTOSProjectList();
            // }
            return false;
        }

        return true;
    }

    public Project[] getProjects(ConnectionBean connBean) {
        if (connBean == null) {
            return null;
        }
        Project[] projects = null;
        if (connBean != null) {
            String user2 = connBean.getUser();
            String repositoryId2 = connBean.getRepositoryId();
            String workSpace = connBean.getWorkSpace();
            String name = connBean.getName();
            if (user2 != null && !"".equals(user2) && repositoryId2 != null && !"".equals(repositoryId2) && workSpace != null //$NON-NLS-1$ //$NON-NLS-2$
                    && !"".equals(workSpace) && name != null && !"".equals(name)) { //$NON-NLS-1$ //$NON-NLS-2$
                boolean valid = Pattern.matches(RepositoryConstants.MAIL_PATTERN, user2);
                if (valid && RepositoryConstants.REPOSITORY_REMOTE_ID.equals(repositoryId2)) {
                    String url = connBean.getDynamicFields().get(RepositoryConstants.REPOSITORY_URL);
                    valid = url != null || !"".equals(url); //$NON-NLS-1$
                }

                connBean.setComplete(valid);
            }
        }

        ProxyRepositoryFactory repositoryFactory = ProxyRepositoryFactory.getInstance();
        repositoryFactory.setRepositoryFactoryFromProvider(RepositoryFactoryProvider.getRepositoriyById(connBean
                .getRepositoryId()));
        if (!connBean.isComplete()) {
            return projects;
        }

        boolean initialized = false;

        final String newLine = ":\n"; //$NON-NLS-1$
        try {
            try {
                repositoryFactory.checkAvailability();
            } catch (WarningException e) {
                String warnings = e.getMessage();
                if (warnings != null && !warnings.equals(lastWarnings)) {
                    lastWarnings = warnings;
                    final Shell shell = new Shell(DisplayUtils.getDisplay(), SWT.ON_TOP | SWT.TOP);
                    MessageDialog.openWarning(shell, Messages.getString("LoginComposite.warningTitle"), warnings); //$NON-NLS-1$
                }
            }

            try {
                IRunnableWithProgress op = new IRunnableWithProgress() {

                    @Override
                    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                        try {
                            ProxyRepositoryFactory.getInstance().initialize();
                        } catch (PersistenceException e) {
                            throw new InvocationTargetException(e);
                        }
                    }
                };
                new ProgressMonitorDialog(getUsableShell()).run(true, false, op);
            } catch (InvocationTargetException e) {
                throw (Exception) e.getTargetException();
            } catch (InterruptedException e) {
                //
            }

            initialized = true;
        } catch (Exception e) {
            projects = new Project[0];
            final Shell shell = new Shell(DisplayUtils.getDisplay(), SWT.ON_TOP | SWT.TOP);
            MessageDialog.openError(shell, Messages.getString("LoginComposite.warningTitle"), //$NON-NLS-1$
                    Messages.getString("LoginComposite.errorMessages1") + newLine + e.getMessage()); //$NON-NLS-1$
        }

        if (initialized) {
            try {
                projects = repositoryFactory.readProject();
                Arrays.sort(projects, new Comparator<Project>() {

                    @Override
                    public int compare(Project p1, Project p2) {
                        return p1.getLabel().compareTo(p2.getLabel());
                    }

                });
            } catch (PersistenceException e) {
                projects = new Project[0];

                MessageDialog.openError(getUsableShell(), Messages.getString("LoginComposite.errorTitle"), //$NON-NLS-1$
                        Messages.getString("LoginComposite.errorMessages1") + newLine + e.getMessage()); //$NON-NLS-1$
            } catch (BusinessException e) {
                projects = new Project[0];

                MessageDialog.openError(getUsableShell(), Messages.getString("LoginComposite.errorTitle"), //$NON-NLS-1$
                        Messages.getString("LoginComposite.errorMessages1") + newLine + e.getMessage()); //$NON-NLS-1$
            }
        }

        return projects;
    }

    public List<String> getProjectBranches(Project p) {
        List<String> brancesList = new ArrayList<String>();
        if (p != null && svnProviderService != null) {
            try {
                if (!p.isLocal() && svnProviderService.isSVNProject(p)) {
                    brancesList.add(SVNConstant.NAME_TRUNK);
                    String[] branchList = svnProviderService.getBranchList(p);
                    if (branchList != null) {
                        brancesList.addAll(Arrays.asList(branchList));
                    }

                }
            } catch (PersistenceException e) {
                CommonExceptionHandler.process(e);
            }
        }
        return brancesList;
    }

    public Project getLastUsedProject(Project[] projects) {
        if (projects == null || projects.length == 0) {
            return null;
        }
        String lastProject = prefManipulator.getLastProject();
        if (lastProject != null) {
            return getProjectByName(projects, lastProject);
        }
        return null;
    }

    public Project getProjectByName(Project[] projects, String projectName) {
        Project goodProject = null;
        if (projects == null || projects.length == 0 || projectName == null) {
            return goodProject;
        }
        for (int i = 0; goodProject == null && i < projects.length; i++) {
            if (projectName.equals(projects[i].getTechnicalLabel()) || projectName.equals(projects[i].getLabel())) {
                goodProject = projects[i];
            }
        }
        return goodProject;
    }

    public static boolean isTalendLogonFirstTimeStartup() {
        int startupTime = PlatformUI.getPreferenceStore().getInt(PREFERENCE_TALEND_LOGON_STARTUP_TIMES);
        if (startupTime == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void refreshTalendLogonStartupTimes() {
        int newStartupTimes = PlatformUI.getPreferenceStore().getInt(PREFERENCE_TALEND_LOGON_STARTUP_TIMES) + 1;
        if (newStartupTimes <= 0) {
            newStartupTimes = 1;
        }
        PlatformUI.getPreferenceStore().setValue(PREFERENCE_TALEND_LOGON_STARTUP_TIMES, newStartupTimes);
    }

    public static void forceShowLogonDialogNextTime() {
        PlatformUI.getPreferenceStore().setValue(PREFERENCE_TALEND_FORCE_SHOW_LOGON_DIALOG, true);
    }

    public static boolean isNeedForceShowLogonDialog() {
        boolean needForceShowLogonDialog = PlatformUI.getPreferenceStore().getBoolean(PREFERENCE_TALEND_FORCE_SHOW_LOGON_DIALOG);
        if (needForceShowLogonDialog) {
            PlatformUI.getPreferenceStore().setValue(PREFERENCE_TALEND_FORCE_SHOW_LOGON_DIALOG, false);
        }
        return needForceShowLogonDialog;
    }

    public static boolean isAlwaysAskAtStartup() {
        IPreferenceStore preferenceStore = PlatformUI.getPreferenceStore();
        boolean isAlwaysAsk = true;
        if (isTalendLogonFirstTimeStartup()) {
            preferenceStore.setValue(ITalendCorePrefConstants.LOGON_DIALOG_ALWAYS_ASK_ME_AT_STARTUP, isAlwaysAsk);
        } else {
            isAlwaysAsk = preferenceStore.getBoolean(ITalendCorePrefConstants.LOGON_DIALOG_ALWAYS_ASK_ME_AT_STARTUP);
        }
        return isAlwaysAsk;
    }

    public static void setAlwaysAskAtStartup(boolean isAlwaysAskAtStartup) {
        PlatformUI.getPreferenceStore().setValue(ITalendCorePrefConstants.LOGON_DIALOG_ALWAYS_ASK_ME_AT_STARTUP,
                isAlwaysAskAtStartup);
    }

    protected Shell getUsableShell() {
        if (usableShell != null) {
            return usableShell;
        } else {
            return new Shell(DisplayUtils.getDisplay(), SWT.ON_TOP | SWT.TOP);
        }
    }

    public static ConnectionBean createDefaultLocalConnection() {
        ConnectionBean defaultConnectionBean = ConnectionBean.getDefaultConnectionBean();
        defaultConnectionBean.setUser("user@talend.com"); //$NON-NLS-1$
        defaultConnectionBean.setWorkSpace(getRecentWorkSpace());
        defaultConnectionBean.setComplete(true);
        return defaultConnectionBean;
    }

    protected static String getRecentWorkSpace() {
        String filePath = new Path(Platform.getInstanceLocation().getURL().getPath()).toFile().getPath();
        return filePath;
    }

    protected static List<ConnectionBean> filterUsableConnections(List<ConnectionBean> iStoredConnections) {
        if (iStoredConnections == null) {
            return null;
        }
        List<ConnectionBean> filteredConnections = new ArrayList<ConnectionBean>(iStoredConnections);
        if (PluginChecker.isSVNProviderPluginLoaded()) {
            // if this plugin loaded, then means support remote connections, then no need to filter
            return filteredConnections;
        }
        Iterator<ConnectionBean> connectionBeanIter = filteredConnections.iterator();
        while (connectionBeanIter.hasNext()) {
            if (LoginHelper.isRemoteConnection(connectionBeanIter.next())) {
                connectionBeanIter.remove();
            }
        }
        return filteredConnections;
    }

    public void setUsableShell(Shell usableShell) {
        this.usableShell = usableShell;
    }

    public ConnectionUserPerReader getPerReader() {
        return this.perReader;
    }

    public void setPerReader(ConnectionUserPerReader perReader) {
        this.perReader = perReader;
    }

    public List<ConnectionBean> getStoredConnections() {
        return this.storedConnections;
    }

    public void setStoredConnections(List<ConnectionBean> storedConnections) {
        this.storedConnections = storedConnections;
    }

    public String getLastConnection() {
        return this.lastConnection;
    }

    public void setLastConnection(String lastConnection) {
        this.lastConnection = lastConnection;
    }

    public ConnectionBean getFirstConnBean() {
        return this.firstConnBean;
    }

    public void setFirstConnBean(ConnectionBean firstConnBean) {
        this.firstConnBean = firstConnBean;
    }

    public PreferenceManipulator getPrefManipulator() {
        return this.prefManipulator;
    }

    public void setPrefManipulator(PreferenceManipulator prefManipulator) {
        this.prefManipulator = prefManipulator;
    }

}
