import jenkins.model.*
import hudson.model.*

node('master') {
           def xtextVersion="${params.XTEXT_VERSION}"
           def branchName="${params.BRANCHNAME}"
           def tagName="${params.TAGNAME}"
	   def releaseType="${params.RELEASE_TYPE}"
                                 
           println xtextVersion
           println branchName
           println tagName
	   println releaseType
	
	stage('Checkout') {
		checkout scm
	}
	
	stage('Checkout-All') {
	withCredentials([usernamePassword(credentialsId: 'adminCred', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
	    dir("${workspace}/xtext-lib") { deleteDir() }
	    dir("${workspace}/xtext-core") { deleteDir() }
	    dir("${workspace}/xtext-extras") { deleteDir() }
	    dir("${workspace}/xtext-eclipse") { deleteDir() }
	    dir("${workspace}/xtext-idea") { deleteDir() }
	    dir("${workspace}/xtext-web") { deleteDir() }
	    dir("${workspace}/xtext-maven") { deleteDir() }
	    dir("${workspace}/xtext-xtend") { deleteDir() }
	    dir("${workspace}/xtext-umbrella") { deleteDir() }
		
	    def libGitUrl="https://github.com/pngowda/xtext-lib.git"
            def coreGitUrl="https://github.com/pngowda/xtext-core.git"
	    def extrasGitUrl="https://github.com/pngowda/xtext-extras.git"
            def eclipseGitUrl="https://github.com/pngowda/xtext-eclipse.git"
	    def ideaGitUrl="https://github.com/pngowda/xtext-idea.git"
            def webGitUrl="https://github.com/pngowda/xtext-web.git"
	    def mavenGitUrl="https://github.com/pngowda/xtext-maven.git"
            def xtendGitUrl="https://github.com/pngowda/xtext-xtend.git"
	    def umbrellaGitUrl="https://github.com/pngowda/xtext-umbrella.git"
		
	    checkoutSCM(libGitUrl, "xtext-lib")
            checkoutSCM(coreGitUrl, "xtext-core")
	    checkoutSCM(extrasGitUrl, "xtext-extras")
            checkoutSCM(eclipseGitUrl, "xtext-eclipse")
	    checkoutSCM(ideaGitUrl, "xtext-idea")
            checkoutSCM(webGitUrl, "xtext-web")
	    checkoutSCM(mavenGitUrl, "xtext-maven")
            checkoutSCM(xtendGitUrl, "xtext-xtend")
	    checkoutSCM(umbrellaGitUrl, "xtext-umbrella")		  
	    sh("find . -type f -exec chmod 777 {} \\;")
            int isBranchExist=verifyGitBranch("xtext-lib", branchName)
            if (isBranchExist!=0){
               createGitBranch("xtext-lib", branchName)
            }
	}
	 }
	
	stage('Adjust_Pipeline') {
		withCredentials([usernamePassword(credentialsId: 'adminCred', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
	    //dir('xtext-umbrella') { 
	     sh """
	     pwd
	     ls -la
	     export XTEXT_VERSION=${xtextVersion}
             export BRANCHNAME=${releaseType}_${xtextVersion}
             export TAGNAME=v${xtextVersion}   
	     ./gitAll reset --hard
	     ./gitAll pull
	     ./gitAll checkout -b $BRANCHNAME
	     ./adjustPipelines.sh $BRANCHNAME
	    """
	    //sh(/gitAll reset --hard')
	    //sh('/gitAll pull')
	    //sh('./gitAll checkout -b $BRANCHNAME')
	    //sh('./adjustPipelines.sh $BRANCHNAME')
	    //}
	    
		}
	 }

	

	
	
}


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

def readWriteMavenVersion(pomFile){
    println "Pom File to process: "+pomFile
    pom = readMavenPom file: pomFile
    
    println "pom version: "+ pom.version
    
    def version = pom.version.replace("-SNAPSHOT", "")
    pom.version = version
    println "pom version: "+ pom.version
    
    writeMavenPom model:pom, file: pomFile
    
}

def readWriteParentMavenVersion(pomFile){
    println "Pom File to process: "+pomFile
    pom = readMavenPom file: pomFile
    
    println "pom parent version: "+ pom.parent.version
    
    def version = pom.parent.version.replace("-SNAPSHOT", "")
    pom.parent.version = version
    println "pom parent version: "+ pom.parent.version
    
    writeMavenPom model:pom, file: pomFile
    
}

def modifyPomVersions(){
    dir("extrasWorkDir") {
    //def command = "find ${workspace}/extrasWorkDir/releng/pom.xml | xargs sed"
    def proc = 'find \"${workspace}\"/extrasWorkDir/releng/pom.xml'.execute() | 'xargs sed -i -e s/2.16.0-SNAPSHOT/2.17.0/g'.execute() 
    //def proc = command.execute()
    proc.waitFor()              

    println "Process exit code: ${proc.exitValue()}"
    println "Std Err: ${proc.err.text}"
    println "Std Out: ${proc.in.text}" 
    }
    
    //dir("extrasWorkDir") {
    //gitRemote = sh (
      //  script: "find ./releng/pom.xml | xargs sed -i -e s/2.16.0-SNAPSHOT/2.17.0/g",
    //returnStdout: true
    //).trim()
   // }
    //return gitRemote
}
def readWriteParentMavenVersion2(pomFile){
    println "Pom File to process: "+pomFile
    //pom = readMavenPom file: pomFile
    
    
    def pom = new XmlSlurper().parse(pomFile)
 
    pom.dependencies.dependency.each { dependency ->
        //println "${dependency.groupId} ${dependency.artifactId} ${dependency.version}"
        //println "${dependency.version}".replace("-SNAPSHOT", "")
        //println dependency.version
        pom.dependencies.dependency.version="${dependency.version}".replace("-SNAPSHOT", "")
    }


    pom.dependencies.dependency.each { dependency ->
        println dependency.version
    }


   //println pom
   //new XmlNodePrinter(new PrintWriter(new FileWriter("${workspace}/extrasWorkDir/pom.xml"))).print(pom)


    //writeMavenPom model:pom, file: pomFile
    
}

def parseGradleFile(oldGradleFile, newGradleFile, regXStr, deLmr, regXRpStr){
    
    println "Parsing Gradle File : "+oldGradleFile
    println "RegEX: "+regXStr
    println "Delimiter: "+ deLmr
    println "Replace String: "+regXRpStr

    File oldFile=new File(oldGradleFile)
    File newfile = new File(newGradleFile)
        
    def lines = oldFile.readLines()
            
    lines.each { String line ->
    //println line
    //regXStr=regXStr.replaceAll("\\s","")
    //println line
    //if(line.startsWith(/\s*'xtext_bootstrap'/)){
    if(line.matches(/\s*$regXStr.*/)){
    //if(line.contains(regXStr)){
        //line=line.replaceAll("\\s","")
        println "inside if: "+regXStr
        def values = line.split(deLmr)
        line=line.replace(values[1],regXRpStr)
    }
    newfile.append(line+"\n")
    }
    oldFile.delete()
    newfile.renameTo(oldGradleFile)
}

def commitGitChanges(path, message, gitEmail='jenkins@localhost', gitName='jenkins-slave') {
    def git_cmd
    dir(path) {
        sh "git config --global user.email '${gitEmail}'"
        sh "git config --global user.name '${gitName}'"

        sh(
            script: 'git add -A',
            returnStdout: true
        ).trim()
        //git_cmd = sh(
        //    script: "git commit -m '${message}'",
        //    returnStdout: true
        //).trim()
        
        sh("git diff-index --quiet HEAD || git commit -m '${message}'")
    }
    println "retrun statment "+git_cmd
    
    //return git_cmd
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



def pushGitChanges(path, branch = 'master', remote = 'origin', credentialsId = null) {
    dir(path) {
        if (credentialsId == null) {
            sh script: "git push ${remote} ${branch}"
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

def checkoutSCM(urlPath, wrkDir){
  checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: wrkDir]], submoduleCfg: [], userRemoteConfigs: [[url: urlPath]]])
}


