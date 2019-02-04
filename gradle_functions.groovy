def gradleVersionUpdate(xtext_version, snapshot_version) {
  def update_cmd
  update_cmd = sh (
      script: "sed -i -e \"s/version = '${snapshot_version}'/version = '${xtext_version}'/g\" gradle/versions.gradle",
      returnStdout: true
  ).trim()
  return update_cmd
}

return this
