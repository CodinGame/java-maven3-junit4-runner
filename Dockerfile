FROM us.gcr.io/codingame-test/compilers/javac:1

# Copy files
COPY target/junit-runner-0.0.1-SNAPSHOT-jar-with-dependencies.jar /usr/src/codingame/junit-runner/junit-runner.jar
COPY src/main/resources/junit-runner /usr/src/codingame/junit-runner/


ENTRYPOINT ["/usr/src/codingame/junit-runner/junit-runner"]
