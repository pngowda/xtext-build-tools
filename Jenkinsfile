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
           if(releaseType=="Release"){
              xtextVersion=xtextVersion
	      branchName="release_"+xtextVersion
	   }
	   if(releaseType=="Milestone"){
	      xtextVersion=xtextVersion+"."+variant
	      branchName="milestone_"+xtextVersion
	   }
	   println "xtext version to be released " + xtextVersion
	   println "branch to be created " + branchName
	
	
	
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
		println "checking out"
		checkout scm
	}
	
	stage('checkout-xtext-repos') {
	def rootDir = pwd()
	def gitFunctions = load "${rootDir}/git_functions.groovy"
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
            if (gitFunctions.verifyGitBranch("xtext-lib", branchName)!=0){
               gitFunctions.createGitBranch("xtext-lib", branchName)
            }
            
            checkoutSCM(coreGitUrl, "xtext-core")
            if (gitFunctions.verifyGitBranch("xtext-core", branchName)!=0){
               gitFunctions.createGitBranch("xtext-core", branchName)
            }

            checkoutSCM(extrasGitUrl, "xtext-extras")
            if (gitFunctions.verifyGitBranch("xtext-extras", branchName)!=0){
               gitFunctions.createGitBranch("xtext-extras", branchName)
            }

            checkoutSCM(eclipseGitUrl, "xtext-eclipse")
            if (gitFunctions.verifyGitBranch("xtext-eclipse", branchName)!=0){
               gitFunctions.createGitBranch("xtext-eclipse", branchName)
            }

            checkoutSCM(ideaGitUrl, "xtext-idea")
            if (gitFunctions.verifyGitBranch("xtext-idea", branchName)!=0){
               gitFunctions.createGitBranch("xtext-idea", branchName)
            }

            checkoutSCM(webGitUrl, "xtext-web")
            if (gitFunctions.verifyGitBranch("xtext-web", branchName)!=0){
               gitFunctions.createGitBranch("xtext-web", branchName)
            }

            checkoutSCM(mavenGitUrl, "xtext-maven")
            if (gitFunctions.verifyGitBranch("xtext-maven", branchName)!=0){
               gitFunctions.createGitBranch("xtext-maven", branchName)
            }

            checkoutSCM(xtendGitUrl, "xtext-xtend")
            if (gitFunctions.verifyGitBranch("xtext-lib", branchName)!=0){
               gitFunctions.createGitBranch("xtext-xtend", branchName)
            }

            checkoutSCM(umbrellaGitUrl, "xtext-umbrella")	
            if (gitFunctions.verifyGitBranch("xtext-umbrella", branchName)!=0){
               gitFunctions.createGitBranch("xtext-umbrella", branchName)
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
	    def rootDir = pwd()
	    println rootDir
	    def pomFunctions = load "${rootDir}/pom_changes.groovy"
	    def gradleFunctions = load "${rootDir}/gradle_functions.groovy"
	    def gitFunctions = load "${rootDir}/git_functions.groovy"
            //preparing xtext-umbrella
	    print "###### Preparing xtext-umbrella ########"
	    if(releaseType=="Release" || releaseType=="Milestone"){
	       pomFunctions.pomZipVersionUpdate("xtext-umbrella", xtextVersion, "releng/org.eclipse.xtext.sdk.p2-repository/pom.xml")
	       gitFunctions.getGitChanges("xtext-umbrella")
	    }
	   
	    //preparing xtext-lib
	    print "##### Preparing xtext-lib ########"
	    if(releaseType=="Release" || releaseType=="Milestone"){
	       gradleFunctions.gradleVersionUpdate("xtext-lib", xtextVersion)
               pomFunctions.changePomDependencyVersion(xtextVersion, "$workspace/xtext-lib/releng/pom.xml")
	       gitFunctions.getGitChanges("xtext-lib")
	    }	
	    //preparing xtext-core
	    print "##### Preparing xtext-core ########"
	    if(releaseType=="Release" || releaseType=="Milestone"){
	       gradleFunctions.gradleVersionUpdate("xtext-core", xtextVersion)
               pomFunctions.changePomDependencyVersion(xtextVersion,"$workspace/xtext-core/releng/pom.xml")
	       gitFunctions.getGitChanges("xtext-core")
	    }
	    //preparing xtext-extras
	    print "##### Preparing xtext-extras ########"
	    if(releaseType=="Release" || releaseType=="Milestone"){
	       gradleFunctions.gradleVersionUpdate("xtext-extras", xtextVersion)
               pomFunctions.changePomDependencyVersion(xtextVersion, "$workspace/xtext-extras/releng/pom.xml")
	       gitFunctions.getGitChanges("xtext-extras")
	    }
	    //preparing xtext-eclipse
	    print "##### Preparing xtext-eclipse ########"
	    if(releaseType=="Release" || releaseType=="Milestone"){
	       gitFunctions.getGitChanges("xtext-eclipse")
	    }	
	    //preparing xtext-idea
	    print "##### Preparing xtext-idea ########"
	    if(releaseType=="Release" || releaseType=="Milestone"){
	       gradleFunctions.gradleVersionUpdate("xtext-idea", xtextVersion)
	       gitFunctions.getGitChanges("xtext-idea")
	    }
	    //preparing xtext-web
	    print "##### Preparing xtext-web ########"
	    if(releaseType=="Release" || releaseType=="Milestone"){
	       gradleFunctions.gradleVersionUpdate("xtext-web", xtextVersion)
	       gitFunctions.getGitChanges("xtext-web")
	    }
	    //preparing xtext-maven
	    print "##### Preparing xtext-maven ########"
	    if(releaseType=="Release" || releaseType=="Milestone"){
	       pomFunctions.pomVersionUpdate("xtext-maven", xtextVersion)
	       gitFunctions.getGitChanges("xtext-maven")
	    }
	   //preparing xtext-xtend
	   print "##### Preparing xtext-xtend ########"
	   if(releaseType=="Release" || releaseType=="Milestone"){
	      pomFunctions.xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "maven-pom.xml")
              pomFunctions.xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "org.eclipse.xtend.maven.android.archetype/pom.xml")
              pomFunctions.xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "org.eclipse.xtend.maven.archetype/pom.xml")
              pomFunctions.xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "org.eclipse.xtend.maven.plugin/pom.xml")
              pomFunctions.xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "releng/org.eclipse.xtend.maven.parent/pom.xml")
	      gitFunctions.getGitChanges("xtext-xtend")
	   }
	}
	
        stage('Commit_GIT_Changes') {
	   def rootDir = pwd()
           def gitFunctions = load "${rootDir}/git_functions.groovy"
           gitFunctions.commitGitChanges("xtext-umbrella", xtextVersion, "[release] version")
           gitFunctions.commitGitChanges("xtext-lib", xtextVersion, "[release] version")
           gitFunctions.commitGitChanges("xtext-core", xtextVersion, "[release] version")
           gitFunctions.commitGitChanges("xtext-extras", xtextVersion, "[release] version")
           gitFunctions.commitGitChanges("xtext-eclipse", xtextVersion, "[release] version")
           gitFunctions.commitGitChanges("xtext-idea", xtextVersion, "[release] version")
           gitFunctions.commitGitChanges("xtext-web", xtextVersion, "[release] version")
           gitFunctions.commitGitChanges("xtext-maven", xtextVersion, "[release] version")
           gitFunctions.commitGitChanges("xtext-xtend", xtextVersion, "[release] version")
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








def checkoutSCM(urlPath, wrkDir){
  checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'UserExclusion', excludedUsers: 'pngowda'],[$class: 'RelativeTargetDirectory', relativeTargetDir: wrkDir]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'adminCred',url: urlPath]]])
}


