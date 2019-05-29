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


def commit(message, gitName='genie.xtext', gitEmail='genie.xtext@git.eclipse.org') {
    def git_cmd
    print sh(
        script: 'git add -A',
        returnStdout: true
    )
    // return status, but ignore
    sh(
        script: "git commit -a -m '${message}\n\nSigned-off-by: ${gitName} <${gitEmail}>'",
        returnStatus: true
    )

    print sh(
            script: "git show --name-only HEAD",
                returnStdout: true
            )
    
    return git_cmd
}


def void printChanges() {
    git_changes = sh (
        script: 'git show --name-only HEAD',
        returnStdout: true
    ).trim()
    print git_changes
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


def tag(tagName) {
    def git_cmd
    git_cmd = sh (
        script: "git tag --force -a ${tagName} -m 'release ${tagName}'",
        returnStdout: true
    ).trim()
    return git_cmd
}


def push(branch, openPR=false) {
    def rc = sh (
        script: "git push --force --tags origin ${branch}",
        returnStatus: true
    )
    /*
    if (rc == 0 && openPR) {
        def message = sh (script: "git log -1 --pretty='format:%s'", returnStdout: true)
        sh(
        script: "hub pull-request -m '**** TEST TEST ${message}'",
        returnStatus: true
        )
    }
    */
    return rc
}

def getGitCommit() {
    git_commit = sh (
        script: 'git rev-parse HEAD',
        returnStdout: true
    ).trim()
    return git_commit
}

def resetHard() {
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
