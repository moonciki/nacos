/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.git.server.jgit.opt;

import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.git.server.constant.NacosGitConstant;
import com.alibaba.nacos.git.server.enums.GitResponseEnum;
import com.alibaba.nacos.git.server.exception.NacosGitException;
import com.alibaba.nacos.git.server.git.support.TransportConfigCallbackFactory;
import com.alibaba.nacos.git.server.jgit.env.JgitConfigProperty;
import com.alibaba.nacos.git.server.jgit.support.JgitCredentialsProviderFactory;
import com.alibaba.nacos.git.server.model.TenantGit;
import com.alibaba.nacos.git.server.vo.GitCommitVo;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.errors.NoRemoteRepositoryException;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.lang.String.format;
import static org.eclipse.jgit.transport.ReceiveCommand.Type.DELETE;

/**
 * a single git repository.
 * reference from spring-cloud-config-server .
 *
 * @author Dave Syer
 * @author Roy Clarkson
 * @author Marcos Barbero
 * @author Daniel Lavoie
 * @author Ryan Lynch
 * @author Gareth Clay
 * @author ChaoDong Xi
 * @author yueshiqi
 */
public class JgitEnvironmentRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(JgitEnvironmentRepository.class);

    /**
     * Error message for URI for git repo.
     */
    public static final String ERROR_URI_MESSAGE = "You need to configure a uri for the git repository.";

    private static final String FILE_URI_PREFIX = "file:";

    private static final String LOCAL_BRANCH_REF_PREFIX = "refs/remotes/origin/";

    private static final String REPO_PROFIX = "config-repo-";

    public static final String DEFAULT_LABEL = "master";

    private boolean cloneSubmodules = false;

    /* ================================ local config ================================ */

    private JgitConfigProperty jgitEnvironmentProperties;

    /**
     * Transport configuration callback for JGit commands.
     */
    private TransportConfigCallback transportConfigCallback;

    /** git repo folder. **/
    private File configRepoFolder;

    private TenantGit tenantGit;

    /** 分支. **/
    private String branch;

    public JgitEnvironmentRepository(TenantGit tenantGit) {
        this.tenantGit = tenantGit;

        String repoUuid = tenantGit.getRepoUuid();
        this.branch = tenantGit.getBranch();
        if (StringUtils.isBlank(branch)) {
            this.branch = DEFAULT_LABEL;
        }

        String gitConfigRepoDir = NacosGitConstant.GIT_CONFIG_REPO_DIR;

        this.configRepoFolder = new File(gitConfigRepoDir, REPO_PROFIX + repoUuid);

        this.jgitEnvironmentProperties = JgitConfigProperty.loadProperties(tenantGit);

        this.transportConfigCallback = TransportConfigCallbackFactory.build(jgitEnvironmentProperties);

    }

    public File getConfigRepoFolder() {
        return configRepoFolder;
    }

    public String getConfigRepoPath() {
        return configRepoFolder.getAbsolutePath();
    }

    public TransportConfigCallback getTransportConfigCallback() {
        return this.transportConfigCallback;
    }

    public void setTransportConfigCallback(TransportConfigCallback transportConfigCallback) {
        this.transportConfigCallback = transportConfigCallback;
    }

    public boolean isSkipSslValidation() {
        boolean skipSslValidation = jgitEnvironmentProperties.isSkipSslValidation();
        return skipSslValidation;
    }

    public int getTimeout() {
        int timeout = jgitEnvironmentProperties.getTimeout();
        return timeout;
    }

    /*
    @Override
    public synchronized Locations getLocations(String application, String profile, String label) {
        if (label == null) {
            label = this.defaultLabel;
        }
        String version;
        try {
            version = refresh(label);
        }
        catch (Exception e) {
            if (this.defaultLabel.equals(label) && JGitEnvironmentProperties.MAIN_LABEL.equals(this.defaultLabel)
                    && tryMasterBranch) {
                logger.info("Could not refresh default label " + label, e);
                logger.info("Will try to checkout master label instead.");
                version = refresh(JGitEnvironmentProperties.MASTER_LABEL);
            }
            else {
                throw e;
            }
        }
        return new Locations(application, profile, label, version,
                getSearchLocations(getWorkingDirectory(), application, profile, label));
    }
    */

    public CloneCommand getCloneCommandByCloneRepository() {
        CloneCommand command = Git.cloneRepository().setCloneSubmodules(cloneSubmodules);
        return command;
    }

    public GitCommitVo getCommitInfo(Git git, ObjectId commitId) throws Exception {

        String commitIdName = commitId.getName();

        Repository repository = git.getRepository();

        RevWalk walk = new RevWalk(repository);
        //ObjectId versionId = repository.resolve(commitId);
        RevCommit revCommit = walk.parseCommit(commitId);

        long commitTime = revCommit.getCommitTime();

        long commitTs = commitTime * (long) 1000;

        Date commitDate = new Date(commitTs);

        String authorName = revCommit.getAuthorIdent().getName();
        String shortMessage = revCommit.getShortMessage();

        GitCommitVo gitCommitVo = new GitCommitVo();

        gitCommitVo.setCommitId(commitIdName);
        gitCommitVo.setAuthor(authorName);
        gitCommitVo.setMessage(shortMessage);
        gitCommitVo.setCommitDate(commitDate);

        return gitCommitVo;
    }

    /**
     * Get the working directory ready.
     * @return head id
     */
    public GitCommitVo refresh() {

        Git git = null;
        try {
            git = this.createGitClient();

            if (shouldPull(git)) {
                FetchResult fetchStatus = fetch(git, this.branch);

                if (fetchStatus != null) {
                    deleteUntrackedLocalBranches(fetchStatus.getTrackingRefUpdates(), git);
                }
            }

            // checkout after fetch so we can get any new branches, tags, ect.
            // if nothing to update so just checkout and merge.
            // Merge because remote branch could have been updated before
            this.checkout(git, this.branch);
            this.tryMerge(git, this.branch);

            ObjectId headId = git.getRepository().findRef("HEAD").getObjectId();

            GitCommitVo commitInfo = this.getCommitInfo(git, headId);

            return commitInfo;

        } catch (RefNotFoundException e) {
            throw NacosGitException.createException("No such label: " + this.branch, e);
        } catch (NoRemoteRepositoryException e) {
            throw NacosGitException.createException("No such repository: " + tenantGit.getUri(), e);
        } catch (GitAPIException e) {
            throw NacosGitException.createException("Cannot clone or checkout repository: " + tenantGit.getUri(), e);
        } catch (Exception e) {
            throw NacosGitException.createException("Cannot load environment", e);
        } finally {
            try {
                if (git != null) {
                    git.close();
                }
            } catch (Exception e) {
                LOGGER.warn("Could not close git repository", e);
            }
        }
    }

    /**
     * try merge .
     * @param git git
     * @param label label
     */
    private void tryMerge(Git git, String label) {
        try {
            if (isBranch(git, label)) {
                // merge results from fetch

                //this.resetHard(git, label, LOCAL_BRANCH_REF_PREFIX + label);

                this.merge(git, label);
                if (!isClean(git, label)) {
                    LOGGER.warn("The local repository is dirty or ahead of origin. Resetting" + " it to origin/"
                            + label + ".");
                    this.resetHard(git, label, LOCAL_BRANCH_REF_PREFIX + label);
                }
            }
        } catch (GitAPIException e) {
            throw NacosGitException.createException("Cannot clone or checkout repository: " + tenantGit.getUri(), e);
        }
    }

    /**
     * Clones the remote repository and then opens a connection to it. Checks out to the
     * defaultLabel if specified.
     * @throws GitAPIException when cloning fails
     * @throws IOException when repo opening fails
     */
    private void initClonedRepository() throws GitAPIException, IOException {
        if (!tenantGit.getUri().startsWith(FILE_URI_PREFIX)) {
            deleteBaseDirIfExists();
            Git git = cloneToBasedir();
            if (git != null) {
                git.close();
            }
            git = this.openGitRepository(configRepoFolder);

            // Check if git points to valid repository and default label is not empty or
            // null.
            if (null != git && git.getRepository() != null && !StringUtils.isEmpty(this.branch)) {
                // Checkout the default branch set for repo in git. This may not always be
                // master. It depends on the
                // admin and organization settings.
                String defaultBranchInGit = git.getRepository().getBranch();
                // If default branch is not empty and NOT equal to defaultLabel, then
                // checkout the branch/tag/commit-id.
                if (!StringUtils.isEmpty(defaultBranchInGit)
                        && !this.branch.equalsIgnoreCase(defaultBranchInGit)) {
                    checkoutDefaultBranchWithRetry(git);
                }
            }

            if (git != null) {
                git.close();
            }
        }

    }

    private void checkoutDefaultBranchWithRetry(Git git) throws GitAPIException {
        try {
            checkout(git, this.branch);
        } catch (Exception e) {

            LOGGER.error("Could not checkout label " + this.branch, e);
            throw e;
        }
    }

    /**
     * Deletes local branches if corresponding remote branch was removed.
     * @param trackingRefUpdates list of tracking ref updates
     * @param git git instance
     * @return list of deleted branches
     */
    private Collection<String> deleteUntrackedLocalBranches(Collection<TrackingRefUpdate> trackingRefUpdates, Git git) {
        if (CollectionUtils.isEmpty(trackingRefUpdates)) {
            return Collections.emptyList();
        }

        Collection<String> branchesToDelete = new ArrayList<>();
        for (TrackingRefUpdate trackingRefUpdate : trackingRefUpdates) {
            ReceiveCommand receiveCommand = trackingRefUpdate.asReceiveCommand();
            if (receiveCommand.getType() == DELETE) {
                String localRefName = trackingRefUpdate.getLocalName();
                if (StringUtils.startsWithIgnoreCase(localRefName, LOCAL_BRANCH_REF_PREFIX)) {
                    String localBranchName = localRefName.substring(LOCAL_BRANCH_REF_PREFIX.length(),
                            localRefName.length());
                    branchesToDelete.add(localBranchName);
                }
            }
        }

        if (CollectionUtils.isEmpty(branchesToDelete)) {
            return Collections.emptyList();
        }

        try {
            // make sure that deleted branch not a current one
            checkoutDefaultBranchWithRetry(git);
            return deleteBranches(git, branchesToDelete);
        } catch (Exception ex) {
            String message = format("Failed to delete %s branches.", branchesToDelete);
            LOGGER.error(message, ex);
            return Collections.emptyList();
        }
    }

    private List<String> deleteBranches(Git git, Collection<String> branchesToDelete) throws GitAPIException {
        DeleteBranchCommand deleteBranchCommand = git.branchDelete()
                .setBranchNames(branchesToDelete.toArray(new String[0]))
                // local branch can contain data which is not merged to HEAD - force
                // delete it anyway, since local copy should be R/O
                .setForce(true);
        List<String> resultList = deleteBranchCommand.call();
        LOGGER.info(format("Deleted %s branches from %s branches to delete.", resultList, branchesToDelete));
        return resultList;
    }

    private Ref checkout(Git git, String label) throws GitAPIException {
        CheckoutCommand checkout = git.checkout();
        if (shouldTrack(git, label)) {
            trackBranch(git, checkout, label);
        } else {
            // works for tags and local branches
            checkout.setName(label);
        }
        return checkout.call();
    }

    protected boolean shouldPull(Git git) throws GitAPIException {
        boolean shouldPull;

        Status gitStatus;
        try {
            gitStatus = git.status().call();
        } catch (JGitInternalException e) {
            onPullInvalidIndex(git, e);
            gitStatus = git.status().call();
        }

        boolean isWorkingTreeClean = gitStatus.isClean();
        String originUrl = git.getRepository().getConfig().getString("remote", "origin", "url");

        if (!isWorkingTreeClean) {
            logDirty(gitStatus);
        }
        shouldPull = true;
        return shouldPull;
    }

    protected void onPullInvalidIndex(Git git, JGitInternalException e) {
        final String msgText = "Short read of block.";
        if (!e.getMessage().contains(msgText)) {
            throw e;
        }
        //默认均强制拉取
        /*
        if (!this.forcePull) {
            throw e;
        }
        */

        try {
            new File(this.getConfigRepoPath(), ".git/index").delete();
            git.reset().setMode(ResetCommand.ResetType.HARD).setRef("HEAD").call();
        } catch (GitAPIException ex) {
            e.addSuppressed(ex);
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private void logDirty(Status status) {
        Set<String> dirties = dirties(status.getAdded(), status.getChanged(), status.getRemoved(), status.getMissing(),
                status.getModified(), status.getConflicting(), status.getUntracked());
        LOGGER.warn(format("Dirty files found: %s", dirties));
    }

    @SuppressWarnings("unchecked")
    private Set<String> dirties(Set<String>... changes) {
        Set<String> dirties = new HashSet<>();
        for (Set<String> files : changes) {
            dirties.addAll(files);
        }
        return dirties;
    }

    private boolean shouldTrack(Git git, String label) throws GitAPIException {
        return isBranch(git, label) && !isLocalBranch(git, label);
    }

    /**
     * fetch origin repo .
     * @param git git
     * @param label label
     * @return FetchResult
     */
    protected FetchResult fetch(Git git, String label) {
        FetchCommand fetch = git.fetch();
        fetch.setRemote("origin");
        fetch.setTagOpt(TagOpt.FETCH_TAGS);
        fetch.setRemoveDeletedRefs(true);

        configureCommand(fetch);
        try {
            FetchResult result = fetch.call();
            if (result.getTrackingRefUpdates() != null && result.getTrackingRefUpdates().size() > 0) {
                LOGGER.info("Fetched for remote " + label + " and found " + result.getTrackingRefUpdates().size()
                        + " updates");
            }
            return result;
        } catch (Exception ex) {

            String errorMsg = "Could not fetch for remote: " + tenantGit.getUri();
            LOGGER.error(errorMsg, ex);

            return null;
        }
    }

    private MergeResult merge(Git git, String label) {
        try {
            MergeCommand merge = git.merge();
            merge.include(git.getRepository().findRef("origin/" + label));
            MergeResult result = merge.call();
            if (!result.getMergeStatus().isSuccessful()) {
                LOGGER.warn("Merged from remote " + label + " with result " + result.getMergeStatus());
            }
            return result;
        } catch (Exception ex) {
            String message = "Could not merge remote for " + label + " remote: "
                    + git.getRepository().getConfig().getString("remote", "origin", "url");
            LOGGER.error(message, ex);
            return null;
        }
    }

    /**
     * clean untrack files.
     * @param git git
     * @return DirCache
     */
    private String cleanUntrack(Git git) {
        CleanCommand cleanCommand = git.clean();

        cleanCommand.setCleanDirectories(true);
        cleanCommand.setForce(true);

        try {
            Set<String> call = cleanCommand.call();

            String result = null;
            if (call != null) {
                result = call.toString();
                LOGGER.info("CleanUntrack files : " + result);
            }
            return result;
        } catch (Exception ex) {
            String message = "Could not allAll .";
            LOGGER.error(message, ex);
            return null;
        }
    }

    private Ref resetHard(Git git, String label, String ref) {

        String cleanResult = this.cleanUntrack(git);

        ResetCommand reset = git.reset();

        reset.setRef(ref);
        reset.setMode(ResetCommand.ResetType.HARD);
        try {
            Ref resetRef = reset.call();
            if (resetRef != null) {
                LOGGER.info("Reset label " + label + " to version " + resetRef.getObjectId());
            }
            return resetRef;
        } catch (Exception ex) {
            String message = "Could not reset to remote for " + label + " (current ref=" + ref + "), remote: "
                    + git.getRepository().getConfig().getString("remote", "origin", "url");
            LOGGER.error(message, ex);
            return null;
        }
    }

    /**
     * 初始化gitclient .
     * @return Git
     * @throws IOException e
     */
    private Git createGitClient() throws Exception {

        File lock = new File(this.getConfigRepoPath(), ".git/index.lock");
        if (lock.exists()) {
            // The only way this can happen is if another JVM (e.g. one that
            // crashed earlier) created the lock. We can attempt to recover by
            // wiping the slate clean.
            LOGGER.info("Deleting stale JGit lock file at " + lock);
            lock.delete();
        }

        final String gitTmp = ".git";

        if (new File(this.getConfigRepoPath(), gitTmp).exists()) {
            return this.openGitRepository(configRepoFolder);
        } else {
            return this.copyRepository();
        }
    }

    /**
     * Synchronize here so that multiple requests don't all try and delete the base dir.<br>
     * together (this is a once only operation, so it only holds things up on the first request)
     * @return Git git
     * @throws IOException when read error
     * @throws GitAPIException when read error
     */
    private synchronized Git copyRepository() throws IOException, GitAPIException {
        deleteBaseDirIfExists();

        if (!configRepoFolder.exists()) {
            configRepoFolder.mkdirs();
        }

        Assert.state(configRepoFolder.exists(), "Could not create basedir: " + getConfigRepoPath());
        if (tenantGit.getUri().startsWith(FILE_URI_PREFIX)) {
            return copyFromLocalRepository();
        } else {
            return cloneToBasedir();
        }
    }

    /**
     * 打开git工作目录 .
     * @return Git
     * @throws IOException e
     */
    private Git openGitRepository(File gitFolder) throws IOException {

        Git git = Git.open(gitFolder);
        return git;
    }

    private Git copyFromLocalRepository() throws IOException {

        String gitUri = tenantGit.getUri();
        File remote = new UrlResource(StringUtils.cleanPath(gitUri)).getFile();
        Assert.state(remote.isDirectory(), "No directory at " + gitUri);
        File gitDir = new File(remote, ".git");
        Assert.state(gitDir.exists(), "No .git at " + gitUri);
        Assert.state(gitDir.isDirectory(), "No .git directory at " + gitUri);
        Git git = this.openGitRepository(remote);
        return git;
    }

    private Git cloneToBasedir() throws GitAPIException {
        CloneCommand clone = this.getCloneCommandByCloneRepository()
                .setURI(tenantGit.getUri())
                .setDirectory(configRepoFolder);
        this.configureCommand(clone);

        try {
            return clone.call();
        } catch (GitAPIException e) {
            LOGGER.error("Error occured cloning to base directory.", e);
            deleteBaseDirIfExists();
            throw e;
        }
    }

    private void deleteBaseDirIfExists() {
        if (configRepoFolder.exists()) {
            for (File file : configRepoFolder.listFiles()) {
                try {
                    FileUtils.delete(file, FileUtils.RECURSIVE);
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to initialize base directory", e);
                }
            }
        }
    }

    private void configureCommand(TransportCommand<?, ?> command) {
        command.setTimeout(this.getTimeout());
        if (this.transportConfigCallback != null) {
            command.setTransportConfigCallback(this.transportConfigCallback);
        }
        CredentialsProvider credentialsProvider = getCredentialsProvider();
        if (credentialsProvider != null) {
            command.setCredentialsProvider(credentialsProvider);
        }

    }

    private CredentialsProvider getCredentialsProvider() {
        CredentialsProvider credentials = JgitCredentialsProviderFactory.createFor(tenantGit.getUri(), tenantGit.getUserName(), tenantGit.getPassword(),
                tenantGit.getPassphrase(), this.isSkipSslValidation());

        return credentials;
    }

    private boolean isClean(Git git, String label) {
        StatusCommand status = git.status();
        try {
            BranchTrackingStatus trackingStatus = BranchTrackingStatus.of(git.getRepository(), label);
            boolean isBranchAhead = trackingStatus != null && trackingStatus.getAheadCount() > 0;
            return status.call().isClean() && !isBranchAhead;
        } catch (Exception e) {
            String message = "Could not execute status command on local repository.";
            LOGGER.error(message, e);
            return false;
        }
    }

    private void trackBranch(Git git, CheckoutCommand checkout, String label) {
        checkout.setCreateBranch(true).setName(label).setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setStartPoint("origin/" + label);
    }

    private boolean isBranch(Git git, String label) throws GitAPIException {
        return containsBranch(git, label, ListBranchCommand.ListMode.ALL);
    }

    private boolean isLocalBranch(Git git, String label) throws GitAPIException {
        return containsBranch(git, label, null);
    }

    private boolean containsBranch(Git git, String label, ListBranchCommand.ListMode listMode) throws GitAPIException {
        ListBranchCommand command = git.branchList();
        if (listMode != null) {
            command.setListMode(listMode);
        }
        List<Ref> branches = command.call();
        for (Ref ref : branches) {
            if (ref.getName().endsWith("/" + label)) {
                return true;
            }
        }
        return false;
    }

    /**
     * clear dir.
     */
    public void clear() {
        this.deleteBaseDirIfExists();
    }

    /**
     * destroy dir .
     */
    public void destroy() {
        if (configRepoFolder.exists()) {
            try {
                FileUtils.delete(configRepoFolder, FileUtils.RECURSIVE);
            } catch (IOException e) {
                LOGGER.error("destroy_error : " + this.getConfigRepoPath(), e);
                throw NacosGitException.createException(GitResponseEnum.sys_error.info("git dir is busy!"));
            }
        }
    }

}
