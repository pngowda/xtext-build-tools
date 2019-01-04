import jenkins.model.*
import hudson.model.*
import groovy.xml.XmlUtil

node('master') {
	   deleteDir()
           def snapshotVersion="${params.SNAPSHOT_VERSION}"
           def xtextVersion=snapshotVersion.split('-')[0]
           def branchName="${params.BRANCHNAME}"
           def tagName="${params.TAGNAME}"
	   def releaseType="${params.RELEASE_TYPE}"
           def variant="${params.VARIANT}"
           def isBranchExist
	   def baseGitURL='https://github.com/pngowda/'
           
           println snapshotVersion
	   println xtextVersion
           println branchName
           println tagName
	   println releaseType
           println variant

	    def libGitUrl=baseGitURL+'xtext-lib.git'
	    def coreGitUrl=baseGitURL+'xtext-core.git'
	    def extrasGitUrl=baseGitURL+'xtext-extras.git'
	    def eclipseGitUrl=baseGitURL+'xtext-eclipse.git'
	    def ideaGitUrl=baseGitURL+'xtext-idea.git'
	    def webGitUrl=baseGitURL+'xtext-web.git'
	    def mavenGitUrl=baseGitURL+'xtext-maven.git'
	    def xtendGitUrl=baseGitURL+'xtext-xtend.git'
	    def umbrellaGitUrl=baseGitURL+'xtext-umbrella.git'
	
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
	     ./adjustPipelines.sh $branchName
	   """
	   }
	}

	stage('release_preparation_xtext-repos') {
            //preparing xtext-umbrella
	    print "###### Preparing xtext-umbrella ########"
	    if(releaseType=="Release" || releaseType=="Milestone"){
	       pomZipVersionUpdate("xtext-umbrella", xtextVersion, "releng/org.eclipse.xtext.sdk.p2-repository/pom.xml")
	       getGitChanges("xtext-umbrella")
	    }
	   
	    //preparing xtext-lib
	    print "##### Preparing xtext-lib ########"
	    if(releaseType=="Release" || releaseType=="Milestone"){
	       gradleVersionUpdate("xtext-lib", xtextVersion)
               changePomDependencyVersion("$workspace/xtext-lib/releng/pom.xml")
	       getGitChanges("xtext-lib")
	    }	
	    //preparing xtext-core
	    print "##### Preparing xtext-core ########"
	    if(releaseType=="Release" || releaseType=="Milestone"){
	       gradleVersionUpdate("xtext-core", xtextVersion)
               changePomDependencyVersion("$workspace/xtext-core/releng/pom.xml")
	       getGitChanges("xtext-core")
	    }
	    //preparing xtext-extras
	    print "##### Preparing xtext-extras ########"
	    if(releaseType=="Release" || releaseType=="Milestone"){
	       gradleVersionUpdate("xtext-extras", xtextVersion)
               changePomDependencyVersion("$workspace/xtext-extras/releng/pom.xml")
	       getGitChanges("xtext-extras")
	    }
	    //preparing xtext-eclipse
	    print "##### Preparing xtext-eclipse ########"
	    if(releaseType=="Release" || releaseType=="Milestone"){
	       getGitChanges("xtext-eclipse")
	    }	
	    //preparing xtext-idea
	    print "##### Preparing xtext-idea ########"
	    if(releaseType=="Release" || releaseType=="Milestone"){
	       gradleVersionUpdate("xtext-idea", xtextVersion)
	       getGitChanges("xtext-idea")
	    }
	    //preparing xtext-web
	    print "##### Preparing xtext-web ########"
	    if(releaseType=="Release" || releaseType=="Milestone"){
	       gradleVersionUpdate("xtext-web", xtextVersion)
	       getGitChanges("xtext-web")
	    }
	    //preparing xtext-maven
	    print "##### Preparing xtext-maven ########"
	    if(releaseType=="Release" || releaseType=="Milestone"){
	       pomVersionUpdate("xtext-maven", xtextVersion)
	       getGitChanges("xtext-maven")
	    }
	   //preparing xtext-xtend
	   print "##### Preparing xtext-xtend ########"
	   if(releaseType=="Release" || releaseType=="Milestone"){
	      xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "maven-pom.xml")
              xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "org.eclipse.xtend.maven.android.archetype/pom.xml")
              xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "org.eclipse.xtend.maven.archetype/pom.xml")
              xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "org.eclipse.xtend.maven.plugin/pom.xml")
              xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "releng/org.eclipse.xtend.maven.parent/pom.xml")
	      getGitChanges("xtext-xtend")
	   }
	}
	
        stage('Commit_GIT_Changes') {
           commitGitChanges("xtext-umbrella", xtextVersion, "[release] version")
           commitGitChanges("xtext-lib", xtextVersion, "[release] version")
           commitGitChanges("xtext-core", xtextVersion, "[release] version")
           commitGitChanges("xtext-extras", xtextVersion, "[release] version")
           commitGitChanges("xtext-eclipse", xtextVersion, "[release] version")
           commitGitChanges("xtext-idea", xtextVersion, "[release] version")
           commitGitChanges("xtext-web", xtextVersion, "[release] version")
           commitGitChanges("xtext-maven", xtextVersion, "[release] version")
           commitGitChanges("xtext-xtend", xtextVersion, "[release] version")
        }

      
      stage('Push_GIT_Changes') {
      /*  
	withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'adminCred', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD']]) {
         dir("xtext-umbrella") {
	   String encoded_password = java.net.URLEncoder.encode(env.GIT_PASSWORD, "UTF-8")
	    //sh("git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/${GIT_USERNAME}/xtext-umbrella.git")
         }
	 //}
           //pushGitChanges("xtext-umbrella", branchName)
           //pushGitChanges("xtext-lib", xtextVersion, "[release] version")
           //pushGitChanges("xtext-core", xtextVersion, "[release] version")
           //pushGitChanges("xtext-extras", xtextVersion, "[release] version")
           //pushGitChanges("xtext-eclipse", xtextVersion, "[release] version")
           //pushGitChanges("xtext-idea", xtextVersion, "[release] version")
           //pushGitChanges("xtext-web", xtextVersion, "[release] version")
           //pushGitChanges("xtext-maven", xtextVersion, "[release] version")
           //pushGitChanges("xtext-xtend", xtextVersion, "[release] version")
      */
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
            script: "sed -i -r 's/tofile=(.*)repository-${xtext_version}-SNAPSHOT.zip/tofile=\\1repository-${xtext_version}.zip/g\' ${pomFile}",
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

def pushGitChanges(path, branch, credentialsId=null, remote = 'origin') {
    dir(path) {
           sh script: "git push ${remote} ${branch}"
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
  checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: wrkDir]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'adminCred',url: urlPath]]])
}


