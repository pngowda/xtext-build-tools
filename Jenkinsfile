import jenkins.model.*
import hudson.model.*
import groovy.xml.XmlUtil

node('master') {
	properties([
		[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '15']],
		parameters([
			string(name: 'XTEXT_VERSION', defaultValue: '2.17.0', description: 'Xtext version (without -SNAPSHOT suffix)'),
			choice(name: 'RELEASE', choices: ['Beta','M1','M2','M3','RC1','RC2','GA'], description: 'Type of release to build')
		])
	])

	deleteDir()
	def xtextVersion="${params.XTEXT_VERSION}"
	def snapshotVersion="${params.XTEXT_VERSION}-SNAPSHOT"
	def branchName="${params.BRANCHNAME}"
	def releaseType="${params.RELEASE}"
	def isBranchExist
	def baseGitURL="git@github.com:eclipse"
	if (!xtextVersion.startsWith("2.")) {
		currentBuild.result = 'ABORTED'
		error('XTEXT_VERSION invalid')
	
	}
	
	def isIntermediateRelease = releaseType != 'GA'
	if(isIntermediateRelease){
		xtextVersion=xtextVersion+"."+releaseType
		branchName="milestone_"+xtextVersion
	} else { // GA release
		xtextVersion=xtextVersion
		branchName="release_"+xtextVersion
	}

	def tagName="v${xtextVersion}"
	
	println "xtext version to be released " + xtextVersion
	println "branch to be created " + branchName
	println "tag to be created " + tagName
	
	def repositoryNames = ['xtext-lib' /*, 'xtext-core', 'xtext-extras', 'xtext-eclipse', 'xtext-xtend', 'xtext-maven', 'xtext-web', 'xtext-idea' */]
	
	def libGitUrl=baseGitURL+'xtext-lib.git'
	def coreGitUrl=baseGitURL+'xtext-core.git'
	def extrasGitUrl=baseGitURL+'xtext-extras.git'
	def eclipseGitUrl=baseGitURL+'xtext-eclipse.git'
	def ideaGitUrl=baseGitURL+'xtext-idea.git'
	def webGitUrl=baseGitURL+'xtext-web.git'
	def mavenGitUrl=baseGitURL+'xtext-maven.git'
	def xtendGitUrl=baseGitURL+'xtext-xtend.git'
	def umbrellaGitUrl=baseGitURL+'xtext-umbrella.git'

	def pomFunctions = load "${rootDir}/pom_changes.groovy"
	def gradleFunctions = load "${rootDir}/gradle_functions.groovy"
	def gitFunctions = load "${rootDir}/git_functions.groovy"
	
	stage('Checkout') {
		// checkout xtext-build-tools
		checkout scm

		def rootDir = pwd()
		
		repositoryNames.each {
			dir("${workspace}/${it}") { deleteDir() }
		}
		//dir("${workspace}/xtext-lib") { deleteDir() }
		//dir("${workspace}/xtext-core") { deleteDir() }
		//dir("${workspace}/xtext-extras") { deleteDir() }
		//dir("${workspace}/xtext-eclipse") { deleteDir() }
		//dir("${workspace}/xtext-idea") { deleteDir() }
		//dir("${workspace}/xtext-web") { deleteDir() }
		//dir("${workspace}/xtext-maven") { deleteDir() }
		//dir("${workspace}/xtext-xtend") { deleteDir() }
		//dir("${workspace}/xtext-umbrella") { deleteDir() }
		
		// make scripts executable
		sh("find . -type f -exec chmod 777 {} \\;")
	
		repositoryNames.each {
			git url: "${baseGitURL}/${it}.git", branch: 'master', credentialsId: 'a7dd6ae8-486e-4175-b0ef-b7bc82dc14a8'
			if (gitFunctions.verifyGitBranch(it, branchName)!=0){
				gitFunctions.createGitBranch(it, branchName)
			}
		}
		
		// git url: "${baseGitURL}/xtext-lib.git", branch: 'master', credentialsId: 'a7dd6ae8-486e-4175-b0ef-b7bc82dc14a8'
		
		
		//checkoutSCM(coreGitUrl, "xtext-core")
		//if (gitFunctions.verifyGitBranch("xtext-core", branchName)!=0){
		//	gitFunctions.createGitBranch("xtext-core", branchName)
		//}
        //
		//checkoutSCM(extrasGitUrl, "xtext-extras")
		//if (gitFunctions.verifyGitBranch("xtext-extras", branchName)!=0){
		//	gitFunctions.createGitBranch("xtext-extras", branchName)
		//}
        //
		//checkoutSCM(eclipseGitUrl, "xtext-eclipse")
		//if (gitFunctions.verifyGitBranch("xtext-eclipse", branchName)!=0){
		//	gitFunctions.createGitBranch("xtext-eclipse", branchName)
		//}
        //
		//checkoutSCM(ideaGitUrl, "xtext-idea")
		//if (gitFunctions.verifyGitBranch("xtext-idea", branchName)!=0){
		//	gitFunctions.createGitBranch("xtext-idea", branchName)
		//}
        //
		//checkoutSCM(webGitUrl, "xtext-web")
		//if (gitFunctions.verifyGitBranch("xtext-web", branchName)!=0){
		//	gitFunctions.createGitBranch("xtext-web", branchName)
		//}
        //
		//checkoutSCM(mavenGitUrl, "xtext-maven")
		//if (gitFunctions.verifyGitBranch("xtext-maven", branchName)!=0){
		//	gitFunctions.createGitBranch("xtext-maven", branchName)
		//}
        //
		//checkoutSCM(xtendGitUrl, "xtext-xtend")
		//if (gitFunctions.verifyGitBranch("xtext-lib", branchName)!=0){
		//	gitFunctions.createGitBranch("xtext-xtend", branchName)
		//}
        //
		//checkoutSCM(umbrellaGitUrl, "xtext-umbrella")	
		//if (gitFunctions.verifyGitBranch("xtext-umbrella", branchName)!=0){
		//	gitFunctions.createGitBranch("xtext-umbrella", branchName)
		//}
	}
	
	stage('Modify') {
		def rootDir = pwd()
		println rootDir
		
		sshagent(['29d79994-c415-4a38-9ab4-7463971ba682']) {
			sh """
			  pwd
			  ls -la
			  ./adjustPipelines.sh $branchName
			"""
		}
		
		//preparing xtext-lib
		print "##### Preparing xtext-lib ########"
		gradleFunctions.gradleVersionUpdate("xtext-lib", xtextVersion,snapshotVersion)
		pomFunctions.changePomDependencyVersion(xtextVersion, "$workspace/xtext-lib/releng/pom.xml", snapshotVersion)
		gitFunctions.getGitChanges("xtext-lib")
			
//		//preparing xtext-core
//		print "##### Preparing xtext-core ########"
//		gradleFunctions.gradleVersionUpdate("xtext-core", xtextVersion, snapshotVersion)
//		pomFunctions.changePomDependencyVersion(xtextVersion,"$workspace/xtext-core/releng/pom.xml", snapshotVersion)
//		gitFunctions.getGitChanges("xtext-core")
//		
//		//preparing xtext-extras
//		print "##### Preparing xtext-extras ########"
//		gradleFunctions.gradleVersionUpdate("xtext-extras", xtextVersion, snapshotVersion)
//		pomFunctions.changePomDependencyVersion(xtextVersion, "$workspace/xtext-extras/releng/pom.xml", snapshotVersion)
//		gitFunctions.getGitChanges("xtext-extras")
//		
//		//preparing xtext-eclipse
//		print "##### Preparing xtext-eclipse ########"
//		gitFunctions.getGitChanges("xtext-eclipse")
//		
//		//preparing xtext-idea
//		print "##### Preparing xtext-idea ########"
//		gradleFunctions.gradleVersionUpdate("xtext-idea", xtextVersion, snapshotVersion)
//		gitFunctions.getGitChanges("xtext-idea")
//		
//		//preparing xtext-web
//		print "##### Preparing xtext-web ########"
//		gradleFunctions.gradleVersionUpdate("xtext-web", xtextVersion, snapshotVersion)
//		gitFunctions.getGitChanges("xtext-web")
//		
//		//preparing xtext-maven
//		print "##### Preparing xtext-maven ########"
//		pomFunctions.pomVersionUpdate("xtext-maven", xtextVersion, snapshotVersion)
//		gitFunctions.getGitChanges("xtext-maven")
//		
//		//preparing xtext-xtend
//		print "##### Preparing xtext-xtend ########"
//		pomFunctions.xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "maven-pom.xml", snapshotVersion)
//		pomFunctions.xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "org.eclipse.xtend.maven.android.archetype/pom.xml", snapshotVersion)
//		pomFunctions.xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "org.eclipse.xtend.maven.archetype/pom.xml", snapshotVersion)
//		pomFunctions.xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "org.eclipse.xtend.maven.plugin/pom.xml", snapshotVersion)
//		pomFunctions.xtextXtendPomVersionUpdate("xtext-xtend", xtextVersion, "releng/org.eclipse.xtend.maven.parent/pom.xml", snapshotVersion)
//		gitFunctions.getGitChanges("xtext-xtend")
//
//		//preparing xtext-umbrella
//		print "###### Preparing xtext-umbrella ########"
//		pomFunctions.pomZipVersionUpdate("xtext-umbrella", xtextVersion, "releng/org.eclipse.xtext.sdk.p2-repository/pom.xml", snapshotVersion)
//		gitFunctions.getGitChanges("xtext-umbrella")
	}

	stage('Commit & Push') {
		def rootDir = pwd()
		gitFunctions.commitGitChanges("xtext-lib", xtextVersion, "[release] version")
//		gitFunctions.commitGitChanges("xtext-core", xtextVersion, "[release] version")
//		gitFunctions.commitGitChanges("xtext-extras", xtextVersion, "[release] version")
//		gitFunctions.commitGitChanges("xtext-eclipse", xtextVersion, "[release] version")
//		gitFunctions.commitGitChanges("xtext-idea", xtextVersion, "[release] version")
//		gitFunctions.commitGitChanges("xtext-web", xtextVersion, "[release] version")
//		gitFunctions.commitGitChanges("xtext-maven", xtextVersion, "[release] version")
//		gitFunctions.commitGitChanges("xtext-xtend", xtextVersion, "[release] version")
//		gitFunctions.commitGitChanges("xtext-umbrella", xtextVersion, "[release] version")

		sshagent(['a7dd6ae8-486e-4175-b0ef-b7bc82dc14a8']) {
			gitFunctions.pushGitChanges("xtext-lib"     , branchName)
//			gitFunctions.pushGitChanges("xtext-core"    , branchName)
//			gitFunctions.pushGitChanges("xtext-extras"  , branchName)
//			gitFunctions.pushGitChanges("xtext-eclipse" , branchName)
//			gitFunctions.pushGitChanges("xtext-idea"    , branchName)
//			gitFunctions.pushGitChanges("xtext-web"     , branchName)
//			gitFunctions.pushGitChanges("xtext-maven"   , branchName)
//			gitFunctions.pushGitChanges("xtext-xtend"   , branchName)
//			gitFunctions.pushGitChanges("xtext-umbrella", branchName)
		}
	}
}

def checkoutSCM(urlPath, wrkDir){
	checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'UserExclusion', excludedUsers: 'pngowda'],[$class: 'RelativeTargetDirectory', relativeTargetDir: wrkDir]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'adminCred',url: urlPath]]])
}


