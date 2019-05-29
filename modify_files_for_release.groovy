// TODO: Properly check input args and that working directory is xtext-lib git repo
def repoId = args[0]
def xtextVersion = args[1]
def snapshotVersion = args[2]
def branchName = args[3]

def pom    = new pom_changes()
def gradle = new gradle_functions()
def jenkinsfile = new jenkins_functions()

switch (repoId) {
  case 'xtext-lib' :
    gradle.gradleVersionUpdate(xtextVersion)
    pom.pomVersionUpdate('releng/org.eclipse.xtext.dev-bom/pom.xml', xtextVersion)
    pom.changePomDependencyVersion(xtextVersion, 'releng/pom.xml', snapshotVersion)
    break
  case 'xtext-core' :
    gradle.gradleVersionUpdate(xtextVersion)
    pom.changePomDependencyVersion(xtextVersion,'releng/pom.xml', snapshotVersion)
    pom.setProperty('releng/pom.xml', 'upstreamBranch', branchName)
    jenkinsfile.addDeclarativeUpstream('Jenkinsfile', 'xtext-lib', branchName)
    jenkinsfile.addDeclarativeUpstream('CBI.Jenkinsfile', 'xtext-lib', branchName)
    break
  case 'xtext-extras' :
    gradle.gradleVersionUpdate(xtextVersion)
    pom.changePomDependencyVersion(xtextVersion, 'releng/pom.xml', snapshotVersion)
    pom.setProperty('releng/pom.xml', 'upstreamBranch', branchName)
    jenkinsfile.addDeclarativeUpstream('Jenkinsfile', 'xtext-core', branchName)
    jenkinsfile.addDeclarativeUpstream('CBI.Jenkinsfile', 'xtext-core', branchName)
    break
  case 'xtext-eclipse' :
    jenkinsfile.addDeclarativeUpstream('Jenkinsfile', 'xtext-extras', branchName)
    jenkinsfile.addDeclarativeUpstream('CBI.Jenkinsfile', 'xtext-extras', branchName)
    break
  case 'xtext-web' :
    gradle.gradleVersionUpdate(xtextVersion)
    jenkinsfile.addDeclarativeUpstream('Jenkinsfile', 'xtext-extras', branchName)
    jenkinsfile.addDeclarativeUpstream('CBI.Jenkinsfile', 'xtext-extras', branchName)
    break
  case 'xtext-maven' :
    pom.pomVersionUpdate('org.eclipse.xtext.maven.parent/pom.xml', xtextVersion)
    pom.pomVersionUpdate('org.eclipse.xtext.maven.plugin/pom.xml', xtextVersion)
    pom.setProperty('org.eclipse.xtext.maven.parent/pom.xml', 'upstreamBranch', branchName)
    pom.setProperty('org.eclipse.xtext.maven.plugin/src/test/resources/it/generate/pom.xml', 'upstreamBranch', branchName)
    pom.setProperty('org.eclipse.xtext.maven.plugin/src/test/resources/it/generate/pom.xml', 'xtext-version', xtextVersion)
    // TODO Change to declarative
    jenkinsfile.addScriptedUpstream('Jenkinsfile', 'xtext-extras', branchName)
    jenkinsfile.addDeclarativeUpstream('CBI.Jenkinsfile', 'xtext-extras', branchName)
    break
  case 'xtext-xtend' :
    gradle.gradleVersionUpdate(xtextVersion)
    pom.pomVersionUpdate('maven-pom.xml', xtextVersion)
    pom.pomVersionUpdate('org.eclipse.xtend.maven.archetype/pom.xml', xtextVersion)
    pom.pomVersionUpdate('org.eclipse.xtend.maven.plugin/pom.xml', xtextVersion)
    pom.pomVersionUpdate('releng/org.eclipse.xtend.maven.parent/pom.xml', xtextVersion)
    pom.setProperty('releng/org.eclipse.xtend.maven.parent/pom.xml', 'upstreamBranch', branchName)
    pom.setProperty('org.eclipse.xtend.maven.plugin/src/test/resources/it/pom.xml', 'xtextVersion', xtextVersion)
    // TODO Change to declarative
    jenkinsfile.addScriptedUpstream('Jenkinsfile', 'xtext-eclipse', branchName)
    jenkinsfile.addDeclarativeUpstream('CBI.Jenkinsfile', 'xtext-eclipse', branchName)
    break
  case 'xtext-umbrella' :
    pom.pomZipVersionUpdate(xtextVersion, 'releng/org.eclipse.xtext.sdk.p2-repository/pom.xml', snapshotVersion)
    // TODO Change to declarative
    jenkinsfile.addScriptedUpstream('Jenkinsfile', 'xtext-xtend', branchName)
    jenkinsfile.addDeclarativeUpstream('CBI.Jenkinsfile', 'xtext-xtend', branchName)
    break
}
