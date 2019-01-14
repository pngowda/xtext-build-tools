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
        sh "git config user.email 'genie-xtext@git.eclipse.org'"
        sh "git config user.name 'genie-xtext'"

        sh(
            script: 'git add -A',
            returnStdout: true
        ).trim()
        git_cmd = sh(
            script: "git commit -a -m '${message} ${xtext_version}'",
            returnStdout: true
        ).trim()
        sh("git diff-index --quiet HEAD || git commit -m '${message} ${xtext_version}'")
	
	sh "echo \"\n#################### Changes in repo ${path} ##################\n \" >> ${workspace}/change.log"
	sh(
	     script: "git show --name-only HEAD >> ${workspace}/change.log",
             returnStdout: true
         ).trim()
    }
    println "return statment "+git_cmd
    
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
            script: '''git tag -a -m "release ${tagName}"''',
            returnStdout: true
        ).trim()
    }
    return git_cmd

}

def pushGitChanges(path, branch) {
    dir(path) {
        sh '''
           git push --force --tags origin ${branch}
         '''
    }
}

def getGitCommit() {
    git_commit = sh (
        script: 'git rev-parse HEAD',
        returnStdout: true
    ).trim()
    return git_commit
}

return this
