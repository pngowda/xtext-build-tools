def gradleVersionUpdate(xtext_version, snapshot_version) {
  def update_cmd
  update_cmd = sh (
      script: "sed -i -e \"s/version = '${snapshot_version}'/version = '${xtext_version}'/g\" gradle/versions.gradle",
      returnStdout: true
  ).trim()
  return update_cmd
}

/**
 * @param versionGradlePath path to versions.gradle file
 * @return Xtext version
 */
def boolean getXtextVersion(versionGradlePath) {
  // grep usage see grep usage see see https://stackoverflow.com/a/16675391/512227
  return sh (script: "grep -Po 'version = \'\\K[^-]*\' ${versionGradlePath}", returnStdout: true).trim()
}

return this
