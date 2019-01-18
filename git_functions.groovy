def createGitBranch(path, branch) {
    def git_cmd
    dir(path) {
        git_cmd = sh (
            script: "git checkout -b ${branch}",
            returnStatus: true
        )
    }
    return git_cmd
}

def verifyGitBranch(path, branch) {
    def git_cmd
    dir(path) {
        git_cmd = sh (
            script: "git rev-parse --verify ${branch}",
            returnStatus: true
        )
    }
    return git_cmd
}


def commitGitChanges(path, xtext_version, message, gitEmail='jenkins@localhost', gitName='jenkins-slave') {
    def git_cmd
    dir(path) {
        print sh(
            script: 'git add -A',
            returnStdout: true
        )
        // return status, but ignore
        sh(
            script: "git commit -a -m '${message} ${xtext_version}' >> commit.log",
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

def pushGitChanges(path, branch) {
    def git_cmd
    dir(path) {
        git_cmd = sh (
            script: "git push --force --tags origin ${branch}",
            returnStatus: true
        )
    }
    return git_cmd
}

def getGitCommit() {
    git_commit = sh (
        script: 'git rev-parse HEAD',
        returnStdout: true
    ).trim()
    return git_commit
}

return this
