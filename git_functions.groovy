def createBranch(branch) {
    def rc = sh (script: "git checkout -b ${branch}", returnStatus: true)
    return rc == 0
}

def boolean branchExists(branch) {
    def rc = sh (script: "git rev-parse --verify ${branch}", returnStatus: true)
    return rc == 0
}

def boolean deleteBranch(branch) {
    def rc = sh (script: "git branch -D ${branch}", returnStatus: true)
    return rc == 0
}

def boolean pull(branch, remote='origin') {
    def rc = sh (script: "git pull ${remote} ${branch}", returnStatus: true)
    return rc == 0
}


def commit(path, message, gitName='genie.xtext', gitEmail='genie.xtext@git.eclipse.org') {
    def git_cmd
    dir(path) {
        print sh(
            script: 'git add -A',
            returnStdout: true
        )
        // return status, but ignore
        sh(
            script: "git -c user.email='${gitEmail}' -c user.name='${gitName}' commit -a -m '**** TEST TEST ${message}\n\nSigned-off-by: ${gitName} <${gitEmail}>'",
            returnStatus: true
        )

        print sh(
             script: "git show --name-only HEAD",
                 returnStdout: true
             )
        
    }
    
    return git_cmd
}

def commitGitChanges(path, xtext_version, message, gitEmail='genie.xtext@git.eclipse.org', gitName='genie.xtext') {
    def git_cmd
    dir(path) {
        print sh(
            script: 'git add -A',
            returnStdout: true
        )
        // return status, but ignore
        sh(
            script: "git commit -a -m '${message} ${xtext_version}'",
            returnStatus: true
        )
        print sh("git diff-index --quiet HEAD || git commit -m '${message} ${xtext_version}'")

        print sh(
             script: "git show --name-only HEAD",
                 returnStdout: true
             )
    }
    
    return git_cmd
}

def getGitChanges(path) {
    dir(path) {
      git_changes = sh (
          script: 'git show --name-only HEAD',
          returnStdout: true
      ).trim()
    }
    print git_changes
    return git_changes
}


def getGitRemote(name = '', type = 'fetch') {
    dir("workDir") {
    gitRemote = sh (
        script: "git remote -v | grep '${name}' | grep ${type} | awk '{print \$2}' | head -1",
        returnStdout: true
    ).trim()
    }
    return gitRemote
}


def tagGit(path, tagName) {
    def git_cmd
    dir(path) {
        git_cmd = sh (
            script: "git tag --force -a ${tagName} -m 'release ${tagName}'",
            returnStdout: true
        ).trim()
    }
    return git_cmd

}

def pushGitChanges(path, branch, openPR=false) {
    dir(path) {
        def rc = sh (
            script: "git push --force --tags origin ${branch}",
            returnStatus: true
        )
        if (rc == 0 && openPR) {
          def message = sh (script: "git log -1 --pretty='format:%s'", returnStdout: true)
          sh(
            script: "hub pull-request -m '${message}\n\nThis PR was opened by Jenkins'",
            returnStatus: true
          )
        }
        return rc
    }
}

def getGitCommit() {
    git_commit = sh (
        script: 'git rev-parse HEAD',
        returnStdout: true
    ).trim()
    return git_commit
}

def gitResetHard() {
    def git_cmd
      git_cmd = sh (
          script: 'git reset --hard',
          returnStdout: true
      ).trim()
    return git_cmd
}

def checkoutBranch(branchName) {
    def git_cmd
      git_cmd = sh (
          script: "git checkout ${branchName}",
          returnStdout: true
      ).trim()
    return git_cmd
}

return this
