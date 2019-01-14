def createGitBranch(path, branch) {
    
    def git_cmd
    dir(path) {
        git_cmd = sh (
            script: "git checkout -b ${branch}",
            returnStdout: true
        ).trim()
    }
    return git_cmd
}

def verifyGitBranch(path, branch) {
    def command1 = "git rev-parse --verify ${branch}"
    def proc1 = command1.execute(null, new File("${workspace}/${path}"))
    proc1.waitFor()
    println "Process exit code: ${proc1.exitValue()}"
    return "${proc1.exitValue()}"
}


def commitGitChanges(path, xtext_version, message, gitEmail='jenkins@localhost', gitName='jenkins-slave') {
    def git_cmd
    dir(path) {
        sh "git config --global user.email '${gitEmail}'"
        sh "git config --global user.name '${gitName}'"

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
    println "retrun statment "+git_cmd
    
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

def pushGitChanges(path, branch, remote) {
    dir(path) {
	sshagent(['559af3c2-7b91-482e-81d1-37792c7cb861']) { //
        sh '''
           git push origin ${branch}
         '''
        }
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
