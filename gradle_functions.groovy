def void gradleVersionUpdate(xtext_version) {
  println "Updating version in gradle/versions.gradle"
  def file = new File('gradle/versions.gradle')
  newContent = file.text.replaceFirst("version = '.*'", "version = '${xtext_version}'")
  file.write(newContent)
}

/**
 * Get current Xtext version. The version is retrieved by querying the SDK parent POM from xtext-umbrella.
 * @param branch Git branch
 * @return Xtext version
 */
def boolean getXtextVersion(branch='master') {
  // grep usage see grep usage see see https://stackoverflow.com/a/16675391/512227
  return sh (script: "curl -s https://raw.githubusercontent.com/eclipse/xtext-umbrella/${branch}/releng/org.eclipse.xtext.sdk.parent/pom.xml | grep -m1 -Po \"<version>\\K[^-]*\"", returnStdout: true).trim()
}

return this
