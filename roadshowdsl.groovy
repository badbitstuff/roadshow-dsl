gitusername = "badbitstuff"
repo = "git@github.com:${gitusername}/roadshow.git"
prefix = "GEN"
branch = "master"

job("${gitusername}.roadshow.${prefix}.build") {
    scm {
        git(repo, branch)
    }
    triggers {
        scm('* * * * *')
    }
    steps {
        gradle('clean war jenkinstest jacoco')
      shell("echo '\n\n#############\n\tBUILDING\n##############\n\n'")
    }
  	publishers {
      	jacocoCodeCoverage()
      	archiveJunit('build/test-results/*.xml')
      	warnings(['Java Compiler (javac)'])
    	downstream("${gitusername}.roadshow.${prefix}.staticanalysis", 'SUCCESS')
    }
}

job("${gitusername}.roadshow.${prefix}.staticanalysis") {
    scm {
        git(repo, branch)
    }
    triggers {
        scm('* * * * *')
    }
    steps {
        gradle('clean staticanalysis')
    }
  	publishers {
      checkstyle('build/reports/checkstyle/*.xml')	
      pmd('build/reports/pmd/*.xml')
      tasks('**/*', '', 'FIXME', 'TODO', 'LOW', true)
  	}
}
