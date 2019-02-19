node {
  properties([
    [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', daysToKeepStr: '40']],
    parameters([
      string(name: 'DAYS', defaultValue: '31', description: 'Delete repositories older than how many days?')
      ,choice(name: 'TYPE', choices: 'N\nS', description: 'Kind of repository to clean up (N=Nightly,S=Stable/Milestone)?')
      ,booleanParam(name: 'DRY_RUN', defaultValue: false, description: 'If enabled do not actually delete the directories, but list them only')
    ])
  ])
  
  def item = Jenkins.instance.getItemByFullName(env.JOB_NAME)
  item.setDescription("Clean up p2 repositories from drop location on Eclipse.org download server.")
  item.save()
  
  stage('Cleanup') {
    sshagent([SSH_TOKEN]) {
      def EXEC = ''
      if ("${DRY_RUN}" == 'false') {
        println '****** WARNING! DELETING DIRECTORIES! ******'
        EXEC = '-exec rm -rf {} \\;'
      }
      def CMD = "ssh genie.xtext@projects-storage.eclipse.org 'find /home/data/httpd/download.eclipse.org/modeling/tmf/xtext/downloads/drops -type d -name ${TYPE}* -mtime +${DAYS} ${EXEC}'"
      sh "${CMD}"
    }
  }
}