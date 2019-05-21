pipeline {
  agent {
    kubernetes {
      label 'xtext-build-pod'
      defaultContainer 'jnlp'
      yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: jnlp
    image: 'eclipsecbi/jenkins-jnlp-agent'
    args: ['\$(JENKINS_SECRET)', '\$(JENKINS_NAME)']
    volumeMounts:
    - mountPath: /home/jenkins/.ssh
      name: volume-known-hosts
  - name: groovy
    image: 'groovy:jre11'
    tty: true
    command: ["/bin/bash"]
  volumes:
  - name: volume-known-hosts
    configMap:
      name: known-hosts
    '''
    }
  }

  parameters {
      string(name: 'XTEXT_VERSION', defaultValue: '2.17.0', description: 'Xtext version (without -SNAPSHOT suffix)')
      string(name: 'SOURCE_BRANCH', defaultValue: 'master', description: 'Source branch for checkout & create the release branches from')
      choice(name: 'RELEASE', choices: ['Beta','M1','M2','M3','RC1','RC2','GA'], description: 'Type of release to build')
      booleanParam(name: 'VERBOSE', defaultValue: false, description: 'Print additional verbose output (e.g. git diff)')
      booleanParam(name: 'DRY_RUN', defaultValue: false, description: 'Dry run mode')
  }

  options {
    buildDiscarder(logRotator(numToKeepStr:'5'))
    timeout(time: 90, unit: 'MINUTES')
  }
  
  environment {
    xtextVersion="${params.RELEASE != 'GA' ? params.XTEXT_VERSION+'.'+params.RELEASE : params.XTEXT_VERSION}"
    snapshotVersion="${params.XTEXT_VERSION}-SNAPSHOT"
    releaseType="${params.RELEASE}"
    baseGitURL='git@github.com:eclipse'
    tagName="v${xtextVersion}"
    repositoryNames = 'xtext-lib,xtext-core,xtext-extras,xtext-eclipse,xtext-xtend,xtext-maven,xtext-web,xtext-umbrella'
    // repositoryNames = 'xtext-umbrella'
    branchName="${params.RELEASE != 'GA' ? 'milestone_'+params.XTEXT_VERSION+'.'+params.RELEASE : 'release_'+params.XTEXT_VERSION}"
    gitUser="genie.xtext"
    gitEmail="genie.xtext@git.eclipse.org"
  }

  // TODO Make property XTEXT_VERSION obsolete. The base version can be retrieved from xtext-lib/gradle/version.gradle
  
  stages {
    stage('Prepare') {
      steps {
        // checkout xtext-build-tools
        checkout scm
        
        script {
          def git    = load 'git_functions.groovy'
  
          if (!xtextVersion.startsWith('2.')) {
            currentBuild.result = 'ABORTED'
            error('XTEXT_VERSION invalid')
          }
          println "xtext version to be released ${xtextVersion}"
          println "branch to be created ${branchName}"
          println "tag to be created ${tagName}"
    
          sshagent([CREDENTIAL_ID_GENIE_XTEXT_GITHUB]) {
            repositoryNames.split(',').each {
              if(fileExists("${it}/.git")) {
                dir(it) {
                      git.resetHard()
                      git.checkoutBranch(params.SOURCE_BRANCH)
                      git.pull(params.SOURCE_BRANCH)
                      // When release branch already exists, then delete it and create a new one
                      if (git.branchExists(branchName)) {
                        git.deleteBranch(branchName)
                      }
                }
              } else {
                sh "git clone -b ${params.SOURCE_BRANCH} --depth 1 --no-tags ${baseGitURL}/${it}.git"
              }
              dir(it) {
                sh """
                  git config user.name ${gitUser}
                  git config user.email ${gitEmail}
                """
                git.createBranch(branchName)
              }
            }

            // compile Groovy scripts, needs xtext-buildenv container
            container('groovy') {
              sh 'groovyc -d target *.groovy'
            }
          } // END sshagent
        } // END script
      } // END steps
    } // END stage
    
    stage('Modify') {
      steps {
        script {
          def pom    = load 'pom_changes.groovy'
          def gradle = load 'gradle_functions.groovy'
          def git    = load 'git_functions.groovy'
          def jenkinsfile = load 'jenkins_functions.groovy'
          
          sh "./adjustPipelines.sh $branchName"
          
          container('groovy') {
            repositoryNames.split(',').each {
              print "##### Preparing $it ########"
              dir(it) {
                // TODO: do not pass snapshotVersion. xtextVersion has to be enough.
                sh "groovy -cp $workspace/target $workspace/modify_files_for_release $it $xtextVersion $snapshotVersion $branchName"
                if (params.VERBOSE) {
                  container('jnlp') {
                    sh "git diff"
                  }
                }
              }
            }
          }

        } // END script
      } // END steps
    } // END stage
  
    stage('Commit & Push') {
      steps {
        script {
          def git    = load 'git_functions.groovy'
          
          repositoryNames.split(',').each {
            dir (it) {
              git.printChanges()
              git.commit("[release] version $xtextVersion")
              git.tag(tagName)
            }
          }
          if(!params.DRY_RUN){
            sshagent([CREDENTIAL_ID_GENIE_XTEXT_GITHUB]) {
              sh "echo pushing branch ${branchName}"
              repositoryNames.split(',').each {
                dir (it) {
                  git.push(branchName)
                }
              }
            }
            slackSend message: "RELEASE BRANCH '${branchName}' PREPARED.", botUser: true, channel: 'xtext-builds', color: '#00FF00'
          }
        } // END script
      } // END steps
    } // END stage
  } // END stages

} // END pipeline