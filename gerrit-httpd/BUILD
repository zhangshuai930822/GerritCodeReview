load('//tools/bzl:junit.bzl', 'junit_tests')

SRCS = glob(
  ['src/main/java/**/*.java'],
)
RESOURCES = glob(['src/main/resources/**/*'])

java_library(
  name = 'httpd',
  srcs = SRCS,
  resources = RESOURCES,
  deps = [
    '//gerrit-antlr:query_exception',
    '//gerrit-common:annotations',
    '//gerrit-common:server',
    '//gerrit-extension-api:api',
    '//gerrit-gwtexpui:linker_server',
    '//gerrit-gwtexpui:server',
    '//gerrit-launcher:launcher',
    '//gerrit-patch-jgit:server',
    '//gerrit-prettify:server',
    '//gerrit-reviewdb:server',
    '//gerrit-server:server',
    '//gerrit-util-cli:cli',
    '//gerrit-util-http:http',
    '//lib:args4j',
    '//lib:gson',
    '//lib:guava',
    '//lib:gwtjsonrpc',
    '//lib:gwtorm',
    '//lib:jsch',
    '//lib:mime-util',
    '//lib:servlet-api-3_1',
    '//lib/auto:auto-value',
    '//lib/commons:codec',
    '//lib/guice:guice',
    '//lib/guice:guice-assistedinject',
    '//lib/guice:guice-servlet',
    '//lib/jgit/org.eclipse.jgit:jgit',
    '//lib/jgit/org.eclipse.jgit.http.server:jgit-servlet',
    '//lib/log:api',
    '//lib/lucene:lucene-core-and-backward-codecs',
  ],
  visibility = ['//visibility:public'],
)

junit_tests(
  name = 'httpd_tests',
  srcs = glob(['src/test/java/**/*.java']),
  deps = [
    ':httpd',
    '//gerrit-common:server',
    '//gerrit-extension-api:api',
    '//gerrit-reviewdb:server',
    '//gerrit-server:server',
    '//gerrit-util-http:http',
    '//gerrit-util-http:testutil',
    '//lib:jimfs',
    '//lib:junit',
    '//lib:gson',
    '//lib:gwtorm',
    '//lib:guava',
    '//lib:servlet-api-3_1-without-neverlink',
    '//lib:truth',
    '//lib/easymock:easymock',
    '//lib/guice:guice',
    '//lib/guice:guice-servlet',
    '//lib/jgit/org.eclipse.jgit:jgit',
    '//lib/jgit/org.eclipse.jgit.junit:junit',
    '//lib/joda:joda-time',
  ],
)