# Contributing to QPP Conversion Tool

We would love for you to contribute to QPP Conversion Tool. As a contributor, here are the guidelines we would like you to follow:

 - [Question or Problem?](#question)
 - [Issues and Bugs](#issue)
 - [Feature Requests](#feature)
 - [Submission Guidelines](#submit)
 - [Coding Rules](#rules)

## <a name="question"></a> Got a Question or Problem?
If you have a question or problem, please [submit an issue](#submit-issue) to our [GitHub Repository][github].

## <a name="issue"></a> Found a Bug?
If you find a bug in the source code, you can help us by
[submitting an issue](#submit-issue) to our [GitHub Repository][github]. Even better, you can
[submit a Pull Request](#submit-pr) with a fix.

## <a name="feature"></a> Missing a Feature?
You can *request* a new feature by [submitting an issue](#submit-issue) to our GitHub
Repository. If you would like to *implement* a new feature, please submit an issue with
a proposal for your work first, to be sure that we can use it.

## <a name="submit"></a> Submission Guidelines

### <a name="submit-issue"></a> Submitting an Issue

Before you submit an issue, please search the issue tracker, maybe an issue for your problem already exists and the discussion might inform you of workarounds readily available.

We want to fix all the issues as soon as possible, but before fixing a bug we need to reproduce and confirm it. Please provide:

- version of qpp-conversion-tool used
- 3rd-party libraries and their versions
- and most importantly - a use-case that fails

We will be insisting on a minimal reproduce scenario in order to save maintainers time and ultimately be able to fix more bugs.

You can file new issues by filling out our [new issue form](https://github.com/flexion/adele-bpa-qpp-conversion-tool/issues/new).

### <a name="submit-pr"></a> Submitting a Pull Request (PR)

#### Prerequisites

The following must be installed on your computer:
* Java JDK 8 or higher - [Download Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

  It is important that you have the right version of java on your path.

  ```shell
  # When you run 'java -version', you should get 1.8.XXXXX. For example:
  java -version
  java version "1.8.0_121"
  ...
  ```

* Maven version 3.3 or higher - [Download Maven](https://maven.apache.org/)

  ```shell
  # When you run 'mvn -v', you should get 1.3.X. For example:
  mvn -v
  Apache Maven 3.3.9
  ...
  ```

* Git - [Download Git](https://git-scm.com/downloads)

#### Building from Source

```shell
# Clone the GitHub repository:
git clone https://github.com/flexion/adele-bpa-qpp-conversion-tool.git qpp-conversion-tool

# Go to the qpp-conversion-tool directory:
cd qpp-conversion-tool

# Run Maven test to build and run tests locally. All tests shoud pass:
mvn test

# Run Maven package to build the JAR:
mvn package
```

#### Run the 'convert' script to verify everything's working.

If you're on linux or OSX, run the shell script

```shell
# Run the 'convert' shell script, passing the valid-QRDA-III.xml file as a parameter:
./convert.sh src/test/resources/valid-QRDA-III.xml

...

created valid-QRDA-III.qpp.xml
```
If you're on Windows, run the .ps1 file.

```shell
# Run the 'convert' Powershell script, passing the valid-QRDA-III.xml file as a parameter:
./convert.ps1 src/test/resources/valid-QRDA-III.xml

...

created valid-QRDA-III.qpp.xml
```

#### Submitting a Pull Request (PR)

Before you submit your Pull Request (PR) consider the following guidelines:

* Search [GitHub](https://github.com/flexion/adele-bpa-qpp-conversion-tool/pulls) for an open or closed PR
  that relates to your submission. You don't want to duplicate effort.
* Make your changes in a new git branch:

     ```shell
     git checkout -b my-fix-branch master
     ```

* Create your patch, **including appropriate test cases**.
* Follow our [Coding Rules](#rules).
* Run the full test suite,
  and ensure that all tests pass.
* Commit your changes using a descriptive commit message.

     ```shell
     git commit -a
     ```
  Note: the optional commit `-a` command line option will automatically "add" and "rm" edited files.

* Push your branch to GitHub:

    ```shell
    git push origin my-fix-branch
    ```

* In GitHub, send a pull request to `adele-bpa-qpp-conversion-tool:master`.
* If we suggest changes then:
  * Make the required updates.
  * Re-run the full test suites to ensure tests are still passing.
  * Rebase your branch and force push to your GitHub repository (this will update your Pull Request):

    ```shell
    git rebase master -i
    git push -f
    ```

That's it! Thank you for your contribution!

#### After your pull request is merged

After your pull request is merged, you can safely delete your branch and pull the changes
from the main (upstream) repository:

* Delete the remote branch on GitHub either through the GitHub web UI or your local shell as follows:

    ```shell
    git push origin --delete my-fix-branch
    ```

* Check out the master branch:

    ```shell
    git checkout master -f
    ```

* Delete the local branch:

    ```shell
    git branch -D my-fix-branch
    ```

* Update your master with the latest upstream version:

    ```shell
    git pull --ff upstream master
    ```

## <a name="rules"></a> Coding Rules
To ensure consistency throughout the source code, keep these rules in mind as you are working:

* All features or bug fixes **must be tested** by one or more specs (unit-tests).
* All public API methods **must be documented**. (Details TBC).

[github]: https://github.com/flexion/adele-bpa-qpp-conversion-tool