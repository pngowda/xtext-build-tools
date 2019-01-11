

def gradleVersionUpdate(path,xtext_version){
  def update_cmd
    dir(path) {
        update_cmd = sh (
            script: "sed -i -e \"s/version = '${xtext_version}-SNAPSHOT'/version = '${xtext_version}'/g\" gradle/versions.gradle",
            returnStdout: true
        ).trim()
    }
    return update_cmd
}

return this
