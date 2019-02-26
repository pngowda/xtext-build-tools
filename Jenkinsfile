import jenkins.model.*
import hudson.model.*
import groovy.xml.XmlUtil

node {
  
  properties([
    [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '15']],
    parameters([
      string(name: 'XTEXT_VERSION', defaultValue: '2.17.0', description: 'Xtext version (without -SNAPSHOT suffix)'),
      choice(name: 'RELEASE', choices: ['Beta','M1','M2','M3','RC1','RC2','GA'], description: 'Type of release to build'),
      booleanParam(name: 'DRY_RUN', defaultValue: false, description: 'Dry run mode')
    ])
  ])

  // TODO Make property XTEXT_VERSION obsolete. The base version can be retrieved from xtext-lib/gradle/version.gradle
  if(params.DRY_RUN){
    println "##### NOTE: Running script in dry run mode, changes will not be pushed to git repos ######"
  }
  def xtextVersion="${params.XTEXT_VERSION}"
  if (!xtextVersion.startsWith('2.')) {
    currentBuild.result = 'ABORTED'
    error('XTEXT_VERSION invalid')
  }

  def snapshotVersion="${params.XTEXT_VERSION}-SNAPSHOT"
  def releaseType="${params.RELEASE}"
  def baseGitURL='git@github.com:eclipse'
  
  def branchName
  def isIntermediateRelease = releaseType != 'GA'
  if(isIntermediateRelease){
    xtextVersion="${xtextVersion}.${releaseType}"
    branchName="milestone_${xtextVersion}"
  } else { // GA release
    branchName="release_${xtextVersion}"
  }

  def tagName="v${xtextVersion}"
  
  println "xtext version to be released ${xtextVersion}"
  println "branch to be created ${branchName}"
  println "tag to be created ${tagName}"
  
  // list of Xtext repository names
  def repositoryNames = ['xtext-lib' , 'xtext-core', 'xtext-extras', 'xtext-eclipse', 'xtext-xtend', 'xtext-maven', 'xtext-web', 'xtext-idea', 'xtext-umbrella']
  
  stage('Checkout') {
    // checkout xtext-build-tools
    checkout scm
    
    sh "ls -al ."

    def gitFunctions    = load 'git_functions.groovy'
    repositoryNames.each {
      dir(it) {
        if(fileExists("/")){ 
          gitFunctions.gitResetHard()
          gitFunctions.gitCheckoutMaster()
        }
      }
      dir(it) {
        git url: "${baseGitURL}/${it}.git", branch: 'master', credentialsId: 'a7dd6ae8-486e-4175-b0ef-b7bc82dc14a8'
      }
      // When release branch already exists, then delete it and create a new one
      if (gitFunctions.verifyGitBranch(it, branchName)!=0){
        sh "git branch -D ${branchName}"
      }
      gitFunctions.createGitBranch(it, branchName)
    }
  }
  
  stage('Modify') {
    def pom    = load 'pom_changes.groovy'
    def gradle = load 'gradle_functions.groovy'
    def git    = load 'git_functions.groovy'
    def jenkinsfile = load 'jenkins_functions.groovy'
  
    sh "./adjustPipelines.sh $branchName"
    
    //preparing xtext-lib
    print "##### Preparing xtext-lib ########"
    dir('xtext-lib') {
      // do not pass snapshotVersion. Just set xtextVersion.
      gradle.gradleVersionUpdate(xtextVersion,snapshotVersion)
      pom.pomVersionUpdate("$workspace/xtext-lib/releng/org.eclipse.xtext.dev-bom/pom.xml", xtextVersion)
      pom.changePomDependencyVersion(xtextVersion, "$workspace/xtext-lib/releng/pom.xml", snapshotVersion)
    }
      
    //preparing xtext-core
    print "##### Preparing xtext-core ########"
    dir('xtext-core') {
      gradle.gradleVersionUpdate(xtextVersion, snapshotVersion)
      pom.changePomDependencyVersion(xtextVersion,"$workspace/xtext-core/releng/pom.xml", snapshotVersion)
      pom.setUpstreamBranch("$workspace/xtext-core/releng/pom.xml", branchName)
      jenkinsfile.addUpstream("$workspace/xtext-core/Jenkinsfile", 'xtext-lib')
    }
    
    //preparing xtext-extras
    print "##### Preparing xtext-extras ########"
    dir('xtext-extras') {
      gradle.gradleVersionUpdate(xtextVersion, snapshotVersion)
      pom.changePomDependencyVersion(xtextVersion, "$workspace/xtext-extras/releng/pom.xml", snapshotVersion)
      pom.setUpstreamBranch("$workspace/xtext-extras/releng/pom.xml", branchName)
      jenkinsfile.addUpstream("$workspace/xtext-extras/Jenkinsfile", 'xtext-core')
    }
    
    //preparing xtext-eclipse
    print "##### Preparing xtext-eclipse ########"
    dir('xtext-eclipse') {
      jenkinsfile.addUpstream("$workspace/xtext-eclipse/Jenkinsfile", 'xtext-extras')
    }
    
    //preparing xtext-idea
    print "##### Preparing xtext-idea ########"
    dir('xtext-idea') {
      gradle.gradleVersionUpdate(xtextVersion, snapshotVersion)
      jenkinsfile.addUpstream("$workspace/xtext-idea/Jenkinsfile", 'xtext-xtend')
    }
    
    //preparing xtext-web
    print "##### Preparing xtext-web ########"
    dir('xtext-web') {
      gradle.gradleVersionUpdate(xtextVersion, snapshotVersion)
      jenkinsfile.addUpstream("$workspace/xtext-web/Jenkinsfile", 'xtext-extras')
    }
    
    //preparing xtext-maven
    print "##### Preparing xtext-maven ########"
    dir('xtext-maven') {
      pom.pomVersionUpdate("$workspace/xtext-maven/org.eclipse.xtext.maven.parent/pom.xml", xtextVersion)
      pom.pomVersionUpdate("$workspace/xtext-maven/org.eclipse.xtext.maven.plugin/pom.xml", xtextVersion)
      pom.setUpstreamBranch("$workspace/xtext-maven/org.eclipse.xtext.maven.parent/pom.xml", branchName)
      pom.setProperty("$workspace/xtext-maven/org.eclipse.xtext.maven.plugin/src/test/resources/it/generate/pom.xml", 'xtext-version', xtextVersion)
      jenkinsfile.addUpstream("$workspace/xtext-maven/Jenkinsfile", 'xtext-extras')
    }
    
    //preparing xtext-xtend
    print "##### Preparing xtext-xtend ########"
    dir('xtext-xtend') {
      gradle.gradleVersionUpdate(xtextVersion, snapshotVersion)
      pom.xtextXtendPomVersionUpdate(xtextVersion, "maven-pom.xml", snapshotVersion)
      pom.xtextXtendPomVersionUpdate(xtextVersion, "org.eclipse.xtend.maven.archetype/pom.xml", snapshotVersion)
      pom.xtextXtendPomVersionUpdate(xtextVersion, "org.eclipse.xtend.maven.plugin/pom.xml", snapshotVersion)
      pom.xtextXtendPomVersionUpdate(xtextVersion, "releng/org.eclipse.xtend.maven.parent/pom.xml", snapshotVersion)
      pom.setProperty("org.eclipse.xtend.maven.plugin/src/test/resources/it/pom.xml", 'xtextVersion', xtextVersion)
      jenkinsfile.addUpstream("$workspace/xtext-xtend/Jenkinsfile", 'xtext-eclipse')
    }

    //preparing xtext-umbrella
    print "###### Preparing xtext-umbrella ########"
    dir('xtext-umbrella') {
      pom.pomZipVersionUpdate(xtextVersion, "releng/org.eclipse.xtext.sdk.p2-repository/pom.xml", snapshotVersion)
      jenkinsfile.addUpstream("$workspace/xtext-umbrella/Jenkinsfile", 'xtext-xtend')
    }
  }


  stage('Commit & Push') {
    def gitFunctions    = load 'git_functions.groovy'
    
    repositoryNames.each {
      gitFunctions.getGitChanges(it)
      gitFunctions.commitGitChanges(it, xtextVersion, "[release] version")
      gitFunctions.tagGit(it, tagName)
    }
    if(!params.DRY_RUN){
      sshagent(['a7dd6ae8-486e-4175-b0ef-b7bc82dc14a8']) {
        sh "echo pushing branch ${branchName}"
        repositoryNames.each {
          gitFunctions.pushGitChanges(it, branchName)
        }
      }
    }
    
    // slackSend message: "RELEASE BRANCH '${branchName}' PREPARED.", baseUrl: 'https://itemis.slack.com/services/hooks/jenkins-ci/', botUser: true, channel: 'xtext-builds', color: '#00FF00', token: '1vbkhv8Hwlp3ausuFGj1BdJb'
  }
}

