package com.github.spotbugs.internal.spotbugs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gradle.api.GradleException;
import org.gradle.api.JavaVersion;
import org.gradle.util.VersionNumber;

public class FindBugsClasspathValidator {

  private final JavaVersion javaVersion;

  public FindBugsClasspathValidator(JavaVersion javaVersion) {
      this.javaVersion = javaVersion;
  }

  public void validateClasspath(Iterable<String> fileNamesOnClasspath) {
      VersionNumber v = getFindbugsVersion(fileNamesOnClasspath);
      boolean java8orMore = javaVersion.compareTo(JavaVersion.VERSION_1_7) > 0;
      boolean findbugs2orLess = v.getMajor() < 3;
      if (java8orMore && findbugs2orLess) {
          throw new FindBugsVersionTooLowException("The version of SpotBugs (" + v + ") inferred from SpotBugs classpath is too low to work with currently used Java version (" + javaVersion + ")."
                  + " Please use higher version of SpotBugs. Inspected SpotBugs classpath: " + fileNamesOnClasspath);
      }
  }

  static class FindBugsVersionTooLowException extends GradleException {
      private static final long serialVersionUID = 1L;

      FindBugsVersionTooLowException(String message) {
          super(message);
      }
  }

  private VersionNumber getFindbugsVersion(Iterable<String> classpath) {
      for (String f: classpath) {
          Matcher m = Pattern.compile("spotbugs-(\\d+.*)\\.jar").matcher(f);
          if (m.matches()) {
              return VersionNumber.parse(m.group(1));
          }
      }
      throw new GradleException("Unable to infer the version of FindBugs from currently specified FindBugs classpath: " + classpath);
  }
}