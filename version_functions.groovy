def getLatestArtifactVersion(groupId, artifactId) {
  return sh (script: "curl -s http://search.maven.org/solrsearch/select?q=g:\"${groupId}\"+AND+a:\"${artifactId}\" |grep -Po 'latestVersion.:.\\K[^\"]*'", returnStdout: true).trim()
}

def getXtextTychoVersion (branch) {
  return sh (script: "curl -s https://raw.githubusercontent.com/eclipse/xtext-eclipse/master/releng/org.eclipse.xtext.tycho.parent/pom.xml |grep -Po '<tycho-version>\\K[^<]*'", returnStdout: true).trim()
}

def getXtextGradlePluginVersion (branch) {
  return sh (script: "curl -s https://raw.githubusercontent.com/eclipse/xtext-lib/master/gradle/versions.gradle |grep -Po 'xtext_gradle_plugin[^\\d]*\\K[\\d\\.]*'", returnStdout: true).trim()
}

return this
