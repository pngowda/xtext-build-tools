def gradleVersionUpdate(xtext_version, snapshot_version) {
  def update_cmd
  update_cmd = sh (
      script: "sed -i -e \"s/version = '${snapshot_version}'/version = '${xtext_version}'/g\" gradle/versions.gradle",
      returnStdout: true
  ).trim()
  return update_cmd
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
