def getLatestArtifactVersion(groupId, artifactId) {
  return sh (script: "curl -s http://search.maven.org/solrsearch/select?q=g:\"${groupId}\"+AND+a:\"${artifactId}\" |grep -Po 'latestVersion.:.\\K[^\"]*'", returnStdout: true).trim()
}

def getLatestReleaseFromGitHubRepository (owner, repository) {
  return sh (script: "curl -s curl https://api.github.com/repos/${owner}/${repository}/releases/latest | grep -Po '\"name\"[^\\d]*\\K[\\d\\.]*'", returnStdout: true).trim()
}

/**
 * Fetch the latest Orbit repository URL
 * @param buildType R=Release, S=Stable, I=Integration
 */
def getLatestOrbitUrl (buildType) {
  assert ['R','S','I'].contains(buildType)
  def repoID= sh (script: "curl -s https://download.eclipse.org/tools/orbit/downloads/ |grep -m1 -Po 'drops/\\K${buildType}\\d+'", returnStdout: true).trim()
  def repoURL = "http://download.eclipse.org/tools/orbit/downloads/drops/${repoID}/repository"
  return repoURL
}

def getXtextTychoVersion (branch) {
  return sh (script: "curl -s https://raw.githubusercontent.com/eclipse/xtext-eclipse/${branch}/releng/org.eclipse.xtext.tycho.parent/pom.xml |grep -Po '<tycho-version>\\K[^<]*'", returnStdout: true).trim()
}

def getXtextGradlePluginVersion (branch) {
  return sh (script: "curl -s https://raw.githubusercontent.com/eclipse/xtext-lib/${branch}/gradle/versions.gradle |grep -Po 'xtext_gradle_plugin[^\\d]*\\K[\\d\\.]*'", returnStdout: true).trim()
}

def getXtextGradleVersion (branch) {
  return sh (script: "curl -s https://raw.githubusercontent.com/eclipse/xtext-lib/${branch}/gradle/wrapper/gradle-wrapper.properties |grep -Po 'distributionUrl=.*/gradle-\\K[\\d\\.]*'", returnStdout: true).trim()
}

def getXtextBootstrapVersion (branch) {
  return sh (script: "curl -s https://raw.githubusercontent.com/eclipse/xtext-lib/${branch}/gradle/versions.gradle |grep -Po 'xtext_bootstrap[^\\d]*\\K[\\d\\.]*'", returnStdout: true).trim()
}

return this
