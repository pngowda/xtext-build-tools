import jenkins.model.*
import hudson.model.*
import groovy.xml.XmlUtil

node('master') {
           //def xtextVersionNew="${params.FROM_XTEXT_VERSION}"
           //def xtextVersionOld="${params.TO_XTEXT_VERSION}"
           def xtextVersion="${params.XTEXT_VERSION}"
           def branchName="${params.BRANCHNAME}"
           def tagName="${params.TAGNAME}"
	   def releaseType="${params.RELEASE_TYPE}"
           def isBranchExist               
           
	   //println xtextVersionNew
	   //println xtextVersionOld
           println branchName
           println tagName
	   println releaseType

	
	stage('checkout_xtext-build-tools') {
		checkout scm
	}
	
	stage('checkout-xtext-repos') {
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
	    
            sh("find . -type f -exec chmod 777 {} \\;")
            	
	    checkoutSCM(libGitUrl, "xtext-lib")
            if (verifyGitBranch("xtext-lib", branchName)!=0){
               createGitBranch("xtext-lib", branchName)
            }
            
            checkoutSCM(coreGitUrl, "xtext-core")
            if (verifyGitBranch("xtext-core", branchName)!=0){
               createGitBranch("xtext-core", branchName)
            }

            checkoutSCM(extrasGitUrl, "xtext-extras")
            if (verifyGitBranch("xtext-extras", branchName)!=0){
               createGitBranch("xtext-extras", branchName)
            }

            checkoutSCM(eclipseGitUrl, "xtext-eclipse")
            if (verifyGitBranch("xtext-eclipse", branchName)!=0){
               createGitBranch("xtext-eclipse", branchName)
            }

            checkoutSCM(ideaGitUrl, "xtext-idea")
            if (verifyGitBranch("xtext-idea", branchName)!=0){
               createGitBranch("xtext-idea", branchName)
            }

            checkoutSCM(webGitUrl, "xtext-web")
            if (verifyGitBranch("xtext-web", branchName)!=0){
               createGitBranch("xtext-web", branchName)
            }

            checkoutSCM(mavenGitUrl, "xtext-maven")
            if (verifyGitBranch("xtext-maven", branchName)!=0){
               createGitBranch("xtext-maven", branchName)
            }

            checkoutSCM(xtendGitUrl, "xtext-xtend")
            if (verifyGitBranch("xtext-lib", branchName)!=0){
               createGitBranch("xtext-xtend", branchName)
            }

            checkoutSCM(umbrellaGitUrl, "xtext-umbrella")	
            if (verifyGitBranch("xtext-umbrella", branchName)!=0){
               createGitBranch("xtext-umbrella", branchName)
            }
	}
	 }
	
	stage('adjust_pipeline') {
	   withCredentials([usernamePassword(credentialsId: 'adminCred', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
	   sh """
	     pwd
	     ls -la
	     ./adjustPipelines.sh $BRANCHNAME
	   """
	   }
	}

        stage('prepare_xtext-umbrella') {
            pomZipVersionUpdate("xtext-umbrella", xtextVersion, "releng/org.eclipse.xtext.sdk.p2-repository/pom.xml")

	
        }
        stage('prepare_xtext-lib') {
	   gradleVersionUpdate("xtext-lib", xtextVersion)
           changePomDependencyVersion("$workspace/xtext-lib/releng/pom.xml")
        }

        stage('prepare_xtext-core') {
           gradleVersionUpdate("xtext-core", xtextVersion)
           changePomDependencyVersion("$workspace/xtext-core/releng/pom.xml")
        }
        
        stage('prepare_xtext-extras') {
           gradleVersionUpdate("xtext-extras", xtextVersion)
           changePomDependencyVersion("$workspace/xtext-extras/releng/pom.xml")
	}
        
        stage('prepare_xtext-eclipse') {
	
        }
        
        stage('prepare_xtext-idea') {
           gradleVersionUpdate("xtext-idea", xtextVersion)
	}
        
        stage('prepare_xtext-web') {
           gradleVersionUpdate("xtext-web", xtextVersion)
	}

        stage('prepare_xtext-maven') {
	   pomVersionUpdate("xtext-maven", xtextVersion)
        }

        stage('prepare_xtext-xtend') {
           xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "maven-pom.xml")
           xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "org.eclipse.xtend.maven.android.archetype/pom.xml")
           xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "org.eclipse.xtend.maven.archetype/pom.xml")
           xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "org.eclipse.xtend.maven.plugin/pom.xml")
           xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "releng/org.eclipse.xtend.maven.parent/pom.xml")
        }
  }

def gradleVersionUpdate(path,xtext_version){
  def update_cmd
    dir(path) {
        update_cmd = sh (
            script: "sed -i -e \"s/version = '${xtext_version}-SNAPSHOT'/version = '${xtext_version}'/g\" gradle/versions.gradle",
            returnStdout: true
        ).trim()
    }
    return update_cmd
}

def changePomDependencyVersion(pomFile){
    println "Pom File to process: "+pomFile
    def xmlFromFile = new File(pomFile)
    def pom = new XmlSlurper( false, false ).parseText(xmlFromFile.getText())
    pom.dependencies.dependency.each { dependency ->
       pom.dependencies.dependency.version="${dependency.version}".replace("-SNAPSHOT", "")
    }
    XmlUtil xmlUtil = new XmlUtil()
    xmlUtil.serialize(pom, new FileWriter(xmlFromFile))
}

def pomVersionUpdate(path,xtext_version){
  def update_cmd
    dir(path) {
        update_cmd = sh (
            script: "find ./ -type f -name \"pom.xml\" | xargs  sed -i -e \"s/${xtext_version}-SNAPSHOT/${xtext_version}/g\"",
	    returnStdout: true
        ).trim()
    }
    return update_cmd
}

def pomZipVersionUpdate(path,xtext_version, pomFile){
  def update_cmd
    dir(path) {
        update_cmd = sh (
            script: "sed -i -e 's/tofile=\(.*\)repository-${xtext_version}-SNAPSHOT.zip/tofile=\1repository-${xtext_version}.zip/g\' ${pomFile}",
	    returnStdout: true
        ).trim()
    }
    return update_cmd
}

def xtextXtendPomVersionUpdate(path,xtext_version, pomFile){
  def update_cmd
    dir(path) {
        update_cmd = sh (
            script: "sed -i -e \"s/${xtext_version}-SNAPSHOT/${xtext_version}/g\" ${pomFile}",
	    returnStdout: true
        ).trim()
    }
    return update_cmd
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


